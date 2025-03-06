package tech.majaliwa.matatu.MatatuWebApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/showCustomLoginForm")
    public String showCustomLoginForm() {
        return "custom-login";
    }
}
