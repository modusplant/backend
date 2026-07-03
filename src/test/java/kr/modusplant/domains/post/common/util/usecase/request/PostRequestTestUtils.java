package kr.modusplant.domains.post.common.util.usecase.request;

import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostRequest;

import java.util.Arrays;
import java.util.List;

import static kr.modusplant.domains.post.common.constant.FileConstant.*;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CONTENT_TEXT;
import static kr.modusplant.domains.post.common.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.domains.post.common.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ID_1;

public interface PostRequestTestUtils {
    /* FileOrder */
    static FileOrder imageJpgFileOrder(int order) {
        return new FileOrder(order, TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_FILE_KEY);
    }
    static FileOrder imagePngFileOrder(int order) {
        return new FileOrder(order, TEST_IMAGE_PNG_FILENAME, TEST_IMAGE_PNG_FILE_KEY);
    }
    static FileOrder videoMp4FileOrder(int order) {
        return new FileOrder(order, TEST_VIDEO_MP4_FILENAME, TEST_VIDEO_MP4_FILE_KEY);
    }
    static FileOrder videoAviFileOrder(int order) {
        return new FileOrder(order, TEST_VIDEO_AVI_FILENAME, TEST_VIDEO_AVI_FILE_KEY);
    }

    /* List<FileOrder> Utils */
    List<FileOrder> allMediaFilesOrder = Arrays.asList(imageJpgFileOrder(1), videoMp4FileOrder(2));
    List<FileOrder> allMediaFilesOrder2 = Arrays.asList(imagePngFileOrder(1), videoAviFileOrder(2));
    List<FileOrder> onlyImageFilesOrder = Arrays.asList(imageJpgFileOrder(1));
    List<FileOrder> onlyImageFilesOrder2 = Arrays.asList(imagePngFileOrder(1));
    List<FileOrder> onlyVideoFileOrder = Arrays.asList(videoMp4FileOrder(1));
    List<FileOrder> onlyVideoFileOrder2 = Arrays.asList(videoAviFileOrder(1));
    List<FileOrder> mixedOrder = Arrays.asList(videoMp4FileOrder(2), imageJpgFileOrder(1));


    /* PostInsertRequest Utils */
    PostRequest requestAllTypes = new PostRequest(
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            "유용한 컨텐츠 모음",
            TEST_POST_CONTENT_TEXT,
            allMediaFilesOrder,
            TEST_IMAGE_JPG_FILENAME
    );

    PostRequest requestWithOnlyImageFile = new PostRequest(
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            "유용한 컨텐츠 모음",
            TEST_POST_CONTENT_TEXT,
            onlyImageFilesOrder,
            TEST_IMAGE_JPG_FILENAME
    );

    PostRequest requestWithOnlyVideoFile = new PostRequest(
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            "유용한 컨텐츠 모음",
            TEST_POST_CONTENT_TEXT,
            onlyVideoFileOrder,
            null
    );

    PostRequest requestAllTypesWithoutContentText = new PostRequest(
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            "유용한 컨텐츠 모음",
            null,
            allMediaFilesOrder,
            TEST_IMAGE_JPG_FILENAME
    );

    PostRequest requestAllTypesWithoutContentFiles = new PostRequest(
            TEST_COMM_PRIMARY_CATEGORY_ID,
            TEST_COMM_SECONDARY_CATEGORY_ID_1,
            "유용한 컨텐츠 모음",
            TEST_POST_CONTENT_TEXT,
            null,
            null
    );

    PostRequest requestWithEmptyValueDraft = new PostRequest(
            TEST_COMM_PRIMARY_CATEGORY_ID,
            null,
            "유용한 컨텐츠 모음",
            null,
            null,
            null
    );
}
