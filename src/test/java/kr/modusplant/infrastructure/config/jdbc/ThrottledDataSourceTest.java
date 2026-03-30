package kr.modusplant.infrastructure.config.jdbc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class ThrottledDataSourceTest {
    private final DataSource dataSource = Mockito.mock(DataSource.class);
    private final ThrottledDataSource throttledDataSource = new ThrottledDataSource(dataSource, 10);

    @Test
    void getLogWriterTest() throws SQLException {
        // given & when
        PrintWriter returnedValue = Mockito.mock(PrintWriter.class);
        given(dataSource.getLogWriter()).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.getLogWriter()).isEqualTo(returnedValue);
    }

    @Test
    void setLogWriterTest() throws SQLException {
        // given
        PrintWriter mockWriter = Mockito.mock(PrintWriter.class);

        // when
        throttledDataSource.setLogWriter(mockWriter);

        // then
        verify(dataSource).setLogWriter(mockWriter);
    }

    @Test
    void setLoginTimeoutTest() throws SQLException {
        // given
        int timeout = 10;

        // when
        throttledDataSource.setLoginTimeout(timeout);

        // then
        verify(dataSource).setLoginTimeout(timeout);
    }

    @Test
    void getLoginTimeoutTest() throws SQLException {
        // given & when
        int returnedValue = 10;
        given(dataSource.getLoginTimeout()).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.getLoginTimeout()).isEqualTo(returnedValue);
    }

    @Test
    void getParentLoggerTest() throws SQLFeatureNotSupportedException {
        // given & when
        Logger returnedValue = Mockito.mock(Logger.class);
        given(dataSource.getParentLogger()).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.getParentLogger()).isEqualTo(returnedValue);
    }

    @Test
    void unwrapTest() throws SQLException {
        // given & when
        Object returnedValue = Mockito.mock(Object.class);
        given(dataSource.unwrap(any())).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.unwrap(Object.class)).isEqualTo(returnedValue);
    }

    @Test
    void isWrapperForTest() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(dataSource.isWrapperFor(any())).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.isWrapperFor(Object.class)).isEqualTo(returnedValue);
    }
}