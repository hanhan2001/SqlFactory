package me.xiaoying.sqlfactory;

import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.merge.MysqlMerge;
import me.xiaoying.sqlfactory.sentence.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SqlFactory {
    private final int maxPoolSize;
    private final int connectionTimeout;

    private final BlockingQueue<Connection> availableConnections;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    static {
        // 默认注册 Mysql 的 merge
        SentenceManager.registerMerge(Create.class, SqlFactory.class, MysqlMerge::create);
        SentenceManager.registerMerge(Insert.class, SqlFactory.class, MysqlMerge::insert);
        SentenceManager.registerMerge(Update.class, SqlFactory.class, MysqlMerge::update);
        SentenceManager.registerMerge(Select.class, SqlFactory.class, MysqlMerge::select);
    }

    public SqlFactory(int maxPoolSize) {
        this(maxPoolSize, 2000);
    }

    public SqlFactory(int maxPoolSize, int connectionTimeout) {
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

    public Object run(String... sentence) {
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

        return null;
    }

    public Object run(Sentence... sentence) {
        for (Sentence sen : sentence) {
            if (!(sen instanceof Select)) {
                this.run(sen.toString());
                continue;
            }

            Select select = (Select) sen;

            if (select.getConstructor() == null)
                continue;

            Object[] parameters = new Object[select.getParameters().size()];
            Map<String, Object> values = new HashMap<>();

            try {
                Connection connection = this.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sen.merge());
                ResultSet resultSet = preparedStatement.executeQuery();

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                while (resultSet.next()) {
                    for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                        String name = resultSetMetaData.getColumnName(i + 1);

                        if (!select.getParameters().containsKey(name)) {
                            values.put(name, resultSet.getObject(i + 1));
                            continue;
                        }

                        parameters[select.getParameters().get(name)] = resultSet.getObject(i + 1);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Object object;

            try {
                select.getConstructor().setAccessible(true);
                object = select.getConstructor().newInstance(parameters);

                for (Field declaredField : select.getClazz().getDeclaredFields()) {
                    declaredField.setAccessible(true);

                    if (declaredField.getAnnotation(Column.class) == null)
                        continue;

                    if (Modifier.isFinal(declaredField.getModifiers()))
                        continue;

                    declaredField.set(object, values.get(declaredField.getName()));

                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }

            return object;
        }

        return null;
    }

    protected abstract Connection createConnection();
}