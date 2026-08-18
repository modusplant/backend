package kr.modusplant.domains.post.framework.outbound.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kr.modusplant.domains.post.framework.outbound.processor.enums.PostFileType;
import kr.modusplant.domains.post.framework.outbound.processor.exception.EmptyThumbnailException;
import kr.modusplant.domains.post.framework.outbound.processor.exception.InvalidThumbnailException;
import kr.modusplant.domains.post.framework.outbound.processor.exception.TextOverLengthException;
import kr.modusplant.domains.post.framework.outbound.processor.exception.ThumbnailNotAllowedException;
import kr.modusplant.domains.post.usecase.port.processor.ContentDataProcessorPort;
import kr.modusplant.domains.post.usecase.record.ContentProcessRecord;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostFileUploadRequest;
import kr.modusplant.domains.post.usecase.response.PostFileUploadUrlResponse;
import kr.modusplant.shared.exception.FileLimitExceededException;
import kr.modusplant.shared.exception.InvalidFileInputException;
import kr.modusplant.shared.exception.UnsupportedFileException;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.generator.RandomUlidGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static kr.modusplant.domains.post.framework.outbound.processor.constant.PostFileConstraints.*;

@Service
public class PostContentDataProcessor implements ContentDataProcessorPort {
    private final AmazonS3Service amazonS3Service;
    private final PendingFileService pendingFileService;
    private final ObjectMapper objectMapper;
    private final RandomUlidGenerator generator;
    public static final String DATA = "data";
    public static final String FILENAME = "filename";
    public static final String FILE_KEY = "fileKey";
    public static final String ORDER = "order";
    public static final String SRC = "src";
    public static final String TYPE = "type";
    public static final int MAX_TEXT_LENGTH = 5000;
    private static final Pattern FILE_KEY_PATTERN = Pattern.compile("^post/[0-9A-HJKMNP-TV-Z]{26}/(image|video)/[^/]+\\.[^/]+$");

    public PostContentDataProcessor(AmazonS3Service amazonS3Service,
                                    PendingFileService pendingFileService,
                                    ObjectMapperHolder objectMapperHolder,
                                    UlidGeneratorHolder ulidGeneratorHolder) {
        this.amazonS3Service = amazonS3Service;
        this.pendingFileService = pendingFileService;
        this.objectMapper = objectMapperHolder.getObjectMapper();
        this.generator = ulidGeneratorHolder.getUlidGenerator();
    }

    public List<PostFileUploadUrlResponse> getMultipleUploadUrls(List<PostFileUploadRequest> files) {
        validateFileConstraints(files);
        String fileUlid = generator.generate();
        List<PostFileUploadUrlResponse> result = new ArrayList<>();
        int order = 0;
        for(PostFileUploadRequest file:files) {
            String fileKey = generateFileKey(fileUlid, PostFileType.from(file.contentType()), file.filename(), ++order);
            String uploadUrl = amazonS3Service.generatePutPresignedUrl(fileKey,file.contentType());
            result.add(new PostFileUploadUrlResponse(file.filename(), uploadUrl, fileKey));
        }
        return result;
    }

    public ContentProcessRecord generateContentJson(String contentText, List<FileOrder> contentFiles, String thumbnailFilename) throws IOException {
        // contentText : 글자수 초과하는지 확인
        // contentFiles : order 값 검증(1부터인지 순차적인지), 파일타입 검증, 파일명 중복 검증
        // thunmbnailFilename이 실제로 filename에 존재하는지, image 타입인지
        // fileKey 형식이 우리가 발급한 형식인지, 우리가 발급한 fileKey가 맞는지
        // jsonnode 타입으로 반환

        // 게시글 내용 검증(텍스트+파일)
        validateContentText(contentText);
        validateContentFiles(contentFiles);
        validateThumbnail(contentFiles, thumbnailFilename);

        // contentFiles을 order순으로 정렬
        if(contentFiles != null) {
            contentFiles.sort(Comparator.comparing(FileOrder::order));
        }
        // 멀티파트 파일 저장 및 json 변환
        ArrayNode contentArray = objectMapper.createArrayNode();
        String thumbnailPath = null;
        // text node
        if(contentText != null) {
            ObjectNode textNode = objectMapper.createObjectNode();
            textNode.put(TYPE, PostFileType.TEXT.getValue());
            textNode.put(ORDER, 0);
            textNode.put(DATA, contentText);
            contentArray.add(textNode);
        }
        // file node
        if (contentFiles != null) {
            for (FileOrder file : contentFiles) {
                String fileKey = file.fileKey();
                String fileType = PostFileType.fromExtension(extractExtension(file.filename())).getValue();
                ObjectNode fileNode = objectMapper.createObjectNode();
                fileNode.put(TYPE, fileType);
                fileNode.put(ORDER, file.order());
                fileNode.put(FILENAME, file.filename());
                fileNode.put(SRC, fileKey);
                contentArray.add(fileNode);
                if (file.filename().equals(thumbnailFilename)) {
                    thumbnailPath = fileKey;
                }
            }
        }
        return new ContentProcessRecord(contentArray,thumbnailPath);
    }

