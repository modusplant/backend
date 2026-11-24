package kr.modusplant.shared.framework.ffmpeg.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties({ThumbnailProperties.class, TranscodeProperties.class})
public class FFmpegConfig {

    @Value("${ffmpeg.ffmpeg-path:/usr/bin/ffmpeg}")
    private String ffmpegPath;

    @Value("${ffmpeg.ffprobe-path:/usr/bin/ffprobe}")
    private String ffprobePath;

    @Bean
    public FFmpeg ffmpeg() throws IOException {
        return new FFmpeg(ffmpegPath);
    }

    @Bean
    public FFprobe ffprobe() throws IOException {
        return new FFprobe(ffprobePath);
    }
}
