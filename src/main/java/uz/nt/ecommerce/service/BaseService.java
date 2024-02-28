package uz.nt.ecommerce.service;

import org.springframework.stereotype.Service;
import uz.nt.ecommerce.domain.dto.BaseResponse;

import java.util.List;

/**
 * @param <E>  E entity
 * @param <CD> CD create dto
 * @author Tojiahmedov Nodir
 */

@Service
public interface BaseService<E, CD> {

    BaseResponse<E> save(CD createDto);

    void delete(Long id);

    E update(CD createDto, Long id);

    BaseResponse<E> getById(Long id);

    List<E> getAll();

}
