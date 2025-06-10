package kr.modusplant.modules.auth.normal.logout.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class NormalLogoutController {

    @PostMapping("/logout")
    public void processLogout() {

    }
}
