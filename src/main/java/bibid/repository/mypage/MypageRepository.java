package bibid.repository.mypage;

import bibid.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MypageRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByNickname(String nickname);
}