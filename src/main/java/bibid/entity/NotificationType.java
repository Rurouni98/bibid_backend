package bibid.entity;

public enum NotificationType {
    AUCTION_START,         // 실시간 경매 시작 알림 30분 전에
    AUCTION_SOLD,          // 내가 올린 경매 낙찰
    HIGHER_BID,            // 상위 입찰자 등장
    AUCTION_WIN,           // 내가 낙찰자
    PURCHASE_CONFIRMATION, // 구매한 물품 정산 공지
    SALE_CONFIRMATION, // 판매된 물품 정산 공지
    SERVER_MAINTENANCE,    // 서버 점검
    EXCHANGE_NOTIFICATION,  // 환전 알림
    DEPOSIT_NOTIFICATION,   // 충전 알림
    DIRECT_MESSAGE         // 낙찰자와 판매자 간의 일대일 메시지
}
