package bibid.repository.member;

import bibid.dto.MemberDto;
import bibid.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);

    long countByMemberId(String memberId);

    long countByNickname(String nickname);

    Member findByEmail(String email);

    //    Optional<Member> findMemberByAuction_AuctionIndex(Long auctionIndex);
    @Query("SELECT a.member FROM Auction a WHERE a.auctionIndex = :auctionIndex")
    Optional<Member> findMemberByAuction_AuctionIndex(@Param("auctionIndex") Long auctionIndex);

    List<Member> findByMemberIndex(Long memberIndex);

    Member findByNickname(String nickname);

}
