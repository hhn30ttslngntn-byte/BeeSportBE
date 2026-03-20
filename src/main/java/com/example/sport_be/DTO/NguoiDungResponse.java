package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDungResponse {
    private Integer id;
    private String hoTen;
    private String email;
    private Boolean trangThai;
    private Integer idVaiTro;
}
