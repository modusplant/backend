package kr.modusplant.infrastructure.config.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

class ThrottledConnection implements Connection {

    private final Connection delegatedConnection;
    private final Semaphore semaphore;
    private boolean closed = false;

    public ThrottledConnection(Connection delegatedConnection, Semaphore semaphore) {
        this.delegatedConnection = delegatedConnection;
        this.semaphore = semaphore;
    }

    @Override
    public void close() throws SQLException {
        if (!closed) {
            try {
                delegatedConnection.close();
            } finally {
                semaphore.release();
                closed = true;
            }
        }
    }

    @Override
    public java.sql.Statement createStatement() throws SQLException {
        return delegatedConnection.createStatement();
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
        return delegatedConnection.prepareStatement(sql);
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
        return delegatedConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return delegatedConnection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        delegatedConnection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return delegatedConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        delegatedConnection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        delegatedConnection.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed || delegatedConnection.isClosed();
    }

    @Override
    public java.sql.DatabaseMetaData getMetaData() throws SQLException {
        return delegatedConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        delegatedConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return delegatedConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        delegatedConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return delegatedConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        delegatedConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return delegatedConnection.getTransactionIsolation();
    }

    @Override
    public java.sql.SQLWarning getWarnings() throws SQLException {
        return delegatedConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        delegatedConnection.clearWarnings();
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return delegatedConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return delegatedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return delegatedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
        return delegatedConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
        delegatedConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        delegatedConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return delegatedConnection.getHoldability();
    }

    @Override
    public java.sql.Savepoint setSavepoint() throws SQLException {
        return delegatedConnection.setSavepoint();
    }

    @Override
    public java.sql.Savepoint setSavepoint(String name) throws SQLException {
        return delegatedConnection.setSavepoint(name);
    }

    @Override
    public void rollback(java.sql.Savepoint savepoint) throws SQLException {
        delegatedConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
        delegatedConnection.releaseSavepoint(savepoint);
    }

    @Override
    public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return delegatedConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return delegatedConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return delegatedConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return delegatedConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return delegatedConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return delegatedConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public java.sql.Clob createClob() throws SQLException {
        return delegatedConnection.createClob();
    }

    @Override
    public java.sql.Blob createBlob() throws SQLException {
        return delegatedConnection.createBlob();
    }

    @Override
    public java.sql.NClob createNClob() throws SQLException {
        return delegatedConnection.createNClob();
    }

    @Override
    public java.sql.SQLXML createSQLXML() throws SQLException {
        return delegatedConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return delegatedConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
        delegatedConnection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException {
        delegatedConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return delegatedConnection.getClientInfo(name);
    }

    @Override
    public java.util.Properties getClientInfo() throws SQLException {
        return delegatedConnection.getClientInfo();
    }

    @Override
    public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return delegatedConnection.createArrayOf(typeName, elements);
    }

    @Override
    public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return delegatedConnection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        delegatedConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return delegatedConnection.getSchema();
    }

    @Override
    public void abort(java.util.concurrent.Executor executor) throws SQLException {
        delegatedConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {
        delegatedConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return delegatedConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegatedConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegatedConnection.isWrapperFor(iface);
    }
}