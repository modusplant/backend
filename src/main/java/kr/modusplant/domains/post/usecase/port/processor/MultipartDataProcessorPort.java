package kr.modusplant.domains.post.usecase.port.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.usecase.record.ContentProcessRecord;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MultipartDataProcessorPort {
    ContentProcessRecord saveFilesAndGenerateContentJson(List<MultipartFile> parts, List<FileOrder> orderInfo, String thumbnailFilename) throws IOException;

    ArrayNode convertFileSrcToFullFileSrc(JsonNode content) throws IOException;

    ArrayNode convertToPreview(JsonNode content, String thumbnailPath) throws IOException;

    void deleteFiles(JsonNode content);

    String extractOriginalFilenameFromFileKey(String fileKey);
}
