package uz.nt.ecommerce.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent_id"}))
public class CategoryEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private List<ProductEntity> products = new ArrayList<>();
}