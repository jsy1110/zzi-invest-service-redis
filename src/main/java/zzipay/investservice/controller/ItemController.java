package zzipay.investservice.controller;

import zzipay.investservice.domain.item.*;
import zzipay.investservice.dto.ItemDto;
import zzipay.investservice.dto.ItemSimpleDto;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.exception.ExceptionEnum;
import zzipay.investservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/items")
    public ItemSimpleDto createItem(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam Long price,
            @RequestParam Long stockQuantity,
            @RequestParam(required = false) Integer rank,
            @RequestParam(required = false) String riskLevel,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime finishedAt
    ) {
        if (type.equals("C")) {
            Credit credit = Credit.builder()
                    .rank(rank)
                    .name(name)
                    .price(new Money(price))
                    .totalQuantity(stockQuantity)
                    .stockQuantity(stockQuantity)
                    .time(new AvailableTime(startedAt, finishedAt))
                    .build();

            Item item = itemService.saveItem(credit);
            return ItemSimpleDto.builder()
                    .title(item.getName())
                    .productId(item.getId())
                    .build();

        }
        if (type.equals("F")) {
            Fund fund = Fund.builder()
                    .riskLevel(riskLevel)
                    .name(name)
                    .price(new Money(price))
                    .totalQuantity(stockQuantity)
                    .stockQuantity(stockQuantity)
                    .time(new AvailableTime(startedAt, finishedAt))
                    .build();

            Item item = itemService.saveItem(fund);
            return ItemSimpleDto.builder()
                    .title(item.getName())
                    .productId(item.getId())
                    .build();
        }

        throw new CustomException(ExceptionEnum.NOT_REGISTER_ITEM);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/items")
    public List<ItemDto> list() {
        return itemService.findItems();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/items/{itemId}")
    public ItemDto findItem(@PathVariable Long itemId) {
        return itemService.findOneItem(itemId);
    }
}
