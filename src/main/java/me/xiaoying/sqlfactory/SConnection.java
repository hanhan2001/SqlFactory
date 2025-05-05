package me.xiaoying.sqlfactory;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

final class SConnection implements Connection {
    private final SqlFactory factory;
    private final Connection originConnection;

    private Date lastAccess;

    public SConnection(SqlFactory factory, Connection originConnection) {
        this.factory = factory;
        this.originConnection = originConnection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.rollback();
    }

    @Override
    public void close() throws SQLException {
        this.lastAccess = new Date();
        this.close(false);
    }

    /**
     * 关闭 Connection
     *
     * @param really 是否真正关闭 Connection
     * @throws SQLException SqlException
     */
    public void close(boolean really) throws SQLException {
        this.lastAccess = new Date();

        if (really)
            this.originConnection.close();
        else
            this.factory.releaseConnection(this);
    }

    @Override
    public boolean isClosed() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.lastAccess = new Date();
        this.originConnection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.lastAccess = new Date();
        this.originConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.lastAccess = new Date();
        this.originConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        this.lastAccess = new Date();
        return this.originConnection.isWrapperFor(iface);
    }

    /**
     * 超过多少 ms 后判断为闲置链接
     *
     * @param ms 超时时间
     * @return 是否为闲置链接
     */
    public boolean timeout(long ms) {
        return new Date().getTime() - this.lastAccess.getTime() >= ms;
    }
}