package kr.modusplant.domains.tip.app.http.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record TipPostUpdateRequest(
        String ulid,
        Integer groupOrder,
        String title,
        List<MultipartFile> content,
        List<FileOrder> orderInfo
){ }
