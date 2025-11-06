package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;

import java.io.IOException;

public interface MemberProfileRepository {
    MemberProfile getById(MemberId memberId) throws IOException;

    MemberProfile save(MemberProfile memberProfile) throws IOException;

    void deleteImage(MemberProfileImage image);

    boolean isIdExist(MemberId memberId);
}
