package kr.modusplant.domains.post.usecase.port.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MultipartDataProcessorPort {
    JsonNode saveFilesAndGenerateContentJson(List<MultipartFile> parts) throws IOException;

    ArrayNode convertFileSrcToFullFileSrc(JsonNode content) throws IOException;

    ArrayNode convertToPreview(JsonNode content) throws IOException;

    void deleteFiles(JsonNode content);
}
