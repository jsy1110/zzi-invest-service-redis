package zzipay.investservice.repository;

import zzipay.investservice.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, CustomItemRepository {
    List<Item> findByName(String name);

}
