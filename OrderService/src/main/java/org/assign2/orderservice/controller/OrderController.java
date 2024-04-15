package org.assign2.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.assign2.orderservice.dto.OrderRequest;
import org.assign2.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) throws IllegalAccessException {
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }


}
