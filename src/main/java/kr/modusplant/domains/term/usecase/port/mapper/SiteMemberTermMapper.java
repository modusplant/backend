package kr.modusplant.domains.term.usecase.port.mapper;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.usecase.response.SiteMemberTermResponse;

import java.util.List;

public interface SiteMemberTermMapper {
    SiteMemberTermResponse toSiteMemberTermResponse(SiteMemberTerm term);
    List<SiteMemberTermResponse> toSiteMemberTermListResponse(List<SiteMemberTerm> termList);
}
