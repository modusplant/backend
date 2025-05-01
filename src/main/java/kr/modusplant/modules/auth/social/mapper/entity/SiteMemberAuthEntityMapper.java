package kr.modusplant.modules.auth.social.mapper.entity;

import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface SiteMemberAuthEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberAuthEntity toSiteMemberAuthEntity(UUID memberUuid, AuthProvider provider, String id, String email, @Context SiteMemberRepository memberRepository) {
        SiteMemberEntity memberEntity = memberRepository.findByUuid(memberUuid).orElseThrow();
        return SiteMemberAuthEntity.builder()
                .activeMember(memberEntity)
                .originalMember(memberEntity)
                .email(email)
                .provider(provider)
                .providerId(id)
                .build();
    }

    @Mapping(source = ACTIVE_MEMBER, target = ACTIVE_MEMBER_UUID, qualifiedByName = "toActiveMemberUuid")
    @Mapping(source = ORIGINAL_MEMBER, target = ORIGINAL_MEMBER_UUID, qualifiedByName = "toOriginalMemberUuid")
    @Mapping(target = MEMBER_AUTH, ignore = true)
    SiteMemberAuth toSiteMemberAuth(SiteMemberAuthEntity memberAuthEntity);

    @Named("toActiveMemberUuid")
    default UUID toActiveMemberUuid(SiteMemberEntity activeMemberEntity) {
        return activeMemberEntity.getUuid();
    }

    @Named("toOriginalMemberUuid")
    default UUID toOriginalMemberUuid(SiteMemberEntity originalMemberEntity) {
        return originalMemberEntity.getUuid();
    }
}
