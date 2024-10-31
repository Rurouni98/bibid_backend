package bibid.entity;

import bibid.dto.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "sellerInfoSeqGenerator",
        sequenceName = "SELLERINFO_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerInfo {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sellerInfoSeqGenerator"
    )
    private Long sellerInfoIndex;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String businessName;
    private String businessClassification;
    private String salesDeclaration;
    private String businessRegistrationNum;
    private String exponent;
    private String businessLocation;
    private String salesCnt;

    public SellerInfoDto toDto() {
        return SellerInfoDto.builder()
                .memberIndex(this.member.getMemberIndex())
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
