package kr.modusplant.domains.communication.conversation.common.util.entity;

import kr.modusplant.domains.communication.conversation.common.util.domain.ConvPostTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity.ConvPostEntityBuilder;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface ConvPostEntityTestUtils extends SiteMemberEntityTestUtils, ConvCategoryEntityTestUtils, ConvPostTestUtils {
    default ConvPostEntityBuilder createConvPostEntityBuilder() {
        return ConvPostEntity.builder()
                .title(testConvPost.getTitle())
                .content(testConvPost.getContent());
    }
}
