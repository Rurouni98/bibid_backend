package bibid.repository.livestation;

import bibid.entity.LiveStationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiveStationChannelRepository extends JpaRepository<LiveStationChannel, Long> {
    Optional<LiveStationChannel> findByChannelId(String channelId);

    Optional<LiveStationChannel> findFirstByIsAvailableTrue();

    List<LiveStationChannel> findAllByIsAvailableFalse();

    Optional<LiveStationChannel> findFirstByIsAvailableTrueAndChannelStatusAndCdnStatusName(String channelStatus, String cdnStatusName);

    Optional<LiveStationChannel> findFirstByIsAllocatedFalse();
}
