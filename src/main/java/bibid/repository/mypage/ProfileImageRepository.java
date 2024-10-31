package bibid.repository.mypage;

import bibid.entity.Member;
import bibid.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByMember(Member member);
}
