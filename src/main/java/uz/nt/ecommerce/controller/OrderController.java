package uz.nt.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uz.nt.ecommerce.domain.dto.request.OrderCreateDto;
import uz.nt.ecommerce.domain.entity.enums.OrderStatus;
import uz.nt.ecommerce.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/show-all-orders")
    public ModelAndView showAllOrders() {
        ModelAndView modelAndView = new ModelAndView("order");
        modelAndView.addObject("orders", orderService.getAll());
        return modelAndView;
    }

    @GetMapping("/show-my-orders")
    public ModelAndView showMyOrders() {
        ModelAndView modelAndView = new ModelAndView("order");
        //modelAndView.addObject("orders", orderService.getMyOrders(AuthController.currentUser.getId()));
        return modelAndView;
    }

    @PostMapping(value = "/create")
    public ModelAndView createOrder(@RequestBody OrderCreateDto orderCreateDto
    ) {
        ModelAndView modelAndView = new ModelAndView("order");
        orderService.save(orderCreateDto);
        return modelAndView;
    }

    @PostMapping(value = "/delete")
    public ModelAndView deleteOrder(@RequestParam(name = "id") Long orderId) {
        ModelAndView modelAndView = new ModelAndView("order");
        orderService.delete(orderId);
        return modelAndView;
    }

    @PostMapping(value = "/updateStatus")
    public ModelAndView updateOrder(@RequestParam(name = "orderId") Long orderId,
                                    @RequestParam(name = "status") OrderStatus status
    ) {
        ModelAndView modelAndView = new ModelAndView("order");
        orderService.updateStatus(orderService.getById(orderId).getData(), status);
        modelAndView.addObject("orders", orderService.getAll());
        modelAndView.addObject("message", "Order successfully update");
        return modelAndView;
    }


}
