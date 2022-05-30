# Zzi`s company for investment

## 개발 환경

- Framework: Springboot 2.6.6 + Spring Data JPA
- DB : H2, Maria DB + Redis
- IDE: IntelliJ IDEA (Ultimate Edition)
- OS: Window 10 Education
- Language : JAVA11
- Build : Gradle
- Test : POSTMAN(통합 테스트), Junit5(단위 테스트)

## 요약

펀드/신용 투자 서비스의 REST API 서버를 개발한다.

## 기능 배경

1. 사용자는 원하는 펀드/신용 투자 상품을 투자할 수 있다.
2. 투자상품이 오픈될 때, 다수의 고객이 동시에 투자를 한다.
3. 투자 후 투자상품의 누적 투자모집 금액, 투자자 수가 증가한다.
4. 총 투자모집금액 달성 시 투자는 마감되고 상품은 Sold out 된다.

## 기능 요구 사항 (API 목록)

- 해당 프로그램은 동일 상품에 대한 중복 투자를 허용한다. 투자가 종료된 상태가 아니라면 동일 회원이 동일 상품을 여러번 구매할 수 있다.
- 위와 같은 이유로 특정 회원의 투자상품 조회 기능은 **주문별 전체 투자 이력**을 보는 history성 API와 **투자상품 별 회원의 투자 정보**를 합쳐서 보여주는 summary성 API 두 가지를 제공한다.
- `투자 금액 = 최소 투자금액 * 투자 상품의 개수`

## TBD
- DDD 패턴으로 리펙토링 필요
- AWS/GCP 등 클라우드 서비스로 올리기 (교육 지원 제도 확인 필요)
- DB 변경 : H2 -> MySQL or postgreSQL
- API 사용권한 등의 처리가 된 access token을 발급하여 사용하는 등 API 접근 제어가 필요 (Spring secure 고려)
- Ehcache로 처리한 cache를 Redis를 이용해서 변경 (local cache -> global cache)
- 재고처리와 주문 로직을 명확히 분리하고, 주문에 대한 처리를 Kafka로 처리하는 로직 개발

### 1. (필수) 전체 투자 상품 조회

- 상품 모집기간(started_at, finished_at) 내의 상품만 응답합니다.
- 전체 투자상품 응답은 다음 내용을 포함합니다. (상품 ID, 상품제목, 총 모집금액, 현재 모집금액, 투자자수, 투자모집상태(모집중, 모집완료), 상품 모집기간)
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Get | /items | JSON |
- 요청 변수 : 없음
- 결과 (List) : 200 OK

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| productId | Long | 상품 아이디 |
| title | String | 상품명 |
| minimumInvestingAmount | Long | 최소 주문 가능 금액 |
| totalInvestingAmount | Long | 전체 투자 모집 금액 |
| currentInvestingAmount | Loing | 현재 투자 모집 금액 |
| investorCount | Long | 참여 투자자 수 |
| status | String | 투자 가능 상태 (OPEN:모집중, CLOSE: 모집 완료) |
| startedAt | Datetime | 투자 시작 시간 |
| finishedAt | Datetime | 투자 종료 시간 |
- 에러코드 : 없음
- 결과 예시

```json
[
    {
        "productId": 1,
        "title": "신용상품A",
        "minimumInvestingAmount": 10000,
        "totalInvestingAmount": 1000000,
        "currentInvestingAmount": 0,
        "investorCount": 0,
        "status": "OPEN",
        "startedAt": "2022-04-01T01:30:00",
        "finishedAt": "2022-04-30T23:59:00"
    },
    {
        "productId": 3,
        "title": "펀드A",
        "minimumInvestingAmount": 50000,
        "totalInvestingAmount": 5000000,
        "currentInvestingAmount": 0,
        "investorCount": 0,
        "status": "OPEN",
        "startedAt": "2022-04-01T01:30:00",
        "finishedAt": "2022-04-30T23:59:00"
    }
]
```

