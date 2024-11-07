package bibid.service.member;

import bibid.dto.MemberDto;
import bibid.entity.Account;
import bibid.entity.Member;
import bibid.entity.SellerInfo;
import bibid.repository.member.SellerInfoRepository;
import bibid.repository.account.AccountRepository;
import bibid.repository.member.MemberRepository;
import bibid.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final SellerInfoRepository sellerInfoRepository;
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;
    private Optional<Member> optionalMember = Optional.empty();

    @Override
    public Map<String, String> memberIdCheck(String memberId) {
        Map<String, String> memberIdCheckMsgMap = new HashMap<>();

        long memberIdCheck = memberRepository.countByMemberId(memberId);

        if(memberIdCheck == 0)
            memberIdCheckMsgMap.put("memberIdCheckMsg", "available memberId");
        else
            memberIdCheckMsgMap.put("memberIdCheckMsg", "invalid memberId");

        return memberIdCheckMsgMap;
    }

    @Override
    public Map<String, String> nicknameCheck(String nickname) {
        Map<String, String> nicknameCheckMsgMap = new HashMap<>();

        long nicknameCheck = memberRepository.countByNickname(nickname);

        if(nicknameCheck == 0)
            nicknameCheckMsgMap.put("nicknameCheckMsg", "available nickname");
        else
            nicknameCheckMsgMap.put("nicknameCheckMsg", "invalid nickname");

        return nicknameCheckMsgMap;
    }

    @Override
    public MemberDto join(MemberDto memberDto) {
        memberDto.setRole("ROLE_USER");
        memberDto.setMemberPw(passwordEncoder.encode(memberDto.getMemberPw()));

        // Member 엔티티 생성
        Member member = memberDto.toEntity();

        // SellerInfo와 Account를 생성하여 Member에 설정
        SellerInfo sellerInfo = SellerInfo.builder()
                .member(member)
                .build();
        member.setSellerInfo(sellerInfo);

        Account account = Account.builder()
                .member(member)
                .userMoney("1000000")
                .build();
        member.setAccount(account);

        // Member 저장 (Cascade로 인해 SellerInfo와 Account도 저장됨)
        Member joinedMember = memberRepository.save(member);

        // 반환할 Dto 설정
        MemberDto joinedMemberDto = joinedMember.toDto();
        joinedMemberDto.setMemberPw("");  // 비밀번호를 빈 문자열로 설정

        return joinedMemberDto;
    }

    @Override
    public MemberDto login(MemberDto memberDto) {
        Member member = memberRepository.findByMemberId(memberDto.getMemberId()).orElseThrow(
                () -> new RuntimeException("memberId not exist")
        );

        if(!passwordEncoder.matches(memberDto.getMemberPw(), member.getMemberPw())) {
            throw new RuntimeException("wrong memberPw");
        }

        MemberDto loginMember = member.toDto();

        loginMember.setMemberPw("");
        loginMember.setRememberMe(memberDto.getRememberMe());

        return loginMember;
    }

    @Override
    public String findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            return member.getMemberId();
        } else {
            System.out.println("이메일에 해당하는 사용자가 없습니다.");
            return null;
        }
    }



    @Override
    public String modifyPasswd(String newPasswd) {

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setMemberPw(passwordEncoder.encode(newPasswd));

            memberRepository.save(member);
            return "비밀번호 변경을 완료했습니다.";
        } else {
            System.out.println("이메일에 해당하는 사용자가 없습니다.");
            return null;
        }
    }

    @Override
    public Member getMemberByMemberIndex(Long memberIndex) {
        return memberRepository.findById(memberIndex).orElseThrow(
                () -> new RuntimeException("member not exist")
        );
    }

}