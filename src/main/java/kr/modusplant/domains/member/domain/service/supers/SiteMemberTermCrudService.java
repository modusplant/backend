package kr.modusplant.domains.member.domain.service.supers;

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;

import java.util.List;
import java.util.Optional;

public interface SiteMemberTermCrudService extends UuidCrudService<SiteMemberTerm> {
    List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion);

    Optional<SiteMemberTerm> getByMember(SiteMember member);
}
