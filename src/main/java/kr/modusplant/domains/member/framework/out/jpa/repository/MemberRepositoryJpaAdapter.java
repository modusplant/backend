package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryJpaAdapter implements MemberRepository {
    private final MemberJpaMapperImpl memberJpaMapper;
    private final SiteMemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> getByNickname(MemberNickname nickname) {
        Optional<SiteMemberEntity> emptyOrMemberEntity = memberJpaRepository.findByNickname(nickname.getValue());
        return emptyOrMemberEntity.isEmpty() ? Optional.empty() : Optional.of(memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow()));
    }

    @Override
    public Member save(Member member) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(member)));
    }

    @Override
    public boolean isNicknameExist(MemberNickname nickname) {
        return memberJpaRepository.existsByNickname(nickname.getValue());
    }
}
