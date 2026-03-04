package com.example.sport_be.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XaDTO {
    private Integer id;
    private Integer idHuyen;
    private String tenXa;
    private Boolean trangThai;
}
