package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMucResponse {
    private Integer id;
    private String tenDanhMuc;
    private Boolean trangThai;
}
