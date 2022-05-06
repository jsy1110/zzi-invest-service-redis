package zzipay.investservice.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomItemRepository {

    Long updateStockQuantity(Long itemId, Long quantity);
}
