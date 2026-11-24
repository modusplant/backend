package kr.modusplant.shared.framework.ffmpeg.service;

import kr.modusplant.shared.framework.ffmpeg.config.TranscodeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoTranscodeService {

    private static final String H264_CODEC = "libx264";
    private static final String COPY_CODEC = "copy";
    private static final String MP4_FORMAT = "mp4";

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final TranscodeProperties transcodeProperties;

    public void transcodeToH264Mp4(Path input, Path output) {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input.toString())
                .overrideOutputFiles(true)
                .addOutput(output.toString())
                .setFormat(MP4_FORMAT)
                .setVideoCodec(H264_CODEC)
                .setConstantRateFactor(transcodeProperties.constantRateFactor())
                .setVideoBitRate(transcodeProperties.maxBitrate())
                .setPreset(transcodeProperties.preset())
                .setAudioCodec(COPY_CODEC)
                .done();

        new FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run();
    }

    public void remuxToMp4(Path input, Path output) {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input.toString())
                .overrideOutputFiles(true)
                .addOutput(output.toString())
                .setFormat(MP4_FORMAT)
                .setVideoCodec(COPY_CODEC)
                .setAudioCodec(COPY_CODEC)
                .done();

        new FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run();
    }
}
