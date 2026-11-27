package kr.modusplant.shared.framework.ffmpeg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ffmpeg.transcode")
public record TranscodeProperties(
        String preset,
        int constantRateFactor,
        long maxBitrate,
        long bufferSize
) {
}
