package me.xiaoying.sqlfactory;

import me.xiaoying.sqlfactory.annotation.Column;
import me.xiaoying.sqlfactory.merge.MysqlMerge;
import me.xiaoying.sqlfactory.sentence.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            Map<Integer, Object[]> parameters = new HashMap<>();
            Map<Integer, Map<String, Object>> values = new HashMap<>();

            try {
                Connection connection = this.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sen.merge());
                ResultSet resultSet = preparedStatement.executeQuery();

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                int index = 0;
                while (resultSet.next()) {
                    Map<String, Object> map;

                    if ((map = values.get(index)) == null)
                        map = new HashMap<>();

                    parameters.put(index, new Object[select.getParameters().size()]);

                    for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                        String name = resultSetMetaData.getColumnName(i + 1);

                        if (!select.getParameters().containsKey(name)) {
                            map.put(name, resultSet.getObject(i + 1));
                            continue;
                        }

                        parameters.get(index)[select.getParameters().get(name)] = resultSet.getObject(i + 1);
                    }

                    values.put(index++, map);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < values.size(); i++) {
                Object object = null;

                try {
                    if (select.getConstructor() == null)
                        object = select.getClazz().newInstance();
                    else {
                        select.getConstructor().setAccessible(true);
                        object = select.getConstructor().newInstance(parameters.get(i));
                    }

                    for (Field declaredField : select.getClazz().getDeclaredFields()) {
                        declaredField.setAccessible(true);

                        if (declaredField.getAnnotation(Column.class) == null)
                            continue;

                        if (Modifier.isFinal(declaredField.getModifiers()))
                            continue;

                        declaredField.set(object, values.get(i).get(declaredField.getName()));
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                objects.add(object);
            }
        }

        return objects;
    }

    protected abstract Connection createConnection();
}