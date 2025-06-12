package kr.modusplant.modules.auth.email.app.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import kr.modusplant.modules.auth.email.enums.EmailType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static kr.modusplant.global.vo.CamelCaseWord.VERIFY_CODE;

@Slf4j
@Service
public class MailService {
    @Value("${mail-api.key}")
    private String API_KEY;

    @Value("${mail-api.secret-key}")
    private String API_SECRET_KEY;

    public MailjetResponse callSendEmail(String email, String verifyCode, EmailType type) {
        int templateId = 0;
        String subject = null;

        switch (type) {
            case SIGNUP_VERIFY_EMAIL:   // 회원가입 인증메일 발송
                templateId = 6747014;
                subject = "[ModusPlant] 회원가입 본인인증 메일입니다.";
                break;
            case RESET_PASSWORD_EMAIL:
                templateId = 7011045; // 비밀번호 재설정 인증메일 발송
                subject = "[ModusPlant] 비밀번호 재설정 메일입니다.";
                break;
            default:break;
        }

        // ClientOptions 생성
        ClientOptions clientOptions = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(API_SECRET_KEY)
                .build();

        // MailjetClient 생성
        MailjetClient client = new MailjetClient(clientOptions);

        // 요청 생성
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(
                        Emailv31.MESSAGES,
                        new JSONArray()
                                .put(
                                        new JSONObject()
                                                .put(
                                                        Emailv31.Message.FROM, new JSONObject()
                                                                .put("Email", "modusplant.master@gmail.com")
                                                                .put("Name", "ModusPlant")
                                                )
                                                .put(
                                                        Emailv31.Message.TO, new JSONArray()
                                                                .put(
                                                                        new JSONObject()
                                                                                .put("Email", email)
                                                                )
                                                )
                                                .put("TemplateID", templateId)
                                                .put("TemplateLanguage", true)
                                                .put(Emailv31.Message.SUBJECT, subject)
                                                .put(Emailv31.Message.VARS, new JSONObject()
                                                        .put(VERIFY_CODE, verifyCode)
                                                )
                                )
                );

        // 요청 전송 및 응답 받기
        MailjetResponse response;
        try {
            response = client.post(request);
        } catch (MailjetException e) {
            throw new RuntimeException(e.getMessage());
        }
        log.info("Mail Send Address : {}, Send Status : {} ", email, response.getStatus());
        return response;
    }
}
