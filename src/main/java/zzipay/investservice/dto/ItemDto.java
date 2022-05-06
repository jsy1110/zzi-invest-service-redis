package zzipay.investservice.dto;

import lombok.Builder;
import lombok.Data;
import zzipay.investservice.domain.item.AvailableTime;
import zzipay.investservice.domain.item.Money;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
public class ItemDto implements Serializable {
    private Long productId;
    private String title;

    private Long minimumInvestingAmount;
    private Long totalInvestingAmount;
    private Long currentInvestingAmount;
    private Long investorCount;
    private InvestStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

}
