package bibid.repository.mypage;

import bibid.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MypageProfileRepository extends JpaRepository<ProfileImage, Long> {
}