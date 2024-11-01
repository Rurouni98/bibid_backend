package bibid.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
    WAITING("대기중"),                // 경매 대기 중 (모든 경매 초기 상태)
    READY("준비중"),                  // 실시간 경매가 30분 전에 준비 중 상태로 변경
    BROADCASTING("방송중"),           // 실시간 경매 방송 중

    STARTED("경매 시작"),             // 경매 시작 (일반 경매만)

    BID_COMPLETE("낙찰"),             // 입찰 성공, 낙찰 완료
    AUCTION_FAILED("유찰"),           // 유찰

    SHIPPING("배송중"),               // 배송 중 상태
    DELIVERY_COMPLETED("배송 완료"),  // 배송 완료 상태
    AUCTION_COMPLETED("경매 완료");   // 경매가 완료되었을 때


    private final String status;
}
