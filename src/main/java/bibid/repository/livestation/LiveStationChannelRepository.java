package bibid.repository.livestation;

import bibid.entity.LiveStationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiveStationChannelRepository extends JpaRepository<LiveStationChannel, Long> {

    // YouTube 스트림 키로 채널 조회 (기존의 channelId 대체)
    Optional<LiveStationChannel> findByYoutubeStreamKey(String youtubeStreamKey);

    // 사용 가능하지 않은 채널 조회
    List<LiveStationChannel> findAllByIsAvailableFalse();

    // 사용 가능 + 미할당된 채널 목록 조회 (우선순위 높은 순 정렬)
    List<LiveStationChannel> findAllByIsAllocatedFalseAndIsAvailableTrueOrderByLiveStationChannelIndexAsc();

    // YouTube 시청 URL로 채널 조회 (옵션: 시청 URL로 접근하는 경우에 대비)
    Optional<LiveStationChannel> findByYoutubeWatchUrl(String youtubeWatchUrl);

    // YouTube 송출 주소로 채널 조회 (특정 서버 송출 주소 기반으로 찾고 싶을 때)
    Optional<LiveStationChannel> findByYoutubeStreamUrl(String youtubeStreamUrl);
}
