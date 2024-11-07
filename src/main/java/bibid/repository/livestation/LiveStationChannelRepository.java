package bibid.repository.livestation;

import bibid.entity.LiveStationChannel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LiveStationChannelRepository extends JpaRepository<LiveStationChannel, Long> {

    // 채널 ID로 채널을 찾을 때 serviceUrlList도 함께 로드
    @Query("SELECT c FROM LiveStationChannel c LEFT JOIN FETCH c.serviceUrlList WHERE c.channelId = :channelId")
    Optional<LiveStationChannel> findByChannelId(@Param("channelId") String channelId);

    // 사용 가능하지 않은 모든 채널과 관련된 serviceUrlList를 함께 로드
    @Query("SELECT c FROM LiveStationChannel c LEFT JOIN FETCH c.serviceUrlList WHERE c.isAvailable = false")
    List<LiveStationChannel> findAllByIsAvailableFalse();

    // 할당되지 않았고 사용 가능한 첫 번째 채널을 serviceUrlList와 함께 로드
    @Query("SELECT c FROM LiveStationChannel c LEFT JOIN FETCH c.serviceUrlList WHERE c.isAllocated = false AND c.isAvailable = true ORDER BY c.channelId")
    List<LiveStationChannel> findAllByIsAllocatedFalseAndIsAvailableTrue();

}
