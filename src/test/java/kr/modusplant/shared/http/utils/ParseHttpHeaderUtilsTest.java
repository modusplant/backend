package kr.modusplant.shared.http.utils;

import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberProfileEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;

class ParseHttpHeaderUtilsTest implements SiteMemberEntityTestUtils, SiteMemberProfileEntityTestUtils {
    private final PasswordEncoder passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Test
    @DisplayName("parseIfNoneMatch 테스트")
    void testParseIfNoneMatch() {
        // given
        String firstEntityTag = passwordEncoder.encode(MEMBER_BASIC_USER_UUID + "-null");
        String secondEntityTag = passwordEncoder.encode(MEMBER_BASIC_USER_UUID + "-0");

        // when
        List<String> strings = parseIfNoneMatch(String.format("\"%s\", \"%s\"", firstEntityTag, secondEntityTag));

        // then
        assertThat(strings.getFirst()).isEqualTo(firstEntityTag);
        assertThat(strings.getLast()).isEqualTo(secondEntityTag);
    }

    @Test
    @DisplayName("parseIfModifiedSince 테스트")
    void testParseIfModifiedSince() {
        // given & when
        LocalDateTime now = LocalDateTime.now();
        String ifModifiedSince = ZonedDateTime.of(now, ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME);

        // then
        assertThat(parseIfModifiedSince(ifModifiedSince)).isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}