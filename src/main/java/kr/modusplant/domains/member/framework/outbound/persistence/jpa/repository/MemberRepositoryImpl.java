package kr.modusplant.domains.member.framework.outbound.persistence.jpa.repository;

import kr.modusplant.domains.member.adapter.repository.MemberRepository;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.repository.supers.MemberJpaRepository;
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