    public ArrayNode convertFileSrcToFullFileSrc(JsonNode content) {
        return convertFileSrcToFullFileSrc(content, false);
    }

    public ArrayNode convertFileSrcToFullFileSrcWithFileKey(JsonNode content) {
        return convertFileSrcToFullFileSrc(content, true);
    }

    private ArrayNode convertFileSrcToFullFileSrc(JsonNode content, boolean includeFileKey) {
        ArrayNode newArray = objectMapper.createArrayNode();
        for (JsonNode node : content) {
            ObjectNode objectNode = node.deepCopy();
            if (node.has(SRC)) {
                String fileKey = objectNode.get(SRC).asText();
                objectNode.remove(SRC);
                String src = amazonS3Service.generateS3SrcUrl(fileKey);
                if (includeFileKey) {
                    objectNode.put(FILE_KEY, fileKey);
                }
                objectNode.put(SRC,src);
            }
            newArray.add(objectNode);
        }
        return newArray;
    }

    public ArrayNode convertToPreview(JsonNode content, String thumbnailPath) {
        ArrayNode newArray = objectMapper.createArrayNode();

        for (JsonNode node : content) {
            if (node.has(TYPE) && PostFileType.TEXT.getValue().equals(node.get(TYPE).asText())) {
                ObjectNode textNode = objectMapper.createObjectNode();
                textNode.put(TYPE,node.get(TYPE).asText());
                textNode.put(DATA,node.get(DATA).asText());
                newArray.add(textNode);
                break;
            }
        }

        if (thumbnailPath != null && !thumbnailPath.isBlank()) {
            ObjectNode thumbnailNode = objectMapper.createObjectNode();
            thumbnailNode.put(TYPE, PostFileType.IMAGE.getValue());
            thumbnailNode.put(SRC, amazonS3Service.generateS3SrcUrl(thumbnailPath));
            newArray.add(thumbnailNode);
        }

        return newArray;
    }

    public void deleteFiles(JsonNode content) {
        if (content == null || !content.isArray())
            return ;
        // null 일때도 돌아가는지 확인
        for (JsonNode node : content) {
            if (node.has(SRC)) {
                String src = node.get(SRC).asText();
                amazonS3Service.deleteFile(src);
            }
        }
    }

    public String extractOriginalFilenameFromFileKey(String fileKey) {
        if (fileKey == null)
            return null;

        // fileKey 끝부분 가져오기
        String filenameWithOrder = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        // 확장자 분리
        int dotIndex = filenameWithOrder.lastIndexOf('.');
        String ext = dotIndex > 0 ? filenameWithOrder.substring(dotIndex) : "";
        String name = dotIndex > 0 ? filenameWithOrder.substring(0, dotIndex) : filenameWithOrder;

        // 마지막 _order 제거
        name = name.replaceAll("_(\\d+)$", "");  // 마지막 _숫자만 제거

        return name + ext;
    }

    public void deleteFilesWithFileKeys(List<String> fileKeys) {
        if (fileKeys == null || fileKeys.isEmpty()) return;
        amazonS3Service.deleteFiles(fileKeys);
    }

    public List<String> extractFileKeysFromContent(JsonNode content) {
        if (content == null || !content.isArray()) return List.of();
        List<String> fileKeys = new ArrayList<>();
        for (JsonNode node : content) {
            if (node.has(SRC)) {
                fileKeys.add(node.get(SRC).asText());
            }
        }
        return fileKeys;
    }

    public void rollbackFiles(List<FileOrder> contentFiles) {
        if (contentFiles == null || contentFiles.isEmpty()) return;
        List<String> fileKeys = contentFiles.stream()
                .map(FileOrder::fileKey)
                .toList();
        deleteFilesWithFileKeys(fileKeys);
    }

    public String getS3SrcUrl(String fileKey) {
        return amazonS3Service.generateS3SrcUrl(fileKey);
    }

