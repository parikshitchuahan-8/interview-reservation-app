package com.example.system.controller;

import com.example.system.entity.User;
import com.example.system.repository.UserRepository;
import com.example.system.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserRegistrationController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/register")
    public String registerForm(){
        return "register";
    }



    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email, Model model){
        if(userRepo.findByUsername(username)!=null){
            model.addAttribute("error","Username already exists.");
            return "register";
        }
        if (userRepo.findByEmail(email) != null) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
        }
        User user=new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(PasswordUtil.hash(password));
        userRepo.save(user);
        return "redirect:/login";
    }
}
