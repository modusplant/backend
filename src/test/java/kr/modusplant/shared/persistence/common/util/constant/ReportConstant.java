package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportConstant {
    public static final String TEST_REPORT_ULID = "01KJ862TB91KP6BK301RZM17K7";
    public static final String TEST_REPORT_TITLE = "보고서 제목";
    public static final String TEST_REPORT_CONTENT = "보고서 컨텐츠";
    public static final byte[] TEST_REPORT_IMAGE_BYTES = "Image for report".getBytes();
    public static final String TEST_REPORT_IMAGE_FILE_NAME = "image.png";
    public static final MultipartFile TEST_REPORT_IMAGE = new MockMultipartFile("image", TEST_REPORT_IMAGE_FILE_NAME, "image/png", TEST_REPORT_IMAGE_BYTES);
    public static final String TEST_REPORT_IMAGE_PATH = "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME;
}