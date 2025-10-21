package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> getByNickname(MemberNickname nickname);

    Member save(MemberNickname memberNickname);

    Member save(MemberId memberId, MemberNickname memberNickname);

    boolean isIdExist(MemberId memberId);

    boolean isNicknameExist(MemberNickname nickname);
}
