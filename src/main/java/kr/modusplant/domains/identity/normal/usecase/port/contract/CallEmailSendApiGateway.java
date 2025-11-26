package kr.modusplant.domains.identity.normal.usecase.port.contract;

import com.mailjet.client.MailjetResponse;
import kr.modusplant.domains.identity.normal.usecase.enums.EmailType;

public interface CallEmailSendApiGateway {
    MailjetResponse execute(String email, String varValue, EmailType type);
}
