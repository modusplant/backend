package kr.modusplant.infrastructure.config.jdbc;

import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("인자 없이 Statement 반환")
    void testCreateStatement_givenNoArgs_willReturnStatement() throws SQLException {
        // given & when
        Statement returnedValue = Mockito.mock(Statement.class);
        given(connection.createStatement()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStatement()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("SQL로 PreparedStatement 반환")
    void testPrepareStatement_givenSql_willReturnPreparedStatement() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1")).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("SQL로 CallableStatement 반환")
    void testPrepareCall_givenSql_willReturnCallableStatement() throws SQLException {
        // given & when
        CallableStatement returnedValue = Mockito.mock(CallableStatement.class);
        given(connection.prepareCall(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareCall("test call")).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("SQL로 문자열 반환")
    void testNativeSQL_givenSql_willReturnString() throws SQLException {
        // given & when
        String returnedValue = "SELECT 1";
        given(connection.nativeSQL(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.nativeSQL("SELECT 1")).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("플래그로 Connection 위임 활동 수행")
    void testSetAutoCommit_givenFlag_willDelegateToConnection() throws SQLException {
        // given
        boolean autoCommit = false;

        // when
        throttledConnection.setAutoCommit(autoCommit);

        // then
        verify(connection).setAutoCommit(autoCommit);
    }

    @Test
    @DisplayName("인자 없이 불리언 반환")
    void testGetAutoCommit_givenNoArgs_willReturnBoolean() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.getAutoCommit()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getAutoCommit()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 Connection 위임 활동 수행")
    void testCommit_givenNoArgs_willDelegateToConnection() throws SQLException {
        // given & when
        throttledConnection.commit();

        // then
        verify(connection).commit();
    }

    @Test
    @DisplayName("인자 없이 Connection 위임 활동 수행")
    void testRollback_givenNoArgs_willDelegateToConnection() throws SQLException {
        // given & when
        throttledConnection.rollback();

        // then
        verify(connection).rollback();
    }

    @Test
    @DisplayName("인자 없이 불리언 반환")
    void testIsClosed_givenNoArgs_willReturnBoolean() throws SQLException {
        // given & when
        boolean returnedValue = false;
        given(connection.isClosed()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isClosed()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 DatabaseMetaData 반환")
    void testGetMetaData_givenNoArgs_willReturnDatabaseMetaData() throws SQLException {
        // given & when
        DatabaseMetaData returnedValue = Mockito.mock(DatabaseMetaData.class);
        given(connection.getMetaData()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getMetaData()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("플래그로 Connection 위임 활동 수행")
    void testSetReadOnly_givenFlag_willDelegateToConnection() throws SQLException {
        // given
        boolean readOnly = true;

        // when
        throttledConnection.setReadOnly(readOnly);

        // then
        verify(connection).setReadOnly(readOnly);
    }

    @Test
    @DisplayName("인자 없이 불리언 반환")
    void testIsReadOnly_givenNoArgs_willReturnBoolean() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.isReadOnly()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isReadOnly()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("카탈로그로 Connection 위임 활동 수행")
    void testSetCatalog_givenCatalog_willDelegateToConnection() throws SQLException {
        // given
        String catalog = "test catalog";

        // when
        throttledConnection.setCatalog(catalog);

        // then
        verify(connection).setCatalog(catalog);
    }

    @Test
    @DisplayName("인자 없이 문자열 반환")
    void testGetCatalog_givenNoArgs_willReturnString() throws SQLException {
        // given & when
        String returnedValue = "test catalog";
        given(connection.getCatalog()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getCatalog()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("격리 수준으로 Connection 위임 활동 수행")
    void testSetTransactionIsolation_givenLevel_willDelegateToConnection() throws SQLException {
        // given
        int level = Connection.TRANSACTION_READ_COMMITTED;

        // when
        throttledConnection.setTransactionIsolation(level);

        // then
        verify(connection).setTransactionIsolation(level);
    }

    @Test
    @DisplayName("인자 없이 int 반환")
    void testGetTransactionIsolation_givenNoArgs_willReturnInt() throws SQLException {
        // given & when
        int returnedValue = Connection.TRANSACTION_READ_COMMITTED;
        given(connection.getTransactionIsolation()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getTransactionIsolation()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 SQLWarning 반환")
    void testGetWarnings_givenNoArgs_willReturnSQLWarning() throws SQLException {
        // given & when
        SQLWarning returnedValue = Mockito.mock(SQLWarning.class);
        given(connection.getWarnings()).willReturn(returnedValue);

        // then
        assertThat(Optional.ofNullable(throttledConnection.getWarnings())).isEqualTo(Optional.ofNullable(returnedValue));
    }

    @Test
    @DisplayName("인자 없이 Connection 위임 활동 수행")
    void testClearWarnings_givenNoArgs_willDelegateToConnection() throws SQLException {
        // given & when
        throttledConnection.clearWarnings();

        // then
        verify(connection).clearWarnings();
    }

    @SuppressWarnings("MagicConstant")
    @Test
    @DisplayName("ResultSet 타입으로 Statement 반환")
    void testCreateStatement_givenResultSetType_willReturnStatement() throws SQLException {
        // given & when
        Statement returnedValue = Mockito.mock(Statement.class);
        given(connection.createStatement(anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    @DisplayName("ResultSet 타입으로 PreparedStatement 반환")
    void testPrepareStatement_givenResultSetType_willReturnPreparedStatement() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("ResultSet 타입으로 CallableStatement 반환")
    void testPrepareCall_givenResultSetType_willReturnCallableStatement() throws SQLException {
        // given & when
        CallableStatement returnedValue = Mockito.mock(CallableStatement.class);
        given(connection.prepareCall(anyString(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareCall("test call", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("인자 없이 Map 반환")
    void testGetTypeMap_givenNoArgs_willReturnMap() throws SQLException {
        // given & when
        Map<String, Class<?>> returnedValue = Mockito.mock(Map.class);
        given(connection.getTypeMap()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getTypeMap()).isEqualTo(returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Map으로 Connection 위임 활동 수행")
    void testSetTypeMap_givenMap_willDelegateToConnection() throws SQLException {
        // given
        Map<String, Class<?>> map = Mockito.mock(Map.class);

        // when
        throttledConnection.setTypeMap(map);

        // then
        verify(connection).setTypeMap(map);
    }

    @Test
    @DisplayName("holdability로 Connection 위임 활동 수행")
    void testSetHoldability_givenHoldability_willDelegateToConnection() throws SQLException {
        // given
        int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;

        // when
        throttledConnection.setHoldability(holdability);

        // then
        verify(connection).setHoldability(holdability);
    }

    @Test
    @DisplayName("인자 없이 int 반환")
    void testGetHoldability_givenNoArgs_willReturnInt() throws SQLException {
        // given & when
        int returnedValue = ResultSet.CLOSE_CURSORS_AT_COMMIT;
        given(connection.getHoldability()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getHoldability()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 Savepoint 반환")
    void testSetSavepoint_givenNoArgs_willReturnSavepoint() throws SQLException {
        // given & when
        Savepoint returnedValue = Mockito.mock(Savepoint.class);
        given(connection.setSavepoint()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.setSavepoint()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("이름으로 Savepoint 반환")
    void testSetSavepoint_givenName_willReturnSavepoint() throws SQLException {
        // given & when
        Savepoint returnedValue = Mockito.mock(Savepoint.class);
        given(connection.setSavepoint(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.setSavepoint("savepoint")).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("Savepoint로 Connection 위임 활동 수행")
    void testRollback_givenSavepoint_willDelegateToConnection() throws SQLException {
        // given
        Savepoint savepoint = Mockito.mock(Savepoint.class);

        // when
        throttledConnection.rollback(savepoint);

        // then
        verify(connection).rollback(savepoint);
    }

    @Test
    @DisplayName("Savepoint로 Connection 위임 활동 수행")
    void testReleaseSavepoint_givenSavepoint_willDelegateToConnection() throws SQLException {
        // given
        Savepoint savepoint = Mockito.mock(Savepoint.class);

        // when
        throttledConnection.releaseSavepoint(savepoint);

        // then
        verify(connection).releaseSavepoint(savepoint);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    @DisplayName("holdability로 Statement 반환")
    void testCreateStatement_givenHoldability_willReturnStatement() throws SQLException {
        // given & when
        Statement returnedValue = Mockito.mock(Statement.class);
        given(connection.createStatement(anyInt(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    @DisplayName("holdability로 PreparedStatement 반환")
    void testPrepareStatement_givenHoldability_willReturnPreparedStatement() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), anyInt(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT)).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("holdability로 CallableStatement 반환")
    void testPrepareCall_givenHoldability_willReturnCallableStatement() throws SQLException {
        // given & when
        CallableStatement returnedValue = Mockito.mock(CallableStatement.class);
        given(connection.prepareCall(anyString(), anyInt(), anyInt(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareCall("test call", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT)).isEqualTo(returnedValue);
    }

    @SuppressWarnings("MagicConstant")
    @Test
    @DisplayName("autoGeneratedKeys로 PreparedStatement 반환")
    void testPrepareStatement_givenAutoGeneratedKeys_willReturnPreparedStatement() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", Statement.RETURN_GENERATED_KEYS)).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("컬럼 인덱스로 PreparedStatement 반환")
    void testPrepareStatement_givenColumnIndexes_willReturnPreparedStatement() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), any(int[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", new int[]{1, 2})).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("컬럼 이름으로 PreparedStatement 반환")
    void testPrepareStatement_givenColumnNames_willReturnPreparedStatement() throws SQLException {
        // given & when
        PreparedStatement returnedValue = Mockito.mock(PreparedStatement.class);
        given(connection.prepareStatement(anyString(), any(String[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.prepareStatement("SELECT 1", new String[]{"id"})).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 Clob 반환")
    void testCreateClob_givenNoArgs_willReturnClob() throws SQLException {
        // given & when
        Clob returnedValue = Mockito.mock(Clob.class);
        given(connection.createClob()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createClob()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 Blob 반환")
    void testCreateBlob_givenNoArgs_willReturnBlob() throws SQLException {
        // given & when
        Blob returnedValue = Mockito.mock(Blob.class);
        given(connection.createBlob()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createBlob()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 NClob 반환")
    void testCreateNClob_givenNoArgs_willReturnNClob() throws SQLException {
        // given & when
        NClob returnedValue = Mockito.mock(NClob.class);
        given(connection.createNClob()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createNClob()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 SQLXML 반환")
    void testCreateSQLXML_givenNoArgs_willReturnSQLXML() throws SQLException {
        // given & when
        SQLXML returnedValue = Mockito.mock(SQLXML.class);
        given(connection.createSQLXML()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createSQLXML()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("타임아웃으로 불리언 반환")
    void testIsValid_givenTimeout_willReturnBoolean() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.isValid(anyInt())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isValid(5)).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("이름과 값으로 Connection 위임 활동 수행")
    void testSetClientInfo_givenNameAndValue_willDelegateToConnection() throws SQLClientInfoException {
        // given
        String name = "ApplicationName";
        String value = "MyApp";

        // when
        throttledConnection.setClientInfo(name, value);

        // then
        verify(connection).setClientInfo(name, value);
    }

    @Test
    @DisplayName("Properties로 Connection 위임 활동 수행")
    void testSetClientInfo_givenProperties_willDelegateToConnection() throws SQLClientInfoException {
        // given
        Properties properties = new Properties();

        // when
        throttledConnection.setClientInfo(properties);

        // then
        verify(connection).setClientInfo(properties);
    }

    @Test
    @DisplayName("이름으로 문자열 반환")
    void testGetClientInfo_givenName_willReturnString() throws SQLException {
        // given & when
        String returnedValue = "ModusPlant";
        given(connection.getClientInfo(anyString())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getClientInfo("ApplicationName")).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("인자 없이 Properties 반환")
    void testGetClientInfo_givenNoArgs_willReturnProperties() throws SQLException {
        // given & when
        Properties returnedValue = Mockito.mock(Properties.class);
        given(connection.getClientInfo()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getClientInfo()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("타입과 요소로 Array 반환")
    void testCreateArrayOf_givenTypeAndElements_willReturnArray() throws SQLException {
        // given & when
        Array returnedValue = Mockito.mock(Array.class);
        given(connection.createArrayOf(anyString(), any(Object[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createArrayOf("VARCHAR", new Object[]{"A", "B"})).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("타입과 속성으로 Struct 반환")
    void testCreateStruct_givenTypeAndAttributes_willReturnStruct() throws SQLException {
        // given & when
        Struct returnedValue = Mockito.mock(Struct.class);
        given(connection.createStruct(anyString(), any(Object[].class))).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.createStruct("MY_STRUCT", new Object[]{"A", 1})).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("스키마로 Connection 위임 활동 수행")
    void testSetSchema_givenSchema_willDelegateToConnection() throws SQLException {
        // given
        String schema = "public";

        // when
        throttledConnection.setSchema(schema);

        // then
        verify(connection).setSchema(schema);
    }

    @Test
    @DisplayName("인자 없이 문자열 반환")
    void testGetSchema_givenNoArgs_willReturnString() throws SQLException {
        // given & when
        String returnedValue = "public";
        given(connection.getSchema()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getSchema()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("Executor로 Connection 위임 활동 수행")
    void testAbort_givenExecutor_willDelegateToConnection() throws SQLException {
        // given
        Executor executor = Mockito.mock(Executor.class);

        // when
        throttledConnection.abort(executor);

        // then
        verify(connection).abort(executor);
    }

    @Test
    @DisplayName("Executor와 타임아웃으로 Connection 위임 활동 수행")
    void testSetNetworkTimeout_givenExecutorAndTimeout_willDelegateToConnection() throws SQLException {
        // given
        Executor executor = Mockito.mock(Executor.class);
        int timeout = 5000;

        // when
        throttledConnection.setNetworkTimeout(executor, timeout);

        // then
        verify(connection).setNetworkTimeout(executor, timeout);
    }

    @Test
    @DisplayName("인자 없이 int 반환")
    void testGetNetworkTimeout_givenNoArgs_willReturnInt() throws SQLException {
        // given & when
        int returnedValue = 5000;
        given(connection.getNetworkTimeout()).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.getNetworkTimeout()).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("클래스로 Object 반환")
    void testUnwrap_givenClass_willReturnObject() throws SQLException {
        // given & when
        Object returnedValue = Mockito.mock(Object.class);
        given(connection.unwrap(any())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.unwrap(Object.class)).isEqualTo(returnedValue);
    }

    @Test
    @DisplayName("클래스로 불리언 반환")
    void testIsWrapperFor_givenClass_willReturnBoolean() throws SQLException {
        // given & when
        boolean returnedValue = true;
        given(connection.isWrapperFor(any())).willReturn(returnedValue);

        // then
        assertThat(throttledConnection.isWrapperFor(Object.class)).isEqualTo(returnedValue);
    }
}
