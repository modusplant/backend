package kr.modusplant.shared.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.constant.Regex.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexTest {

    @Test
    @DisplayName("유효한 이메일 문자열로 참 반환")
    void testPatternEmail_givenValidEmailString_willReturnTrue() {
        // given & when & then
        assertTrue(PATTERN_EMAIL.matcher("email@naver.kr").matches());
        assertTrue(PATTERN_EMAIL.matcher("1234@5678.com").matches());
        assertTrue(PATTERN_EMAIL.matcher("email999@naver423.item").matches());
    }

    @Test
    @DisplayName("유효하지 않은 이메일 문자열로 거짓 반환")
    void testPatternEmail_givenInvalidEmailString_willReturnFalse() {
        // given & when & then
        // TLD 가 1자리 이하
        assertFalse(PATTERN_EMAIL.matcher("email@naver.k").matches());
        // TLD 가 5자리 이상
        assertFalse(PATTERN_EMAIL.matcher("email@naver.abcde").matches());
        // TLD 가 대문자
        assertFalse(PATTERN_EMAIL.matcher("1234@5678.AZ").matches());
        // 도메인 주소에 특수 문자 포함
        assertFalse(PATTERN_EMAIL.matcher("email!999@naver.item").matches());
        // TLD, SLD, 또는 3단계 도메인 생략
        assertFalse(PATTERN_EMAIL.matcher("email!999@naver.").matches());
        assertFalse(PATTERN_EMAIL.matcher("email!999@.com").matches());
        assertFalse(PATTERN_EMAIL.matcher("@naver.com").matches());
    }

    @Test
    @DisplayName("유효한 구체화 경로 문자열로 참 반환")
    void testPatternMaterializedPath_givenValidPathString_willReturnTrue() {
        // given & when & then
        assertTrue(PATTERN_MATERIALIZED_PATH.matcher("5").matches());
        assertTrue(PATTERN_MATERIALIZED_PATH.matcher("2.9").matches());
        assertTrue(PATTERN_MATERIALIZED_PATH.matcher("13.435").matches());
        assertTrue(PATTERN_MATERIALIZED_PATH.matcher("17.354.2146").matches());
    }

    @Test
    @DisplayName("유효하지 않은 구체화 경로 문자열로 거짓 반환")
    void testPatternMaterializedPath_givenInvalidPathString_willReturnFalse() {
        // given & when & then
        // 잘못된 구분자 사용
        assertFalse(PATTERN_MATERIALIZED_PATH.matcher("2\\4").matches());
        assertFalse(PATTERN_MATERIALIZED_PATH.matcher("2/4").matches());
        assertFalse(PATTERN_MATERIALIZED_PATH.matcher("2-4").matches());
        // 구분자로 시작
        assertFalse(PATTERN_MATERIALIZED_PATH.matcher(".5").matches());
        // 구분자로 종료
        assertFalse(PATTERN_MATERIALIZED_PATH.matcher("5.3.").matches());
    }

    @Test
    @DisplayName("유효한 닉네임 문자열로 참 반환")
    void testPatternNickname_givenValidNicknameString_willReturnTrue() {
        // given & when & then
        assertTrue(PATTERN_NICKNAME.matcher("개화").matches());
        assertTrue(PATTERN_NICKNAME.matcher("Monster").matches());
        assertTrue(PATTERN_NICKNAME.matcher("낙화Armond1214").matches());
    }

    @Test
    @DisplayName("유효하지 않은 닉네임 문자열로 거짓 반환")
    void testPatternNickname_givenInvalidNicknameString_willReturnFalse() {
        // given & when & then
        // 1자리 이하
        assertFalse(PATTERN_NICKNAME.matcher("K").matches());
        // 17자리 이상
        assertFalse(PATTERN_NICKNAME.matcher("a".repeat(17)).matches());
        // 한글, 영문, 숫자 이외에 다른 문자 사용
        assertFalse(PATTERN_NICKNAME.matcher("비!").matches());
        assertFalse(PATTERN_NICKNAME.matcher("눈?").matches());
    }

    @Test
    @DisplayName("유효한 비밀번호 문자열로 참 반환")
    void testPatternPassword_givenValidPasswordString_willReturnTrue() {
        // given & when & then
        assertTrue(PATTERN_PASSWORD.matcher("abcd1234!").matches());
        assertTrue(PATTERN_PASSWORD.matcher(".ranking35").matches());
        assertTrue(PATTERN_PASSWORD.matcher("/trUcker133333/").matches());
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호 문자열로 거짓 반환")
    void testPatternPassword_givenInvalidPasswordString_willReturnFalse() {
        // given & when & then
        // 7자리 이하
        assertFalse(PATTERN_PASSWORD.matcher("ABCD13;").matches());
        // 65자 이상
        assertFalse(PATTERN_PASSWORD.matcher("a".repeat(63) + "1!").matches());
        // 영문 없음
        assertFalse(PATTERN_PASSWORD.matcher("12345678[]").matches());
        // 숫자 없음
        assertFalse(PATTERN_PASSWORD.matcher("abcdefgh=").matches());
        // 특수문자 없음
        assertFalse(PATTERN_PASSWORD.matcher("abcd1234").matches());
        // 허용되지 않은 특수문자만을 포함
        assertFalse(PATTERN_PASSWORD.matcher("a93안녕하세요").matches());
    }

    @Test
    @DisplayName("유효한 버전 문자열로 참 반환")
    void testPatternVersion_givenValidVersionString_willReturnTrue() {
        // given & when & then
        assertTrue(PATTERN_VERSION.matcher("v1.0.0").matches());
        assertTrue(PATTERN_VERSION.matcher("v1.10.131").matches());
        assertTrue(PATTERN_VERSION.matcher("v148.21.14").matches());
    }

    @Test
    @DisplayName("유효하지 않은 버전 문자열로 거짓 반환")
    void testPatternVersion_givenInvalidVersionString_willReturnFalse() {
        // given & when & then
        // 맨 앞 v 생략
        assertFalse(PATTERN_VERSION.matcher("1.3.5").matches());
        // 맨 뒤에 점 표기
        assertFalse(PATTERN_VERSION.matcher("v1.9.12.").matches());
        // Major, Minor, Patch 중 하나 이상 생략
        assertFalse(PATTERN_VERSION.matcher("v.1.6").matches());
        assertFalse(PATTERN_VERSION.matcher("v6..6").matches());
        assertFalse(PATTERN_VERSION.matcher("v9.3.").matches());
        // Major, Minor, Patch에 숫자가 아닌 문자 포함
        assertFalse(PATTERN_VERSION.matcher("v1.a.4").matches());
        assertFalse(PATTERN_VERSION.matcher("v1.5.4!").matches());
    }
}
