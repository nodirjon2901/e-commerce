package uz.nt.ecommerce.domain.dto.request;

import lombok.*;
import uz.nt.ecommerce.domain.entity.ProductEntity;
import uz.nt.ecommerce.domain.entity.UserEntity;
import uz.nt.ecommerce.domain.entity.enums.OrderStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateDto {

    private ProductEntity product;

    private UserEntity user;

    private Integer amount;

    private Double totalPrice;

    private OrderStatus status;

}