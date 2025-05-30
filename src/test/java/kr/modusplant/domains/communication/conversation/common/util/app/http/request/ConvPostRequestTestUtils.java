package kr.modusplant.domains.communication.conversation.common.util.app.http.request;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostInsertRequest;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public interface ConvPostRequestTestUtils {
    /* MultipartFile, FileOrder Utils */
    MultipartFile textFile0 = new MockMultipartFile("content", "text_0.txt", "text/plain", "This is text for test".getBytes());
    MultipartFile textFile1 = new MockMultipartFile("content", "text_1.txt", "text/plain", "This is text for test".getBytes());
    static FileOrder textFileOrder(int num,int order) {
        return new FileOrder("text_"+num+".txt",order);
    }

    byte[] jpegData = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,  // JPEG 시그니처
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00,
            (byte) 0xFF, (byte) 0xD9  // JPEG 종료
    };
    MultipartFile imageFile = new MockMultipartFile("content", "image_0.jpeg", "image/jpeg", jpegData);
    static FileOrder imageFileOrder(int order) {
        return new FileOrder("image_0.jpeg",order);
    }

    byte[] mp4Data = {
            0x00, 0x00, 0x00, 0x20, 0x66, 0x74, 0x79, 0x70,  // ftyp box
            0x69, 0x73, 0x6F, 0x6D, 0x00, 0x00, 0x02, 0x00,  // isom major brand
            0x69, 0x73, 0x6F, 0x6D, 0x69, 0x73, 0x6F, 0x32,  // compatible brands
            0x61, 0x76, 0x63, 0x31, 0x6D, 0x70, 0x34, 0x31,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05  // 임의 데이터
    };
    MultipartFile videoFile = new MockMultipartFile("content", "video_0.mp4", "video/mp4", mp4Data);
    static FileOrder videoFileOrder(int order) {
        return new FileOrder("video_0.mp4",order);
    }

    byte[] wavData = {
            0x52, 0x49, 0x46, 0x46,  // "RIFF"
            0x24, 0x00, 0x00, 0x00,  // 파일 크기
            0x57, 0x41, 0x56, 0x45,  // "WAVE"
            0x66, 0x6D, 0x74, 0x20,  // "fmt "
            0x10, 0x00, 0x00, 0x00,  // chunk size
            0x01, 0x00, 0x01, 0x00,  // format, channels
            0x44, (byte) 0xAC, 0x00, 0x00,  // sample rate
            0x00, 0x01, 0x02, 0x03   // 임의 데이터
    };
    MultipartFile audioFile = new MockMultipartFile("content", "audio_0.wav", "audio/wav", wavData);
    static FileOrder audioFileOrder(int order) {
        return new FileOrder("audio_0.wav",order);
    }

    byte[] pdfData = {
            0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34,  // "%PDF-1.4"
            0x0A, 0x25, (byte) 0xE2, (byte) 0xE3, (byte) 0xCF, (byte) 0xD3, 0x0A,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05
    };
    MultipartFile applicationFile = new MockMultipartFile("content","file_0.pdf", "application/pdf", pdfData);
    static FileOrder applicationFileOrder(int order) {
        return new FileOrder("file_0.pdf",order);
    }

    /* List<MultipartFile>, List<FileOrder> Utils */
    List<MultipartFile> allMediaFiles = Arrays.asList(textFile0,imageFile,videoFile,audioFile,applicationFile);
    List<FileOrder> allMediaFilesOrder = Arrays.asList(
            textFileOrder(0,1),
            imageFileOrder(2),
            videoFileOrder(3),
            audioFileOrder(4),
            applicationFileOrder(5)
    );

    List<MultipartFile> textImageFiles = Arrays.asList(textFile0,imageFile);
    List<FileOrder> textImageFilesOrder = Arrays.asList(textFileOrder(0,1), imageFileOrder(2));

    List<MultipartFile> imageTextFiles = Arrays.asList(imageFile, textFile0);
    List<FileOrder> imageTextFilesOrder = Arrays.asList(imageFileOrder(1),textFileOrder(0,2));

    List<MultipartFile> basicMediaFiles = Arrays.asList(textFile0,imageFile,videoFile);
    List<FileOrder> basicMediaFilesOrder = Arrays.asList(textFileOrder(0,1), imageFileOrder(2), videoFileOrder(3));

    List<MultipartFile> duplicatedTextFiles = Arrays.asList(textFile0,imageFile,textFile1);
    List<FileOrder> duplicatedTextFilesOrder = Arrays.asList(textFileOrder(0,1),imageFileOrder(2),textFileOrder(1,3));


    /* ConvPostInsertRequest Utils */
    ConvPostInsertRequest requestAllTypes = new ConvPostInsertRequest(
            1,
            "유용한 팁 모음",
            allMediaFiles,
            allMediaFilesOrder
    );

    ConvPostInsertRequest requestBasicTypes = new ConvPostInsertRequest(
            2,
            "유용한 식물 기르기 팁",
            basicMediaFiles,
            basicMediaFilesOrder
    );
}
