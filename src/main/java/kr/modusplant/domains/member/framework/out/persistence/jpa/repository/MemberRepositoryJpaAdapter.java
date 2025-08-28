package kr.modusplant.domains.member.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.member.adapter.out.repository.MemberRepository;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.out.persistence.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.framework.out.persistence.jpa.repository.supers.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryJpaAdapter implements MemberRepository {
    private final MemberJpaMapperImpl memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member updateNickname(Member member) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(member)));
    }

    @Override
    public Member save(Member member) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(member)));
    }
}
