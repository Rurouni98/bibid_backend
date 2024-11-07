package bibid.dto;

import bibid.entity.Member;
import bibid.entity.SellerInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SellerInfoDto {
    private Long sellerInfoIndex;
    private Long memberIndex;
    private String businessName;
    private String businessClassification;
    private String salesDeclaration;
    private String businessRegistrationNum;
    private String exponent;
    private String businessLocation;
    private String salesCnt;

    public SellerInfo toEntity(Member member) {
        return SellerInfo.builder()
                .member(member)
                .businessName(this.businessName)
                .businessClassification(this.businessClassification)
                .salesDeclaration(this.salesDeclaration)
                .businessRegistrationNum(this.businessRegistrationNum)
                .exponent(this.exponent)
                .businessLocation(this.businessLocation)
                .salesCnt(this.salesCnt)
                .build();
    }

}
