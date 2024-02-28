package uz.nt.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.BasketCreateDto;
import uz.nt.ecommerce.domain.entity.BasketEntity;
import uz.nt.ecommerce.repository.BasketRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BasketService implements BaseService<BasketEntity, BasketCreateDto> {

    private final UserService userService;

    private final BasketRepository basketRepository;

    private final ModelMapper modelMapper;

    @Override
    public BaseResponse<BasketEntity> save(BasketCreateDto createDto) {
        BasketEntity entity = modelMapper.map(createDto, BasketEntity.class);
        try {
            BasketEntity save = basketRepository.save(entity);
            System.out.println(save);
            return new BaseResponse<>("☑️ SUCCESSFULLY ADDED TO BASKET ☑️", 200, save, true);
        } catch (Exception e) {
            return new BaseResponse<>("✖ FAILED ADDING TO BASKET ✖", 403, null, false);
        }
    }

    @Override
    public void delete(Long id) {
        basketRepository.deleteById(id);
    }

    @Override
    public BasketEntity update(BasketCreateDto createDto, Long id) {
        return null;
    }

    @Override
    public BaseResponse<BasketEntity> getById(Long id) {
        if (basketRepository.findById(id).isPresent()) {
            BasketEntity basketEntity = basketRepository.findById(id).get();
            return new BaseResponse<>("Basket Found", 200, basketEntity, true);
        }
        return new BaseResponse<>("Basket not found", 404, null, false);
    }

    @Override
    public List<BasketEntity> getAll() {
        return basketRepository.findAll();
    }


    public List<BasketEntity> getMyBaskets(Long userId) {
        return basketRepository.findByUserId(userId);
    }

    public BaseResponse<BasketEntity> removeMyBasket(Long userId) {
        BasketEntity basketEntity = basketRepository.findByUserIdForRemove(userId).get();
        basketRepository.deleteById(basketEntity.getId());
        return new BaseResponse<>("Success", 200, basketEntity, true);
    }

    public List<BasketEntity> getUserBaskets(Long chatId) {
        return basketRepository.findByUserId(userService.findByChatId(chatId.toString()).getId());
    }
}
