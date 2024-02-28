package uz.nt.ecommerce.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.nt.ecommerce.domain.entity.CategoryEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCreateDto {

    private String name;

    private Double price;

    private int quantity;

    private CategoryEntity category;

}