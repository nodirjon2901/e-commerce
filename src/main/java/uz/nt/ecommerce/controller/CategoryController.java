package uz.nt.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.CategoryCreateDto;
import uz.nt.ecommerce.domain.entity.CategoryEntity;
import uz.nt.ecommerce.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@EnableMethodSecurity
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/create")
    public ModelAndView createCategory(@ModelAttribute CategoryCreateDto categoryCreateDto
    ){
        ModelAndView modelAndView=new ModelAndView("category");
        BaseResponse<CategoryEntity> save = categoryService.save(categoryCreateDto);
        modelAndView.addObject("categories",categoryService.getAll());
        modelAndView.addObject("message",save.getMessage());
        return modelAndView;
    }

    @PostMapping(value = "/delete")
    public ModelAndView deleteCategory(@RequestParam(name = "id") Long categoryId) {
        ModelAndView modelAndView=new ModelAndView("category");
        categoryService.delete(categoryId);
        modelAndView.addObject("categories", categoryService.getAll());
        modelAndView.addObject("message","Category successfully deleted!");
        return modelAndView;
    }

    @PostMapping(value = "/update")
    public ModelAndView updateCategory(@ModelAttribute CategoryCreateDto categoryCreateDto,
                                       @RequestParam(name = "id") Long id
    ){
        ModelAndView modelAndView=new ModelAndView("category");
        categoryService.update(categoryCreateDto,id);
        modelAndView.addObject("categories",categoryService.getAll());
        modelAndView.addObject("message","Category successfully updated!");
        return modelAndView;
    }
    @PostMapping(value = "/addChildCategory")
    public ModelAndView addChildCategory(@ModelAttribute CategoryCreateDto childCategoryCreateDto,
                                         @RequestParam(name="id") Long parentId){
        ModelAndView modelAndView=new ModelAndView("category");
        BaseResponse<CategoryEntity> categoryEntityBaseResponse = categoryService.addChildCategory(parentId, childCategoryCreateDto);
        modelAndView.addObject("categories",categoryService.getAll());
        modelAndView.addObject("message",categoryEntityBaseResponse.getMessage());
        return modelAndView;
    }
}
