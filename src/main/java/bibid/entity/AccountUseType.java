package bibid.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountUseType {
    PURCHASE("구매"),
    SALE("판매"),
    EXCHANGE("환전"),
    CHARGE("충전");

    private final String koreanName;
}
