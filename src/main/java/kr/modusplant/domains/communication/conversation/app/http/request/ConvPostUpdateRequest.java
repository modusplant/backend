package kr.modusplant.domains.communication.conversation.app.http.request;

import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record ConvPostUpdateRequest(
        String ulid,
        UUID categoryUuid,
        String title,
        List<MultipartFile> content,
        List<FileOrder> orderInfo
){ }
