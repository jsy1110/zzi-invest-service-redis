package zzipay.investservice.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCredit is a Querydsl query type for Credit
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCredit extends EntityPathBase<Credit> {

    private static final long serialVersionUID = -1077432447L;

    public static final QCredit credit = new QCredit("credit");

    public final QItem _super = new QItem(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> finishedAt = _super.finishedAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final NumberPath<Long> price = _super.price;

    public final NumberPath<Integer> rank = createNumber("rank", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> startedAt = _super.startedAt;

    //inherited
    public final NumberPath<Long> stockQuantity = _super.stockQuantity;

    //inherited
    public final NumberPath<Long> totalQuantity = _super.totalQuantity;

    public QCredit(String variable) {
        super(Credit.class, forVariable(variable));
    }

    public QCredit(Path<? extends Credit> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCredit(PathMetadata metadata) {
        super(Credit.class, metadata);
    }

}

