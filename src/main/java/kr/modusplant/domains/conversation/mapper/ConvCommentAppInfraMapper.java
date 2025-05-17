package kr.modusplant.domains.conversation.mapper;

import kr.modusplant.domains.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.conversation.persistence.entity.ConvCommentEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ConvCommentAppInfraMapper {

    ConvCommentResponse toConvCommentResponse(ConvCommentEntity convComment);

    ConvCommentEntity toConvCommentEntity(ConvCommentInsertRequest insertRequest);
}
