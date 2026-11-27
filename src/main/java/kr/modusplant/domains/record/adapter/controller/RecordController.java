package kr.modusplant.domains.record.adapter.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.record.domain.enums.RecordPublishedStatus;
import kr.modusplant.domains.record.domain.exception.InvalidVideoDurationException;
import kr.modusplant.domains.record.domain.exception.ThumbnailGenerationException;
import kr.modusplant.domains.record.domain.exception.UnsupportedVideoCodecException;
import kr.modusplant.domains.record.domain.exception.VideoFileNotFoundException;
import kr.modusplant.domains.record.domain.exception.VideoResolutionTooLargeException;
import kr.modusplant.domains.record.domain.exception.RecordAccessDeniedException;
import kr.modusplant.domains.record.framework.outbound.jpa.entity.PlantRecordEntity;
import kr.modusplant.domains.record.framework.outbound.jpa.repository.PlantRecordJpaRepository;
import kr.modusplant.domains.record.usecase.request.RecordCreateRequest;
import kr.modusplant.domains.record.usecase.request.RecordUploadUrlRequest;
import kr.modusplant.domains.record.usecase.response.RecordCreateResponse;
import kr.modusplant.domains.record.usecase.response.RecordUploadUrlResponse;
import kr.modusplant.infrastructure.file.service.PendingFileService;
import kr.modusplant.infrastructure.jwt.claim.UploadTokenClaims;
import kr.modusplant.infrastructure.jwt.enums.TokenScope;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.shared.enums.VideoContentType;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.ffmpeg.model.VideoProbeResult;
import kr.modusplant.shared.framework.ffmpeg.service.VideoProbeService;
import kr.modusplant.shared.framework.ffmpeg.service.VideoThumbnailService;
import kr.modusplant.shared.framework.ffmpeg.service.VideoTranscodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordController {

    private static final long UPLOAD_TOKEN_DURATION_MS = 10 * 60 * 1000L;
    private static final String VIDEO_KEY_PREFIX = "record";
    private static final String H264_CODEC = "h264";
    private static final String HEVC_CODEC = "hevc";
    private static final String FINAL_VIDEO_EXTENSION = "mp4";
    private static final String FINAL_VIDEO_CONTENT_TYPE = "video/mp4";
    private static final String THUMBNAIL_EXTENSION = "jpg";
    private static final String THUMBNAIL_CONTENT_TYPE = "image/jpeg";
    private static final double MIN_DURATION_SECONDS = 2.0;
    private static final double MAX_DURATION_SECONDS = 3.0;
    private static final int MAX_RESOLUTION_PIXELS = 1920;

    private final AmazonS3Service amazonS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final PendingFileService pendingFileService;
    private final VideoProbeService videoProbeService;
    private final VideoThumbnailService videoThumbnailService;
    private final VideoTranscodeService videoTranscodeService;
    private final PlantRecordJpaRepository plantRecordJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final TransactionTemplate transactionTemplate;

    public RecordUploadUrlResponse createUploadUrl(UUID memberId, RecordUploadUrlRequest request) {
        String normalizedContentType = request.contentType().toLowerCase(Locale.ROOT);
        VideoContentType videoContentType = VideoContentType.fromMimeType(normalizedContentType);

        String recordId = UlidCreator.getMonotonicUlid().toString();
        String extension = videoContentType.getExtension().getValue();
        String videoKey = String.format("%s/%s/%s/video.%s", VIDEO_KEY_PREFIX, memberId, recordId, extension);

        String uploadUrl = amazonS3Service.generateVideoPutPresignedUrl(videoKey, normalizedContentType);

        pendingFileService.trackPendingFiles(List.of(videoKey));

        Map<String, String> privateClaims = Map.of(
                "scope", TokenScope.UPLOAD_RECORD.getValue(),
                "memberId", memberId.toString(),
                "videoKey", videoKey,
                "originalFilename", request.originalFilename()
        );
        String uploadToken = jwtTokenProvider.generateToken(recordId, privateClaims, UPLOAD_TOKEN_DURATION_MS);

        Instant expiresAt = Instant.now().plusMillis(UPLOAD_TOKEN_DURATION_MS);

        return new RecordUploadUrlResponse(
                recordId,
                uploadUrl,
                uploadToken,
                Map.of("Content-Type", normalizedContentType),
                expiresAt
        );
    }

    public RecordCreateResponse createRecord(UUID authenticatedMemberId, RecordCreateRequest request) {
        UploadTokenClaims claims = jwtTokenProvider.getUploadTokenClaims(request.uploadToken());
        if (!claims.memberId().equals(authenticatedMemberId)) {
            throw new RecordAccessDeniedException();
        }
        if (plantRecordJpaRepository.existsByUlid(claims.recordId())) {
            return new RecordCreateResponse(claims.recordId());
        }
        if (!amazonS3Service.checkIfFileExists(claims.videoKey())) {
            throw new VideoFileNotFoundException();
        }

        Path workDir = createWorkingDirectory(claims.recordId());
        try {
            Path downloadedVideo = workDir.resolve("original." + extensionOf(claims.videoKey()));
            byte[] videoBytes = amazonS3Service.downloadFile(claims.videoKey());
            Files.write(downloadedVideo, videoBytes);

            VideoProbeResult probe = videoProbeService.probe(downloadedVideo);
            validateVideoPolicy(probe);

            Path finalVideo = workDir.resolve("final." + FINAL_VIDEO_EXTENSION);
            Path thumbnail = workDir.resolve("thumbnail." + THUMBNAIL_EXTENSION);
            String finalVideoKey = toFinalVideoKey(claims.videoKey());
            String thumbnailKey = toThumbnailKey(finalVideoKey);

            joinAll(
                    CompletableFuture.runAsync(() -> buildFinalVideo(downloadedVideo, finalVideo, probe)),
                    CompletableFuture.runAsync(() -> extractThumbnail(downloadedVideo, thumbnail))
            );

            pendingFileService.trackPendingFiles(
                    finalVideoKey.equals(claims.videoKey())
                            ? List.of(thumbnailKey)                     // 최종 키 = 비디오키인 경우 썸네일만 추가(원본 .mp4)
                            : List.of(finalVideoKey, thumbnailKey)      // 최종 키 != 비디오키인 경우 변환된 비디오, 썸네일 추가(원본 .mp4 이외)
            );
            byte[] finalVideoBytes = Files.readAllBytes(finalVideo);
            byte[] thumbnailBytes = Files.readAllBytes(thumbnail);
            joinAll(
                    CompletableFuture.runAsync(() -> amazonS3Service.uploadFile(finalVideoBytes, finalVideoKey, FINAL_VIDEO_CONTENT_TYPE)),
                    CompletableFuture.runAsync(() -> amazonS3Service.uploadFile(thumbnailBytes, thumbnailKey, THUMBNAIL_CONTENT_TYPE))
            );

            transactionTemplate.executeWithoutResult(status -> persistRecord(
                    claims, request.description(), finalVideoKey, thumbnailKey, finalVideoBytes.length));

            if (!finalVideoKey.equals(claims.videoKey())) {
                amazonS3Service.deleteFile(claims.videoKey());
            }

            return new RecordCreateResponse(claims.recordId());
        } catch (IOException e) {
            throw new ThumbnailGenerationException();
        } finally {
            deleteWorkingDirectory(workDir);
        }
    }

    private void joinAll(CompletableFuture<?>... futures) {
        try {
            CompletableFuture.allOf(futures).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            }
            throw e;
        }
    }

    private Path createWorkingDirectory(String recordId) {
        try {
            return Files.createTempDirectory("record-" + recordId + "-");
        } catch (IOException e) {
            throw new ThumbnailGenerationException();
        }
    }

    private void deleteWorkingDirectory(Path workDir) {
        if (workDir == null || !Files.exists(workDir)) return;
        try (Stream<Path> paths = Files.walk(workDir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    log.warn("임시 파일 정리 실패: {}", path, e);
                }
            });
        } catch (IOException e) {
            log.warn("임시 디렉토리 정리 실패: {}", workDir, e);
        }
    }

    private String extensionOf(String key) {
        return StringUtils.substringAfterLast(key, ".");
    }

    private String toFinalVideoKey(String originalKey) {
        return StringUtils.substringBeforeLast(originalKey, ".") + "." + FINAL_VIDEO_EXTENSION;
    }

    private String toThumbnailKey(String finalVideoKey) {
        return StringUtils.substringBeforeLast(finalVideoKey, "/") + "/thumbnail." + THUMBNAIL_EXTENSION;
    }

    private void validateVideoPolicy(VideoProbeResult probe) {
        if (probe.durationSeconds() < MIN_DURATION_SECONDS || probe.durationSeconds() > MAX_DURATION_SECONDS) {
            throw new InvalidVideoDurationException();
        }
        if (probe.width() > MAX_RESOLUTION_PIXELS || probe.height() > MAX_RESOLUTION_PIXELS) {
            throw new VideoResolutionTooLargeException();
        }
        if (!H264_CODEC.equals(probe.videoCodec()) && !HEVC_CODEC.equals(probe.videoCodec())) {
            throw new UnsupportedVideoCodecException();
        }
    }

    private void buildFinalVideo(Path input, Path output, VideoProbeResult probe) {
        if (H264_CODEC.equals(probe.videoCodec())) {
            videoTranscodeService.remuxToMp4(input, output);
        } else {
            videoTranscodeService.transcodeToH264Mp4(input, output);
        }
    }

    private void extractThumbnail(Path input, Path output) {
        try {
            videoThumbnailService.extractThumbnail(input, output);
            if (!Files.exists(output) || Files.size(output) == 0) {
                throw new ThumbnailGenerationException();
            }
        } catch (IOException e) {
            throw new ThumbnailGenerationException();
        }
    }

    private void persistRecord(UploadTokenClaims claims, String description,
                               String finalVideoKey, String thumbnailKey,
                               long finalVideoSizeBytes) {
        MemberEntity authMember = memberJpaRepository.getReferenceById(claims.memberId());
        PlantRecordEntity entity = PlantRecordEntity.builder()
                .ulid(claims.recordId())
                .authMember(authMember)
                .description(StringUtils.isBlank(description) ? null : description)
                .originalFilename(claims.originalFilename())
                .videoKey(finalVideoKey)
                .thumbnailKey(thumbnailKey)
                .contentType(FINAL_VIDEO_CONTENT_TYPE)
                .sizeBytes(finalVideoSizeBytes)
                .isPublished(RecordPublishedStatus.PUBLISHED)
                .build();
        plantRecordJpaRepository.save(entity);

        List<String> keysToUntrack = finalVideoKey.equals(claims.videoKey())
                ? List.of(claims.videoKey(), thumbnailKey)
                : List.of(claims.videoKey(), finalVideoKey, thumbnailKey);
        pendingFileService.untrackPendingFiles(keysToUntrack);
    }
}
