package kr.modusplant.shared.framework.ffmpeg.model;

public record VideoProbeResult(
        double durationSeconds,
        int width,
        int height,
        String videoCodec,
        long sizeBytes
) {
}
