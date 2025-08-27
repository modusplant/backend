package kr.modusplant.domains.member.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.member.adapter.out.repository.MemberRepository;
import kr.modusplant.domains.member.domain.model.Member;
import kr.modusplant.domains.member.framework.out.persistence.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.framework.out.persistence.jpa.repository.supers.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaMapperImpl memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(member)));
    }
}
