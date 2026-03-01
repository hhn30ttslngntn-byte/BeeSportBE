package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaiTroDTO {
    private Integer id;
    private String tenVaiTro;
    private Boolean trangThai;
}
