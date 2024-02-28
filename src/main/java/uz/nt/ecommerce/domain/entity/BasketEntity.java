package uz.nt.ecommerce.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "baskets")
public class BasketEntity extends BaseEntity {

    @ManyToOne(cascade = CascadeType.MERGE)
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ProductEntity product;

    @Column(nullable = false)
    @Positive
    private int amount;

    @Positive
    private Double totalPrice;

}
