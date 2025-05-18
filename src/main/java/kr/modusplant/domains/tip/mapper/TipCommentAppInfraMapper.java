package kr.modusplant.domains.tip.mapper;

import kr.modusplant.domains.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.domain.model.TipComment;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import org.mapstruct.Mapper;

@Mapper
public interface TipCommentAppInfraMapper {

    TipCommentResponse toTipCommentResponse(TipCommentEntity tipCommentEntity);

    TipCommentEntity toTipCommentEntity(TipCommentInsertRequest insertRequest);
}
