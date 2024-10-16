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
    private Long profileIndex;
    private Long memberIndex;
    private String filename;
    private String filepath;
    private String filetype;
    private Long filesize;
    private String originalname;
    private String filestatus;
    private String newfilename;

    public ProfileImage toEntiy(Member member) {
        return ProfileImage.builder()
                .profileIndex(this.profileIndex)
                .member(member)
                .filename(this.filename)
                .filepath(this.filepath)
                .filetype(this.filetype)
                .filesize(this.filesize)
                .originalname(this.originalname)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .build();
    }

}
