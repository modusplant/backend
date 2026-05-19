package kr.modusplant.domains.member.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReportConstant {
    public static final String TEST_REPORT_ULID = "01KJ862TB91KP6BK301RZM17K7";
    public static final String TEST_REPORT_TITLE = "보고서 제목";
    public static final String TEST_REPORT_CONTENT = "보고서 컨텐츠";
    public static final Integer TEST_REPORT_IMAGE_NUMBER = 3;
    public static final String TEST_REPORT_IMAGE_FILE_NAME_1 = "image_0.png";
    public static final String TEST_REPORT_IMAGE_FILE_NAME_2 = "image_1.png";
    public static final String TEST_REPORT_IMAGE_FILE_NAME_3 = "image_2.png";
    public static final List<String> TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_FILE_NAMES =
            List.of(TEST_REPORT_IMAGE_FILE_NAME_1,
                    TEST_REPORT_IMAGE_FILE_NAME_2,
                    TEST_REPORT_IMAGE_FILE_NAME_3);
    public static final String TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1 =
            "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME_1;
    public static final String TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2 =
            "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME_2;
    public static final String TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3 =
            "member/" + MEMBER_BASIC_USER_UUID + "/report/proposal-or-bug/" + TEST_REPORT_ULID + "/" + TEST_REPORT_IMAGE_FILE_NAME_3;
    public static final List<String> TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS =
            List.of(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3);
    public static final byte[] TEST_REPORT_IMAGE_BYTES_1 = "Image 1 for report".getBytes();
    public static final byte[] TEST_REPORT_IMAGE_BYTES_2 = "Image 2 for report".getBytes();
    public static final byte[] TEST_REPORT_IMAGE_BYTES_3 = "Image 3 for report".getBytes();
    public static final List<MultipartFile> TEST_REPORT_IMAGES =
            List.of(new MockMultipartFile(
                            "image", TEST_REPORT_IMAGE_FILE_NAME_1, "image/png", TEST_REPORT_IMAGE_BYTES_1),
                    new MockMultipartFile(
                            "image", TEST_REPORT_IMAGE_FILE_NAME_2, "image/png", TEST_REPORT_IMAGE_BYTES_2),
                    new MockMultipartFile(
                            "image", TEST_REPORT_IMAGE_FILE_NAME_3, "image/png", TEST_REPORT_IMAGE_BYTES_3));
}