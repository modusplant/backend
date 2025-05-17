package kr.modusplant.domains.conversation.common.util.entity;

import kr.modusplant.domains.conversation.persistence.entity.ConvCommentEntity;

import java.util.UUID;

public interface ConvCommentEntityTestUtils extends ConvPostEntityTestUtils {

    default ConvCommentEntity createConvCommentEntity() {
        return ConvCommentEntity.builder()
                .postEntity(createConvPostEntity())
                .materializedPath("/1/6/2")
                .authMemberUuid(UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd"))
                .createMemberUuid(UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"))
                .content("테스트 댓글 내용")
                .build();
    }
}
