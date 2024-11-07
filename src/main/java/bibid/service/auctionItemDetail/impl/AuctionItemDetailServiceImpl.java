package bibid.service.auctionItemDetail.impl;

import bibid.dto.*;
import bibid.entity.*;
import bibid.repository.account.AccountRepository;
import bibid.repository.account.AccountUseHistoryRepository;
import bibid.repository.auction.AuctionImageRepository;
import bibid.repository.member.SellerInfoRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import bibid.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionItemDetailServiceImpl implements AuctionItemDetailService {

    private final AuctionRepository auctionRepository;
    private final AuctionInfoRepository auctionInfoRepository; // specialAuction 패키지 내부에 존재
    private final MemberRepository memberRepository;
    private final SellerInfoRepository sellerInfoRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final AccountRepository accountRepository;
    private final AccountUseHistoryRepository accountUseHistoryRepository;
    private final NotificationService notificationService;

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
                .toList();
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
    public AuctionInfoDto updateAuctionItemDetail(Long auctionIndex, BidRequestDto bidRequestDto, Member member) {
        AuctionInfo auctionInfo = new AuctionInfo();
        LocalDateTime currentTime = LocalDateTime.now();

        // 경매 객체 조회 및 할당
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("Auction not found with index: " + auctionIndex));
        auctionInfo.setAuction(auction);

        // 현재 입찰자의 계좌 금액 확인 및 차감
        Account currentBidderAccount = accountRepository.findByMember_MemberIndex(member.getMemberIndex())
                .orElseThrow(() -> new RuntimeException("현재 입찰자의 계좌 정보를 찾을 수 없습니다."));
        int currentBalance = Integer.parseInt(currentBidderAccount.getUserMoney());
        int bidAmount = bidRequestDto.getUserBiddingPrice().intValue();

        // 계좌 금액 확인: 잔액이 입찰 금액보다 적으면 예외 발생
        if (currentBalance < bidAmount) {
            throw new RuntimeException("잔액이 부족합니다.");
        }

        // 입찰자 정보 조회 및 할당
        auctionInfo.setBidder(member);

        // 입찰 시간 및 입찰 금액 설정
        auctionInfo.setBidTime(currentTime);
        auctionInfo.setBidAmount(bidRequestDto.getUserBiddingPrice());
        auctionInfo.setBidderNickname(member.getNickname());

        // 이전 입찰자와 비교하여 higherBid, lowerBid 계산
        List<AuctionInfo> previousBids = auctionInfoRepository.findByAuctionOrderByBidTimeDesc(auction);

        Long higherBid = auctionInfo.getBidAmount();
        // 이전 입찰자 정보 가져오기 (최신 입찰 정보를 가져옴)
        AuctionInfo previousBidInfo = previousBids.isEmpty() ? null : previousBids.get(0);

        // 이전 입찰자가 있을 경우, 해당 입찰자의 입찰 금액 및 정보를 사용
        if (previousBidInfo != null) {
            Long lowerBid = previousBidInfo.getBidAmount();
            Account previousBidderAccount = accountRepository.findByMember_MemberIndex(previousBidInfo.getBidder().getMemberIndex())
                    .orElseThrow(() -> new RuntimeException("이전 입찰자의 계좌 정보를 찾을 수 없습니다."));
            Member previousHighestBidder = previousBidInfo.getBidder(); // 직전 입찰자 정보

            // 이전 입찰자의 금액 환불
            int previousBalance = Integer.parseInt(previousBidderAccount.getUserMoney());
            previousBidderAccount.setUserMoney(String.valueOf(previousBalance + lowerBid.intValue()));
            accountRepository.save(previousBidderAccount);
            log.info("이전 입찰자 {}에게 {} 원 환불 완료", previousHighestBidder.getNickname(), lowerBid);

            // AccountUseHistoryDto 생성 및 저장 (환불)
            AccountUseHistoryDto refundHistoryDto = AccountUseHistoryDto.builder()
                    .auctionType("일반 경매")
                    .accountIndex(previousBidderAccount.getAccountIndex())
                    .afterBalance(String.valueOf(previousBalance + lowerBid.intValue()))
                    .beforeBalance(String.valueOf(previousBalance))
                    .createdTime(currentTime)
                    .productName(auction.getProductName())
                    .changeAccount(String.valueOf(lowerBid))
                    .useType("반환")
                    .memberIndex(previousHighestBidder.getMemberIndex())
                    .auctionIndex(auctionIndex)
                    .build();
            accountUseHistoryRepository.save(refundHistoryDto.toEntity(previousHighestBidder, auction, previousBidderAccount));

            log.info("이전 입찰자 {}에게 {} 원 환불 완료 및 히스토리 기록", previousBidInfo.getBidder().getNickname(), lowerBid);

            // 현재 입찰자와 직전 입찰자가 다른 경우에만 알림 전송
            if (!previousHighestBidder.getNickname().equals(member.getNickname())) {
                // 알림 전송
                notificationService.notifyHigherBid(previousHighestBidder, auctionIndex, higherBid, lowerBid);
            } else{
                currentBidderAccount.setUserMoney(previousBidderAccount.getUserMoney());
                currentBalance = Integer.parseInt(currentBidderAccount.getUserMoney());
            }
        }

        currentBidderAccount.setUserMoney(String.valueOf(currentBalance - bidAmount));
        accountRepository.save(currentBidderAccount);
        log.info("현재 입찰자 {}의 계좌에서 {} 원 차감 완료", member.getNickname(), bidAmount);

        // AccountUseHistoryDto 생성 및 저장 (입찰)
        AccountUseHistoryDto bidHistoryDto = AccountUseHistoryDto.builder()
                .auctionType("일반 경매")
                .accountIndex(currentBidderAccount.getAccountIndex())
                .afterBalance(String.valueOf(currentBalance - bidAmount))
                .beforeBalance(String.valueOf(currentBalance))
                .createdTime(currentTime)
                .productName(auction.getProductName())
                .changeAccount(String.valueOf(bidAmount))
                .useType("입찰")
                .memberIndex(member.getMemberIndex())
                .auctionIndex(auctionIndex)
                .build();
        accountUseHistoryRepository.save(bidHistoryDto.toEntity(member, auction, currentBidderAccount));

        log.info("현재 입찰자 {}의 계좌에서 {} 원 차감 완료 및 히스토리 기록", member.getNickname(), bidAmount);

        // 입찰 유형이 '즉시구매'일 경우 경매 상태를 '완료'로 설정하고 DB에 업데이트
        if (bidRequestDto.getUserBiddingType().equals("buyNow")) {
            auction.setAuctionStatus("경매 완료");
            auctionRepository.save(auction); // 경매 상태를 "완료"로 갱신
        }

        // AuctionInfo 저장 및 DTO 반환
        auctionInfoRepository.save(auctionInfo);
        log.info("AuctionInfo 저장 완료 - Auction Index: {}, Bidder: {}, Bid Amount: {}", auctionIndex, member.getNickname(), bidAmount);

        return auctionInfo.toDto();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    @Override
    public void updateOngoingAuctions() {
        auctionRepository.updateOngoingAuctions(LocalDateTime.now(), "일반 경매");
        log.info("Updated ongoing auctions based on the current time");
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    @Override
    public void updateCompletedAuctionStatus() {

        LocalDateTime currentTime = LocalDateTime.now();


        List<Auction> completedAuctions = auctionRepository.findByEndingLocalDateTimeBeforeAndAuctionStatusAndAuctionType(
                currentTime, "경매 시작", "일반 경매");

        if (completedAuctions.isEmpty()) {
            log.info("No completed auctions to finalize. Skipping this cycle.");
            return;
        }

        for (Auction auction : completedAuctions) {
            finalizeAuction(auction.getAuctionIndex());
        }
    }

    @Transactional
    public void finalizeAuction(Long auctionIndex) {
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매를 찾을 수 없습니다. ID: " + auctionIndex));

        if (auction.getEndingLocalDateTime().isBefore(LocalDateTime.now())) {
            // 최고 입찰 정보 조회
            AuctionInfo lastBidInfo = auction.getAuctionInfoList()
                    .stream()
                    .max(Comparator.comparing(AuctionInfo::getBidTime))
                    .orElse(null);

            if (lastBidInfo != null) {
                // 낙찰자 정보 및 상세 정보 설정
                AuctionDetail auctionDetail = auction.getAuctionDetail();
                auctionDetail.setWinnerIndex(lastBidInfo.getBidder().getMemberIndex());
                auctionDetail.setWinningBid(lastBidInfo.getBidAmount());
                auctionDetail.setWinnerNickname(lastBidInfo.getBidder().getNickname());

                // 기존의 AccountUseHistory에서 경매 인덱스와 일치하는 가장 높은 금액의 입찰 기록 찾기
                Account account = lastBidInfo.getBidder().getAccount();
                AccountUseHistory highestBidHistory = account.getAccountUseHistoryList()
                        .stream()
                        .filter(history -> history.getAuction() != null && // auction이 null이 아닌 경우만 필터링
                                history.getAuction().getAuctionIndex().equals(auctionIndex) &&
                                history.getUseType().equals("입찰"))
                        .max(Comparator.comparing(AccountUseHistory::getChangeAccount)) // 가장 높은 금액의 입찰 기록 찾기
                        .orElseThrow(() -> new RuntimeException("입찰 기록을 찾을 수 없습니다."));

                // useType을 '낙찰'로 변경
                highestBidHistory.setUseType("낙찰");
                accountUseHistoryRepository.save(highestBidHistory); // 변경 사항 저장

                auction.setAuctionStatus("낙찰"); // 상태를 '낙찰'로 변경

                // 낙찰자와 판매자에게 알림 전송
                notificationService.notifyAuctionWin(lastBidInfo.getBidder(), auctionIndex);
                notificationService.notifyAuctionSold(auction.getMember(), lastBidInfo, auctionIndex);

                log.info("Auction finalized with winner for auction ID: {}, winner ID: {}, winning bid: {}",
                        auctionIndex, lastBidInfo.getBidder().getMemberIndex(), lastBidInfo.getBidAmount());
            } else {
                // 입찰 정보가 없을 경우 유찰 처리
                auction.setAuctionStatus("유찰"); // 경매 상태를 '유찰'로 설정
                log.info("Auction finalized without winner for auction ID: {}, status set to '유찰'", auctionIndex);
            }

            auctionRepository.save(auction); // 업데이트된 정보 저장
        }
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

    // 유효한 일반경매 요청인지 확인하는 메서드
    @Override
    public String auctionChecking(Long auctionIndex) {
        String auctionChecking;

        // 옥션 조회
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionIndex);

        // 옥션이 존재하는지 확인
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            // 옥션 타입이 "일반 경매"인지 확인
            if (auction.getAuctionType().equals("일반 경매")) {
                auctionChecking = "접속성공. 옥션번호 : " + auctionIndex;
            } else {
                auctionChecking = "잘못된 접근입니다.-잘못된 옥션접근";
            }
        } else {
            auctionChecking = "잘못된 접근입니다.-존재하지 않는 옥션";
        }

        return auctionChecking;
    }

    @Override
    public void plusAuctionView(Long auctionIndex) {
//        auctionRepository.updateAuctionViewCnt(auctionIndex);
    }

    @Override
    public String biddingItem(Long auctionIndex, BidRequestDto bidRequestDto, Long memberIndex) {
        return "";
    }

    // 맴버 인덱스도 추가해주어야됨
    @Override
    public String biddingItem(Long auctionIndex, BidRequestDto bidRequestDto, MemberDto memberDto) {

        log.info("auctionIndex : {}", auctionIndex);

        Account account = accountRepository.findByMember_MemberIndex(memberDto.getMemberIndex())
                .orElseThrow( () -> new RuntimeException("account not exist"));

        // 유저 계좌조회
        if (

        Integer.parseInt(account.getUserMoney()) > 0
        ){
            // accountUseHistory 처리
            // 조회한 accountUseHistory 객체에 auction_auctionIndex 와
            // bidRequestDto 의 userBiddingItemName +" 경매 "+ userBiddingPrice (수수료 미포함) + " 원 입찰"
            // setUseType = 구매
//            accountUseHistoryRepository.findByMember_MemberIndex(memberDto.getMemberIndex());

            return "success";
        } else {
            log.error("잔액부족입니다. 잔액을 충전하세요.");
            return "fail";
        }
        // useType : "일반경매" + {입찰/즉시구매} "-" + bidRequestDto.userBiddingPrice
        // useType 필드에 입찰가 만 보여지며 changeAccount 에 차액 증감 보여주기
    }

}
