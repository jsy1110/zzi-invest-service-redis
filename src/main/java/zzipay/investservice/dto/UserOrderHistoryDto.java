package zzipay.investservice.dto;

import lombok.Builder;
import lombok.Data;
import zzipay.investservice.domain.item.Money;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
public class UserOrderHistoryDto implements Serializable {
    private Long orderId;
    private Long productId;
    private String title;
    private Long totalInvestingAmount;
    private Long myInvestingAmount;
    private LocalDateTime investingDate;

}
