package kr.modusplant.domains.identity.usecase.port.contract;

import com.mailjet.client.MailjetResponse;
import kr.modusplant.domains.identity.usecase.enums.EmailType;

public interface CallEmailSendApiGateway {
    MailjetResponse execute(String email, String verifyCode, EmailType type);
}
