package kr.modusplant.domains.common.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kr.modusplant.domains.common.enums.FileType;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.global.app.service.S3FileService;
import kr.modusplant.global.persistence.generator.UlidIdGenerator;
import lombok.RequiredArgsConstructor;
import org.hibernate.generator.EventType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static kr.modusplant.global.vo.CamelCaseWord.DATA;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;
import static kr.modusplant.global.vo.FileSystem.FILENAME;
import static kr.modusplant.global.vo.FileSystem.SRC;

@Service
@RequiredArgsConstructor
public class MultipartDataProcessor {
    private final S3FileService s3FileService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final UlidIdGenerator generator = new UlidIdGenerator();

    public JsonNode saveFilesAndGenerateContentJson(PostType postType, List<MultipartFile> parts) throws IOException {
        String fileUlid = generator.generate(null,null,null, EventType.INSERT);
        ArrayNode contentArray = objectMapper.createArrayNode();
        int order=1;
        for (MultipartFile part:parts) {
            contentArray.add(convertSinglePartToJson(postType,fileUlid, part,order++));
        }
        return contentArray;
    }

    private ObjectNode convertSinglePartToJson(PostType postType, String fileUlid, MultipartFile part, int order) throws IOException {
        String contentType = part.getContentType();
        String filename = part.getOriginalFilename();

        ObjectNode node = objectMapper.createObjectNode();
        node.put(FILENAME,filename);
        node.put(ORDER,order);

        FileType fileType = FileType.from(contentType);
        if (fileType == FileType.TEXT) {
            String text = new String(part.getBytes(), StandardCharsets.UTF_8);
            node.put("type",fileType.getValue());
            node.put(DATA,text);
        } else if (fileType.getUploadable()) {
            String fileKey = generateFileKey(postType,fileUlid,fileType,filename,order);
            s3FileService.uploadFile(part,fileKey);
            node.put("type",fileType.getValue());
            node.put(SRC,fileKey);
        } else {
            throw new IllegalArgumentException("Unsupported file type: "+contentType);
        }
        return node;
    }

    private String generateFileKey(PostType postType, String fileUlid, FileType fileType, String originalFilename, int order) {
        // {tip/qna/conv}-post/{RAMDOM UlID}/{fileType}/{fileName}
        String directory = postType.getValue() + "/" + fileUlid + "/" + fileType.getValue() + "/";

        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0)
            ext = originalFilename.substring(i);
        String filename = originalFilename.substring(0, i > 0 ? i : originalFilename.length())
                + "_" + order + ext;

        return directory + filename;
    }

    public JsonNode convertFileSrcToBinaryData(JsonNode content) throws IOException {
        ArrayNode newArray = objectMapper.createArrayNode();
        for(JsonNode node:content) {
            ObjectNode objectNode = node.deepCopy();
            if(node.isObject() && node.has(SRC)) {
                String src = objectNode.get(SRC).asText();
                byte[] fileBytes = s3FileService.downloadFile(src);
                String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
                objectNode.put(DATA,base64Encoded);
                objectNode.remove(SRC);
            }
            newArray.add(objectNode);
        }
        return newArray;
    }

    public void deleteFiles(JsonNode content) throws IOException {
        for (JsonNode node : content) {
            if (node.isObject()) {
                if (node.has(SRC)) {
                    String src = node.get(SRC).asText();
                    s3FileService.deleteFiles(src);
                }
            }
        }
    }
}
