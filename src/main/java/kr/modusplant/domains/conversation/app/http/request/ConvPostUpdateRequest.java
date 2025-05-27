package kr.modusplant.domains.conversation.app.http.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ConvPostUpdateRequest(
        String ulid,
        Integer groupOrder,
        String title,
        List<MultipartFile> content,
        List<FileOrder> orderInfo
){ }
