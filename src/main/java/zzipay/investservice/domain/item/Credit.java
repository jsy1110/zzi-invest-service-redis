package zzipay.investservice.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("C")
@Getter
@SuperBuilder
@NoArgsConstructor
public class Credit extends Item {
    private Integer rank;
}
