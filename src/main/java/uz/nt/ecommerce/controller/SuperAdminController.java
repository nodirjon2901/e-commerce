package uz.nt.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.UserRequest;
import uz.nt.ecommerce.domain.entity.UserEntity;
import uz.nt.ecommerce.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/superAdmin")
@EnableMethodSecurity
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final UserService userService;

    @PostMapping(value = "/delete")
    public ModelAndView deleteUser(@RequestParam(name = "id") Long userId) {
        ModelAndView modelAndView = new ModelAndView("menu");
        userService.delete(userId);
        modelAndView.addObject("users", userService.getAll());
        modelAndView.addObject("message", "User successfully deleted!");
        return modelAndView;
    }

    @PostMapping(value = "/update")
    public ModelAndView updateUser(@ModelAttribute UserRequest userCreateDto,
                                   @RequestParam(name = "id") Long id
    ) {
        ModelAndView modelAndView = new ModelAndView("menu");
        userService.update(userCreateDto, id);
        modelAndView.addObject("users", userService.getAll());
        modelAndView.addObject("message", "User successfully updated!");
        return modelAndView;
    }

    @GetMapping(value = "/info")
    public ModelAndView userInfo(@RequestParam(name = "userId") Long userId) {
        ModelAndView modelAndView = new ModelAndView("menu");
        BaseResponse<UserEntity> byId = userService.getById(userId);
        UserEntity userEntity = byId.getData();
        modelAndView.addObject("currentUser", userEntity);
        modelAndView.addObject("users", userService.getAll());
        return modelAndView;
    }
}