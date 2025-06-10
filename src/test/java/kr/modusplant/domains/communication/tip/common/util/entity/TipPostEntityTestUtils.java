package kr.modusplant.domains.communication.tip.common.util.entity;

import kr.modusplant.domains.communication.tip.common.util.domain.TipPostTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity.TipPostEntityBuilder;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface TipPostEntityTestUtils extends SiteMemberEntityTestUtils, TipCategoryEntityTestUtils, TipPostTestUtils {
    default TipPostEntityBuilder createTipPostEntityBuilder() {
        return TipPostEntity.builder()
                .title(tipPost.getTitle())
                .content(tipPost.getContent());
    }
}
