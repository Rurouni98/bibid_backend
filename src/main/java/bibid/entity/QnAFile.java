package bibid.entity;

import bibid.dto.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "qnaFileSeqGenerator",
        sequenceName = "QNAFILE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnAFile {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "qnaFileSeqGenerator"
    )
    private Long qnaFileIndex;
    @OneToOne
    @JoinColumn(name = "qnaIndex")
    private QnA qna;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String fileName;
    private String fileOriginName;

    public QnAFileDto toDto() {
        return QnAFileDto.builder()
                .qnaFileIndex(this.qnaFileIndex)
                .qnaIndex(this.qna.getQnaIndex())
                .memberIndex(this.member.getMemberIndex())
                .fileName(this.fileName)
                .fileOriginName(this.fileOriginName)
                .build();
    }









}
