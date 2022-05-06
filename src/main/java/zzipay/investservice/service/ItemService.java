package zzipay.investservice.service;

import zzipay.investservice.domain.item.Item;
import zzipay.investservice.domain.item.Stock;
import zzipay.investservice.dto.InvestStatus;
import zzipay.investservice.dto.ItemDto;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.exception.ExceptionEnum;
import zzipay.investservice.repository.ItemRepository;
import zzipay.investservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zzipay.investservice.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    @Transactional
    @CacheEvict(value = "myOrder", key = "#result.id")
    public Item saveItem(Item item) {
        validateDuplicateItem(item);

        Item savedItem = itemRepository.save(item);
        stockRepository.save(new Stock(savedItem.getId(), savedItem.getStockQuantity()));
        return savedItem;
    }

    private void validateDuplicateItem(Item item) {

        List<Item> findItems = itemRepository.findByName(item.getName());
        if(!findItems.isEmpty()) {
            log.info("Cannot register duplicate item. ItemName = {}", item.getName());
            throw new CustomException(ExceptionEnum.DUPLICATE_ITEM);
        }
    }

    @Cacheable(value = "myOrder", key = "#itemId")
    public ItemDto findOneItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    log.error("Cannot find item. ItemId = {}", itemId);
                    throw new CustomException(ExceptionEnum.NOT_FOUND_PRODUCT);
                });

        Long count = orderRepository.countMemberOrderById(item.getId());
        ItemDto dto = getItemDto(item, count);

        return dto;
    }

    public List<ItemDto> findItems() {

        List<ItemDto> itemDtoList = new ArrayList<>();
        List<Item> items = itemRepository.findAll();

        for (Item item : items) {
            addItemToDto(itemDtoList, item);
        }
        return itemDtoList;
    }

    private void addItemToDto(List<ItemDto> itemDtoList, Item item) {

        Long count = orderRepository.countMemberOrderById(item.getId());

        if (item.isOpenTime()) {
            ItemDto dto = getItemDto(item, count);
            itemDtoList.add(dto);
        }
    }

    private ItemDto getItemDto(Item item, Long count) {
        Stock stock = stockRepository.findById(item.getId()).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionEnum.NOT_FOUND_PRODUCT);
                });

        item.updateStock(stock.getStock());

        ItemDto dto = ItemDto.builder()
                .productId(item.getId())
                .title(item.getName())
                .minimumInvestingAmount(item.getPrice().getValue())
                .currentInvestingAmount(item.calculateCurrentInvestingAmount().getValue())
                .totalInvestingAmount(item.calculateTotalInvestingAmount().getValue())
                .investorCount(count)
                .startedAt(item.getTime().getStartedAt())
                .finishedAt(item.getTime().getFinishedAt())
                .status(item.isOpened())
                .build();
        return dto;
    }
}
