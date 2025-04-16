package kr.modusplant.api.crud.member.domain.service.supers;

import kr.modusplant.api.crud.member.domain.model.SiteMemberTerm;

import java.util.List;

public interface SiteMemberTermService extends SiteMemberCrudService<SiteMemberTerm> {
    List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);
}
