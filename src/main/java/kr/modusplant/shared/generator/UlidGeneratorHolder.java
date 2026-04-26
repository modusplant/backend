package kr.modusplant.shared.generator;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class UlidGeneratorHolder {
    @Getter
    private final RandomUlidGenerator UlidGenerator;

    @Getter  // 주의: 최소한으로만 사용할 것.
    private static RandomUlidGenerator staticUlidGenerator;

    public UlidGeneratorHolder(RandomUlidGenerator UlidGenerator) {
        this.UlidGenerator = UlidGenerator;
        UlidGeneratorHolder.staticUlidGenerator = UlidGenerator;
    }}
