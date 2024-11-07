package bibid.entity;

import bibid.dto.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "profileImageSeqGenerator",
        sequenceName = "PROFILEIMAGE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileImage {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "profileImageSeqGenerator"
    )
    private Long profileImageIndex;

    @OneToOne
    @JoinColumn(name = "memberIndex")
    @JsonBackReference
    private Member member;

    private String filepath;
    private String filetype;
    private Long filesize;
    private String originalname;
    private String filestatus;
    private String newfilename;

    public ProfileImageDto toDto() {
        return ProfileImageDto.builder()
                .profileImageIndex(this.profileImageIndex)
                .memberIndex(this.member.getMemberIndex())
                .filepath(this.filepath)
                .filetype(this.filetype)
                .filesize(this.filesize)
                .originalname(this.originalname)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .build();
    }
}