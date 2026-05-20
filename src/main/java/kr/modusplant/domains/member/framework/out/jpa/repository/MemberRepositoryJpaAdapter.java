package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryJpaAdapter implements MemberRepository {
    private final MemberJpaMapperImpl memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member getById(MemberId memberId) {
        Optional<MemberEntity> emptyOrMemberEntity = memberJpaRepository.findByUuid(memberId.getValue());
        if (emptyOrMemberEntity.isPresent()) {
            return memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow());
        } else {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER, "member");
        }
    }

    @Override
    public Optional<Member> getByNickname(Nickname nickname) {
        Optional<MemberEntity> emptyOrMemberEntity = memberJpaRepository.findByNickname(nickname.getValue());
        return emptyOrMemberEntity.isEmpty() ?
                Optional.empty() : Optional.of(memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow()));
    }

    @Override
    public Member add(Nickname nickname) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(nickname)));
    }

    @Override
    public Member add(MemberId memberId, Nickname nickname) {
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
