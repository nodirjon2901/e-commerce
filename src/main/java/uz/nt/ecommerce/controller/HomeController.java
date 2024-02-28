package uz.nt.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uz.nt.ecommerce.service.CategoryService;
import uz.nt.ecommerce.service.OrderService;
import uz.nt.ecommerce.service.ProductService;
import uz.nt.ecommerce.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final UserService userService;

    private final CategoryService categoryService;

    private final ProductService productService;

    private final OrderService orderService;

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/category")
    public ModelAndView category(){
        ModelAndView modelAndView=new ModelAndView("category");
        modelAndView.addObject("categories", categoryService.getAll());
        return modelAndView;
    }
    @GetMapping("/product")
    public ModelAndView product(){
        ModelAndView modelAndView=new ModelAndView("product");
        modelAndView.addObject("categories", categoryService.getChildCategories());
        modelAndView.addObject("products", productService.getAll());
        return modelAndView;
    }
    @GetMapping("/orders")
    public ModelAndView orders(){
        ModelAndView modelAndView=new ModelAndView("order");
        modelAndView.addObject("orders", orderService.getAll());
        return modelAndView;
    }

    @PostMapping("/go_menu")
    public ModelAndView goMenu(){
        ModelAndView modelAndView=new ModelAndView("menu");
        modelAndView.addObject("products", productService.getAll());
        modelAndView.addObject("categories", categoryService.getAll());
        modelAndView.addObject("users", userService.getAll());
        return modelAndView;
    }
    @GetMapping("/employee")
    public ModelAndView Menu(){
        ModelAndView modelAndView=new ModelAndView("menu");
        modelAndView.addObject("users", userService.getAll());
        return modelAndView;
    }

}
