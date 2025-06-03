package kr.modusplant.domains.communication.tip.app.http.request;

import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record TipPostInsertRequest(
        UUID categoryUuid,
        String title,
        List<MultipartFile> content,
        List<FileOrder> orderInfo
){ }
