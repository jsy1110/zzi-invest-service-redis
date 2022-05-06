package zzipay.investservice.dto;

import zzipay.investservice.domain.Address;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class MemberDto implements Serializable {

    private Long memberId;
    private String name;
    private Address address;
}
