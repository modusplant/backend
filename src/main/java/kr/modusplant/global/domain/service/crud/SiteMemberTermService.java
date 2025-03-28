package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.domain.service.crud.supers.SiteMemberCrudService;

import java.util.List;

public interface SiteMemberTermService extends SiteMemberCrudService<SiteMemberTerm> {
    List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}
