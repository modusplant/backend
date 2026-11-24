package kr.modusplant.shared.framework.ffmpeg.service;

import kr.modusplant.shared.framework.ffmpeg.model.VideoProbeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoProbeService {

    private final FFprobe ffprobe;

    public VideoProbeResult probe(Path videoPath) throws IOException {
        FFmpegProbeResult result = ffprobe.probe(videoPath.toString());

        FFmpegStream videoStream = result.getStreams().stream()
                .filter(s -> s.codec_type == FFmpegStream.CodecType.VIDEO)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("비디오 스트림이 존재하지 않습니다."));

        return new VideoProbeResult(
                result.getFormat().duration,
                videoStream.width,
                videoStream.height,
                videoStream.codec_name,
                result.getFormat().size
        );
    }
}