### 2. (필수) 투자하기

- 사용자 식별값, 상품 ID, 투자 금액을 입력값으로 받습니다.
- 총 투자모집 금액(total_investing_amount)을 넘어서면 sold-out 상태를 응답합니다.
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Post | /order | JSON |
- 요청 변수

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| ACCESS_USER_ID | Long | 회원 아이디 |
| itemId | Long | 상품 아이디 |
| count | Long | 구매 개수 |
- 결과 : 200 OK

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| orderId | Long | 주문 아이디 |
| productId | Long | 상품 아이디 |
| title | String | 상품명 |
| totalInvestingAmount | Loing | 전체 투자 모집 금액 |
| myInvestingAmount | Long | 요청 회원의 해당 투자에 대한 투자 금액 |
| investingDate | Datetime | 투자 시간 |
- 에러코드

| HttpStatus | 에러코드 | 설명 |
| --- | --- | --- |
| 400 | E0001 | 투자 가능 금액보다 투자 금액이 클 때 발생 (sold-out) |
| 400 | E0002 | 투자 가능 시간 전일 때 발생 |
| 400 | E0003 | 투자 종료 상품일 때 발생 |
| 404 | E0021 | 주문한 상품을 찾을 수 없을 때 발생 |
| 404 | E0022 | 주문 청한 회원을 찾을 수 없을 때 발생 |
- 결과 예시

```json
{
    "orderId": 7,
    "productId": 3,
    "title": "펀드A",
    "totalInvestingAmount": 5000000,
    "myInvestingAmount": 1500000,
    "investingDate": "2022-04-18T16:58:57.1049827"
}
```

### 3. (필수) 나의 투자상품 조회 - 주문별

- 내가 투자한 모든 상품을 반환합니다.
- 나의 투자 상품 응답은 다음 내용을 포함합니다. (상품ID, 상품 제목, 총 모집금액, 나의 투자금액, 투자일시)
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Get | /order/history | JSON |
- 요청 변수

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| ACCESS_USER_ID | Long | 회원 아이디 |
- 결과 (List) : 200 OK

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| orderId | Long | 주문 아이디 |
| productId | Long | 상품 아이디 |
| title | String | 상품명 |
| totalInvestingAmount | Loing | 전체 투자 모집 금액 |
| myInvestingAmount | Long | 요청 회원의 해당 주문에 대한 투자 금액 |
| investingDate | Datetime | 투자 일시 |
- 에러코드 : 없음
- 결과 예시

```json
[
    {
        "orderId": 7,
        "productId": 3,
        "title": "펀드A",
        "totalInvestingAmount": 5000000,
        "myInvestingAmount": 1500000,
        "investingDate": "2022-04-18T16:58:57.104983"
    },
    {
        "orderId": 8,
        "productId": 1,
        "title": "신용상품A",
        "totalInvestingAmount": 1000000,
        "myInvestingAmount": 300000,
        "investingDate": "2022-04-18T17:27:42.018606"
    },
    {
        "orderId": 9,
        "productId": 3,
        "title": "펀드A",
        "totalInvestingAmount": 5000000,
        "myInvestingAmount": 1500000,
        "investingDate": "2022-04-18T17:27:48.621764"
    }
]
```

### 4. 나의 투자상품 조회 (요약) - 상품별

- 나의 투자에 대한 상품별 투자 정보를 반환합니다. (동일 상품에 대한 중복 투자 허용)
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Get | /order/summary | JSON |
- 요청 변수

| 필드 | 타입 | 설명      |
| --- | --- |---------|
| ACCESS_USER_ID | Long | 회원 아이디  |
- 결과 (List) : 200 OK

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| productId | Long | 상품 아이디 |
| title | String | 상품명 |
| totalInvestingAmount | Loing | 전체 투자 모집 금액 |
| myInvestingAmount | Long | 요청 회원의 해당 투자에 대한 투자 금액 |
- 에러코드 : 없음
- 결과 예시

