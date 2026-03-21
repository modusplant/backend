package kr.modusplant.infrastructure.config.jdbc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class ThrottledConnectionTest {
    private final Connection connection = Mockito.mock(Connection.class);
    private final Semaphore semaphore = Mockito.mock(Semaphore.class);
    private final ThrottledConnection throttledConnection = new ThrottledConnection(connection, semaphore);

    @Test
    void createStatementTest() throws SQLException {
        // given & when
        Statement returnedValue = Mockito.mock(Statement.class);
        given(connection.createStatement()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStatement()).isEqualTo(returnedValue);
    }

    @Test
    void prepareStatementTest() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1")).isEqualTo(returnedValue);
    }

    @Test
    void prepareCallTest() throws SQLException {
        // given & when
        CallableStatement returnedValue = Mockito.mock(CallableStatement.class);
        given(connection.prepareCall(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareCall("test call")).isEqualTo(returnedValue);
    }

    @Test
    void nativeSQLTest() throws SQLException {
        // given & when
        String returnedValue = "SELECT 1";
        given(connection.nativeSQL(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.nativeSQL("SELECT 1")).isEqualTo(returnedValue);
    }

    @Test
    void setAutoCommitTest() throws SQLException {
        // given
        boolean autoCommit = false;

        // when
        throttledConnection.setAutoCommit(autoCommit);

        // then
        verify(connection).setAutoCommit(autoCommit);
    }

    @Test
    void getAutoCommitTest() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.getAutoCommit()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getAutoCommit()).isEqualTo(returnedValue);
    }

    @Test
    void commitTest() throws SQLException {
        // given & when
        throttledConnection.commit();

        // then
        verify(connection).commit();
    }

    @Test
    void rollbackTest() throws SQLException {
        // given & when
        throttledConnection.rollback();

        // then
        verify(connection).rollback();
    }

    @Test
    void isClosedTest() throws SQLException {
        // given & when
        boolean returnedValue = false;
        given(connection.isClosed()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isClosed()).isEqualTo(returnedValue);
    }

    @Test
    void getMetaDataTest() throws SQLException {
        // given & when
        DatabaseMetaData returnedValue = Mockito.mock(DatabaseMetaData.class);
        given(connection.getMetaData()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getMetaData()).isEqualTo(returnedValue);
    }

    @Test
    void setReadOnlyTest() throws SQLException {
        // given
        boolean readOnly = true;

        // when
        throttledConnection.setReadOnly(readOnly);

        // then
        verify(connection).setReadOnly(readOnly);
    }

    @Test
    void isReadOnlyTest() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.isReadOnly()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isReadOnly()).isEqualTo(returnedValue);
    }

    @Test
    void setCatalogTest() throws SQLException {
        // given
        String catalog = "test catalog";

        // when
        throttledConnection.setCatalog(catalog);

        // then
        verify(connection).setCatalog(catalog);
    }

    @Test
    void getCatalogTest() throws SQLException {
        // given & when
        String returnedValue = "test catalog";
        given(connection.getCatalog()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getCatalog()).isEqualTo(returnedValue);
    }

    @Test
    void setTransactionIsolationTest() throws SQLException {
        // given
        int level = Connection.TRANSACTION_READ_COMMITTED;

        // when
        throttledConnection.setTransactionIsolation(level);

        // then
        verify(connection).setTransactionIsolation(level);
    }

    @Test
    void getTransactionIsolationTest() throws SQLException {
        // given & when
        int returnedValue = Connection.TRANSACTION_READ_COMMITTED;
        given(connection.getTransactionIsolation()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getTransactionIsolation()).isEqualTo(returnedValue);
    }

    @Test
    void getWarningsTest() throws SQLException {
        // given & when
        SQLWarning returnedValue = Mockito.mock(SQLWarning.class);
        given(connection.getWarnings()).willReturn(returnedValue);

        // then
        assertThat(Optional.ofNullable(throttledConnection.getWarnings())).isEqualTo(Optional.ofNullable(returnedValue));
    }

    @Test
    void clearWarningsTest() throws SQLException {
        // given & when
        throttledConnection.clearWarnings();

        // then
        verify(connection).clearWarnings();
    }

    @SuppressWarnings("MagicConstant")
    @Test
    void createStatementWithResultSetTypeTest() throws SQLException {
        // given & when
        Statement returnedValue = Mockito.mock(Statement.class);
        given(connection.createStatement(anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    void prepareStatementWithResultSetTypeTest() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isEqualTo(returnedValue);
    }

    @Test
    void prepareCallWithResultSetTypeTest() throws SQLException {
        // given & when
        CallableStatement returnedValue = Mockito.mock(CallableStatement.class);
        given(connection.prepareCall(anyString(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareCall("test call", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getTypeMapTest() throws SQLException {
        // given & when
        Map<String, Class<?>> returnedValue = Mockito.mock(Map.class);
        given(connection.getTypeMap()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getTypeMap()).isEqualTo(returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    void setTypeMapTest() throws SQLException {
        // given
        Map<String, Class<?>> map = Mockito.mock(Map.class);

        // when
        throttledConnection.setTypeMap(map);

        // then
        verify(connection).setTypeMap(map);
    }

    @Test
    void setHoldabilityTest() throws SQLException {
        // given
        int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;

        // when
        throttledConnection.setHoldability(holdability);

        // then
        verify(connection).setHoldability(holdability);
    }

    @Test
    void getHoldabilityTest() throws SQLException {
        // given & when
        int returnedValue = ResultSet.CLOSE_CURSORS_AT_COMMIT;
        given(connection.getHoldability()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getHoldability()).isEqualTo(returnedValue);
    }

    @Test
    void setSavepointTest() throws SQLException {
        // given & when
        Savepoint returnedValue = Mockito.mock(Savepoint.class);
        given(connection.setSavepoint()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.setSavepoint()).isEqualTo(returnedValue);
    }

    @Test
    void setSavepointWithNameTest() throws SQLException {
        // given & when
        Savepoint returnedValue = Mockito.mock(Savepoint.class);
        given(connection.setSavepoint(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.setSavepoint("savepoint")).isEqualTo(returnedValue);
    }

    @Test
    void rollbackWithSavepointTest() throws SQLException {
        // given
        Savepoint savepoint = Mockito.mock(Savepoint.class);

        // when
        throttledConnection.rollback(savepoint);

        // then
        verify(connection).rollback(savepoint);
    }

    @Test
    void releaseSavepointTest() throws SQLException {
        // given
        Savepoint savepoint = Mockito.mock(Savepoint.class);

        // when
        throttledConnection.releaseSavepoint(savepoint);

        // then
        verify(connection).releaseSavepoint(savepoint);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    void createStatementWithHoldabilityTest() throws SQLException {
        // given & when
        Statement returnedValue = Mockito.mock(Statement.class);
        given(connection.createStatement(anyInt(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    void prepareStatementWithHoldabilityTest() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), anyInt(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT)).isEqualTo(returnedValue);
    }

    @Test
    void prepareCallWithHoldabilityTest() throws SQLException {
        // given & when
        CallableStatement returnedValue = Mockito.mock(CallableStatement.class);
        given(connection.prepareCall(anyString(), anyInt(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareCall("test call", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    void prepareStatementWithAutoGeneratedKeysTest() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", Statement.RETURN_GENERATED_KEYS)).isEqualTo(returnedValue);
    }

    @Test
    void prepareStatementWithColumnIndexesTest() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), any(int[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", new int[]{1, 2})).isEqualTo(returnedValue);
    }

    @Test
    void prepareStatementWithColumnNamesTest() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), any(String[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", new String[]{"id"})).isEqualTo(returnedValue);
    }

    @Test
    void createClobTest() throws SQLException {
        // given & when
        Clob returnedValue = Mockito.mock(Clob.class);
        given(connection.createClob()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createClob()).isEqualTo(returnedValue);
    }

    @Test
    void createBlobTest() throws SQLException {
        // given & when
        Blob returnedValue = Mockito.mock(Blob.class);
        given(connection.createBlob()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createBlob()).isEqualTo(returnedValue);
    }

    @Test
    void createNClobTest() throws SQLException {
        // given & when
        NClob returnedValue = Mockito.mock(NClob.class);
        given(connection.createNClob()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createNClob()).isEqualTo(returnedValue);
    }

    @Test
    void createSQLXMLTest() throws SQLException {
        // given & when
        SQLXML returnedValue = Mockito.mock(SQLXML.class);
        given(connection.createSQLXML()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createSQLXML()).isEqualTo(returnedValue);
    }

    @Test
    void isValidTest() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.isValid(anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isValid(5)).isEqualTo(returnedValue);
    }

    @Test
    void setClientInfoWithNameTest() throws SQLClientInfoException {
        // given
        String name = "ApplicationName";
        String value = "MyApp";

        // when
        throttledConnection.setClientInfo(name, value);

        // then
        verify(connection).setClientInfo(name, value);
    }

    @Test
    void setClientInfoWithPropertiesTest() throws SQLClientInfoException {
        // given
        Properties properties = new Properties();

        // when
        throttledConnection.setClientInfo(properties);

        // then
        verify(connection).setClientInfo(properties);
    }

    @Test
    void getClientInfoWithNameTest() throws SQLException {
        // given & when
        String returnedValue = "ModusPlant";
        given(connection.getClientInfo(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getClientInfo("ApplicationName")).isEqualTo(returnedValue);
    }

    @Test
    void getClientInfoTest() throws SQLException {
        // given & when
        Properties returnedValue = Mockito.mock(Properties.class);
        given(connection.getClientInfo()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getClientInfo()).isEqualTo(returnedValue);
    }

    @Test
    void createArrayOfTest() throws SQLException {
        // given & when
        Array returnedValue = Mockito.mock(Array.class);
        given(connection.createArrayOf(anyString(), any(Object[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createArrayOf("VARCHAR", new Object[]{"A", "B"})).isEqualTo(returnedValue);
    }

    @Test
    void createStructTest() throws SQLException {
        // given & when
        Struct returnedValue = Mockito.mock(Struct.class);
        given(connection.createStruct(anyString(), any(Object[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStruct("MY_STRUCT", new Object[]{"A", 1})).isEqualTo(returnedValue);
    }

    @Test
    void setSchemaTest() throws SQLException {
        // given
        String schema = "public";

        // when
        throttledConnection.setSchema(schema);

        // then
        verify(connection).setSchema(schema);
    }

    @Test
    void getSchemaTest() throws SQLException {
        // given & when
        String returnedValue = "public";
        given(connection.getSchema()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getSchema()).isEqualTo(returnedValue);
    }

    @Test
    void abortTest() throws SQLException {
        // given
        Executor executor = Mockito.mock(Executor.class);

        // when
        throttledConnection.abort(executor);

        // then
        verify(connection).abort(executor);
    }

    @Test
    void setNetworkTimeoutTest() throws SQLException {
        // given
        Executor executor = Mockito.mock(Executor.class);
        int timeout = 5000;

        // when
        throttledConnection.setNetworkTimeout(executor, timeout);

        // then
        verify(connection).setNetworkTimeout(executor, timeout);
    }

    @Test
    void getNetworkTimeoutTest() throws SQLException {
        // given & when
        int returnedValue = 5000;
        given(connection.getNetworkTimeout()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getNetworkTimeout()).isEqualTo(returnedValue);
    }

    @Test
    void unwrapTest() throws SQLException {
        // given & when
        Object returnedValue = Mockito.mock(Object.class);
        given(connection.unwrap(any())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.unwrap(Object.class)).isEqualTo(returnedValue);
    }

    @Test
    void isWrapperForTest() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.isWrapperFor(any())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isWrapperFor(Object.class)).isEqualTo(returnedValue);
    }
}