package zzipay.investservice.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderSummaryDto implements Serializable {
    private Long productId;
    private String title;
    private Long totalInvestingAmount;
    private Long myInvestingAmount;
}
