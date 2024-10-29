package bibid.dto;

import bibid.entity.Address;
import lombok.*;

import bibid.entity.Member;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDto {
    private Long addressIndex;
    private Long memberIndex;
    private String userAddress;
    private String typeAddress;
    private String baseAddress;
    private String detailAddress;
    private String receiverName;
    private Long receiverPnum;
    private boolean isDefault;

    public Address toEntity(Member member) {
        return Address.builder()
                .member(member)
                .addressIndex(this.addressIndex)
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
