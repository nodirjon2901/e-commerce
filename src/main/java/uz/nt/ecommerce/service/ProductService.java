package uz.nt.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.ProductCreateDto;
import uz.nt.ecommerce.domain.entity.ProductEntity;
import uz.nt.ecommerce.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductService implements BaseService<ProductEntity, ProductCreateDto> {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Override
    public BaseResponse<ProductEntity> save(ProductCreateDto createDto) {
        System.out.println(createDto);
        ProductEntity productEntity = modelMapper.map(createDto, ProductEntity.class);
        try {
            ProductEntity product = productRepository.save(productEntity);
            return new BaseResponse<>("Successfully product", 200, product, true);
        } catch (Exception e) {
            return new BaseResponse<>("Failed creating new product!", 400, null, false);
        }
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductEntity update(ProductCreateDto createDto, Long id) {
        Optional<ProductEntity> entity = productRepository.findById(id);
        entity.get().setName(createDto.getName());
        entity.get().setPrice(createDto.getPrice());
        entity.get().setQuantity(createDto.getQuantity());
        return productRepository.save(entity.get());
    }

    @Override
    public BaseResponse<ProductEntity> getById(Long id) {
        if (productRepository.findById(id).isPresent()) {
            ProductEntity product = productRepository.findById(id).get();
            return new BaseResponse<>("Product found", 200, product, true);
        }
        return new BaseResponse<>("Product not found", 404, null, false);
    }

    @Override
    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    public List<ProductEntity> getProductsByCategoryId(Long categoryId) {
        List<ProductEntity> productEntityList = new ArrayList<>();
        for (ProductEntity product : productRepository.findAll()) {
            if (product.getCategory().getId().equals(categoryId)) {
                productEntityList.add(product);
            }
        }
        return productEntityList;
    }

    public String checkProductName(String name) {
        for (ProductEntity product : productRepository.findAll()) {
            if (product.getName().equals(name)) {
                return "✖ PRODUCT NAME ALREADY TAKEN! ✖";
            }
        }
        return "success";
    }

    public ProductEntity updateQuantity(Integer quantity, Long id) {
        Optional<ProductEntity> entity = productRepository.findById(id);
        entity.get().setQuantity(quantity);
        return productRepository.save(entity.get());
    }
}