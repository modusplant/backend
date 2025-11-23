package kr.modusplant.domains.identity.normal.framework.out.mailjet;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import kr.modusplant.domains.identity.normal.usecase.enums.EmailType;
import kr.modusplant.domains.identity.normal.usecase.port.contract.CallEmailSendApiGateway;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CallEmailSendApiGatewayImpl implements CallEmailSendApiGateway {
    @Value("${mail-api.key}")
    private String API_KEY;

    @Value("${mail-api.secret-key}")
    private String API_SECRET_KEY;

    @Override
    public MailjetResponse execute(String email, String varValue, EmailType type) {
        int templateId;
        String subject;

        // ClientOptions 생성
        ClientOptions clientOptions = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(API_SECRET_KEY)
                .build();

        // MailjetClient 생성
        MailjetClient client = new MailjetClient(clientOptions);

        // 요청 생성
        MailjetRequest request = new MailjetRequest(Emailv31.resource);

        switch (type) {
            case AUTHENTICATION_CODE_EMAIL: // 회원가입 인증 코드 메일 발송
                templateId = 6747014;
                subject = "[ModusPlant] 인증 코드를 포함하는 메일입니다.";

                // 요청 생성
                request.property(
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
                                                                .put("verifyCode", varValue)
                                                        )
                                        )
                        );
                break;
            case RESET_PASSWORD_EMAIL:
                templateId = 7011045; // 비밀번호 재설정 메일 발송
                subject = "[ModusPlant] 비밀번호 재설정 전용 메일입니다.";

                // 요청 생성
                request.property(
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
                                                                .put("resetUrl", String.format("https://app.modusplant.kr/api/auth/reset-password-request/verify/email?uuid={%s}", varValue))
                                                        )
                                        )
                        );
                break;
            default:break;
        }

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
