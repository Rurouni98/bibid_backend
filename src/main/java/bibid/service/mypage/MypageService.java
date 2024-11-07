package bibid.service.mypage;

import bibid.dto.AuctionDto;
import bibid.dto.MemberDto;
import bibid.dto.ProfileImageDto;
import bibid.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MypageService {
    MemberDto modify(MemberDto memberDto, MultipartFile[] uploadProfiles);

    MemberDto findById(long id);

    MemberDto findByMemberIndex(long memberIndex);

    MemberDto findByNickname(String nickname);

    List<AuctionDto> findBiddedAuctions(Long memberIndex);

    ProfileImageDto uploadOrUpdateProfileImage(MultipartFile file, Member member);

    List<AuctionDto> findMyAuctions(Long memberIndex);

    MemberDto updateProfile(Long memberIndex, MemberDto memberDto);
}