```json
[
    {
        "productId": 1,
        "title": "신용상품A",
        "totalInvestingAmount": 1000000,
        "myInvestingAmount": 300000
    },
    {
        "productId": 3,
        "title": "펀드A",
        "totalInvestingAmount": 5000000,
        "myInvestingAmount": 1500000
    }
]
```

### 5. 투자 상품 조회 (단건)

- 상품 모집기간(started_at, finished_at) 내의 상품만 응답합니다.
- Path variable로 요청한 productId에 대한 투자 상품 정보를 반환합니다.
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Get | /items/{itemId} | JSON |
- 요청 변수 : 없음
- 결과 : 200 OK

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| productId | Long | 상품 아이디 |
| title | String | 상품명 |
| minimumInvestingAmount | Long | 최소 주문 가능 금액 |
| totalInvestingAmount | Long | 전체 투자 모집 금액 |
| currentInvestingAmount | Loing | 현재 투자 모집 금액 |
| investorCount | Long | 참여 투자자 수 |
| status | String | 투자 가능 상태 (OPEN:모집중, CLOSE: 모집 완료) |
| startedAt | Datetime | 투자 시작 시간 |
| finishedAt | Datetime | 투자 종료 시간 |
- 에러코드

| HttpStatus | 에러코드 | 설명 |
| --- | --- | --- |
| 404 | E0024 | 요청 받은 상품 아이디가 존재하지 않을 경우 발생 |
- 결과 예시

```json
{
    "productId": 1,
    "title": "신용상품A",
    "minimumInvestingAmount": 10000,
    "totalInvestingAmount": 1000000,
    "currentInvestingAmount": 320000,
    "investorCount": 3,
    "status": "OPEN",
    "startedAt": "2022-04-01T01:30:00",
    "finishedAt": "2022-04-30T23:59:00"
}
```

### 6. 회원 가입

- 회원정보값을 받아 회원 가입합니다.
- 회원 아이디(memberId)는 시스템 내부 고유 식별자료 사용되며 향후 Request에서 해당 값은 ACCESS_USER_ID로 사용됩니다.
- 예제에서는 해당 값은 향후 내부 알고리즘에 의해 암호화 될 것이라 가정합니다.
- 가입 시 name이 같을 경우 중복 아이디로 체크합니다.
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Post | /order | JSON |
- 요청 변수

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| name | Long | 이름 |
| city | String | 상품명 |
| street | String | 도로명주소 |
| zipcode | Sting | 우편번호 |
- 결과 : 201 CREATED

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| memberId | Long | 회원 아이디 |
| name | Long | 이름 |
| address | Object | 주소정보 |
| address.city | String | 도시명 |
| address.street | String | 도로명주소 |
| address.zipcode | String | 우편번호 |
- 에러코드

| HttpStatus | 에러코드 | 설명 |
| --- | --- | --- |
| 409 | E0011 | 중복 name이 있을 경우 발생 |
- 결과 예시

```json
{
    "memberId": 16,
    "name": "이지안",
    "address": {
        "city": "대전광역시",
        "street": "이거리",
        "zipcode": "111-1"
    }
}
```

### 7. 상품 등록

- 등록할 상품의 정보를 받아 투자 상품을 등록합니다.
- 상품의 type은 펀드(F), 신용투자(C)로 구분합니다.
- 상품이 펀드(F)일 경우 houseType 파라미터를 통해 위험 레벨을 관리할 수 있습니다.
- 상품이 신용투자(C)일 경우 rank 파라미터를 통해 신용상품의 신용등급을 관리할 수 있습니다.
- 상품 등록시 name이 같을 경우 중복 상품으로 체크합니다.
- 최소 투자단위는 price이며, price * stockQuantity 값이 해당 상품의 총 투자모집금액입니다.
- 입력 받은 type이 펀드(F), 신용투자(C)가 아닌경우 에러값을 반환합니다.
- API 기본 정보

