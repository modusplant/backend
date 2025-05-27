package kr.modusplant.domains.communication.conversation.common.util.entity;


import kr.modusplant.domains.communication.conversation.common.util.domain.ConvPostTestUtils;
import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity.ConvPostEntityBuilder;


public interface ConvPostEntityTestUtils extends SiteMemberEntityTestUtils, PlantGroupEntityTestUtils, ConvPostTestUtils {
    default ConvPostEntityBuilder createConvPostEntityBuilder() {
        return ConvPostEntity.builder()
                .title(convPost.getTitle())
                .content(convPost.getContent());
    }
}
