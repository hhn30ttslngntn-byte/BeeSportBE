package com.example.sport_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class KiemHangRequest {
    private String nguoiKiem;
    private List<ChiTietKiemItem> chiTietKiem;

    @Getter
    @Setter
    public static class ChiTietKiemItem {
        private Integer idDtct;
        private String skuDoiChieu;
        private Map<String, Boolean> checklist;
        private String ketQuaKiem;
        private List<String> anhKiem;
        private String ghiChuKiem;
    }
}
