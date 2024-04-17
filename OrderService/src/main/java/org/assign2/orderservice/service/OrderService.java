package org.assign2.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.assign2.orderservice.dto.InventoryResponse;
import org.assign2.orderservice.dto.OrderLineItemsDto;
import org.assign2.orderservice.dto.OrderRequest;
import org.assign2.orderservice.model.Order;
import org.assign2.orderservice.model.OrderLineItems;
import org.assign2.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;
    public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

       List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();


       order.setOrderLineItemsList(orderLineItems);

       List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

       InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
               .uri(inventoryServiceUrl, uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
               .retrieve()
               .bodyToMono(InventoryResponse[].class)
               .block();

       boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);

       if(allProductsInStock){
           orderRepository.save(order);
       }else{
           throw new IllegalAccessException("Product is not in stock, please try again later");
       }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

       OrderLineItems orderLineItems =  new OrderLineItems();
       orderLineItems.setPrice(orderLineItemsDto.getPrice());
       orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
       orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
       return orderLineItems;
    }
}
