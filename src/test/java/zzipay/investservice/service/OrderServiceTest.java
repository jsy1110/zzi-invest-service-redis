package zzipay.investservice.service;

import zzipay.investservice.domain.Address;
import zzipay.investservice.domain.Member;
import zzipay.investservice.domain.Order;
import zzipay.investservice.domain.OrderStatus;
import zzipay.investservice.domain.item.AvailableTime;
import zzipay.investservice.domain.item.Credit;
import zzipay.investservice.domain.item.Money;
import zzipay.investservice.dto.InvestStatus;
import zzipay.investservice.dto.ItemDto;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.repository.ItemRepository;
import zzipay.investservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import zzipay.investservice.repository.StockRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@Transactional
@Rollback
@SpringBootTest
class OrderServiceTest {
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired StockRepository stockRepository;

    @Autowired ItemService itemService;
    @Autowired MemberService memberService;

    @Autowired EntityManager em;

    @BeforeEach
    void beforeEach() {
        stockRepository.deleteAll();
    }

    @Test
    public void 상품주문() throws Exception {
        //given
        Address address = new Address("수원시", "이거리", "123-1");
        Member member = createMember("아이유", address);
        Credit credit = createCredit("신용상품A", 5000L, 100L, 100L,
                LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-15T01:30"),
                1);

        Long orderCount = 2L;
        Long id = credit.getId();

        //when
        Order order = orderService.order(member.getId(), credit.getId(), orderCount);
        Order getOrder = orderRepository.getById(order.getId());

        //then
        assertEquals("상품의 class type 확인", getOrder.getItem().getClass(), Credit.class);
        assertEquals("상품 주문 시 상태는 ORDER가 되어야 한다.", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품수가 정확해야 한다.", orderCount, getOrder.getCount());

        ItemDto oneItem = itemService.findOneItem(id);
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",
                98L,
                (oneItem.getTotalInvestingAmount()- oneItem.getCurrentInvestingAmount()) / oneItem.getMinimumInvestingAmount());
    }

    @Test
    public void 상품주문_상품상태변경() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-15T01:30"),
                1);

        Long orderCount = 10L;
        Long id = credit.getId();

        //when
        Order order = orderService.order(member.getId(), credit.getId(), orderCount);

        //em.flush();
        //em.clear();
        //Credit getCredit = em.find(Credit.class, id);
        ItemDto dto = itemService.findOneItem(id);

        //then
        assertEquals("재고가 0개가 되면 item의 상태는 CLOSE가 되어야 한다.", InvestStatus.CLOSE, dto.getStatus());
    }

    @Test
    public void 상품주문X_재고수량초과() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-15T01:30"),
                1);

        Long orderCount = 12L;

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.order(member.getId(), credit.getId(), orderCount);
        });

        //then
        assertEquals("재고부족 에러가 발생해야 한다.", customException.getMessage(), "투자금액이 투자 가능금액보다 큽니다.");
    }

    @Test
    public void 상품주문X_투자오픈전() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-12-30T01:30"), LocalDateTime.parse("2022-12-31T01:30"),
                1);

        Long orderCount = 12L;

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.order(member.getId(), credit.getId(), orderCount);
        });

        //then
        assertEquals("투자 오픈 전 에러가 발생해야 한다.", customException.getMessage(), "투자 오픈 시간 전입니다.");
    }

    @Test
    public void 상품주문X_투자종료() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-03-10T01:30"), LocalDateTime.parse("2022-04-15T01:30"),
                1);

        Long orderCount = 12L;

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.order(member.getId(), credit.getId(), orderCount);
        });

        //then
        assertEquals("투자 오픈 전 에러가 발생해야 한다.", customException.getMessage(), "투자 가능 시간이 종료되었습니다.");
    }

    @Test
    public void 상품주문X_NO상품() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-04-30T01:30"), LocalDateTime.parse("2022-05-15T01:30"),
                1);

        Long orderCount = 12L;

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.order(member.getId(), 999L, orderCount);
        });

        //then
        assertEquals("등록되지 않은 상품 주문이 불가해야 한다.", customException.getMessage(), "주문 가능한 상품을 찾을 수 없습니다.");
    }

    @Test
    public void 상품주문X_NO회원() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-04-30T01:30"), LocalDateTime.parse("2022-05-15T01:30"),
                1);

        Long orderCount = 12L;

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.order(999L, credit.getId(), orderCount);
        });

        //then
        assertEquals("가입하지 않은 회원은 주문을 할 수 없어야 한다.", customException.getMessage(), "주문을 요청한 회원을 찾을 수 없습니다.");
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-15T01:30"),
                1);

        Long orderCount = 2L;
        Long id = credit.getId();

        Order order = orderService.order(member.getId(), credit.getId(), orderCount);

        //when
        orderService.cancelOrder(order.getId());

        //then
        Order getOrder = orderRepository.getById(order.getId());
        assertEquals("주문 취소시 상태는 CANCEL로 변경되야 한다.", OrderStatus.CANCEL, getOrder.getStatus());

        ItemDto oneItem = itemService.findOneItem(id);
        assertEquals("주문 취소된 상품은 그만큼 재고가 증가되어야 한다.",
                10L,
                (oneItem.getTotalInvestingAmount()- oneItem.getCurrentInvestingAmount()) / oneItem.getMinimumInvestingAmount());
    }

    @Test
    public void 주문취소X_NO상품() throws Exception {
        //given
        //주문 상품이 없음

        //when
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.cancelOrder(999L);
        });

        //then
        assertEquals("등록되지 않은 상품은 취소가 불가해야 한다.", customException.getMessage(), "취소 가능한 주문을 찾을 수 없습니다.");
    }


    @Test
    public void 주문취소X_이미취소() throws Exception {
        //given
        Member member = createMember("김연아", new Address("용인시", "덕영대로", "123-1"));
        Credit credit = createCredit("신용상품A", 5000L, 10L, 10L,
                LocalDateTime.parse("2022-04-01T01:30"), LocalDateTime.parse("2022-12-15T01:30"),
                1);

        Long orderCount = 2L;
        Long id = credit.getId();

        Order order = orderService.order(member.getId(), credit.getId(), orderCount);

        //when
        orderService.cancelOrder(order.getId());

        //then
        CustomException customException = assertThrows(CustomException.class, () -> {
            orderService.cancelOrder(order.getId());
        });

        assertEquals("이미 취소한 주문은 취소가 불가해야 한다.", customException.getMessage(), "이미 취소된 주문입니다.");
    }

    private Member createMember(String name, Address address) {
        Member member = new Member(name, address);

        Long memberId = memberService.join(member);

        return member;
    }

    private Credit createCredit(String name, Long price, Long totalStockQuantity, Long stockQuantity, LocalDateTime start, LocalDateTime end, Integer rank) {
        Credit credit = Credit.builder()
                .rank(rank)
                .name(name)
                .price(new Money(price))
                .totalQuantity(totalStockQuantity)
                .stockQuantity(stockQuantity)
                .time(new AvailableTime(start, end))
                .build();

        itemService.saveItem(credit);

        return credit;
    }
}