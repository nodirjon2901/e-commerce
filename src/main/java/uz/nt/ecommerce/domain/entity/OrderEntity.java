package uz.nt.ecommerce.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import uz.nt.ecommerce.domain.entity.enums.OrderStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "orders")
public class OrderEntity extends BaseEntity {

    @ManyToOne(cascade = CascadeType.MERGE)
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ProductEntity product;

    @Column(nullable = false)
    @Positive
    private int amount;

    @Positive
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}