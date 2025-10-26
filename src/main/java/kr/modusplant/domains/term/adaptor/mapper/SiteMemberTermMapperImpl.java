package kr.modusplant.domains.term.adaptor.mapper;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.usecase.port.mapper.SiteMemberTermMapper;
import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SiteMemberTermMapperImpl implements SiteMemberTermMapper {
    @Override
    public SiteMemberTermResponse toSiteMemberTermResponse(SiteMemberTerm siteMemberTerm) {
        return new SiteMemberTermResponse(
                siteMemberTerm.getSiteMemberTermId().getValue(),
                siteMemberTerm.getAgreedTermsOfUseVersion(),
                siteMemberTerm.getAgreedPrivacyPolicyVersion(),
                siteMemberTerm.getAgreedAdInfoReceivingVersion()
        );
    }

    @Override
    public List<SiteMemberTermResponse> toSiteMemberTermListResponse(List<SiteMemberTerm> siteMemberTermList) {
        return siteMemberTermList.stream()
                .map(this::toSiteMemberTermResponse)
                .toList();
    }
}
