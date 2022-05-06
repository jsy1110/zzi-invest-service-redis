package zzipay.investservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ItemSimpleDto implements Serializable {
    private Long productId;
    private String title;
}
