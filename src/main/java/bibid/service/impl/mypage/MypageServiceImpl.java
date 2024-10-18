package bibid.service.impl.mypage;

import bibid.common.FileUtils;
import bibid.dto.MemberDto;
import bibid.dto.ProfileImageDto;
import bibid.entity.Member;
import bibid.entity.ProfileImage;

import bibid.repository.mypage.MypageProfileRepository;
import bibid.repository.mypage.MypageRepository;
import bibid.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageServiceImpl implements MypageService {
    private final FileUtils fileUtils;
    private final MypageRepository mypageRepository;
    private final MypageProfileRepository mypageProfileRepository;

    @Override
    public MemberDto modify(MemberDto memberDto, MultipartFile[] uploadProfiles) {
        // 기존 회원 정보를 가져옵니다.
        Member existingMember = mypageRepository.findById(memberDto.getMemberIndex()).orElseThrow(
                () -> new RuntimeException("Member not found")
        );

//        ProfileImage myPageProfile = null;
//        if(memberDto.getProfileImage() != null){
//            myPageProfile = memberDto.getProfileImage();
//        }
        existingMember.setMemberPw(memberDto.getMemberPw());
        existingMember.setMemberPnum(memberDto.getMemberPnum());
        existingMember.setEmail(memberDto.getEmail());
        existingMember.setMemberAddress(memberDto.getMemberAddress());
        existingMember.setAddressDetail(memberDto.getAddressDetail());

        ProfileImage myPageProfile = existingMember.getProfileImage();

        if(uploadProfiles != null && uploadProfiles.length > 0) {

            MultipartFile file = uploadProfiles[uploadProfiles.length - 1];
            if(!file.getOriginalFilename().equalsIgnoreCase("")
                    && file.getOriginalFilename() != null){
                ProfileImageDto addProfileFileDto = fileUtils.parserFileInfo(file, "bitcamp73/");

                addProfileFileDto.setMemberIndex(memberDto.getMemberIndex());
                addProfileFileDto.setFilestatus("I");

                log.info("addProfileFileDto : {}", addProfileFileDto.toString());
                ProfileImage updateProfile = addProfileFileDto.toEntity(memberDto.toEntity());
                if(myPageProfile != null){
                    myPageProfile.setFilepath(addProfileFileDto.getFilepath());
                    myPageProfile.setFilesize(addProfileFileDto.getFilesize());
                    myPageProfile.setFiletype(addProfileFileDto.getFiletype());
                    myPageProfile.setOriginalname(addProfileFileDto.getOriginalname());
                    myPageProfile.setNewfilename(addProfileFileDto.getNewfilename());
                    myPageProfile.setFilestatus("U");
//                    addProfileFileDto.setFilestatus("U");
//                    myPageProfile = addProfileFileDto.toEntity(memberDto.toEntity());
//                    existingMember.setProfileImage(myPageProfile);
                    mypageProfileRepository.save(myPageProfile);
                } else {
//                    myPageProfile = updateProfile;
//                    existingMember.setProfileImage(myPageProfile);
                    myPageProfile = addProfileFileDto.toEntity(memberDto.toEntity());
                    existingMember.setProfileImage(myPageProfile);
                }
            }
        }
        mypageRepository.save(existingMember);
        return existingMember.toDto();
    }

    @Override
    public MemberDto findById(long id) {
        MemberDto findMember = mypageRepository.findById(id).orElseThrow(() -> new RuntimeException("member not found")).toDto();
        findMember.setMemberPw("");
        return findMember;
    }
}
