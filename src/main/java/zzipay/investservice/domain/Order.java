package zzipay.investservice.domain;

import zzipay.investservice.domain.item.Item;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import zzipay.investservice.domain.item.Money;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "orders") // 적어주지 않으면 관례로 table name 이 order 가 되는데 order by 의 SQL 구문때문에 orders 로 테이블 name 을 지정
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Long count;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public boolean isCancel() {
        return status == OrderStatus.CANCEL;
    }

    public Long getItemId() {
        return item.getId();
    }

    /**
     * 주문 취소
     */
    public void cancel(Long itemId) {
        this.status = OrderStatus.CANCEL;
        getItem().addStock(count);
    }

    public Money calculateTotalInvestingAmount() {
        return item.calculateTotalInvestingAmount();
    }

    public Money calculateOrderInvestingAmount() {
        return item.getPrice().multiply(count);
    }
}
