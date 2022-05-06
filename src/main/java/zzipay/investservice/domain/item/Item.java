package zzipay.investservice.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import zzipay.investservice.dto.InvestStatus;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.exception.ExceptionEnum;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    @Embedded
    private Money price;

    private Long totalQuantity;
    private Long stockQuantity;

    @Embedded
    private AvailableTime time;

    /**
     * stock 증가
     */
    public void addStock(Long quantity) {
        this.stockQuantity += quantity;
    }

    public void updateStock(Long quantity) {
        this.stockQuantity = quantity;
    }

    public void validateTime() {
        time.validateTime();
    }

    public boolean isOpenTime() {
        return time.isOpenTime();
    }

    public Money calculateCurrentInvestingAmount() {
        return price.multiply(totalQuantity-stockQuantity);
    }

    public Money calculateTotalInvestingAmount() {
        return price.multiply(totalQuantity);
    }

    public Money calculateAvailableInvestingAmount() {
        return price.multiply(stockQuantity);
    }

    public InvestStatus isOpened() {
        return stockQuantity > 0 ? InvestStatus.OPEN : InvestStatus.CLOSE;
    }

}
