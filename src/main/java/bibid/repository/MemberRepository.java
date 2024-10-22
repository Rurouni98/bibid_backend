package bibid.repository;

import bibid.dto.MemberDto;
import bibid.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);

    long countByMemberId(String memberId);

    long countByNickname(String nickname);

    Optional<Member> findByEmail(String email);


}
