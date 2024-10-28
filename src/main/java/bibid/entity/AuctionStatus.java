package bibid.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
    READY("준비중"),        // 경매 준비 중
    WAITING("대기중"),      // 경매 대기 중
    BROADCASTING("방송중"), // 방송 중
    BID_COMPLETE("낙찰"),   // 낙찰 완료
    COMPLETED("경매종료");   // 경매 종료

    private final String status;
}