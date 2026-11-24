package kr.modusplant.shared.framework.ffmpeg.service;

import kr.modusplant.shared.framework.ffmpeg.config.ThumbnailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoThumbnailService {

    private static final String THUMBNAIL_FILTER_FORMAT =
            "thumbnail,scale=%d:%d:force_original_aspect_ratio=decrease,pad=%d:%d:(ow-iw)/2:(oh-ih)/2";

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final ThumbnailProperties thumbnailProperties;

    public void extractThumbnail(Path videoPath, Path thumbnailPath) {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setStartOffset(thumbnailProperties.startOffsetMs(), TimeUnit.MILLISECONDS)
                .setInput(videoPath.toString())
                .overrideOutputFiles(true)
                .addOutput(thumbnailPath.toString())
                .setVideoFilter(buildThumbnailFilter())
                .setFrames(1)
                .setVideoQuality(thumbnailProperties.quality())
                .done();

        new FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run();
    }

    private String buildThumbnailFilter() {
        int w = thumbnailProperties.width();
        int h = thumbnailProperties.height();
        return String.format(THUMBNAIL_FILTER_FORMAT, w, h, w, h);
    }
}
