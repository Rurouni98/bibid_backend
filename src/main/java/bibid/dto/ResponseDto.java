package bibid.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ResponseDto<T> {
    private T item;
    private List<T> items;
    private Page<T> pageItems;
    private int statusCode;
    private String statusMessage;
    private String errorMessage;
}
