package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.vo.MemberId;

import java.io.IOException;

public interface MemberProfileRepository {
    MemberProfile getByIdWithoutImageBytes(MemberId memberId) throws IOException;

    MemberProfile update(MemberProfile memberProfile, boolean needsUntracking, boolean needsImageBytes) throws IOException;

    boolean isIdExist(MemberId memberId);
}
