package zzipay.investservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.exception.ExceptionEnum;

import java.io.Serializable;

@Slf4j
@RedisHash
@AllArgsConstructor
@Getter
public class Stock implements Serializable {
    @Id
    private Long id;

    private Long stock;

    public Long calculateRemainStock(Long count) {

        if (this.stock < count) {
            log.info("Illegal ordered. ItemId = {}, count = {}", id, count);
            throw new CustomException(ExceptionEnum.NOT_ENOUGH_STOCK);
        }
        return this.stock - count;
    }
}
