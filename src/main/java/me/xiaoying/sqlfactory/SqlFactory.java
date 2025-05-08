package me.xiaoying.sqlfactory;

import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.merge.MysqlMerge;
import me.xiaoying.sqlfactory.sentence.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SqlFactory {
    private final int maxPoolSize;
    private final long connectionTimeout;

    private final BlockingQueue<Connection> availableConnections;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    static {
        // 默认注册 Mysql 的 merge
        SentenceManager.registerMerge(Create.class, SqlFactory.class, MysqlMerge::create);
        SentenceManager.registerMerge(Insert.class, SqlFactory.class, MysqlMerge::insert);
        SentenceManager.registerMerge(Update.class, SqlFactory.class, MysqlMerge::update);
        SentenceManager.registerMerge(Select.class, SqlFactory.class, MysqlMerge::select);
        SentenceManager.registerMerge(Delete.class, SqlFactory.class, MysqlMerge::delete);
    }

    public SqlFactory(int maxPoolSize) {
        this(maxPoolSize, 2000);
    }

    public SqlFactory(int maxPoolSize, long connectionTimeout) {
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;

        this.availableConnections = new LinkedBlockingQueue<>(this.maxPoolSize);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = this.availableConnections.poll();

        if (connection != null && !connection.isClosed())
            return connection;

        if (this.activeConnections.get() < this.maxPoolSize) {
            if ((this.activeConnections.incrementAndGet()) > this.maxPoolSize)
                this.activeConnections.decrementAndGet();
            else
                return new SConnection(this, this.createConnection());
        }

        try {
            connection = this.availableConnections.poll(this.connectionTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (connection != null && !connection.isClosed())
            return connection;

        throw new SQLException("No available connection");
    }

    public void releaseConnection(SConnection connection) {
        this.availableConnections.offer(connection);
    }

    public void run(String... sentence) {
        try {
            Connection connection = this.getConnection();

            for (String string : sentence) {
                PreparedStatement preparedStatement = connection.prepareStatement(string);
                preparedStatement.execute();
                preparedStatement.close();
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> run(Sentence... sentence) {
        List<Object> objects = new ArrayList<>();

        for (Sentence sen : sentence) {
            if (!(sen instanceof Select)) {
                this.run(sen.toString());
                continue;
            }

            Select select = (Select) sen;

            try {
                PreparedStatement preparedStatement = this.getConnection().prepareStatement(select.merge());
                ResultSet resultSet = preparedStatement.executeQuery();
                ResultSetMetaData metaData = resultSet.getMetaData();

                while (resultSet.next()) {
                    Object[] parameters = new Object[select.getParameters().size()];
                    Map<String, Object> fields = new HashMap<>();

                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnName(i + 1);

                        if (select.getParameters().containsKey(columnName)) {
                            parameters[i] = resultSet.getObject(columnName);
                            continue;
                        }

                        if (select.getConstructor() != null) {
                            if (select.getConstructor().getParameters()[i].getType().isPrimitive())
                                parameters[i] = 0;
                            else
                                parameters[i] = null;
                        }

                        fields.put(columnName, resultSet.getObject(columnName));
                    }

                    Object object;

                    if (select.getConstructor() == null)
                        object = select.getClazz().newInstance();
                    else
                        object = select.getConstructor().newInstance(parameters);

                    for (Field declaredField : select.getClazz().getDeclaredFields()) {
                        if (declaredField.getAnnotation(Column.class) == null)
                            continue;

                        if (select.getParameters().containsKey(declaredField.getName()))
                            continue;

                        declaredField.setAccessible(true);
                        declaredField.set(object, fields.get(declaredField.getName()));
                    }

                    objects.add(object);
                }
            } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e){
                throw new RuntimeException(e);
            }
        }

        return objects;
    }

    protected abstract Connection createConnection();
}