| 메서드 | 요청URL | 출력포맷 |
| --- | --- | --- |
| Post | /items | JSON |
- 요청 변수

| 필드            | 타입 | 설명                     |
|---------------| --- |------------------------|
| type          | Long | 상품 Type (펀드:F, 신용투자:C) |
| name          | String | 상품명                    |
| price         | String | 최소 투자 가능 금액            |
| stockQuantity | Sting | 투자 가능 상품 개수            |
| startedAt     | Datetime | 투자 시작 시간               |
| finishedAt    | Datetime | 투자 종료 시간               |
| riskLevel     | String | 위험 레벨 (선택)             |
| rank          | Integer | 신용등급 (선택)              |
- 결과 : 201 CREATED

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| productId | Long | 상품 아이디 |
| title | Long | 상품명 |
- 에러코드

| HttpStatus | 에러코드 | 설명                                      |
| --- | --- |-----------------------------------------|
| 409 | E0012 | 중복 상품 name이 있을 경우 발생                    |
| 400 | E0013 | 입력받은 상품 type이 펀드(F) 혹은 신용투자(C)가 아닐경우 발생 |
- 결과 예시

```json
{
    "productId": 10,
    "title": "신용상품D"
}
```

### 8. 투자 취소

- 주문 아이디를 받아 해당 주문을 취소합니다.
- 주문이 취소될 경우 주문 상태는 CANCEL이 되고, 주문한 상품의 재고는 취소된 개수만큼 증가됩니다.
- API 기본 정보

| 메서드  | 요청URL         | 출력포맷 |
|------|---------------| --- |
| POST | /order/cancel | JSON |
- 요청 변수

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| orderId | Long | 주문 아이디 |
- 결과 : 200 OK
- 에러코드

| HttpStatus | 에러코드 | 설명 |
| --- | --- | --- |
| 404 | E0023 | 취소 요청한 주문아이디가 없을 때 발생 |
| 404 | E0025 | 취소 요청한 주문이 이미 취소된 주문일 경우 발생 |
- 결과 예시 : 없음

## 도메인 & 테이블 분석

- 도메인은 크게 회원(Member), 주문(Order), 상품(Item)으로 나뉜다.
- 회원과 주문은 1:N 양방향 관계, 주문과 상품은 1:1 단방향 관계를 갖는다.
- 두 관계의 연관관계 주인은 주문(Order)이 갖는다. (FK)

### 회원(Member)

- 회원 정보를 갖는 도메인으로 주소값은 Embedded type으로 City, Street, Zipcode로 구성되어 있다.
- 로직상 회원명이 같을 경우 중복 회원으로 정의한다.

### 주문(Order)

- 주문 정보를 갖는 도메인으로 order_id를 자체 키값으로 사용한다.
- 회원과 주문 1:N 양방향 연관관계, 주문과 상품 1:1 단방향 연관관계를 맺고, 두 연관관계에서 모두 연관관계의 주인이 된다. 그러므로 해당 테이블에서 member_id와 item_id값을 FK로 갖는다.

### 상품(Item)

- 상품 정보는 펀드 상품과 신용 투자 상품으로 나뉘어 진다. 해당 도메인은 Single table 전략을 사용하여 전체 상품을 하나의 테이블로 관리한다.
- 상위 도메인은 Item이고, 각각 펀드(Fund), 신용(Credit)으로 하위 도메인을 생성하여 상속관계를 갖는다. 향후 상품 종류가 추가될 경우 하위 도메인으로 추가하여 Item을 상속받는다.

