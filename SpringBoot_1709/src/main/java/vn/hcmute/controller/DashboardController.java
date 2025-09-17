package vn.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/generate-password")
    @ResponseBody
    public String generatePassword() {
        String password = "1";
        String hashedPassword = passwordEncoder.encode(password);
        return "Original password: " + password + "<br>BCrypt hash: " + hashedPassword;
    }
}
