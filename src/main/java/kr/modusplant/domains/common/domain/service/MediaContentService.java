package kr.modusplant.domains.common.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.DATA;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;
import static kr.modusplant.global.vo.FileSystem.FILENAME;
import static kr.modusplant.global.vo.FileSystem.SRC;

@Service
@RequiredArgsConstructor
public class MediaContentService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /* Wasabi 연동 전 임시 구현 : 로컬 저장 경로 지정 */
    private static final String BASE_DIRECTORY = "uploads/";
    private static final String IMAGE_DIR = "images/";
    private static final String VIDEO_DIR = "video/";
    private static final String AUDIO_DIR = "audio/";
    private static final String FILE_DIR = "files/";
    private static final Map<String,String> CONTENT_TYPE_DIR_MAP = Map.of(
            "image", IMAGE_DIR,
            "video", VIDEO_DIR,
            "audio", AUDIO_DIR,
            "file", FILE_DIR
    );

    public JsonNode saveFilesAndGenerateContentJson(List<MultipartFile> parts) throws IOException {
        ArrayNode contentArray = objectMapper.createArrayNode();
        int order = 1;
        for (MultipartFile part:parts) {
            contentArray.add(convertSinglePartToJson(part,order++));
        }
        return contentArray;
    }

    private ObjectNode convertSinglePartToJson(MultipartFile part, int order) throws IOException {
        String contentType = part.getContentType();
        String filename = part.getOriginalFilename();

        ObjectNode node = objectMapper.createObjectNode();
        node.put(FILENAME, filename);
        node.put(ORDER, order);

        String type = extractType(contentType);
        if (type.equals("text")) {
            String text = new String(part.getBytes(), StandardCharsets.UTF_8);
            node.put("type", "text");
            node.put(DATA, text);
        } else if (CONTENT_TYPE_DIR_MAP.containsKey(type)) {
            String path = saveFileToLocal(part, CONTENT_TYPE_DIR_MAP.get(type), filename);
            node.put("type", type);
            node.put(SRC, path);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
        return node;
    }

    private String extractType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "unknown";
        }
        String type = contentType.contains("/") ? contentType.split("/")[0] : contentType;
        return type.equals("application") ? "file" : type;
    }

    /* Wasabi 연동 전 임시 구현 : 파일 로컬 저장 */
    private String saveFileToLocal(MultipartFile part,  String directory, String originalFilename) throws IOException {
        String uploadDirectory = BASE_DIRECTORY + directory;
        File fileDirectory = new File(uploadDirectory);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0)
            ext = originalFilename.substring(i);

        String filename = originalFilename.substring(0, i > 0 ? i : originalFilename.length())
                + "_" + UUID.randomUUID() + ext;

        File savedFile = new File(fileDirectory,filename);
        part.transferTo(savedFile);
        return uploadDirectory + filename;
    }

    public JsonNode convertFileSrcToBinaryData(JsonNode content) throws IOException {
        ArrayNode newArray = objectMapper.createArrayNode();
        for(JsonNode node:content) {
            ObjectNode objectNode = node.deepCopy();
            if(node.isObject() && node.has(SRC)) {
                String src = objectNode.get(SRC).asText();
                byte[] fileBytes = readMediaFileAsBytes(src);
                String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
                objectNode.put(DATA, base64Encoded);
                objectNode.remove(SRC);
            }
            newArray.add(objectNode);
        }
        return newArray;
    }

    /* Wasabi 연동 전 임시 구현 : 파일을 로컬에서 읽음 */
    private byte[] readMediaFileAsBytes(String src) throws IOException {
        Path path = Path.of(src);
        return Files.readAllBytes(path);
    }

    public void deleteFiles(JsonNode content) throws IOException {
        for (JsonNode node : content) {
            if (node.isObject()) {
                if (node.has(SRC)) {
                    String src = node.get(SRC).asText();
                    deleteMediaFile(src);
                }
            }
        }
    }

    /* Wasabi 연동 전 임시 구현 : 로컬 파일 삭제 */
    private void deleteMediaFile(String src) throws IOException {
        Path path = Path.of(src);
        if (Files.exists(path)) {
            Files.delete(path);
        } else {
            throw new FileNotFoundException("Cannot find the file with the path: " + src);
        }
    }
}
