package kr.modusplant.shared.util;

import kr.modusplant.shared.exception.InvalidValueException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.INCORRECT_ALGORITHM;

public abstract class EncryptUtils {
    private static final String SHA_256 = "SHA-256";

    public static String encryptWithSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidValueException(INCORRECT_ALGORITHM, List.of(SHA_256), e);
        }
    }

}
