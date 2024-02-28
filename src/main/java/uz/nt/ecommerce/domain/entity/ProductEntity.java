package uz.nt.ecommerce.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "products")
public class ProductEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    @NotBlank(message = "name cannot be empty or null")
    private String name;

    @Column(nullable = false)
    @Positive
    private Double price;

    @Column(nullable = false)
    @Positive
    private int quantity;

    @ManyToOne(cascade = CascadeType.MERGE)
    private CategoryEntity category;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<BasketEntity> baskets = new ArrayList<>();

}