package bibid.service.auction;

import bibid.dto.AuctionDetailDto;
import bibid.dto.AuctionDto;
import bibid.entity.Auction;
import bibid.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuctionService {
    Page<AuctionDto> post(AuctionDto auctionDto, AuctionDetailDto auctionDetailDto, MultipartFile thumbnail, MultipartFile[] additionalImages, Member member, Pageable pageable);

    Page<AuctionDto> findTopByViewCount(Pageable pageable);

    Page<AuctionDto> findAll(Pageable pageable);

    Page<AuctionDto> findByCategory(String category, Pageable pageable);

    Page<AuctionDto> findByCategory2(String category, Pageable pageable);

    Page<AuctionDto> findConveyor(Pageable pageable);

    Page<AuctionDto> searchFind(String searchCondition, String searchKeyword, Pageable pageable);

    void remove(Long auctionIndex);
}
