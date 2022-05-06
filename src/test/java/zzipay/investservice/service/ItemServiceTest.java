package zzipay.investservice.service;

import zzipay.investservice.domain.item.*;
import zzipay.investservice.dto.ItemDto;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@Transactional
@Rollback
class ItemServiceTest {

    @Autowired ItemRepository itemRepository;
    @Autowired ItemService itemService;

    @Test
    public void 아이템_단순_추가() {
        //given
        //when
        Item item = createCreditOrder("신용아이템B", 10000L, 100L, LocalDateTime.now(), LocalDateTime.now());
        //then
        assertThat(item).isEqualTo(itemRepository.findById(item.getId()).get());
    }

    @Test
    public void 아이템_중복이름_실패() throws Exception {
        //given
        Item item = createCreditOrder("신용아이템A", 10000L, 100L, LocalDateTime.now(), LocalDateTime.now());

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            createCreditOrder("신용아이템A", 50000L, 200L, LocalDateTime.now(), LocalDateTime.now());
        });

        //then
        assertEquals("중복 아이템 등록 에러가 발생해야 한다..", customException.getMessage(), "상품 등록을 할 수 없습니다. 중복된 이름이 있습니다.");
    }
    
    @Test
    public void 아이템_조회_전체() {
        //given
        Item item1 = createCreditOrder("신용아이템A", 50000L, 200L, LocalDateTime.now(), LocalDateTime.now());
        Item item2 = createCreditOrder("신용아이템B", 10000L, 100L, LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-30T01:30"));
        Item item3 = createFundOrder("펀드아이템A", 10000L, 100L, LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-30T01:30"));


        //when
        List<String> itemNames = new ArrayList<>();
        List<ItemDto> items = itemService.findItems();
        items.stream().forEach(itemDto -> itemNames.add(itemDto.getTitle()));

        //then
        assertThat(itemNames).containsExactly("신용아이템B", "펀드아이템A");
    }


    @Test
    public void 아이템_조회_단건() {
        //given
        Item item = createCreditOrder("신용아이템B", 10000L, 100L, LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-30T01:30"));

        //when
        ItemDto dto = itemService.findOneItem(item.getId());

        //then
        assertThat(dto.getProductId()).isEqualTo(item.getId());
    }

    @Test
    public void 아이템_조회X_단건() {
        //given
        Item item = createCreditOrder("신용아이템B", 10000L, 100L, LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-30T01:30"));

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            ItemDto dto = itemService.findOneItem(999L);
        });

        //then
        assertEquals("등록되지 않은 상품은 조회되지 않아야 한다.", customException.getMessage(), "해당 상품을 조회할 수 없습니다.");
    }

    private Item createCreditOrder(String name, long value, long totalQuantity, LocalDateTime start, LocalDateTime end) {
        Credit credit = Credit.builder()
                .rank(1)
                .name(name)
                .price(new Money(value))
                .totalQuantity(totalQuantity)
                .stockQuantity(totalQuantity)
                .time(new AvailableTime(start, end))
                .build();

        Item item = itemService.saveItem(credit);

        return item;
    }

    private Item createFundOrder(String name, long value, long totalQuantity, LocalDateTime start, LocalDateTime end)  {
        Fund fund = Fund.builder()
                .riskLevel("high")
                .name(name)
                .price(new Money(value))
                .totalQuantity(totalQuantity)
                .stockQuantity(totalQuantity)
                .time(new AvailableTime(start, end))
                .build();

        Item item = itemService.saveItem(fund);

        return item;
    }

}

