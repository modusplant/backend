package kr.modusplant.modules.auth.social.mapper.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryOnlyContext
class SiteMemberEntityMapperTest {
    private final SiteMemberEntityMapper memberMapper =  new SiteMemberEntityMapperImpl();

    @Test
    @DisplayName("nickname으로 SiteMemberEntity 생성")

    void createEntityFromNickname() {
        // given
        String nickname = "testUser";

        // when
        SiteMemberEntity entity = memberMapper.toSiteMemberEntity(nickname);

        // then
        assertThat(entity.getNickname()).isEqualTo(nickname);
        assertThat(entity.getLoggedInAt()).isNotNull();
    }

}