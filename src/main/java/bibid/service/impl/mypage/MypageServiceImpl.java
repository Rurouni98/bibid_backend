package bibid.service.impl.mypage;

import bibid.common.FileUtils;
import bibid.dto.MemberDto;
import bibid.dto.MypageProfileFileDto;
import bibid.entity.Member;
import bibid.repository.mypage.MypageRepository;
import bibid.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageServiceImpl implements MypageService {
    private final FileUtils fileUtils;
    private final MypageRepository mypageRepository;

    @Override
    public MemberDto modify(MemberDto memberDto, MultipartFile[] uploadProfiles) {
        List<MypageProfileFileDto> myPageFileList = new ArrayList<>();

        if(uploadProfiles != null && uploadProfiles.length > 0) {
            Arrays.stream(uploadProfiles).forEach(file -> {
                if(!file.getOriginalFilename().equalsIgnoreCase("")
                    && file.getOriginalFilename() != null){
                    MypageProfileFileDto addMyageProfileFileDto = fileUtils.parserFileInfo(file, "bibid_profile/");

                    addMyageProfileFileDto.setProfile_id(memberDto.getMemberIndex());
                    addMyageProfileFileDto.setFilestatus("I");

                    myPageFileList.add(addMyageProfileFileDto);
                }
            });
        }

        Member member = mypageRepository.findById(memberDto.getMemberIndex()).orElseThrow(
                () -> new RuntimeException("Member not found")
        );

        log.info("member info : " + member.toString());
        return null;
    }

    @Override
    public MemberDto findById(long id) {
        MemberDto findMember = mypageRepository.findById(id).orElseThrow(() -> new RuntimeException("member not found")).toDto();
        findMember.setMemberPw("");
        return findMember;
    }
}