    /* 파일 검증 - 업로드용 presigned url 생성 */
    private void validateFileConstraints(List<PostFileUploadRequest> files) {
        int imageCount = 0;
        int videoCount = 0;
        int fileCount = 0;
        Set<String> filenames = new HashSet<>();
        for(PostFileUploadRequest file : files) {
            // 파일명 중복 검증
            if (!filenames.add(file.filename())) {
                throw new InvalidFileInputException();
            }
            // 지원하는 파일 타입인지 검증
            PostFileType actualFileType = PostFileType.fromExtension(extractExtension(file.filename()));
            if (actualFileType == PostFileType.UNKNOWN) {
                throw new UnsupportedFileException();
            }
            // 파일 개수 검증 (이미지, 비디오)
            if (actualFileType == PostFileType.IMAGE) {
                if (++imageCount > MAX_IMAGE_FILES) throw new FileLimitExceededException();
            } else if (actualFileType == PostFileType.VIDEO) {
                if (++videoCount > MAX_VIDEO_FILES) throw new FileLimitExceededException();
            }
            fileCount++;
        }
        // 전체 파일 개수 검증
        if (fileCount > MAX_TOTAL_FILES) {
            throw new FileLimitExceededException();
        }
    }

    /* 텍스트 및 파일 정보 검증 - 게시글 추가/수정 */
    private void validateContentText(String contentText) {
        if (contentText != null && contentText.length() > MAX_TEXT_LENGTH) {
            throw new TextOverLengthException();
        }
    }

    private void validateContentFiles(List<FileOrder> contentFiles) {
        if (contentFiles == null || contentFiles.isEmpty()) return;

        // 제출된 fileKey는 예외 없이 전부 presign 발급 후 pending 상태여야 한다
        List<String> fileKeysToVerify = contentFiles.stream().map(FileOrder::fileKey).toList();
        Set<String> trackedFileKeys = new HashSet<>(pendingFileService.findTrackedFileKeys(fileKeysToVerify));

        Set<String> filenames = new HashSet<>();
        List<Integer> orders = new ArrayList<>();
        for (FileOrder contentFile: contentFiles) {
            // 파일명 중복 검증
            if (!filenames.add(contentFile.filename())) {
                throw new InvalidFileInputException();
            }
            // 지원하는 파일 타입인지 검증
            PostFileType actualFileType = PostFileType.fromExtension(extractExtension(contentFile.filename()));
            if (actualFileType == PostFileType.UNKNOWN) {
                throw new UnsupportedFileException();
            }
            // file key 형식 검증
            if (!FILE_KEY_PATTERN.matcher(contentFile.fileKey()).matches()) {
                throw new InvalidFileInputException();
            }
            // 우리가 발급한 fileKey가 맞는지 검증(presign 발급 후 pending 상태여야 함)
            if (!trackedFileKeys.contains(contentFile.fileKey())) {
                throw new InvalidFileInputException();
            }
            // 순서 검증을 위한 추가
            orders.add(contentFile.order());
        }
        // 파일 순서 검증
        Collections.sort(orders);
        for (int i=0; i<orders.size(); i++) {
            if(orders.get(i) != i+1) {
                throw new InvalidFileInputException();
            }
        }
    }

    private void validateThumbnail(List<FileOrder> contentFiles, String thumbnailFilename) {
        boolean hasImage = contentFiles != null
                && contentFiles.stream()
                .anyMatch(f -> PostFileType.fromExtension(extractExtension(f.filename())) == PostFileType.IMAGE);

        if(hasImage) {
            if (thumbnailFilename == null) {
                throw new EmptyThumbnailException();
            }
            // 대표사진 파일명이 파일목록에 존재하는지 검증
            FileOrder thumbnailFile = contentFiles.stream()
                    .filter(f -> f.filename().equals(thumbnailFilename))
                    .findFirst()
                    .orElseThrow(InvalidThumbnailException::new);
            // 대표사진이 이미지인지 검증
            String extension = extractExtension(thumbnailFile.filename());
            if (!PostFileType.IMAGE.isAllowedExtension(extension)) {
                throw new InvalidThumbnailException();
            }
        } else {
            if (thumbnailFilename != null) throw new ThumbnailNotAllowedException();
        }
    }

    /* 공통 메서드 */
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

    private String generateFileKey(String fileUlid, PostFileType fileType, String originalFilename, int order) {
        if (originalFilename == null) {
            throw new UnsupportedFileException();
        }

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
