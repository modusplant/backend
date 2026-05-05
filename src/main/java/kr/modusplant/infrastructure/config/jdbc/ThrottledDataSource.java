package kr.modusplant.infrastructure.config.jdbc;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

class ThrottledDataSource implements DataSource {

    private final DataSource delegatedDataSource;
    private final Semaphore semaphore;

    public ThrottledDataSource(DataSource delegatedDataSource, int maxConcurrentConnections) {
        this.delegatedDataSource = delegatedDataSource;
        this.semaphore = new Semaphore(maxConcurrentConnections);
    }

    @Override
    public Connection getConnection() throws SQLException {
        boolean isSuccess = false;

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection: ", e);
        }

        try {
            Connection connection = delegatedDataSource.getConnection();
            ThrottledConnection throttledConnection = new ThrottledConnection(connection, semaphore);
            isSuccess = true;
            return throttledConnection;
        } finally {
            if (!isSuccess) {
                semaphore.release();
            }
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        boolean isSuccess = false;

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection: ", e);
        }

        try {
            Connection connection = delegatedDataSource.getConnection(username, password);
            ThrottledConnection throttledConnection = new ThrottledConnection(connection, semaphore);
            isSuccess = true;
            return throttledConnection;
        } finally {
            if (!isSuccess) {
                semaphore.release();
            }
        }

    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegatedDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegatedDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegatedDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegatedDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegatedDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegatedDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegatedDataSource.isWrapperFor(iface);
    }
}