package com.example.sport_be.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSchemaFixer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        ensureTinhShippingColumn();
        ensureHoaDonStatusConstraint();
        ensureDoiTraStatusConstraint();
        ensureHoaDonStatusTrigger();
        ensureDoiTraValidationTrigger();
    }

    private void ensureTinhShippingColumn() {
        jdbcTemplate.execute("""
                IF COL_LENGTH('dbo.tinh', 'phi_ship_mac_dinh') IS NULL
                BEGIN
                    EXEC(N'ALTER TABLE dbo.tinh ADD phi_ship_mac_dinh DECIMAL(18,2) NULL;');
                END;

                IF COL_LENGTH('dbo.tinh', 'phi_ship_mac_dinh') IS NOT NULL
                BEGIN
                    EXEC(N'UPDATE dbo.tinh
                          SET phi_ship_mac_dinh = 30000
                          WHERE phi_ship_mac_dinh IS NULL;');
                END;
                """);
    }

    private void ensureHoaDonStatusConstraint() {
        if (!tableExists("hoa_don")) {
            return;
        }
        jdbcTemplate.execute("""
                DECLARE @sql NVARCHAR(MAX) = N'';

                SELECT @sql = STRING_AGG(
                    N'ALTER TABLE dbo.hoa_don DROP CONSTRAINT [' + cc.name + N']',
                    N';'
                )
                FROM sys.check_constraints cc
                JOIN sys.columns c
                    ON c.object_id = cc.parent_object_id
                   AND c.column_id = cc.parent_column_id
                WHERE cc.parent_object_id = OBJECT_ID(N'dbo.hoa_don')
                  AND c.name = N'trang_thai_don';

                IF @sql IS NOT NULL AND LEN(@sql) > 0
                BEGIN
                    EXEC sp_executesql @sql;
                END;

                IF NOT EXISTS (
                    SELECT 1
                    FROM sys.check_constraints
                    WHERE name = N'chk_hoa_don_trang_thai_don'
                )
                BEGIN
                    ALTER TABLE dbo.hoa_don
                    ADD CONSTRAINT chk_hoa_don_trang_thai_don
                    CHECK (trang_thai_don IN (
                        N'CHO_XAC_NHAN',
                        N'DA_XAC_NHAN',
                        N'DANG_GIAO',
                        N'DA_GIAO',
                        N'YEU_CAU_TRA_HANG',
                        N'HOAN_TRA_MOT_PHAN',
                        N'HOAN_TRA',
                        N'DA_HUY'
                    ));
                END;
                """);
    }

    private void ensureDoiTraStatusConstraint() {
        if (!tableExists("doi_tra")) {
            return;
        }
        jdbcTemplate.execute("""
                DECLARE @sql NVARCHAR(MAX) = N'';

                SELECT @sql = STRING_AGG(
                    N'ALTER TABLE dbo.doi_tra DROP CONSTRAINT [' + cc.name + N']',
                    N';'
                )
                FROM sys.check_constraints cc
                JOIN sys.columns c
                    ON c.object_id = cc.parent_object_id
                   AND c.column_id = cc.parent_column_id
                WHERE cc.parent_object_id = OBJECT_ID(N'dbo.doi_tra')
                  AND c.name = N'trang_thai';

                IF @sql IS NOT NULL AND LEN(@sql) > 0
                BEGIN
                    EXEC sp_executesql @sql;
                END;

                IF NOT EXISTS (
                    SELECT 1
                    FROM sys.check_constraints
                    WHERE name = N'chk_doi_tra_trang_thai'
                )
                BEGIN
                    ALTER TABLE dbo.doi_tra
                    ADD CONSTRAINT chk_doi_tra_trang_thai
                    CHECK (trang_thai IN (
                        N'CHO_XAC_NHAN',
                        N'CHO_TRA_HANG',
                        N'DA_NHAN_HANG',
                        N'HOAN_THANH',
                        N'TU_CHOI',
                        N'CANCELLED'
                    ));
                END;
                """);
    }

    private void ensureHoaDonStatusTrigger() {
        if (!tableExists("hoa_don")) {
            return;
        }
        jdbcTemplate.execute("""
                IF OBJECT_ID(N'dbo.trg_validate_trang_thai', N'TR') IS NOT NULL
                BEGIN
                    DROP TRIGGER dbo.trg_validate_trang_thai;
                END;
                """);

        jdbcTemplate.execute("""
                CREATE TRIGGER dbo.trg_validate_trang_thai
                ON dbo.hoa_don
                AFTER UPDATE
                AS
                BEGIN
                    SET NOCOUNT ON;
                    IF NOT UPDATE(trang_thai_don) RETURN;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN deleted d ON i.id_hoa_don = d.id_hoa_don
                        WHERE d.trang_thai_don = 'DA_HUY'
                          AND i.trang_thai_don <> 'DA_HUY'
                    )
                    BEGIN
                        RAISERROR(N'Không thể thay đổi trạng thái của đơn hàng đã hủy', 16, 1);
                        ROLLBACK TRANSACTION;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN deleted d ON i.id_hoa_don = d.id_hoa_don
                        WHERE i.loai_don_hang = 'ONLINE'
                          AND (
                              (d.trang_thai_don = 'CHO_XAC_NHAN' AND i.trang_thai_don NOT IN ('DA_XAC_NHAN', 'DA_HUY')) OR
                              (d.trang_thai_don = 'DA_XAC_NHAN' AND i.trang_thai_don NOT IN ('DANG_GIAO', 'DA_HUY')) OR
                              (d.trang_thai_don = 'DANG_GIAO' AND i.trang_thai_don <> 'DA_GIAO') OR
                              (d.trang_thai_don = 'DA_GIAO' AND i.trang_thai_don NOT IN ('DA_GIAO', 'YEU_CAU_TRA_HANG', 'HOAN_TRA_MOT_PHAN', 'HOAN_TRA')) OR
                              (d.trang_thai_don = 'YEU_CAU_TRA_HANG' AND i.trang_thai_don NOT IN ('YEU_CAU_TRA_HANG', 'DA_GIAO', 'HOAN_TRA_MOT_PHAN', 'HOAN_TRA')) OR
                              (d.trang_thai_don = 'HOAN_TRA_MOT_PHAN' AND i.trang_thai_don NOT IN ('HOAN_TRA_MOT_PHAN', 'YEU_CAU_TRA_HANG', 'HOAN_TRA')) OR
                              (d.trang_thai_don = 'HOAN_TRA' AND i.trang_thai_don <> 'HOAN_TRA')
                          )
                    )
                    BEGIN
                        RAISERROR(N'Cập nhật trạng thái không hợp lệ cho đơn hàng Online', 16, 1);
                        ROLLBACK TRANSACTION;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN deleted d ON i.id_hoa_don = d.id_hoa_don
                        WHERE i.loai_don_hang = 'TAI_QUAY'
                          AND (
                              (d.trang_thai_don = 'DA_GIAO' AND i.trang_thai_don NOT IN ('DA_GIAO', 'YEU_CAU_TRA_HANG', 'HOAN_TRA_MOT_PHAN', 'HOAN_TRA')) OR
                              (d.trang_thai_don = 'YEU_CAU_TRA_HANG' AND i.trang_thai_don NOT IN ('YEU_CAU_TRA_HANG', 'DA_GIAO', 'HOAN_TRA_MOT_PHAN', 'HOAN_TRA')) OR
                              (d.trang_thai_don = 'HOAN_TRA_MOT_PHAN' AND i.trang_thai_don NOT IN ('HOAN_TRA_MOT_PHAN', 'YEU_CAU_TRA_HANG', 'HOAN_TRA')) OR
                              (d.trang_thai_don = 'HOAN_TRA' AND i.trang_thai_don <> 'HOAN_TRA')
                          )
                    )
                    BEGIN
                        RAISERROR(N'Cập nhật trạng thái không hợp lệ cho đơn hàng tại quầy', 16, 1);
                        ROLLBACK TRANSACTION;
                        RETURN;
                    END;

                    UPDATE dbo.hoa_don
                    SET ngay_giao = GETDATE()
                    WHERE id_hoa_don IN (
                        SELECT id_hoa_don
                        FROM inserted
                        WHERE trang_thai_don = 'DA_GIAO'
                          AND ngay_giao IS NULL
                    );
                END;
                """);

        jdbcTemplate.execute("""
                EXEC sp_settriggerorder
                    @triggername = 'dbo.trg_validate_trang_thai',
                    @order = 'FIRST',
                    @stmttype = 'UPDATE';
                """);
    }

    private void ensureDoiTraValidationTrigger() {
        if (!tableExists("doi_tra_chi_tiet")
                || !tableExists("doi_tra")
                || !tableExists("hoa_don")
                || !tableExists("hoa_don_chi_tiet")
                || !tableExists("san_pham_chi_tiet")
                || !columnExists("hoa_don_chi_tiet", "da_doi_tra")) {
            return;
        }

        jdbcTemplate.execute("""
                IF OBJECT_ID(N'dbo.trg_check_so_luong_tra', N'TR') IS NOT NULL
                BEGIN
                    DROP TRIGGER dbo.trg_check_so_luong_tra;
                END;
                """);

        jdbcTemplate.execute("""
                CREATE TRIGGER dbo.trg_check_so_luong_tra
                ON dbo.doi_tra_chi_tiet
                INSTEAD OF INSERT
                AS
                BEGIN
                    SET NOCOUNT ON;
                    SET XACT_ABORT ON;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        JOIN dbo.hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don
                        WHERE hd.trang_thai_don NOT IN ('DA_GIAO', 'YEU_CAU_TRA_HANG', 'HOAN_TRA_MOT_PHAN')
                    )
                    BEGIN
                        RAISERROR(N'Chỉ được đổi trả khi đơn đã giao hoặc đang xử lý đổi trả', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        JOIN dbo.hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don
                        WHERE (hd.loai_don_hang = 'ONLINE' AND (hd.ngay_giao IS NULL OR DATEDIFF(DAY, hd.ngay_giao, GETDATE()) > 7))
                           OR (hd.loai_don_hang = 'TAI_QUAY' AND DATEDIFF(DAY, hd.ngay_tao, GETDATE()) > 3)
                    )
                    BEGIN
                        RAISERROR(N'Quá thời gian đổi trả', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        JOIN dbo.hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don
                        WHERE (hd.loai_don_hang = 'ONLINE' AND dt.loai_doi_tra <> 'HOAN_TIEN')
                           OR (hd.loai_don_hang = 'TAI_QUAY' AND dt.loai_doi_tra <> 'DOI_HANG')
                    )
                    BEGIN
                        RAISERROR(N'Loại đổi trả không hợp lệ', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        WHERE dt.tinh_trang_hang = 'DA_SU_DUNG'
                    )
                    BEGIN
                        RAISERROR(N'Hàng đã sử dụng không được đổi trả', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        WHERE dt.loai_doi_tra = 'DOI_HANG'
                          AND dt.id_spct_moi IS NULL
                    )
                    BEGIN
                        RAISERROR(N'Phải chọn sản phẩm đổi', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        JOIN dbo.san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK) ON dt.id_spct_moi = spct.id_spct
                        WHERE dt.loai_doi_tra = 'DOI_HANG'
                          AND spct.so_luong < i.so_luong_tra
                    )
                    BEGIN
                        RAISERROR(N'Sản phẩm đổi không đủ hàng', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
                        JOIN dbo.hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct
                        JOIN dbo.san_pham_chi_tiet spct_cu ON hdct.id_spct = spct_cu.id_spct
                        JOIN dbo.san_pham_chi_tiet spct_moi ON dt.id_spct_moi = spct_moi.id_spct
                        WHERE dt.loai_doi_tra = 'DOI_HANG'
                          AND spct_cu.id_san_pham <> spct_moi.id_san_pham
                    )
                    BEGIN
                        RAISERROR(N'Chỉ được đổi cùng loại sản phẩm', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct
                        WHERE hdct.da_doi_tra = 1
                    )
                    BEGIN
                        RAISERROR(N'Sản phẩm đã được đổi trả', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    IF EXISTS (
                        SELECT 1
                        FROM inserted i
                        JOIN dbo.hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct
                        LEFT JOIN (
                            SELECT id_hdct, SUM(so_luong_tra) AS tong_da_tra
                            FROM dbo.doi_tra_chi_tiet
                            GROUP BY id_hdct
                        ) d ON d.id_hdct = i.id_hdct
                        WHERE ISNULL(d.tong_da_tra, 0) + i.so_luong_tra > hdct.so_luong
                    )
                    BEGIN
                        RAISERROR(N'Vượt quá số lượng mua', 16, 1);
                        ROLLBACK;
                        RETURN;
                    END;

                    INSERT INTO dbo.doi_tra_chi_tiet (id_doi_tra, id_hdct, so_luong_tra, gia_tri_hoan)
                    SELECT i.id_doi_tra, i.id_hdct, i.so_luong_tra, i.gia_tri_hoan
                    FROM inserted i;
                END;
                """);
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys.tables WHERE name = ? AND schema_id = SCHEMA_ID('dbo')",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM sys.columns c
                JOIN sys.tables t ON c.object_id = t.object_id
                WHERE t.schema_id = SCHEMA_ID('dbo')
                  AND t.name = ?
                  AND c.name = ?
                """,
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }
}
