package bibid.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountUseType {

    EXCHANGE("환전"),
    CHARGE("충전"),
    BID("입찰"),
    PAYMENT("낙찰"), // 낙찰자가 대금을 지급하는 경우
    REFUND("반환"),
    RECEIVE_PAYMENT("대금 수령"); // 판매자가 대금을 수령하는 경우

    private final String koreanName;
}
