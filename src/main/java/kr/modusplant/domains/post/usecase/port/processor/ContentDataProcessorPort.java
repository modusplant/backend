package kr.modusplant.domains.post.usecase.port.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.usecase.record.ContentProcessRecord;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostFileUploadRequest;
import kr.modusplant.domains.post.usecase.response.PostFileUploadUrlResponse;

import java.io.IOException;
import java.util.List;

public interface ContentDataProcessorPort {
    List<PostFileUploadUrlResponse> getMultipleUploadUrls(List<PostFileUploadRequest> files);

    ContentProcessRecord generateContentJson(String contentText, List<FileOrder> contentFiles, String thumbnailFilename) throws IOException;

    ArrayNode convertFileSrcToFullFileSrc(JsonNode content) throws IOException;

    ArrayNode convertToPreview(JsonNode content, String thumbnailPath) throws IOException;

    void deleteFiles(JsonNode content);

    String extractOriginalFilenameFromFileKey(String fileKey);

    void deleteFilesWithFileKeys(List<String> fileKeys);

    List<String> extractFileKeysFromContent(JsonNode content);

    void rollbackFiles(List<FileOrder> contentFiles);

    String getS3SrcUrl(String fileKey);
}
