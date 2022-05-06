package zzipay.investservice.domain.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.exception.ExceptionEnum;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Access(AccessType.FIELD)
@Getter
public class AvailableTime {

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public AvailableTime(LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public boolean isOpenTime() {
        return (startedAt.isBefore(LocalDateTime.now()) && finishedAt.isAfter(LocalDateTime.now()));
    }

    public void validateTime() {
        if (startedAt.isAfter(LocalDateTime.now())) {
            log.error("Illegal order detected.");
            throw new CustomException(ExceptionEnum.NOT_OPEN_PRODUCT,
                    "상품 오픈 시간은 " + startedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "입니다.");
        }

        if (finishedAt.isBefore(LocalDateTime.now())) {
            log.warn("Illegal order detected.");
            throw new CustomException(ExceptionEnum.CLOSED_PRODUCT);
        }
    }

}
