package kr.modusplant.modules.auth.normal.login.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class NormalLoginController {

    @PostMapping("/login-success")
    public ResponseEntity sendLoginSuccess(HttpServletRequest request) {
        System.out.println("Arrived at the success controller method.");

        Authentication auth = (Authentication) request.getAttribute("authentication");
        System.out.println("The auth from the filters: " + auth);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-fail")
    public ResponseEntity sendLoginFailure(HttpServletRequest request) {
        System.out.println("Arrived at the failure controller method.");
        AuthenticationException authException = (AuthenticationException) request.getAttribute("exception");
        System.out.println("The error message: " + authException.getMessage());

        return ResponseEntity.badRequest().build();
    }
}
