package kr.modusplant.domains.post.framework.out.processor.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FileTypeTest {

    @ParameterizedTest
    @CsvSource({
            "text/plain, TEXT",
            "image/jpeg, IMAGE",
            "image/jpg, IMAGE",
            "image/png, IMAGE",
            "video/mp4, VIDEO",
            "video/mov, VIDEO",
            "audio/mpeg, AUDIO",
            "application/pdf, FILE"
    })
    @DisplayName("contentType으로 FileType을 반환")
    void testFrom_givenContentType_willReturnFileType(String contentType, FileType expected) {
        assertThat(FileType.from(contentType)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("null이나 빈 문자열로 구성된 ContentType은 UNKNOWN 반환")
    void testFrom_givenNullOrEmptyContentType_willReturnUnknown(String contentType) {
        assertThat(FileType.from(contentType)).isEqualTo(FileType.UNKNOWN);
        assertThat(FileType.from(null)).isEqualTo(FileType.UNKNOWN);
    }

    @ParameterizedTest
    @CsvSource({
            "txt, TEXT",
            "TXT, TEXT",
            "jpeg, IMAGE",
            "jpg, IMAGE",
            "png, IMAGE",
            "heif, IMAGE",
            "gif, IMAGE",
            "mp4, VIDEO",
            "mov, VIDEO",
            "wmv, VIDEO",
            "avi, VIDEO"
    })
    @DisplayName("확장자로 FileType을 반환한다 (대소문자 무관)")
    void testFromExtension_givenExtension_willReturnFileType(String extension, FileType expected) {
        assertThat(FileType.fromExtension(extension)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "TEXT, txt, true",
            "TEXT, jpg, false",
            "IMAGE, jpeg, true",
            "IMAGE, mp4, false",
            "AUDIO, mp3, false"  // allowedExtensions가 빈 경우
    })
    @DisplayName("확장자 허용 여부를 검증한다")
    void testIsAllowedExtension_givenExtension_willReturnBoolean(FileType fileType, String extension, boolean expected) {
        assertThat(fileType.isAllowedExtension(extension)).isEqualTo(expected);
    }


}