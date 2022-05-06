package zzipay.investservice.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import zzipay.investservice.domain.item.QItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Slf4j
@Repository
public class CustomItemRepositoryImpl implements CustomItemRepository {

    private final JPAQueryFactory queryFactory;

    public CustomItemRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     *  UPDATE ITEM
     *  SET STOCK_QUANTITY = STOCK_QUANTITY-{QUANTITY}
     *  WHERE ITEM_ID = {ITEM_ID}
     *  AND   STOCK_QUANTITY >= {QUANTITY}
     */
    @Override
    public Long updateStockQuantity(Long itemId, Long quantity) {
        QItem item = new QItem("i");

        return queryFactory.update(item)
                .set(item.stockQuantity, item.stockQuantity.subtract(quantity))
                .where(item.id.eq(itemId).and(item.stockQuantity.goe(quantity)))
                .execute();
    }
}
