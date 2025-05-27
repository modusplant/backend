package kr.modusplant.domains.qna.app.http.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record QnaPostUpdateRequest(
        String ulid,
        Integer groupOrder,
        String title,
        List<MultipartFile> content,
        List<FileOrder> orderInfo
){ }
