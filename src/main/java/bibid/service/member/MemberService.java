package bibid.service.member;



import bibid.dto.MemberDto;
import bibid.entity.Member;

import java.util.Map;

public interface MemberService {
    Map<String, String> memberIdCheck(String memberId);

    Map<String, String> nicknameCheck(String nickname);

    MemberDto join(MemberDto memberDto);

    MemberDto login(MemberDto memberDto);

    String findByEmail(String email);

    String modifyPasswd(String newPasswd);

    Member getMemberByMemberIndex(Long memberIndex);
}
