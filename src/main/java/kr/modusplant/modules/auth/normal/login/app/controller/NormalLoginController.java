package kr.modusplant.modules.auth.normal.login.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class NormalLoginController {

    @PostMapping("/login")
    public DataResponse<Void> processLogin(HttpServletRequest request) {
        Authentication auth = (Authentication) request.getAttribute("authentication");
        System.out.println("The auth from the filters: {}" + auth);


        // TODO: JWT 토큰 생성 후 반환
        return DataResponse.ok();
    }
}
