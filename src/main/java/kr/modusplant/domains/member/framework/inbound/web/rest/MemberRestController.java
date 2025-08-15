package kr.modusplant.domains.member.framework.inbound.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.member.adapter.presenter.MemberPresenter;
import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.adapter.service.MemberService;
import kr.modusplant.domains.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 API", description = "회원의 상태와 생명 주기를 관리하는 API 입니다.")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Validated
public class MemberRestController {
    private final MemberService memberService;
    private final MemberPresenter memberPresenter;

    @Operation(summary = "회원 등록 API", description = "닉네임을 통해 회원을 등록합니다.")
    @PostMapping
    public ResponseEntity<MemberResponse> registerMember(MemberRegisterRequest request) {
        Member member = memberService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(memberPresenter.presentMemberResponse(member));
    }
}
