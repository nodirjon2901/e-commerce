package uz.nt.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.nt.ecommerce.common.DataNotFoundException;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.OrderCreateDto;
import uz.nt.ecommerce.domain.entity.OrderEntity;
import uz.nt.ecommerce.domain.entity.enums.OrderStatus;
import uz.nt.ecommerce.repository.OrderRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements BaseService<OrderEntity, OrderCreateDto> {

    private final OrderRepository orderRepostory;
    private final ModelMapper modelMapper;

    @Override
    public BaseResponse<OrderEntity> save(OrderCreateDto orderCreateDto) {
        OrderEntity orderEntity = modelMapper.map(orderCreateDto, OrderEntity.class);
        try {
            OrderEntity save = orderRepostory.save(orderEntity);
            return new BaseResponse<>("Successfully order", 200, save, true);
        } catch (Exception e) {
            return new BaseResponse<>("Failed new order", 400, null, false);
        }
    }

    @Override
    public void delete(Long id) {
        orderRepostory.deleteById(id);
    }

    @Override
    public OrderEntity update(OrderCreateDto createDto, Long id) {
        return null;
    }

    @Override
    public BaseResponse<OrderEntity> getById(Long id) {
        if (orderRepostory.findById(id).isPresent()) {
            OrderEntity orderEntity = orderRepostory.findById(id).get();
            return new BaseResponse<>("Order found", 200, orderEntity, true);
        }
        return new BaseResponse<>("Order not found", 404, null, false);
    }

    @Override
    public List<OrderEntity> getAll() {
        return orderRepostory.findAll();
    }

    public void updateStatus(OrderEntity order, OrderStatus status) {
        OrderEntity orderEntity = orderRepostory.findById(order.getId()).orElseThrow(() -> new DataNotFoundException("Order don`t found"));
        orderEntity.setStatus(status);
        orderRepostory.save(orderEntity);
    }

    public List<OrderEntity> getMyOrders(Long userId) {
        return orderRepostory.findByUserId(userId);
    }

}
