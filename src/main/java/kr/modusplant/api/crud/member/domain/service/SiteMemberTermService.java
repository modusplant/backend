package kr.modusplant.api.crud.member.domain.service;

import kr.modusplant.api.crud.member.domain.model.SiteMemberTerm;
import kr.modusplant.api.crud.member.domain.service.supers.SiteMemberCrudService;

import java.util.List;

public interface SiteMemberTermService extends SiteMemberCrudService<SiteMemberTerm> {
    List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}