![image](https://user-images.githubusercontent.com/6336815/166485017-effa969b-da8a-4105-8fb0-acdcaf1092dd.png)

## 문제 해결 전략

- 기본적인 Spring MVC 패턴을 사용했다.
- Controller에서 반환하는 Data는 Domain을 직접 사용하지 않고 DTO 생성하여 처리했다.
- Spring data JPA 활용을 위해 ItemRepository 와 OrderRepository는 아래와 같이 구성했다.

![image](https://user-images.githubusercontent.com/6336815/166473769-4e2e85bf-c570-486d-aedc-e50760707421.png)

### 1. 동시성 접근에 대한 제어 (두 번의 갱신 문제)

- 위 서비스의 핵심 비즈니스 로직은 `투자 상품이 오픈될 때, 다수의 고객이 동시에 투자를 한다`는 문제에 대한 해결이다.
- 투자상품의 오픈시간에 고객이 몰린다는 의미는 인기상품에 대한 경쟁이 있다는 뜻이고, 짧은 시간 내에 재고 부족(Sold out) 상태에 이를 것이다.
- 여기서 `재고 조회 → 재고 업데이트` 가 발생하는데 동시성 문제가 해결되지 않을 경우 여러 이용자의 주문에 대한 재고가 정상적으로 반영되지 않는다. 이를 `두 번의 갱신 문제`라 부른다.
- 서버에서는 동시 요청에 대한 처리와 재고에 대한 신뢰성 두가지 모두 만족시켜야 하는 상황으로 문제 정의에 따라 해결 전략을 다르게 선택할 수 있다.
    - JPA 영속성 컨텍스트를 이용
        - 낙관적 락(Optimistic Lock) : 조회→업데이트 사이에 다른 트랜잭션이 발생하지 않는다고 가정한다. 두 번의 갱신문제가 발생할 경우 최초 커밋만 인정하고, 나중에 들어온 업데이트 요청에 대해서는 예외가 발생한다. (실 서비스 적용 불가)
        - 비관적 락(Pessimistic Lock) : 조회→업데이트 사이에 다른 트랜잭션이 발생할 수 있다고 가정한다. 두 번의 갱신문제가 발생하지 않도록 최초 트랜잭션이 발생할 경우 LOCK을 걸고 이후 들어오는 트랜잭션은 대기한다. 해당 트랜잭션에 외부 연동이 있는 경우 외부 연동 결과에 따라 시스템에 심각한 위험을 가져올 수 있다. (각 트랜잭션이 타임아웃 시간만큼 무한정 대기)
    - **DB 트랜잭션의 Write lock 이용** : DB update 쿼리 트랜잭션 시 발생하는 데이터 lock을 이용한다. 아래와 같이 update query where절에 재고 조회 기능을 넣고, 재고 업데이트를 할 경우 해당 row는 DB 트랜잭션에 의해서 관리된다. 해당 서비스에서는 이 방법을 채택하였다.
    
    ```sql
    UPDATE ITEM
    SET STOCK_QUANTITY = STOCK_QUANTITY-{QUANTITY}
    WHERE ITEM_ID = {ITEM_ID}
    AND   STOCK_QUANTITY >= {QUANTITY}
    ```
    

### 2. 동일 요청에 대한 조회 속도 개선

- 해당 과제는 대규모 트래픽이 발생할 수 있음을 가정한다. 대부분의 조회성 트래픽은 동일 요청일 가능성이 높다
- 예를 들어 인기 투자상품의 재고가 없을 경우 누군가의 투자 취소로 인한 재고 확보 경쟁이 일어날 가능성이 높은데 사용자들은 이를 확인하기 위해 이미 재고가 0개인 상품에 대한 정보를 계속해서 요청하고, 이는 서버에 부하를 일으키게 된다.
- 위 문제를 해결하기 위해 Spring에서 제공하는 `Cache`를 사용했다. 캐시 구현체는 Ehcache를 사용했고, 주문, 주문취소, 상품 등록시 캐시를 삭제하고, 조회 로직 발생시 1차적으로 캐시를 확인하고 캐시가 없을 경우 DB에 접근한다.

```java
@Caching(evict = {@CacheEvict(value = "myOrder", key = "#memberId"),
            @CacheEvict(value = "myOrder", key = "#itemId")})
public Order order(Long memberId, Long itemId, Long count) { }
...

@Cacheable(value = "myOrder", key = "#itemId")
public ItemDto findOneItem(Long itemId) { }
```

### 3. AOP 를 이용한 에러 코드 처리

- `@RestControllerAdvice` 기능을 통해 Exception 발생시 미리 정의한 에러 코드가 반환되도록 구현하였다.
- 발생할 수 있는 Exception의 종류는 ENUM 값을 이용하여 정의했고, 에러 발생시 에러 코드와 함께 에러 메시지를 함께 반환한다.

```java
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
...
}
```

### 4. Log 사용 기준

- 해당 과제에서의 사용자 로그는 Info, Warn, Error 세 단계로 구분하였다. 각각 로그 레벨의 기준은 Frontend UI 단에서 기본 예외처리를 한다는 가정하에 아래와 같이 정의하였다.
    - Error : 해당 시스템에서 발생하지 않아야 하는 로직이 발생했을 경우이다. 해당 로그가 발생했을 경우 사용자의 비정상적인 접근이라 판단한다. (미등록 회원의 주문, 미등록 상품에 대한 주문, 투자 시작시간 전의 주문 등)
    - Warn : 일반적으로 시스템에서 발생하지 않아야 하는 로직이지만 특정 상황에서는 발생할 수 있는 경우이다. 해당 과제에서는 주문시간이 종료된 상품에 대한 주문이 발생했을 경우 해당 레벨의 로그를 사용했는데 사용자가 투자 종료시간 직전에 조회한 후 투자 종료시간 이후에 주문 프로세스를 탔을 경우 발생할 수 있다. 하지만 이 경우를 제외한 대부분의 상황은 사용자의 비정상 접근일 가능성이 높으므로 Warn 단계로 설정하였다.
    - Info : 시스템에서 발생할 수 있지만 통계 활용 등으로 관리자에게 편의성을 줄수 있는 정보인 경우 Info 레벨을 사용했다. 해당 레벨의 경우 throw exception 과 함께 발생하므로 서버에 부하가 갈 경우 삭제해도 무방하다.

## Test

- 테스트는 단위테스트와 통합테스트로 나누어서 수행했다. 단위 테스트는 Service layer에서 비즈니스 로직에 대한 테스트, 통합 테스트는 Controller layer에서 Rest API에 대한 테스트를 진행했다.
- 기본적인 테스트는 과제 요구사항을 모두 만족하도록 설계하였고, 추가로 정상동작을 확인하기 위한 테스트 항목을 추가하였다.

### 1. 단위테스트 (JUnit5)

- MemberService : 회원가입, 회원가입 시 중복 발생
- OrderService : 상품 주문, 주문 취소, 재고0 시 상품 상태 변경, 예외처리 (투자 시간, 재고 확인, 회원 확인, 상품 확인 등)
- ItemService : 상품 추가, 상품 조회(단건, 전체), 예외처리 등

### 2. 통합테스트

- POSTMAN 테스트 환경 ([링크](https://www.postman.com/crimson-sunset-229343/workspace/zzi-s-investment))
- 테스트 항목은 크게 5가지로 구분하였다.
    - (1) 사전 설정
    - (2) 주문
    - (3) 조회
    - (4) 에러
    - (5) 주문(동시시도)
- 기본적인 통합테스트는 아래와 같은 순서로 진행하였으나 필요한 Edge 케이스에 대한 테스트는 해당 항목을 수정하면서 수행하였다.

```java
1. (1)사전설정 전체 Run (회원등록, 아이템등록)
2. (2)주문, (1)조회 request 발생 후 결과 확인
3. (4)에러 케이스 확인
4. (5)주문(동시시도)를 이용해 iteration 1000회 후 재고 확인
```
