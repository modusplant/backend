package kr.modusplant.domains.post.framework.out.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kr.modusplant.domains.post.framework.out.processor.enums.FileType;
import kr.modusplant.domains.post.framework.out.processor.exception.UnsupportedFileException;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.infrastructure.persistence.generator.UlidIdGenerator;
import lombok.RequiredArgsConstructor;
import org.hibernate.generator.EventType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MultipartDataProcessor implements MultipartDataProcessorPort {
    private final S3FileService s3FileService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final UlidIdGenerator generator = new UlidIdGenerator();
    public static final String DATA = "data";
    public static final String FILENAME = "filename";
    public static final String ORDER = "order";
    public static final String SRC = "src";
    public static final String TYPE = "type";

    public JsonNode saveFilesAndGenerateContentJson(List<MultipartFile> parts) throws IOException {
        String fileUlid = generator.generate(null, null, null, EventType.INSERT);
        ArrayNode contentArray = objectMapper.createArrayNode();
        int order = 1;
        for (MultipartFile part:parts) {
            contentArray.add(convertSinglePartToJson(fileUlid, part, order++));
        }
        return contentArray;
    }

    private ObjectNode convertSinglePartToJson(String fileUlid, MultipartFile part, int order) throws IOException {
        String contentType = part.getContentType();
        String filename = part.getOriginalFilename();

        ObjectNode node = objectMapper.createObjectNode();
        node.put(FILENAME, filename);
        node.put(ORDER, order);

        FileType fileType = FileType.from(contentType);
        if (fileType == FileType.TEXT) {
            String text = new String(part.getBytes(), StandardCharsets.UTF_8);
            node.put(TYPE, fileType.getValue());
            node.put(DATA, text);
        } else if (fileType.getUploadable()) {
            String fileKey = generateFileKey(fileUlid, fileType, filename, order);
            s3FileService.uploadFile(part, fileKey);
            node.put(TYPE, fileType.getValue());
            node.put(SRC, fileKey);
        } else {
            throw new UnsupportedFileException();
        }
        return node;
    }

    private String generateFileKey(String fileUlid, FileType fileType, String originalFilename, int order) {
        // post/{RAMDOM UlID}/{fileType}/{fileName}
        String directory = "post/" + fileUlid + "/" + fileType.getValue() + "/";

        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0)
            ext = originalFilename.substring(i);
        String filename = originalFilename.substring(0, i > 0 ? i : originalFilename.length())
                + "_" + order + ext;

        return directory + filename;
    }

    public ArrayNode convertFileSrcToBinaryData(JsonNode content) throws IOException {
        ArrayNode newArray = objectMapper.createArrayNode();
        for (JsonNode node : content) {
            ObjectNode objectNode = node.deepCopy();
            if (node.has(SRC)) {
                String src = objectNode.get(SRC).asText();
                byte[] fileBytes = s3FileService.downloadFile(src);
                String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
                objectNode.put(DATA, base64Encoded);
                objectNode.remove(SRC);
            }
            newArray.add(objectNode);
        }
        return newArray;
    }

    public ArrayNode convertToPreviewData(JsonNode content) throws IOException {
        ArrayNode newArray = objectMapper.createArrayNode();

        JsonNode firstTextNode = null;
        JsonNode firstImageNode = null;

        for (JsonNode node : content) {
            if (node.has(TYPE)) {
                String type = node.get(TYPE).asText();
                if (type.equals(FileType.TEXT.getValue()) && firstTextNode == null) {
                    firstTextNode = node;
                } else if (type.equals(FileType.IMAGE.getValue()) && firstImageNode == null) {
                    firstImageNode = node;
                }

                if (firstTextNode != null && firstImageNode != null) {
                    break;
                }
            }
        }

        if (firstTextNode != null) {
            ObjectNode textObjectNode = firstTextNode.deepCopy();
            newArray.add(textObjectNode);
        }

        if (firstImageNode != null) {
            ObjectNode imageObjectNode = firstImageNode.deepCopy();
            if (imageObjectNode.has(SRC)) {
                String src = imageObjectNode.get(SRC).asText();
                byte[] fileBytes = s3FileService.downloadFile(src);
                String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
                imageObjectNode.put(DATA, base64Encoded);
                imageObjectNode.remove(SRC);
            }
            newArray.add(imageObjectNode);
        }

        return newArray;
    }

    public void deleteFiles(JsonNode content) {
        for (JsonNode node : content) {
            if (node.has(SRC)) {
                String src = node.get(SRC).asText();
                s3FileService.deleteFiles(src);
            }
        }
    }
}
