package bibid.service.auction.impl;

import bibid.common.FileUtils;
import bibid.dto.AuctionDetailDto;
import bibid.dto.AuctionDto;
import bibid.dto.AuctionImageDto;
import bibid.entity.Auction;
import bibid.entity.AuctionDetail;
import bibid.entity.ChatRoom;
import bibid.entity.Member;
import bibid.repository.auction.AuctionDetailRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.service.livestation.LiveStationService;
import bibid.service.specialAuction.impl.SpecialAuctionScheduler;
import bibid.service.auction.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {
    private final SpecialAuctionScheduler specialAuctionScheduler;
    private final AuctionRepository auctionRepository;
    private final FileUtils fileUtils;
    private final AuctionDetailRepository auctionDetailRepository;

    @Override
    public Page<AuctionDto> post(AuctionDto auctionDto,
                                 AuctionDetailDto auctionDetailDto,
                                 MultipartFile thumbnail,
                                 MultipartFile[] additionalImages,
                                 Member member,
                                 Pageable pageable) {
        auctionDto.setRegdate(LocalDateTime.now());
        auctionDto.setModdate(LocalDateTime.now());
        auctionDto.setAuctionStatus("대기중");

        Auction auction = auctionDto.toEntity(member);
        AuctionDetail auctionDetail = auctionDetailDto.toEntity(auction);
        auction.setAuctionDetail(auctionDetail);

        if(auctionDto.getAuctionType().equals("실시간 경매")){
            ChatRoom chatRoom = ChatRoom.builder()
                    .roomName("경매 " + auctionDto.getProductName() + " 채팅방")
                    .createdAt(LocalDateTime.now())
                    .auction(auction)
                    .build();
            auction.setChatRoom(chatRoom);
        }

        if (thumbnail != null) {

            AuctionImageDto auctionImageDto = fileUtils.auctionImageParserFileInfo(thumbnail, "auction/thumbnail");
            auctionImageDto.setThumbnail(true);

            auction.getAuctionImageList().add(auctionImageDto.toEntity(auction));
        }

        if (additionalImages != null) {
            Arrays.stream(additionalImages).forEach(additionalImage -> {
                if(additionalImage.getOriginalFilename() != null &&
                        !additionalImage.getOriginalFilename().equalsIgnoreCase("")) {

                    AuctionImageDto auctionImageDto = fileUtils.auctionImageParserFileInfo(additionalImage, "auction/additionalImages");
                    auctionImageDto.setThumbnail(false);

                    auction.getAuctionImageList().add(auctionImageDto.toEntity(auction));
                }
            });
        }

        Auction savedAuction = auctionRepository.save(auction);

        if(auctionDto.getAuctionType().equals("실시간 경매")){
            specialAuctionScheduler.scheduleChannelAllocation(savedAuction.getAuctionIndex(), auctionDto.getStartingLocalDateTime());
            specialAuctionScheduler.scheduleChannelRelease(savedAuction.getAuctionIndex(), auctionDto.getStartingLocalDateTime());
            specialAuctionScheduler.scheduleAuctionEnd(savedAuction.getAuctionIndex(), auctionDto.getEndingLocalDateTime());
        }

        return auctionRepository.findAll(pageable).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findAll(Pageable pageable) {
        Pageable sortedByRegdate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("regdate").descending());
        return auctionRepository.findAllGeneralAuction(sortedByRegdate).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findTopByViewCount(Pageable pageable) {
        Pageable sortedByViewCount = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("viewCnt").descending());
        return auctionRepository.findBest(sortedByViewCount).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findByCategory(String category, Pageable pageable) {
        Pageable sortedByViewCount = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("viewCnt").descending());
        return auctionRepository.findByCategory(category, sortedByViewCount).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findByCategory2(String category, Pageable pageable) {
        Pageable sortedByRegdate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("regdate").descending());
        return auctionRepository.findByCategory2(category, sortedByRegdate).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findConveyor(Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable sortedByEndingLocalDateTime = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("endingLocalDateTime").descending());
        return auctionRepository.findConveyor(currentTime, sortedByEndingLocalDateTime).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> searchFind(String searchCondition, String searchKeyword, Pageable pageable) {
        Pageable sortedByRegdate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("regdate").descending());
        return auctionRepository.searchAll(searchCondition, searchKeyword, sortedByRegdate).map(Auction::toDto);
    }

    @Transactional
    @Override
    public void remove(Long auctionIndex) {
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new RuntimeException("Auction not found with index: " + auctionIndex));

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

//        // 경매 상태와 시작 시간을 확인하여 삭제 가능 여부 판단
//        if ("대기중".equals(auction.getAuctionStatus()) && auction.getStartingLocalDateTime().isAfter(now)) {
//            // AuctionDetail을 수동으로 삭제
//            if (auction.getAuctionDetail() != null) {
//                auctionDetailRepository.delete(auction);
//            }
//
//            // Auction 삭제
//            auctionRepository.delete(auction);
//        } else {
//            throw new RuntimeException("Auction cannot be deleted because it is either completed or has already started.");
//        }

        // AuctionDetail을 수동으로 삭제
        if (auction.getAuctionDetail() != null) {
            auctionDetailRepository.delete(auction);
        }

        // Auction 삭제
        auctionRepository.delete(auction);

    }
}
