package kr.modusplant.domains.comment.support.utils.framework;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentMemberEntity;
import kr.modusplant.domains.member.test.utils.domain.MemberBirthDateTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberIdTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberNicknameTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberStatusTestUtils;

import java.time.LocalDateTime;

public interface CommentMemberEntityTestUtils extends
        MemberIdTestUtils, MemberNicknameTestUtils,
        MemberBirthDateTestUtils, MemberStatusTestUtils {
    default CommentMemberEntity createCommentMemberEntity() {
        return CommentMemberEntity.builder()
                .nickname(testMemberNickname.getValue())
                .birthDate(testMemberBirthDate.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }

    default CommentMemberEntity createCommentMemberEntityWithUuid() {
        return CommentMemberEntity.builder()
                .uuid(testMemberId.getValue())
                .nickname(testMemberNickname.getValue())
                .birthDate(testMemberBirthDate.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }
}
