package bibid.service.qna;

import bibid.dto.QnADto;
import bibid.entity.Member;

import java.util.List;

public interface QnaService {

    List<QnADto> findQnaListByAuctionIndex(Long auctionIndex);

    void postQnA(QnADto qnADto);
}
