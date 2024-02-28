package uz.nt.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.nt.ecommerce.domain.entity.BasketEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<BasketEntity, Long> {

    List<BasketEntity> findByUserId(Long userId);

    @Query("select b from baskets b where b.user.id=:userId")
    Optional<BasketEntity> findByUserIdForRemove(Long userId);

}
