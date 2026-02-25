package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportConstant {
    public static final String REPORT_ULID = "01KJ862TB91KP6BK301RZM17K7";
    public static final String REPORT_TITLE = "보고서 제목";
    public static final String REPORT_CONTENT = "보고서 컨텐츠";
    public static final byte[] REPORT_IMAGE_BYTES = "Image for report".getBytes();
    public static final MultipartFile REPORT_IMAGE = new MockMultipartFile("image", "image.png", "image/png", REPORT_IMAGE_BYTES);
    public static final String REPORT_IMAGE_PATH = "member/" + MEMBER_BASIC_USER_UUID + "/report/image.png";
}