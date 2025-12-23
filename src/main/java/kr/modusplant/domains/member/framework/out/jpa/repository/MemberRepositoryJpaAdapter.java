package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryJpaAdapter implements MemberRepository {
    private final MemberJpaMapperImpl memberJpaMapper;
    private final SiteMemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> getById(MemberId memberId) {
        Optional<SiteMemberEntity> emptyOrMemberEntity = memberJpaRepository.findByUuid(memberId.getValue());
        return emptyOrMemberEntity.isEmpty() ?
                Optional.empty() : Optional.of(memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow()));
    }

    @Override
    public Optional<Member> getByNickname(Nickname nickname) {
        Optional<SiteMemberEntity> emptyOrMemberEntity = memberJpaRepository.findByNickname(nickname.getValue());
        return emptyOrMemberEntity.isEmpty() ?
                Optional.empty() : Optional.of(memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow()));
    }

    @Override
    public Member save(Nickname nickname) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(nickname)));
    }

    @Override
    public Member save(MemberId memberId, Nickname nickname) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(memberId, nickname)));
    }

    @Override
    public boolean isIdExist(MemberId memberId) {
        return memberJpaRepository.existsByUuid(memberId.getValue());
    }

    @Override
    public boolean isNicknameExist(Nickname nickname) {
        return memberJpaRepository.existsByNickname(nickname.getValue());
    }
}
