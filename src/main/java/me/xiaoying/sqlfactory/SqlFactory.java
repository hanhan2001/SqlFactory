package me.xiaoying.sqlfactory;

import me.xiaoying.sqlfactory.merge.MysqlMerge;
import me.xiaoying.sqlfactory.sentence.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public void run(String... sentence) {
        try {
            Connection connection = this.getConnection();

            for (String string : sentence) {
                PreparedStatement preparedStatement = connection.prepareStatement(string);
                preparedStatement.execute();
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(Sentence... sentence) {
        for (Sentence s : sentence) this.run(s.merge());
    }

    protected abstract Connection createConnection();
}