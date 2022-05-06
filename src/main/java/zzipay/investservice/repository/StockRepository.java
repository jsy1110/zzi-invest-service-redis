package zzipay.investservice.repository;

import org.springframework.data.repository.CrudRepository;
import zzipay.investservice.domain.item.Stock;

public interface StockRepository extends CrudRepository<Stock, Long> {

}
