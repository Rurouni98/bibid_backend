package bibid.entity;

import bibid.dto.AuctionDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@SequenceGenerator(
        name = "auctionSeqGenerator",
        sequenceName = "AUCTION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "auctionSeqGenerator"
    )
    private Long auctionIndex;

    // 경매 등록 멤버
    @ManyToOne
    @JoinColumn(name = "memberIndex")
    private Member member;

    private String auctionType; // 경매 타입 ('실시간 경매', '블라인드 경매', '일반 경매')
    private String category; // 카테고리
    private String subcategory; // 세부 카테고리 

    private String productName; // 물품 제목
    @Column(length = 1000)
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

    // 경매 이미지
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AuctionImage> auctionImageList;

    // 채팅방과의 비식별 1:1 관계
    @OneToOne(mappedBy = "auction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private ChatRoom chatRoom;

    // 경매 정보
    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AuctionInfo> auctionInfoList;

    // 경매세부
    @OneToOne(mappedBy = "auction", cascade = CascadeType.ALL)
    @JsonManagedReference
    private AuctionDetail auctionDetail;

    @OneToOne
    @JoinColumn(name = "liveStationChannelIndex")
    private LiveStationChannel liveStationChannel;

    // 검색 키워드
    @Transient
    private String searchKeyword;
    @Transient
    private String searchCondition;

    public AuctionDto toDto() {
        return AuctionDto.builder()
                .auctionIndex(this.auctionIndex)
                .memberIndex(this.member.getMemberIndex())
                .memberNickname(this.member.getNickname())
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
                .auctionImageDtoList(
                        Optional.ofNullable(auctionImageList).map(list -> list.stream().map(AuctionImage::toDto).toList())
                                .orElse(new ArrayList<>()))
                .auctionInfoDtoList(
                        Optional.ofNullable(auctionInfoList).map(list -> list.stream().map(AuctionInfo::toDto).toList())
                                .orElse(new ArrayList<>()))
                .auctionDetailDto(this.auctionDetail != null ? this.auctionDetail.toDto() : null)
                .searchKeyword(this.searchKeyword)
                .searchCondition(this.searchCondition)
                .build();
    }

}
