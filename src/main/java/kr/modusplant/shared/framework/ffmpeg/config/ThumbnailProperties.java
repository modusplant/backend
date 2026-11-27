package kr.modusplant.shared.framework.ffmpeg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ffmpeg.thumbnail")
public record ThumbnailProperties(
        int width,
        int height,
        double quality,
        long startOffsetMs
) {
}
