package kr.modusplant.domains.normalidentity.normal.usecase.port.contract;

import com.mailjet.client.MailjetResponse;
import kr.modusplant.domains.normalidentity.normal.usecase.enums.EmailType;

public interface CallEmailSendApiGateway {
    MailjetResponse execute(String email, String verifyCode, EmailType type);
}
