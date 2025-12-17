package kr.modusplant.domains.post.framework.out.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kr.modusplant.domains.post.framework.out.processor.enums.FileType;
import kr.modusplant.domains.post.framework.out.processor.exception.FileLimitExceededException;
import kr.modusplant.domains.post.framework.out.processor.exception.InvalidFileInputException;
import kr.modusplant.domains.post.framework.out.processor.exception.UnsupportedFileException;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import lombok.RequiredArgsConstructor;
import org.hibernate.generator.EventType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static kr.modusplant.domains.post.framework.out.processor.constant.FileConstraints.*;


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

    public JsonNode saveFilesAndGenerateContentJson(List<MultipartFile> parts, List<FileOrder> orderInfo) throws IOException {
        // 멀티파트 파일 및 순서 정보 검증
        validatePartsAndOrderInfo(parts,orderInfo);
        validateFileConstraints(parts);
        List<MultipartFile> orderedParts = reorderParts(parts, orderInfo);

        // 멀티파트 파일 저장 및 json 변환
        String fileUlid = generator.generate(null, null, null, EventType.INSERT);
        ArrayNode contentArray = objectMapper.createArrayNode();
        for (int i=0; i<orderedParts.size(); i++) {
            MultipartFile part = orderedParts.get(i);
            int order = orderInfo.get(i).order();
            contentArray.add(convertSinglePartToJson(fileUlid,part,order));
        }
        return contentArray;
    }

    public ArrayNode convertFileSrcToFullFileSrc(JsonNode content) {
        ArrayNode newArray = objectMapper.createArrayNode();
        for (JsonNode node : content) {
            ObjectNode objectNode = node.deepCopy();
            if (node.has(SRC)) {
                String fileKey = objectNode.get(SRC).asText();
                objectNode.remove(SRC);
                String src = s3FileService.generateS3SrcUrl(fileKey);
                objectNode.put(SRC,src);
            }
            newArray.add(objectNode);
        }
        return newArray;
    }

    public ArrayNode convertToPreview(JsonNode content) {
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
                String fileKey = imageObjectNode.get(SRC).asText();
                imageObjectNode.remove(SRC);
                String src = s3FileService.generateS3SrcUrl(fileKey);
                imageObjectNode.put(SRC,src);
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

    private void validatePartsAndOrderInfo(List<MultipartFile> parts, List<FileOrder> orderInfo) {
        // parts와 orderInfo 크기 검증
        if (parts == null || orderInfo == null || parts.size() != orderInfo.size()) {
            throw new InvalidFileInputException();
        }

        Map<String, MultipartFile> partMap = parts.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, part -> part));

        // 파일명 매칭 검증
        Set<String> orderFilenames = orderInfo.stream()
                .map(FileOrder::filename)
                .collect(Collectors.toSet());
        if(!partMap.keySet().equals(orderFilenames)) {
            throw new InvalidFileInputException();
        }

        // orderInfo의 order 정보 검증 (order가 순차적으로 증가하는지)
        List<FileOrder>  sortedorderInfo = orderInfo.stream()
                .sorted(Comparator.comparing(FileOrder::order))
                .collect(Collectors.toList());
        int zeroCount = 0;
        int expectedOrder = 1;
        for (FileOrder info : sortedorderInfo) {
            int order = info.order();
            if (order == 0) {
                zeroCount++;
                if (zeroCount > 1) {
                    throw new InvalidFileInputException();
                }
                MultipartFile part = partMap.get(info.filename());
                if (FileType.from(part.getContentType()) != FileType.TEXT) {
                    throw new InvalidFileInputException();
                }
            } else {
                if (order != expectedOrder) {
                    throw new InvalidFileInputException();
                }
                expectedOrder++;
            }
        }
    }

    private void validateFileConstraints(List<MultipartFile> parts) {
        int imageCount = 0;
        int videoCount = 0;
        int fileCount = 0;

        for(MultipartFile part : parts) {
            String contentType = part.getContentType();
            String originalFilename = part.getOriginalFilename();
            long fileSize = part.getSize();
            // text이면 제외
            if (FileType.from(contentType) == FileType.TEXT) {
                continue;
            }

            // 지원하는 파일 타입인지 검증
            String extension = extractExtension(originalFilename);
            FileType actualFileType = FileType.fromExtension(extension);
            if (actualFileType == FileType.UNKNOWN || !actualFileType.hasExtensionRestriction()) {
                throw new UnsupportedFileException();
            }

            // 파일 개수 및 크기 검증
            if (actualFileType == FileType.IMAGE) {
                imageCount++;
                if (imageCount > MAX_IMAGE_FILES || fileSize > MAX_IMAGE_SIZE) {
                    throw new FileLimitExceededException();
                }
            } else if (actualFileType == FileType.VIDEO) {
                videoCount++;
                if (videoCount > MAX_VIDEO_FILES || fileSize > MAX_VIDEO_SIZE) {
                    throw new FileLimitExceededException();
                }
            }
            fileCount++;
        }
        if (fileCount > MAX_TOTAL_FILES) {
            throw new FileLimitExceededException();
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new UnsupportedFileException();
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new UnsupportedFileException();
        }
        return filename.substring(lastDotIndex + 1);
    }

    private List<MultipartFile> reorderParts(List<MultipartFile> parts, List<FileOrder> orderInfo) {
        Map<String, MultipartFile> partMap = parts.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, part -> part));

        return orderInfo.stream()
                .sorted(Comparator.comparing(FileOrder::order))
                .map(info -> partMap.get(info.filename()))
                .collect(Collectors.toList());
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
}
