package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TinhDTO {
    private Integer id;
    private String tenTinh;
    private Boolean trangThai;
}
