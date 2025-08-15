package kr.modusplant.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EncryptUtilsTest {

    @Test
    @DisplayName("null 입력 시 NullPointerException 발생")
    void encrypt_withSha256NullInput_throwsNullPointerException() {
        // given
        String input = null;

        // when & then
        assertThatThrownBy(() -> EncryptUtils.encryptWithSha256(input))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("빈 문자열 입력 시 올바른 해시값 반환")
    void encrypt_withSha256EmptyString_returnsCorrectHash() {
        // given
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        // when
        String result = EncryptUtils.encryptWithSha256(input);

        // then
        assertThat(result).isEqualTo(expectedHash);
        assertThat(result).hasSize(64);
    }

    @Test
    @DisplayName("같은 입력에 대해서는 항상 같은 해시값 반환")
    void encrypt_withSha256SameInput_returnsSameHash() {
        // given
        String input = "test input";

        // when
        String hash1 = EncryptUtils.encryptWithSha256(input);
        String hash2 = EncryptUtils.encryptWithSha256(input);

        // then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("다른 입력에 대해서는 다른 해시값 반환")
    void encrypt_withSha256DifferentInputs_returnsDifferentHashes() {
        // given
        String input1 = "test1";
        String input2 = "test2";

        // when
        String hash1 = EncryptUtils.encryptWithSha256(input1);
        String hash2 = EncryptUtils.encryptWithSha256(input2);

        // then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("sha256 정상 생성")
    void encrypt_withSha256String_returnsCorrectHash() {
        // given
        String input = "test input";
        String expectedHash = "9dfe6f15d1ab73af898739394fd22fd72a03db01834582f24bb2e1c66c7aaeae";

        // when
        String result = EncryptUtils.encryptWithSha256(input);

        // then
        assertThat(result).isEqualTo(expectedHash);
    }
}
