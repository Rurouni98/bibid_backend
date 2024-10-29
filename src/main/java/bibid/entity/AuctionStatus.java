package bibid.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
    WAITING("대기중"),            // 경매 대기 중 (모든 경매 초기 상태)
    READY("준비중"),              // 실시간 경매가 30분 전에 준비 중 상태로 변경
    BROADCASTING("방송중"),       // 실시간 경매 방송 중
    BIDDING("경매중"),            // 일반 경매 입찰 중
    BID_COMPLETE("낙찰"),         // 입찰 성공, 낙찰 완료
    AUCTION_COMPLETED("경매종료"), // 낙찰된 경매가 종료
    AUCTION_FAILED("유찰"),       // 유찰
    BROADCAST_ENDED("방송종료");   // 실시간 경매 방송 종료

    private final String status;
}