package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DoiTraRequest {
    private Integer hoaDonId;
    private String lyDo;
    private List<DoiTraChiTietRequest> chiTiets;
}
