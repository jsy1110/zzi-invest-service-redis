package zzipay.investservice.repository;

import zzipay.investservice.dto.UserOrderSummaryDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomOrderRepository {

    List<UserOrderSummaryDto> findMemberOrderSummary(Long memberId);

    Long countMemberOrderById(Long orderId);
}
