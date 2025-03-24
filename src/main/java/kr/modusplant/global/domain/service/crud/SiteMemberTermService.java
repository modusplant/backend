package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.domain.service.crud.supers.UuidCrudService;

import java.util.List;

public interface SiteMemberTermService extends UuidCrudService<SiteMemberTerm> {
    List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}
