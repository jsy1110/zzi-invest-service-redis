package zzipay.investservice.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "E0001", "투자금액이 투자 가능금액보다 큽니다."),
    NOT_OPEN_PRODUCT(HttpStatus.BAD_REQUEST, "E0002", "투자 오픈 시간 전입니다."),
    CLOSED_PRODUCT(HttpStatus.BAD_REQUEST, "E0003", "투자 가능 시간이 종료되었습니다."),

    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "E0011", "회원가입을 할 수 없습니다. 중복된 이름이 있습니다."),
    DUPLICATE_ITEM(HttpStatus.CONFLICT, "E0012", "상품 등록을 할 수 없습니다. 중복된 이름이 있습니다."),
    NOT_REGISTER_ITEM(HttpStatus.BAD_REQUEST, "E0013", "해당 상품을 등록할 수 없습니다. 상품 타입을 확인해주세요."),

    NOT_FOUND_ORDER_PRODUCT(HttpStatus.NOT_FOUND, "E0021", "주문 가능한 상품을 찾을 수 없습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "E0022", "주문을 요청한 회원을 찾을 수 없습니다."),
    NOT_FOUND_CANCEL_ORDER(HttpStatus.NOT_FOUND, "E0023", "취소 가능한 주문을 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "E0024", "해당 상품을 조회할 수 없습니다."),
    ALREADY_CANCEL_ORDER(HttpStatus.NOT_FOUND, "E0025", "이미 취소된 주문입니다.");


    private final HttpStatus status;
    private final String code;
    private String message;

    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
