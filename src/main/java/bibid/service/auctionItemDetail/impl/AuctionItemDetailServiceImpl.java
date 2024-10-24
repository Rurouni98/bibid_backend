package bibid.service.auctionItemDetail.impl;

import bibid.dto.*;
import bibid.entity.AuctionInfo;
import bibid.entity.Member;
import bibid.entity.SellerInfo;
import bibid.repository.SellerInfoRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import lombok.RequiredArgsConstructor;
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
        String maxNowPrice = "" + ( auctionInfoRepository.findMaxBidAmountByAuctionIndex(auctionIndex).orElseThrow(
                () -> new RuntimeException("not found auctionMaxBidInfo : findAuctionInfoEtc")
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

    @Override
    public AuctionInfoDto updateAuctionItemDetail(Long auctionIndex, BidRequestDto bidRequestDto) {
        AuctionInfo auctionInfo = new AuctionInfo();

        System.out.println("updateAuctionItemDetail 실행 - setAuction");
        auctionInfo.setAuction(
                auctionRepository.findById(auctionIndex).orElseThrow(
                        () -> new RuntimeException("cant find auction to save auctionInfo")
                )
        );
        System.out.println("updateAuctionItemDetail 실행 - setBidder");
        auctionInfo.setBidder(
                memberRepository.findMemberByAuction_AuctionIndex(auctionIndex).orElseThrow(
                        () -> new RuntimeException("cant find bidder to save auctionInfo")
                )
        );
        auctionInfo.setBidTime(LocalDateTime.now());
        auctionInfo.setBidAmount(bidRequestDto.getUserBiddingPrice());

        if(bidRequestDto.getUserBiddingType().equals("즉시구매")){
            System.out.println("완료처리 로직 필요.");
        }

        System.out.println("nowwwwwwwww:" + auctionInfo.toDto());
        auctionInfoRepository.save(auctionInfo);
        return auctionInfo.toDto();
    }
}
