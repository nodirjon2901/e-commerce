package uz.nt.ecommerce.common;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(value = DataNotFoundException.class)
    public String handleDataNotFoundException(DataNotFoundException e,
                                              Model model) {
        model.addAttribute("message", e.getMessage());
        return "auth";
    }

}
