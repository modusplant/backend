package kr.modusplant.domains.communication.qna.app.http.request;

import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public record QnaPostUpdateRequest(
        String ulid,
        Integer groupOrder,
        String title,
        List<MultipartFile> content,
        List<FileOrder> orderInfo
){ }
