package bibid.service.mypage;

import bibid.common.FileUtils;
import bibid.dto.AuctionDto;
import bibid.dto.MemberDto;
import bibid.dto.ProfileImageDto;
import bibid.entity.Auction;
import bibid.entity.AuctionInfo;
import bibid.entity.Member;
import bibid.entity.ProfileImage;

import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.repository.mypage.MypageProfileRepository;
import bibid.repository.mypage.MypageRepository;
import bibid.repository.mypage.ProfileImageRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageServiceImpl implements MypageService {
    private final FileUtils fileUtils;
    private final MypageRepository mypageRepository;
    private final MypageProfileRepository mypageProfileRepository;
    private final AuctionInfoRepository auctionInfoRepository;
    private final ProfileImageRepository profileImageRepository;
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;

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

        if (uploadProfiles != null && uploadProfiles.length > 0) {

            MultipartFile file = uploadProfiles[uploadProfiles.length - 1];
            if (!file.getOriginalFilename().equalsIgnoreCase("")
                    && file.getOriginalFilename() != null) {
                ProfileImageDto addProfileFileDto = fileUtils.parserFileInfo(file, "bitcamp121/");

                addProfileFileDto.setMemberIndex(memberDto.getMemberIndex());
                addProfileFileDto.setFilestatus("I");

                log.info("addProfileFileDto : {}", addProfileFileDto.toString());
                ProfileImage updateProfile = addProfileFileDto.toEntity(memberDto.toEntity());
                if (myPageProfile != null) {
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

    @Override
    public MemberDto findByMemberIndex(long memberIndex) {
        Member member = mypageRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 로그 추가
        log.info("Found member: {}", member);

        MemberDto memberDto = member.toDto();
        log.info("Converted memberDto: {}", memberDto);

        memberDto.setMemberPw(""); // 비밀번호를 숨김
        log.info("MemberDto after hiding password: {}", memberDto);

        return memberDto;
    }

    @Override
    public MemberDto findByNickname(String nickname) {
        Member member = mypageRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MemberDto memberDto = member.toDto();
        memberDto.setMemberPw(""); // 비밀번호를 숨김
        return memberDto;
    }

    @Override
    public List<AuctionDto> findBiddedAuctions(Long memberIndex) {
        log.info("Finding bidded auctions for memberIndex: {}", memberIndex);

        // AuctionInfo 엔티티를 통해 입찰 기록을 가져온 후, 동일 경매에 대한 중복을 제거하고 최신 입찰 기록만 유지
        List<AuctionInfo> biddedInfos = auctionInfoRepository.findByBidder_MemberIndex(memberIndex);

        // 중복 제거: 같은 경매에 대해 가장 최근의 입찰 기록만 남김
        Map<Long, AuctionInfo> latestBidsMap = new HashMap<>();
        for (AuctionInfo info : biddedInfos) {
            Long auctionIndex = info.getAuction().getAuctionIndex();
            if (!latestBidsMap.containsKey(auctionIndex) || info.getBidTime().isAfter(latestBidsMap.get(auctionIndex).getBidTime())) {
                latestBidsMap.put(auctionIndex, info);
            }
        }

        // 중복 제거 후, 최신 입찰 기록 리스트 생성
        List<AuctionInfo> latestBids = new ArrayList<>(latestBidsMap.values());

        log.info("Number of bidded auctions found: {}", biddedInfos.size());
        biddedInfos.forEach(info -> log.info("Auction Info: {}", info));

        return latestBids.stream()
                .map(auctionInfo -> auctionInfo.getAuction().toDto())
                .toList();
    }

    @Override
    public ProfileImageDto uploadOrUpdateProfileImage(MultipartFile profileImage, Member member) {

        // 기존 프로필 이미지 조회 또는 새로 생성
        ProfileImage prevProfileImage = profileImageRepository.findByMember(member)
                .orElse(new ProfileImage());

        if (profileImage != null && !profileImage.isEmpty()) {
            // parserFileInfo 메서드를 사용하여 파일 정보를 ProfileImageDto로 생성
            ProfileImageDto profileImageDto = fileUtils.parserFileInfo(profileImage, "mypage/profileImage");

            // ProfileImage 엔티티에 파일 정보 업데이트
            prevProfileImage.setFilepath(profileImageDto.getFilepath());
            prevProfileImage.setFiletype(profileImageDto.getFiletype());
            prevProfileImage.setFilesize(profileImageDto.getFilesize());
            prevProfileImage.setOriginalname(profileImageDto.getOriginalname());
            prevProfileImage.setNewfilename(profileImageDto.getNewfilename());
            prevProfileImage.setFilestatus("UPDATED");
        } else {
            throw new IllegalArgumentException("프로필 이미지 파일이 비어 있습니다.");
        }

        // Member와 연관 관계 설정
        prevProfileImage.setMember(member);

        // 데이터베이스에 프로필 이미지 저장
        ProfileImage savedProfileImage = profileImageRepository.save(prevProfileImage);

        // 저장된 프로필 이미지를 ProfileImageDto로 변환하여 반환
        return savedProfileImage.toDto();
    }

    @Override
    public List<AuctionDto> findMyAuctions(Long memberIndex) {
        log.info("Finding my auctions for memberIndex: {}", memberIndex);

        List<Auction> myAuctions = auctionRepository.findByMember_MemberIndex(memberIndex);
        log.info("Number of my auctions found: {}", myAuctions.size());

        return myAuctions.stream()
                .map(Auction::toDto)
                .toList();
    }

    @Override
    public MemberDto updateProfile(Long memberIndex, MemberDto memberDto) {
        // 사용자를 데이터베이스에서 조회
        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 사용자의 정보 업데이트
        member.setName(memberDto.getName());
        member.setNickname(memberDto.getNickname());
        member.setMemberPnum(memberDto.getMemberPnum());
        member.setEmail(memberDto.getEmail());
        member.setMemberAddress(memberDto.getMemberAddress());
        member.setAddressDetail(memberDto.getAddressDetail());

        // 변경된 사용자 정보 저장
        memberRepository.save(member);

        // 업데이트된 정보를 MemberDto로 변환하여 반환
        return member.toDto(); // toDto 메소드 사용
    }
}
