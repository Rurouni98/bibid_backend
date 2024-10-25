package bibid.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionType {

    REALTIME("실시간 경매"),
    BLIND("블라인드 경매"),
    GENERAL("일반 경매");

    private final String koreanName;
}
