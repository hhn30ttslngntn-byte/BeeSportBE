package com.example.sport_be.DTO;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoiTraDTO {
    private Integer id;

    @NotNull
    private Integer idHoaDon;

    @NotBlank
    @Size(max = 255)
    private String lyDo;

    private String trangThai;
    private LocalDateTime ngayYeuCau;
}
