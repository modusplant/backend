package kr.modusplant.shared.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ImageContentTypeTest {
    @Test
    @DisplayName("여러 확장자가 공유하는 Content-Type으로 목록 반환")
    void testGetImageExtensionValues_givenSharedContentTypeValue_willReturnList() {
        // given & when
        List<String> imageExtensionValues = ImageContentType.getImageExtensionValues("image/jpeg");

        // then
        assertThat(imageExtensionValues).containsExactly("jpeg", "jpg");
    }

    @Test
    @DisplayName("단일 확장자와 대응하는 Content-Type으로 목록 반환")
    void testGetImageExtensionValues_givenUniqueContentTypeValue_willReturnList() {
        // given & when
        List<String> imageExtensionValues = ImageContentType.getImageExtensionValues("image/png");

        // then
        assertThat(imageExtensionValues).containsExactly("png");
    }

    @Test
    @DisplayName("대응하지 않는 Content-Type으로 빈 목록 반환")
    void testGetImageExtensionValues_givenUnknownContentTypeValue_willReturnList() {
        // given & when
        List<String> imageExtensionValues = ImageContentType.getImageExtensionValues("image/webp");

        // then
        assertThat(imageExtensionValues).isEmpty();
    }
}
