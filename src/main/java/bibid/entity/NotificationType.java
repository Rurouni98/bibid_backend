package bibid.entity;

public enum NotificationType {
    AUCTION_START,         // 실시간 경매 시작 알림
    SERVER_MAINTENANCE,    // 서버 점검
    AUCTION_SOLD,          // 내가 올린 경매 낙찰
    HIGHER_BID,            // 상위 입찰자 등장
    AUCTION_WIN,           // 내가 낙찰자
    DELIVERY_CONFIRMATION  // 판매자/낙찰자 간 물품 배송/수령
}
