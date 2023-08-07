package umc.stockoneqback.admin.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public record AddFARequest(
        @NotNull @Valid
        List<AddFAKeyValue> addFAKeyValueList
) {
    public record AddFAKeyValue(
            @NotBlank
            String question,

            @NotBlank
            String answer
    ){}
}
