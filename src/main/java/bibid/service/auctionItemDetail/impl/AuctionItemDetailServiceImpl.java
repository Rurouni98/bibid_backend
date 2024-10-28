package bibid.service.auctionItemDetail.impl;

import bibid.dto.*;
import bibid.entity.Auction;
import bibid.entity.AuctionImage;
import bibid.entity.AuctionInfo;
import bibid.entity.Member;
import bibid.repository.AuctionImageRepository;
import bibid.repository.SellerInfoRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionItemDetailServiceImpl implements AuctionItemDetailService {

    private final AuctionRepository auctionRepository;
    private final AuctionInfoRepository auctionInfoRepository; // specialAuction 패키지 내부에 존재
    private final MemberRepository memberRepository;
    private final SellerInfoRepository sellerInfoRepository;
    private final AuctionImageRepository auctionImageRepository;

    @Override
    public AuctionDto findAuctionItem(Long auctionIndex) {
        return auctionRepository.findById(auctionIndex).orElseThrow(
                () -> new RuntimeException("not found auction ByIndex. : findAuctionItem")
        ).toDto();
    }

    @Override
    public AuctionInfoDto findAuctionBidInfo(Long auctionIndex) {
        System.out.println("findAUCITONBIDINFO->");
        return auctionInfoRepository.findByAuction_AuctionIndex(auctionIndex).orElseThrow(
                () -> new RuntimeException("not found auctionBidInfo : findAuctionBidInfo")
        ).toDto();
    }

    @Override
    public MemberDto findSeller(Long auctionIndex) {
        return memberRepository.findMemberByAuction_AuctionIndex(auctionIndex).orElseThrow(
                () -> new RuntimeException("not found Seller : findSeller")
        ).toDto();
    }

    @Override
    public List<AuctionInfoDto> findLastBidder(Long auctionIndex) {
        return auctionInfoRepository.findTop3ByAuction_AuctionIndexOrderByAuctionInfoIndexDesc(auctionIndex).stream().map(
                this::convertToDto
        ).toList();
    }
    private AuctionInfoDto convertToDto(AuctionInfo auctionInfo) {
        return auctionInfo.toDto();
    }

    @Override
    public List<MemberDto> findLastBidderName(List<AuctionInfoDto> auctionBidInfo) {
        return auctionBidInfo.stream()
                .flatMap(info -> memberRepository.findByMemberIndex(info.getBidderIndex()).stream())
                .map(Member::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<String> findAuctionInfoEtc(Long auctionIndex) {
        List<String> extension = new ArrayList<>();
        String bidCount = "" + auctionInfoRepository.countByAuction_AuctionIndex(auctionIndex);
        String maxNowPrice = "" + ( auctionInfoRepository.findMaxBidAmountByAuctionIndex(auctionIndex).orElse(
                (auctionRepository.findById(auctionIndex).get().getStartingPrice())
        ));
        String ownerBidCount = "" + (auctionRepository.countByMember_MemberIndex(
                findSeller(auctionIndex).getMemberIndex())
        );

        extension.add(bidCount);
        extension.add(maxNowPrice);
        extension.add(ownerBidCount);
        return extension;
    }

    @Override
    public SellerInfoDto findSellerInfo(Long auctionIndex) {
        return sellerInfoRepository.findByMember_MemberIndex(findSeller(auctionIndex).getMemberIndex()).toDto();
    }

    @Transactional
    @Override
    public AuctionInfoDto updateAuctionItemDetail(Long auctionIndex, BidRequestDto bidRequestDto) {
        AuctionInfo auctionInfo = new AuctionInfo();
        LocalDateTime currentTime = LocalDateTime.now();

        // 경매 객체 조회 및 할당
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("Auction not found with index: " + auctionIndex));
        auctionInfo.setAuction(auction);

        // 입찰자 정보 조회 및 할당
        auctionInfo.setBidder(
                memberRepository.findMemberByAuction_AuctionIndex(auctionIndex)
                        .orElseThrow(() -> new RuntimeException("Bidder not found for auction with index: " + auctionIndex))
        );

        // 입찰 시간 및 입찰 금액 설정
        auctionInfo.setBidTime(currentTime);
        auctionInfo.setBidAmount(bidRequestDto.getUserBiddingPrice());

        // 입찰 유형이 '즉시구매'일 경우 경매 상태를 '완료'로 설정하고 DB에 업데이트
        if (bidRequestDto.getUserBiddingType().equals("buyNow")) {
            auction.setAuctionStatus("완료");
            auctionRepository.save(auction); // 경매 상태를 "완료"로 갱신
        }

        // AuctionInfo 저장 및 DTO 반환
        auctionInfoRepository.save(auctionInfo);
        System.out.println(auction);
        return auctionInfo.toDto();
    }


    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateAuctionBiddingState() {
        auctionRepository.updateCompletedAuctions(LocalDateTime.now());
        auctionRepository.updateOngoingAuctions(LocalDateTime.now());
    }

    @Override
    public List<String> findAuctionImagesByAuctionIndex(Long auctionIndex) {

        List<String> imagePathList = new ArrayList<>();

        List<AuctionImage> auctionImages = auctionImageRepository.findByAuction_AuctionIndex(auctionIndex);

        auctionImages.stream()
                .filter(AuctionImage::isThumbnail)
                .findFirst()
                .ifPresent(thumbnail -> {
                    String fullPath = thumbnail.getFilepath() + thumbnail.getFilename();
                    imagePathList.add(fullPath);
                });

        // 리스트의 0번에 썸네일을 저장한 후에 나머지 일반 이미지를 리스트에 추가
        auctionImages.stream()
                .filter(image -> !image.isThumbnail())
                .forEach(image -> {
                    String fullPath = image.getFilepath() + image.getFilename();
                    imagePathList.add(fullPath);
                });

        return imagePathList;
    }

}
