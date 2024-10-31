package bibid.service.qna;


import bibid.dto.QnADto;
import bibid.repository.qna.QnaRepository;
import bibid.repository.member.MemberRepository;
import bibid.service.auctionItemDetail.AuctionItemDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaServiceImpl implements QnaService{

    private final QnaRepository qnaRepository;
    private final AuctionItemDetailService auctionItemDetailService;
    private final MemberRepository memberRepository;

    @Override
    public List<QnADto> findQnaListByAuctionIndex(Long auctionIndex) {
        return qnaRepository.findByAuction_AuctionIndex(auctionIndex).stream().map(qna -> {
            QnADto dto = qna.toDto();
            String nickname = memberRepository.findById(qna.getMember().getMemberIndex())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."))
                    .getNickname();
            dto.setNickname(nickname);
            return dto;
        }).toList();
    }


    @Override
    public void postQnA(QnADto qnADto) {
        qnADto.setRegDate(LocalDateTime.now());
        System.out.println("임의로 만든 옥션이라 auctionIndex 설정이 안되는듯 함 loginMember 받아서 옥션만들고 문의해보면 될듯함");
        System.out.println(qnADto);
        try {
            qnaRepository.save(
                    qnADto.toEntity(memberRepository.findByMemberIndex(qnADto.getMemberIndex()).get(0))
            );
        }catch (Exception e){
            log.error("postQnA err : {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
