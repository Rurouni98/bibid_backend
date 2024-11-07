package bibid.dto;

import bibid.entity.*;
import jakarta.persistence.Transient;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class AuctionDto {
    private Long auctionIndex;
    private Long memberIndex;
    private String memberNickname;
    private String auctionType; // 경매 타입 ('실시간 경매', '블라인드 경매', '일반 경매')
    private String category; // 카테고리
    private String subcategory; // 세부 카테고리
    private String productName; // 물품 제목
    private String productDescription; // 물품 설명
    private Long startingPrice; // 경매 시작가
    private LocalDateTime startingLocalDateTime; // 경매시작시간
    private LocalDateTime endingLocalDateTime; // 경매종료시간
    private Long bidIncrement; // 입찰 단위

    private Boolean instantPurchaseEnabled; // 즉시 구매 가능 여부
    private Long instantPurchasePrice; // 즉시 구매가격
    private Boolean autoReauctionEnabled; // 자동재경매 여부
    private Long reauctionStartingPrice; // 재경매 시작가
    private String auctionStatus; // 경매 상태 ('준비중', '진행중', '낙찰', '유찰', '완료')
    private Long viewCnt; // 조회수

    private LocalDateTime regdate; // 경매 등록시간
    private LocalDateTime moddate; // 경매 수정시간

    private List<AuctionImageDto> auctionImageDtoList;
    private List<AuctionInfoDto> auctionInfoDtoList;
    private AuctionDetailDto auctionDetailDto;

    private String searchKeyword;
    private String searchCondition;

    public Auction toEntity(Member member) {
        return Auction.builder()
                .auctionIndex(this.auctionIndex)
                .member(member)
                .auctionType(this.auctionType)
                .category(this.category)
                .subcategory(this.subcategory)
                .productName(this.productName)
                .productDescription(this.productDescription)
                .startingPrice(this.startingPrice)
                .startingLocalDateTime(this.startingLocalDateTime)
                .endingLocalDateTime(this.endingLocalDateTime)
                .bidIncrement(this.bidIncrement)
                .instantPurchaseEnabled(this.instantPurchaseEnabled)
                .instantPurchasePrice(this.instantPurchasePrice)
                .autoReauctionEnabled(this.autoReauctionEnabled)
                .reauctionStartingPrice(this.reauctionStartingPrice)
                .auctionStatus(this.auctionStatus)
                .viewCnt(this.viewCnt)
                .regdate(this.regdate)
                .moddate(this.moddate)
                .auctionImageList(new ArrayList<>())
                .chatRoom(null)
                .auctionDetail(null)
                .searchKeyword(this.searchKeyword)
                .searchCondition(this.searchCondition)
                .build();
    }


}
