package bibid.entity;

import bibid.dto.AddressDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@SequenceGenerator(
        name = "addressSeqGenerator",
        sequenceName = "ADDRESS_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "addressSeqGenerator"
    )
    private Long addressIndex;
    @OneToOne
    @JoinColumn(name = "memberIndex")
    private Member member;
    private String userAddress;
    private String typeAddress;
    private String baseAddress;
    private String detailAddress;
    private String receiverName;
    private Long receiverPnum;
    private boolean isDefault;

    public AddressDto toDto() {
        return AddressDto.builder()
                .addressIndex(this.addressIndex)
                .memberIndex(this.member.getMemberIndex())
                .userAddress(this.userAddress)
                .typeAddress(this.typeAddress)
                .baseAddress(this.baseAddress)
                .detailAddress(this.detailAddress)
                .receiverName(this.receiverName)
                .receiverPnum(this.receiverPnum)
                .isDefault(this.isDefault)
                .build();
    }









}
