package uz.nt.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.CategoryCreateDto;
import uz.nt.ecommerce.domain.entity.CategoryEntity;
import uz.nt.ecommerce.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoryService implements BaseService<CategoryEntity, CategoryCreateDto> {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Override
    public BaseResponse<CategoryEntity> save(CategoryCreateDto createDto) {
        CategoryEntity entity = modelMapper.map(createDto, CategoryEntity.class);
        try {
            CategoryEntity category = categoryRepository.save(entity);
            return new BaseResponse<>("Successfully category", 200, category, true);
        } catch (Exception e) {
            return new BaseResponse<>("Failed", 404, null, false);
        }
    }

    @Override
    public void delete(Long id) {
        for (CategoryEntity category : categoryRepository.findAll()) {
            if (category.getParentId() != null && category.getParentId().equals(id)) {
                categoryRepository.deleteById(category.getId());
            }
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryEntity update(CategoryCreateDto createDto, Long id) {
        Optional<CategoryEntity> entity = categoryRepository.findById(id);
        entity.get().setName(createDto.getName());
        return categoryRepository.save(entity.get());
    }

    @Override
    public BaseResponse<CategoryEntity> getById(Long id) {
        if (categoryRepository.findById(id).isPresent()) {
            CategoryEntity category = categoryRepository.findById(id).get();
            return new BaseResponse<>("Category found", 200, category, true);
        }
        return new BaseResponse<>("Category not found", 404, null, false);
    }

    @Override
    public List<CategoryEntity> getAll() {
        return categoryRepository.findAll();
    }

    public List<CategoryEntity> getChildCategories() {
        List<CategoryEntity> childCategories = new ArrayList<>();
        for (CategoryEntity category : categoryRepository.findAll()) {
            if (category.getParentId() != null) {
                childCategories.add(category);
            }
        }
        return childCategories;
    }

    public BaseResponse<CategoryEntity> addChildCategory(Long parentId, CategoryCreateDto childCategoryDTO) {
        CategoryEntity childCategory = CategoryEntity.builder()
                .name(childCategoryDTO.getName())
                .parentId(parentId)
                .build();
        try {
            CategoryEntity save = categoryRepository.save(childCategory);
            return new BaseResponse<>("Successfully category", 200, save, true);
        } catch (Exception e) {
            return new BaseResponse<>("Failed", 404, null, false);
        }
    }
}