package Nathanael.expense_tracker.controller;

import Nathanael.expense_tracker.dto.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

// @Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // This is the updated part:
    @GetMapping("/register")
    public String register(@ModelAttribute("user") User user) {
        return "register";
    }
}