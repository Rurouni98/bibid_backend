package bibid.service;



import bibid.dto.MemberDto;

import java.util.Map;

public interface MemberService {
    Map<String, String> memberIdCheck(String memberId);

    Map<String, String> nicknameCheck(String nickname);

    MemberDto join(MemberDto memberDto);

    MemberDto login(MemberDto memberDto);
}
