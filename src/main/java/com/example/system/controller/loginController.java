package com.example.system.controller;

import com.example.system.entity.User;
import com.example.system.repository.UserRepository;
import com.example.system.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class loginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(HttpSession session) {
        return session.getAttribute("username") == null ? "home" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            if (user.getPassword().equals(password)) {
                user.setPassword(PasswordUtil.hash(password));
                userRepository.save(user);
            }
            session.setAttribute("username", user.getUsername());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
