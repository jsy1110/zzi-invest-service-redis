package zzipay.investservice.controller;

import zzipay.investservice.domain.Order;
import zzipay.investservice.dto.UserOrderHistoryDto;
import zzipay.investservice.dto.UserOrderSummaryDto;
import zzipay.investservice.service.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/order")
    public UserOrderHistoryDto order(@RequestHeader("ACCESS_USER_ID") Long memberId,
                                     @RequestParam("itemId") Long itemId,
                                     @RequestParam("count") Long count) {

        Order order = orderService.order(memberId, itemId, count);
        UserOrderHistoryDto orderDto = getUserOrderHistoryDto(order);

        return orderDto;
    }

    private UserOrderHistoryDto getUserOrderHistoryDto(Order order) {

        UserOrderHistoryDto orderDto = UserOrderHistoryDto.builder()
                .orderId(order.getId())
                .productId(order.getItem().getId())
                .title(order.getItem().getName())
                .totalInvestingAmount(order.calculateTotalInvestingAmount().getValue())
                .myInvestingAmount(order.calculateOrderInvestingAmount().getValue())
                .investingDate(order.getOrderDate())
                .build();

        return orderDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/order/cancel")
    public void cancel(@RequestParam("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/order/history")
    public List<UserOrderHistoryDto> orderList(@RequestHeader("ACCESS_USER_ID") Long memberId) {
        return orderService.findOrderHistory(memberId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/order/summary")
    public List<UserOrderSummaryDto> orderSummaryList(@RequestHeader("ACCESS_USER_ID") Long memberId) {
        return orderService.findOrderSummary(memberId);
    }
}
