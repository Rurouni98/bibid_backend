package bibid.service.mypage;

import bibid.dto.MemberDto;
import org.springframework.web.multipart.MultipartFile;

public interface MypageService {
    MemberDto modify(MemberDto memberDto, MultipartFile[] uploadProfiles);

    MemberDto findById(long id);

    MemberDto findByMemberIndex(long memberIndex);

    MemberDto findByNickname(String nickname);
}
