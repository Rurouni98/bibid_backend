package bibid.dto;

import bibid.entity.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QnAFileDto {
    private Long qnaFileIndex;
    private Long qnaIndex;
    private Long memberIndex;
    private String fileName;
    private String fileOriginName;

    public QnAFile toEntity(QnA qna, Member member) {
        return QnAFile.builder()
                .qnaFileIndex(this.qnaFileIndex)
                .qna(qna)
                .member(member)
                .fileName(this.fileName)
                .fileOriginName(this.fileOriginName)
                .build();
    }

}
