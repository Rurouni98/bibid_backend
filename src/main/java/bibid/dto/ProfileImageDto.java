package bibid.dto;

import bibid.entity.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProfileImageDto {
    private Long profileImageIndex;
    private Long memberIndex;
    private String filepath;
    private String filetype;
    private Long filesize;
    private String originalname;
    private String filestatus;
    private String newfilename;

    public ProfileImage toEntity(Member member) {
        return ProfileImage.builder()
                .profileImageIndex(this.profileImageIndex)
                .member(member)
                .filepath(this.filepath)
                .filetype(this.filetype)
                .filesize(this.filesize)
                .originalname(this.originalname)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .build();
    }

}
