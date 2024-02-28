package uz.nt.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.ProductCreateDto;
import uz.nt.ecommerce.domain.entity.ProductEntity;
import uz.nt.ecommerce.service.CategoryService;
import uz.nt.ecommerce.service.ProductService;

@Controller
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@EnableMethodSecurity
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class ProductController {

    private final ProductService productService;

    private final CategoryService categoryService;

    @PostMapping(value = "/create")
    public ModelAndView createProduct(@ModelAttribute ProductCreateDto productCreateDto) {
        ModelAndView modelAndView = new ModelAndView("product");
        System.out.println(productCreateDto);
        BaseResponse<ProductEntity> save = productService.save(productCreateDto);
        modelAndView.addObject("categories", categoryService.getChildCategories());
        modelAndView.addObject("products", productService.getAll());
        modelAndView.addObject("message", save.getMessage());
        return modelAndView;
    }

    @PostMapping(value = "/delete")
    public ModelAndView deleteProduct(@RequestParam(name = "id") Long productId) {
        ModelAndView modelAndView = new ModelAndView("product");
        productService.delete(productId);
        modelAndView.addObject("categories", categoryService.getChildCategories());
        modelAndView.addObject("products", productService.getAll());
        modelAndView.addObject("message", "Product successfully deleted");
        return modelAndView;
    }

    @PostMapping(value = "/update")
    public ModelAndView updateProduct(@ModelAttribute ProductCreateDto productCreateDto,
                                      @RequestParam(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView("product");
        productService.update(productCreateDto, id);
        modelAndView.addObject("categories", categoryService.getChildCategories());
        modelAndView.addObject("products", productService.getAll());
        modelAndView.addObject("message", "Product successfully updated!");
        return modelAndView;
    }


}
