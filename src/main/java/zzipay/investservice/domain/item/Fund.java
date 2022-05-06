package zzipay.investservice.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("F")
@Getter
@SuperBuilder
@NoArgsConstructor
public class Fund extends Item {
    private String riskLevel;
}
