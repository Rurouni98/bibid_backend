package bibid.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MypageProfileFileDto {
    private Long id;
    private Long profile_id;
    private String userName;
    private String userId;
    private String filename;
    private String filepath;
    private String fileoriginname;
    private String filetype;
    private String filestatus;
    private String newfilename;
}
