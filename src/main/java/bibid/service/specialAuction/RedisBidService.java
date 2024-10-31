package bibid.service.specialAuction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisBidService {

    private final RedisTemplate<String, String> redisTemplate;

    // 입찰 추가 (입찰가와 사용자 ID)
    public void placeBid(Long auctionIndex, double bidAmount, String userId) {
        String redisKey = "auction:bid:" + auctionIndex;

        // 입찰 금액을 Sorted Set에 추가
        redisTemplate.opsForZSet().add(redisKey, userId, bidAmount);
        log.info("Bid placed for auction {} by user {}: {} (bidAmount: {})", auctionIndex, userId, redisKey, bidAmount);
    }

    // 최고 입찰가 가져오기
    public Double getHighestBid(Long auctionIndex) {
        String redisKey = "auction:bid:" + auctionIndex;

        // Sorted Set에서 가장 높은 점수 (입찰가)를 가져옴
        Set<String> highestBid = redisTemplate.opsForZSet().reverseRangeByScore(redisKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 1);
        if (highestBid != null && !highestBid.isEmpty()) {
            Double highestBidAmount = redisTemplate.opsForZSet().score(redisKey, highestBid.iterator().next());
            log.info("Highest bid for auction {}: {} (amount: {})", auctionIndex, highestBid.iterator().next(), highestBidAmount);
            return highestBidAmount;
        }
        log.info("No bids found for auction {}", auctionIndex);
        return null;
    }

    // 모든 입찰 기록 조회
    public Set<String> getAllBids(Long auctionIndex) {
        String redisKey = "auction:bid:" + auctionIndex;
        Set<String> allBids = redisTemplate.opsForZSet().reverseRange(redisKey, 0, -1);
        log.info("All bids for auction {}: {}", auctionIndex, allBids);
        return allBids;
    }

}
