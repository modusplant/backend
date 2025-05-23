package kr.modusplant.domains.tip.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MediaContentService {
    private final ObjectMapper objectMapper;

    private static final String BASE_DIRECTORY = "/uploads/";
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
        int i=1;
        for (MultipartFile part:parts) {
            contentArray.add(convertSinglePartToJson(part,i++));
        }
        return contentArray;
    }

    private ObjectNode convertSinglePartToJson(MultipartFile part, int i) throws IOException {
        String contentType = part.getContentType();
        String filename = part.getOriginalFilename();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("filename",filename);
        node.put("order",i);
        String type = contentType.contains("/") ? contentType.split("/")[0] : contentType;
        type = type.equals("application") ? "file" : type;
        if (type.equals("text")) {
            String text = new String(part.getBytes(), StandardCharsets.UTF_8);
            node.put("type","text");
            node.put("data",text);
        } else if (CONTENT_TYPE_DIR_MAP.containsKey(type)) {
            String path = saveFileToLocal(part,CONTENT_TYPE_DIR_MAP.get(type),filename);
            node.put("type",type);
            node.put("src",path);
        } else {
            throw new IllegalArgumentException("Unsupported file type: "+contentType);
        }
        return node;
    }

    /* Wasabi 적용 전 파일 임의 저장 */
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
        ArrayNode contentArray = (ArrayNode) content;
        for(JsonNode node:contentArray) {
            if(node.isObject()) {
                ObjectNode objectNode = (ObjectNode) node;
                if (objectNode.has("src")) {
                    String src = objectNode.get("src").asText();
                    byte[] fileBytes = readMediaFileAsBytes(src);
                    String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
                    objectNode.put("data",base64Encoded);
                    objectNode.remove("src");
                }
            }
        }
        return contentArray;
    }

    private byte[] readMediaFileAsBytes(String src) throws IOException {
        Path path = Path.of(src);
        return Files.readAllBytes(path);
    }
}
