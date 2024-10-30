package bibid.controller.specialAuction;

import bibid.service.specialAuction.RedisBidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@Slf4j
public class BidRestController {

    private final RedisBidService redisBidService;

    @PostMapping("/{auctionIndex}")
    public ResponseEntity<String> placeBid(@PathVariable Long auctionIndex, @RequestParam double bidAmount, @RequestParam String userId) {
        log.info("Received bid request - auction: {}, user: {}, amount: {}", auctionIndex, userId, bidAmount);
        redisBidService.placeBid(auctionIndex, bidAmount, userId);
        return ResponseEntity.ok("Bid placed successfully.");
    }

    @GetMapping("/{auctionIndex}/highest")
    public ResponseEntity<Double> getHighestBid(@PathVariable Long auctionIndex) {
        log.info("Fetching highest bid for auction {}", auctionIndex);
        return ResponseEntity.ok(redisBidService.getHighestBid(auctionIndex));
    }

    @GetMapping("/{auctionIndex}/all")
    public ResponseEntity<Set<String>> getAllBids(@PathVariable Long auctionIndex) {
        log.info("Fetching all bids for auction {}", auctionIndex);
        return ResponseEntity.ok(redisBidService.getAllBids(auctionIndex));
    }

}
