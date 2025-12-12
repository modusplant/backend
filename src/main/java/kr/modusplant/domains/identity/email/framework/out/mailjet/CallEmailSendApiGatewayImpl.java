package kr.modusplant.domains.identity.email.framework.out.mailjet;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import kr.modusplant.domains.identity.email.domain.exception.NotSendableEmailException;
import kr.modusplant.domains.identity.email.usecase.enums.EmailType;
import kr.modusplant.domains.identity.email.usecase.port.gateway.CallEmailSendApiGateway;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

        ZonedDateTime expiredTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(5);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        switch (type) {
            case AUTHENTICATION_CODE_EMAIL: // 회원가입 인증 코드 메일 발송
                templateId = 7559214;
                subject = "[ModusPlant] 인증 코드를 포함하는 메일입니다.";

                // 요청 생성
                request.property(
                                Emailv31.MESSAGES,
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put(
                                                                Emailv31.Message.FROM, new JSONObject()
                                                                        .put("Email", "support@modusplant.kr")
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
                                                                .put("expiredTime", expiredTime.format(formatter))
                                                        )
                                        )
                        );
                break;
            case RESET_PASSWORD_EMAIL:
                templateId = 7559217; // 비밀번호 재설정 메일 발송
                subject = "[ModusPlant] 비밀번호 재설정 전용 메일입니다.";

                // 요청 생성
                request.property(
                                Emailv31.MESSAGES,
                                new JSONArray()
                                        .put(
                                                new JSONObject()
                                                        .put(
                                                                Emailv31.Message.FROM, new JSONObject()
                                                                        .put("Email", "support@modusplant.kr")
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
                                                                .put("emailAddress", email)
                                                                .put("resetUrl", String.format("https://www.modusplant.kr/reset-password?uuid=%s", varValue))
                                                                .put("expiredTime", expiredTime.format(formatter))
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
            if (response == null || !(200 <= response.getStatus() && response.getStatus() < 300)) {
                throw new NotSendableEmailException();
            }
        } catch (MailjetException e) {
            throw new NotSendableEmailException();
        }
        log.info("Mail Send Address : {}, Send Status : {} ", email, response.getStatus());
        return response;
    }
}
