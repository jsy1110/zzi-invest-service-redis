package zzipay.investservice.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRealEstate is a Querydsl query type for RealEstate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRealEstate extends EntityPathBase<Fund> {

    private static final long serialVersionUID = -1062415598L;

    public static final QRealEstate realEstate = new QRealEstate("realEstate");

    public final QItem _super = new QItem(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> finishedAt = _super.finishedAt;

    public final StringPath houseType = createString("houseType");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final NumberPath<Long> price = _super.price;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> startedAt = _super.startedAt;

    //inherited
    public final NumberPath<Long> stockQuantity = _super.stockQuantity;

    //inherited
    public final NumberPath<Long> totalQuantity = _super.totalQuantity;

    public QRealEstate(String variable) {
        super(Fund.class, forVariable(variable));
    }

    public QRealEstate(Path<? extends Fund> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRealEstate(PathMetadata metadata) {
        super(Fund.class, metadata);
    }

}

