package zzipay.investservice.domain.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Access(AccessType.FIELD)
public class Money {

    private Long value;

    public Money(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
    public Money multiply(Long multiplier) {
        return new Money(value * multiplier);
    }
}
