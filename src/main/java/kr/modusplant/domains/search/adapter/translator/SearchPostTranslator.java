package kr.modusplant.domains.search.adapter.translator;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.exception.ContentProcessingException;
import kr.modusplant.domains.post.usecase.port.processor.ContentDataProcessorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SearchPostTranslator {
    private final ContentDataProcessorPort contentDataProcessorPort;

    public JsonNode getJsonNodeContentPreview(JsonNode content, String thumbnailPath) {
        if (content == null) return null;
        JsonNode contentPreview;
        try {
            contentPreview = contentDataProcessorPort.convertToPreview(content, thumbnailPath);
        } catch (IOException e) {
            throw new ContentProcessingException();
        }
        return contentPreview;
    }
}
