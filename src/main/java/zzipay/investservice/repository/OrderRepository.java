package zzipay.investservice.repository;

import zzipay.investservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {

    List<Order> findAllByMemberId(Long memberId);
}
