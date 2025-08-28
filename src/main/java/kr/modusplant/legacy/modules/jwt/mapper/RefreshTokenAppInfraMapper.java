package kr.modusplant.legacy.modules.jwt.mapper;

import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import static kr.modusplant.framework.out.persistence.constant.EntityFieldName.EXPIRED_AT;
import static kr.modusplant.framework.out.persistence.constant.EntityFieldName.ISSUED_AT;
import static kr.modusplant.legacy.domains.member.vo.MemberUuid.MEMBER_UUID;

@Mapper
public interface RefreshTokenAppInfraMapper {
    @BeanMapping(ignoreByDefault = true)
    default RefreshTokenEntity toRefreshTokenEntity(RefreshToken refreshToken, @Context SiteMemberRepository memberRepository) {
        return RefreshTokenEntity.builder()
                .member(memberRepository.findByUuid(refreshToken.getMemberUuid()).orElseThrow())
                .refreshToken(refreshToken.getRefreshToken())
                .issuedAt(convertToLocalDateTime(refreshToken.getIssuedAt()))
                .expiredAt(convertToLocalDateTime(refreshToken.getExpiredAt()))
                .build();
    }

    @Mapping(source = "member", target = MEMBER_UUID, qualifiedByName = "toMemberUuid")
    @Mapping(source = ISSUED_AT, target = ISSUED_AT, qualifiedByName = "toDate")
    @Mapping(source = EXPIRED_AT, target = EXPIRED_AT, qualifiedByName = "toDate")
    RefreshToken toRefreshToken(RefreshTokenEntity refreshTokenEntity);

    @Named("toMemberUuid")
    default UUID toMemberUuid(SiteMemberEntity siteMember) {
        return siteMember.getUuid();
    }

    @Named("toDate")
    default Date convertToDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
