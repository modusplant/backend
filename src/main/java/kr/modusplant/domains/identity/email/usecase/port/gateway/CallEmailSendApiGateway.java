package kr.modusplant.domains.identity.email.usecase.port.gateway;

import com.mailjet.client.MailjetResponse;
import kr.modusplant.domains.identity.email.usecase.enums.EmailType;

public interface CallEmailSendApiGateway {
    MailjetResponse execute(String email, String varValue, EmailType type);
}
