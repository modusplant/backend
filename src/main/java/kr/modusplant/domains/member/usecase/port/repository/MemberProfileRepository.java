package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.vo.MemberId;

import java.io.IOException;
import java.util.Optional;

public interface MemberProfileRepository {
    Optional<MemberProfile> getById(MemberId memberId) throws IOException;

    MemberProfile add(MemberProfile memberProfile) throws IOException;

    MemberProfile addOrUpdate(MemberProfile memberProfile) throws IOException;

    boolean isIdExist(MemberId memberId);
}
