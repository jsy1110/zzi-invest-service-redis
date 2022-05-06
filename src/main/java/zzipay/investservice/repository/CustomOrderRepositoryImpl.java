package zzipay.investservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import zzipay.investservice.domain.OrderStatus;
import zzipay.investservice.domain.QOrder;
import zzipay.investservice.domain.item.QItem;
import zzipay.investservice.dto.UserOrderSummaryDto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {
    private final JPAQueryFactory queryFactory;

    public CustomOrderRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     *
         SELECT A.MEMBER_ID, B.NAME, B.TOTAL_QUANTITY * B.PRICE, SUM(A.COUNT) * B.PRICE
         FROM ORDERS A, ITEM B
         WHERE A.ITEM_ID = B.ITEM_ID
         AND A.MEMBER_ID = {member_id}
         GROUP BY A.MEMBER_ID, A.ITEM_ID
         ;
     */
    @Override
    public List<UserOrderSummaryDto> findMemberOrderSummary(Long memberId) {
        QOrder order = new QOrder("o");
        QItem item = new QItem("i");

        List<UserOrderSummaryDto> result =
                queryFactory.select(Projections.bean(UserOrderSummaryDto.class,
                                item.id.as("productId"),
                                item.name.as("title"),
                                item.totalQuantity.multiply(item.price.value).as("totalInvestingAmount"),
                                order.count.sum().multiply(item.price.value).as("myInvestingAmount")))
                .from(order)
                .join(order.item, item)
                .where(order.member.id.eq(memberId).and(order.status.eq(OrderStatus.ORDER)))
                .groupBy(order.member, item.id)
                .fetch();

        return result;
    }

    @Override
    public Long countMemberOrderById(Long orderId) {
        QOrder order = new QOrder("o");

        return queryFactory.select(order.member.id.countDistinct())
                .from(order)
                .where(order.item.id.eq(orderId))
                .fetchFirst();
    }
}
