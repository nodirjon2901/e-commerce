package uz.nt.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.UserRequest;
import uz.nt.ecommerce.domain.entity.UserEntity;
import uz.nt.ecommerce.service.UserService;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/reg")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute UserRequest userCreateDto, Model model
    ) {
        BaseResponse<UserEntity> save = userService.save(userCreateDto);
        model.addAttribute("users", userService.getAll());
        model.addAttribute("message", save.getMessage());
        return "menu";
    }

}
