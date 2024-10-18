package bibid.service.auction.impl;

import bibid.common.FileUtils;
import bibid.dto.AuctionDetailDto;
import bibid.dto.AuctionDto;
import bibid.dto.AuctionImageDto;
import bibid.entity.Auction;
import bibid.entity.AuctionDetail;
import bibid.entity.ChatRoom;
import bibid.entity.Member;
import bibid.livestation.service.LiveStationService;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.specialAuction.StreamingRepository;
import bibid.service.auction.AuctionService;
import bibid.service.specialAuction.ChatRoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final FileUtils fileUtils;

    @Override
    public Page<AuctionDto> post(AuctionDto auctionDto,
                                 AuctionDetailDto auctionDetailDto,
                                 MultipartFile thumbnail,
                                 MultipartFile[] additionalImages,
                                 Member member,
                                 Pageable pageable) {
        auctionDto.setRegdate(LocalDateTime.now());
        auctionDto.setModdate(LocalDateTime.now());

        Auction auction = auctionDto.toEntity(member);
        AuctionDetail auctionDetail = auctionDetailDto.toEntity(auction);
        auction.setAuctionDetail(auctionDetail);

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

        auctionRepository.save(auction);

        return auctionRepository.findAll(pageable).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findAll(Pageable pageable) {
        return auctionRepository.findAll(pageable).map(Auction::toDto);
    }

    @Override
    public Page<AuctionDto> findTopByViewCount(Pageable pageable) {
        Pageable sortedByViewCount = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("viewCnt").descending());
        return auctionRepository.findAll(sortedByViewCount).map(Auction::toDto);
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
        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간을 가져옵니다.
        Pageable sortedByEndingLocalDateTime = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("endingLocalDateTime").descending());
        return auctionRepository.findConveyor(currentTime, sortedByEndingLocalDateTime).map(Auction::toDto);
    }
}
