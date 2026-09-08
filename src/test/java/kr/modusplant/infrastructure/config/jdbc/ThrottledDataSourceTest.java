package kr.modusplant.infrastructure.config.jdbc;

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("인자 없이 PrintWriter 반환")
    void testGetLogWriter_givenNoArgs_willReturnPrintWriter() throws SQLException {
        // given & when
        PrintWriter returnedValue = Mockito.mock(PrintWriter.class);
        given(dataSource.getLogWriter()).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.getLogWriter()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("Writer로 DataSource 위임 활동 수행")
    void testSetLogWriter_givenWriter_willDelegateToDataSource() throws SQLException {
        // given
        PrintWriter mockWriter = Mockito.mock(PrintWriter.class);

        // when
        throttledDataSource.setLogWriter(mockWriter);

        // then
        verify(dataSource).setLogWriter(mockWriter);
    }

    @Test
    @DisplayName("타임아웃으로 DataSource 위임 활동 수행")
    void testSetLoginTimeout_givenTimeout_willDelegateToDataSource() throws SQLException {
        // given
        int timeout = 10;

        // when
        throttledDataSource.setLoginTimeout(timeout);

        // then
        verify(dataSource).setLoginTimeout(timeout);
    }

    @Test
    @DisplayName("인자 없이 int 반환")
    void testGetLoginTimeout_givenNoArgs_willReturnInt() throws SQLException {
        // given & when
        int returnedValue = 10;
        given(dataSource.getLoginTimeout()).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.getLoginTimeout()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 Logger 반환")
    void testGetParentLogger_givenNoArgs_willReturnLogger() throws SQLFeatureNotSupportedException {
        // given & when
        Logger returnedValue = Mockito.mock(Logger.class);
        given(dataSource.getParentLogger()).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.getParentLogger()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("클래스로 Object 반환")
    void testUnwrap_givenClass_willReturnObject() throws SQLException {
        // given & when
        Object returnedValue = Mockito.mock(Object.class);
        given(dataSource.unwrap(any())).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.unwrap(Object.class)).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("클래스로 불리언 반환")
    void testIsWrapperFor_givenClass_willReturnBoolean() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(dataSource.isWrapperFor(any())).willReturn(returnedValue);

        // then
        assertThat(throttledDataSource.isWrapperFor(Object.class)).isEqualTo(returnedValue);
    }
}
