	CREATE DATABASE BeeSport;
	GO
	USE BeeSport;
	GO

	CREATE TABLE vai_tro (
    id_vai_tro INT IDENTITY PRIMARY KEY,
    ma_vai_tro NVARCHAR(50),
    ten_vai_tro NVARCHAR(50),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE danh_muc (
    id_danh_muc INT IDENTITY PRIMARY KEY,
    ma_danh_muc NVARCHAR(50),
    ten_danh_muc NVARCHAR(150),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE thuong_hieu (
    id_thuong_hieu INT IDENTITY PRIMARY KEY,
    ma_thuong_hieu NVARCHAR(50) UNIQUE,
    ten_thuong_hieu NVARCHAR(150) NOT NULL,
    mo_ta NVARCHAR(255),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE chat_lieu (
    id_chat_lieu INT IDENTITY PRIMARY KEY,
    ma_chat_lieu NVARCHAR(50),
    ten_chat_lieu NVARCHAR(50),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE kich_thuoc (
    id_kich_thuoc INT IDENTITY PRIMARY KEY,
    ma_kich_thuoc NVARCHAR(50),
    ten_kich_thuoc NVARCHAR(50),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE mau_sac (
    id_mau_sac INT IDENTITY PRIMARY KEY,
    ma_mau_sac NVARCHAR(50),
    ten_mau NVARCHAR(50),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE pt_thanh_toan (
    id_pttt INT IDENTITY PRIMARY KEY,
    ma_pt_thanh_toan NVARCHAR(50),
    ten_pttt NVARCHAR(100),
    trang_thai BIT DEFAULT 1
);
GO
select * from pt_thanh_toan

CREATE TABLE tinh (
    id_tinh INT IDENTITY PRIMARY KEY,
    ma_tinh NVARCHAR(50),
    ten_tinh NVARCHAR(100),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE huyen (
    id_huyen INT IDENTITY PRIMARY KEY,
    ma_huyen NVARCHAR(50),
    id_tinh INT FOREIGN KEY REFERENCES tinh(id_tinh),
    ten_huyen NVARCHAR(100),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE xa (
    id_xa INT IDENTITY PRIMARY KEY,
    ma_xa NVARCHAR(50),
    id_huyen INT FOREIGN KEY REFERENCES huyen(id_huyen),
    ten_xa NVARCHAR(100),
    trang_thai BIT DEFAULT 1
);
GO

-- ==============================================================================
-- PHẦN 2: QUẢN LÝ NGƯỜI DÙNG & SẢN PHẨM
-- ==============================================================================
CREATE TABLE nguoi_dung (
    id_nguoi_dung INT IDENTITY PRIMARY KEY,
    id_vai_tro INT FOREIGN KEY REFERENCES vai_tro(id_vai_tro),
    ma_nguoi_dung NVARCHAR(50),
    ho_ten NVARCHAR(150),
    so_dien_thoai NVARCHAR(20) UNIQUE,
    email NVARCHAR(150) NULL, 
    mat_khau NVARCHAR(255) NULL, 
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO
CREATE UNIQUE INDEX idx_unique_email_not_null ON nguoi_dung(email) WHERE email IS NOT NULL;
GO

CREATE TABLE xac_thuc (
    id_xac_thuc INT IDENTITY PRIMARY KEY,
    id_nguoi_dung INT FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung),
    ma_xac_thuc NVARCHAR(50),
    loai_xac_thuc NVARCHAR(50),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE san_pham (
    id_san_pham INT IDENTITY PRIMARY KEY,
    id_danh_muc INT NOT NULL FOREIGN KEY REFERENCES danh_muc(id_danh_muc),
    id_thuong_hieu INT FOREIGN KEY REFERENCES thuong_hieu(id_thuong_hieu),
    id_chat_lieu INT FOREIGN KEY REFERENCES chat_lieu(id_chat_lieu), 
    ma_san_pham NVARCHAR(50) UNIQUE,
    ten_san_pham NVARCHAR(200) NOT NULL,
    gia_goc DECIMAL(18,2) NOT NULL,
    anh_dai_dien NVARCHAR(500) NULL,
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE hinh_anh_san_pham (
    id_hinh_anh INT IDENTITY PRIMARY KEY,
    id_san_pham INT FOREIGN KEY REFERENCES san_pham(id_san_pham),
    ma_hinh_anh_san_pham NVARCHAR(50),
    url NVARCHAR(255),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE san_pham_chi_tiet (
    id_spct INT IDENTITY PRIMARY KEY,
    id_san_pham INT NOT NULL FOREIGN KEY REFERENCES san_pham(id_san_pham),
    id_kich_thuoc INT NOT NULL FOREIGN KEY REFERENCES kich_thuoc(id_kich_thuoc),
    id_mau_sac INT NOT NULL FOREIGN KEY REFERENCES mau_sac(id_mau_sac),
    id_thuong_hieu INT NOT NULL FOREIGN KEY REFERENCES thuong_hieu(id_thuong_hieu),
    ma_san_pham_chi_tiet NVARCHAR(50) UNIQUE,
    so_luong INT NOT NULL CHECK (so_luong >= 0),
    so_luong_da_ban INT DEFAULT 0 CHECK (so_luong_da_ban >= 0),
    gia_ban DECIMAL(18,2) NOT NULL,
    trang_thai BIT DEFAULT 1,
    CONSTRAINT unique_spct UNIQUE (id_san_pham, id_kich_thuoc, id_mau_sac, id_thuong_hieu)
);
GO

CREATE TABLE danh_gia_san_pham (
    id_danh_gia INT IDENTITY PRIMARY KEY,
    ma_danh_gia NVARCHAR(50) UNIQUE,
    id_nguoi_dung INT NOT NULL FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung),
    id_san_pham INT NOT NULL FOREIGN KEY REFERENCES san_pham(id_san_pham),
    so_sao INT CHECK (so_sao BETWEEN 1 AND 5),
    noi_dung NVARCHAR(500),
    ngay_danh_gia DATETIME DEFAULT GETDATE(),
    trang_thai BIT DEFAULT 1,
    CONSTRAINT unique_review UNIQUE (id_nguoi_dung, id_san_pham)
);
GO

CREATE TABLE san_pham_yeu_thich (
    id_yeu_thich INT IDENTITY PRIMARY KEY,
    ma_san_pham_yeu_thich NVARCHAR(50) UNIQUE,
    id_nguoi_dung INT NOT NULL FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung),
    id_san_pham INT NOT NULL FOREIGN KEY REFERENCES san_pham(id_san_pham),
    ngay_them DATETIME DEFAULT GETDATE(),
    CONSTRAINT unique_wishlist UNIQUE (id_nguoi_dung, id_san_pham)
);
GO

-- ==============================================================================
-- PHẦN 3: MARKETING (GIẢM GIÁ)
-- ==============================================================================
CREATE TABLE ma_giam_gia (
    id_ma_giam_gia INT IDENTITY PRIMARY KEY,
    ma_code NVARCHAR(50) UNIQUE,
    kieu_giam_gia NVARCHAR(20) CHECK (kieu_giam_gia IN ('PERCENT','AMOUNT')),
    gia_tri_giam DECIMAL(18,2) CHECK (gia_tri_giam > 0),
    gia_tri_giam_toi_da DECIMAL(18,2),
    gia_tri_toi_thieu DECIMAL(18,2),
    so_luong INT,
    so_luong_da_dung INT DEFAULT 0,
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    trang_thai BIT DEFAULT 1,
    CONSTRAINT chk_usage CHECK (so_luong_da_dung <= so_luong),
    CONSTRAINT chk_discount_range CHECK ((kieu_giam_gia = 'PERCENT' AND gia_tri_giam <= 100) OR (kieu_giam_gia = 'AMOUNT'))
);
GO

CREATE TABLE dot_giam_gia (
    id_dot_giam_gia INT IDENTITY PRIMARY KEY,
    ma_dot_giam_gia NVARCHAR(50) UNIQUE,
    ten_dot NVARCHAR(150),
    kieu_giam_gia NVARCHAR(20) CHECK (kieu_giam_gia IN ('PERCENT','AMOUNT')),
    gia_tri_giam DECIMAL(18,2),
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE giam_gia_san_pham (
    id_giam_gia_san_pham INT IDENTITY PRIMARY KEY,
    ma_giam_gia_san_pham NVARCHAR(50),
    id_dot_giam_gia INT FOREIGN KEY REFERENCES dot_giam_gia(id_dot_giam_gia),
    id_spct INT FOREIGN KEY REFERENCES san_pham_chi_tiet(id_spct),
    trang_thai BIT DEFAULT 1,
    CONSTRAINT unique_discount_spct UNIQUE (id_dot_giam_gia, id_spct)
);
GO

-- ==============================================================================
-- PHẦN 4: GIAO DỊCH (GIỎ HÀNG, HÓA ĐƠN, ĐỔI TRẢ)
-- ==============================================================================
CREATE TABLE gio_hang (
    id_gio_hang INT IDENTITY PRIMARY KEY,
    ma_gio_hang NVARCHAR(50) UNIQUE,
    id_nguoi_dung INT NOT NULL FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung),
    loai_gio_hang NVARCHAR(20) DEFAULT 'ONLINE' CHECK (loai_gio_hang IN ('ONLINE', 'TAI_QUAY')),
    ten_gio_hang NVARCHAR(100),
    voucher_code NVARCHAR(50),
    trang_thai NVARCHAR(30) DEFAULT 'DANG_SU_DUNG' CHECK (trang_thai IN ('DANG_SU_DUNG','DA_THANH_TOAN')),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO
CREATE UNIQUE INDEX idx_unique_active_cart ON gio_hang(id_nguoi_dung) WHERE trang_thai = 'DANG_SU_DUNG' AND loai_gio_hang = 'ONLINE';
GO

CREATE TABLE gio_hang_chi_tiet (
    id_ghct INT IDENTITY PRIMARY KEY,
    id_gio_hang INT NOT NULL FOREIGN KEY REFERENCES gio_hang(id_gio_hang),
    id_spct INT NOT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id_spct),
    ma_gio_hang_chi_tiet NVARCHAR(50) UNIQUE,
    so_luong INT NOT NULL CHECK (so_luong > 0),
    don_gia DECIMAL(18,2),
    chon BIT DEFAULT 1,
    ngay_them DATETIME DEFAULT GETDATE(),
    CONSTRAINT unique_cart_product UNIQUE (id_gio_hang, id_spct)
);
GO

CREATE TABLE hoa_don (
    id_hoa_don INT IDENTITY PRIMARY KEY,
    ma_hoa_don NVARCHAR(50) UNIQUE,
    id_nguoi_dung INT NOT NULL FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung),
    id_ma_giam_gia INT NULL FOREIGN KEY REFERENCES ma_giam_gia(id_ma_giam_gia),
    id_pttt INT NULL FOREIGN KEY REFERENCES pt_thanh_toan(id_pttt),
    loai_don_hang NVARCHAR(20) DEFAULT 'ONLINE' CHECK (loai_don_hang IN ('ONLINE','TAI_QUAY')),
    ten_nguoi_nhan NVARCHAR(150) NOT NULL,
    so_dien_thoai NVARCHAR(20) NOT NULL,
    tinh NVARCHAR(100), huyen NVARCHAR(100), xa NVARCHAR(100), dia_chi_chi_tiet NVARCHAR(255),
    tong_tien_hang DECIMAL(18,2) DEFAULT 0,
    tien_giam DECIMAL(18,2) DEFAULT 0,
    phi_van_chuyen DECIMAL(18,2) DEFAULT 0,
    tong_thanh_toan DECIMAL(18,2) DEFAULT 0,
    trang_thai_don NVARCHAR(30) DEFAULT 'CHO_XAC_NHAN' CHECK (trang_thai_don IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO', 'YEU_CAU_TRA_HANG', 'HOAN_TRA_MOT_PHAN', 'HOAN_TRA', 'DA_HUY')),
    ghi_chu NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_cap_nhat DATETIME DEFAULT GETDATE(),
    ngay_giao DATETIME
);
GO

CREATE TABLE hoa_don_chi_tiet (
    id_hdct INT IDENTITY PRIMARY KEY,
    ma_hoa_don_chi_tiet NVARCHAR(50) UNIQUE,
    id_hoa_don INT NOT NULL FOREIGN KEY REFERENCES hoa_don(id_hoa_don),
    id_spct INT NOT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id_spct),
    ten_san_pham NVARCHAR(200) NOT NULL,
    kich_thuoc NVARCHAR(50), mau_sac NVARCHAR(50), chat_lieu NVARCHAR(50),
    don_gia DECIMAL(18,2) NOT NULL,
    so_luong INT NOT NULL CHECK (so_luong > 0),
    thanh_tien DECIMAL(18,2) NOT NULL,
    da_doi_tra BIT DEFAULT 0,
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE lich_su_thanh_toan (
    id_lstt INT IDENTITY PRIMARY KEY,
    ma_lich_su_thanh_toan NVARCHAR(50),
    id_hoa_don INT FOREIGN KEY REFERENCES hoa_don(id_hoa_don),
    id_pttt INT FOREIGN KEY REFERENCES pt_thanh_toan(id_pttt),
    so_tien DECIMAL(18,2),
    loai_giao_dich NVARCHAR(20) DEFAULT 'THANH_TOAN' CHECK (loai_giao_dich IN ('THANH_TOAN','HOAN_TIEN')),
    trang_thai_thanh_toan NVARCHAR(30) CHECK (trang_thai_thanh_toan IN ('CHO_THANH_TOAN','DA_THANH_TOAN','THAT_BAI')),
    ngay_thanh_toan DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE lich_su_hoa_don (
    id_ls_hd INT IDENTITY PRIMARY KEY,
    ma_lich_su NVARCHAR(50) UNIQUE,
    id_hoa_don INT NOT NULL FOREIGN KEY REFERENCES hoa_don(id_hoa_don),
    trang_thai_cu NVARCHAR(30),
    trang_thai_moi NVARCHAR(30),
    loai_hanh_dong NVARCHAR(50), 
    hanh_dong NVARCHAR(255),
    id_nguoi_thuc_hien INT NULL FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung), 
    thoi_gian DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE lich_su_su_dung_ma_giam_gia (
    id_ls INT IDENTITY PRIMARY KEY,
    ma_lich_su_su_dung_ma_giam_gia NVARCHAR(50),
    id_ma_giam_gia INT FOREIGN KEY REFERENCES ma_giam_gia(id_ma_giam_gia),
    id_hoa_don INT FOREIGN KEY REFERENCES hoa_don(id_hoa_don),
    ngay_su_dung DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE dia_chi_van_chuyen (
    id_dia_chi INT IDENTITY PRIMARY KEY,
    ma_dia_chi_van_chuyen NVARCHAR(50),
    id_nguoi_dung INT FOREIGN KEY REFERENCES nguoi_dung(id_nguoi_dung),
    id_xa INT FOREIGN KEY REFERENCES xa(id_xa),
    ten_nguoi_nhan NVARCHAR(150),
    so_dien_thoai NVARCHAR(20),
    dia_chi_chi_tiet NVARCHAR(255),
    loai_dia_chi NVARCHAR(30), 
    la_mac_dinh BIT DEFAULT 0,
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE cau_hinh_doi_tra (
    id INT IDENTITY PRIMARY KEY,
    phi_xu_ly_phan_tram DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    phi_ship_hoan DECIMAL(18,0) NOT NULL DEFAULT 30000,
    so_ngay_cho_phep INT NOT NULL DEFAULT 7,
    ngay_cap_nhat DATETIME2 DEFAULT SYSDATETIME()
);
GO
INSERT INTO cau_hinh_doi_tra DEFAULT VALUES;
GO

CREATE TABLE doi_tra (
    id_doi_tra INT IDENTITY PRIMARY KEY,
    ma_doi_tra NVARCHAR(50),
    id_hoa_don INT NOT NULL FOREIGN KEY REFERENCES hoa_don(id_hoa_don),
    loai_doi_tra NVARCHAR(20) CHECK (loai_doi_tra IN ('HOAN_TIEN','DOI_HANG')),
    id_spct_moi INT NULL FOREIGN KEY REFERENCES san_pham_chi_tiet(id_spct),
    ly_do NVARCHAR(255),
    ly_do_tu_choi NVARCHAR(255),
    tinh_trang_hang NVARCHAR(20) CHECK (tinh_trang_hang IN ('NGUYEN_VEN','LOI','DA_SU_DUNG')),
    danh_sach_anh NVARCHAR(MAX) NULL,
    ben_chiu_loi NVARCHAR(10) NOT NULL DEFAULT 'KHACH',
    tien_hang_hoan DECIMAL(18,0),
    phi_xu_ly DECIMAL(18,0),
    phi_ship_hoan_tru DECIMAL(18,0),
    tong_tien_hoan DECIMAL(18,2) DEFAULT 0 CHECK (tong_tien_hoan >= 0),
    phuong_thuc_hoan NVARCHAR(20),
    so_tk_nhan NVARCHAR(50),
    ten_chu_tk NVARCHAR(100),
    ngan_hang NVARCHAR(100),
    ma_giao_dich_hoan NVARCHAR(100),
    anh_chung_tu NVARCHAR(MAX),
    khach_xac_nhan_nhan_tien BIT DEFAULT 0,
    ngay_khach_xac_nhan DATETIME2,
    token_xac_nhan NVARCHAR(100) UNIQUE,
    ghi_chu_admin NVARCHAR(500),
    ngay_xu_ly DATETIME,
    trang_thai NVARCHAR(30) CHECK (trang_thai IN (
        'CHO_XAC_NHAN','CHO_TRA_HANG','DA_NHAN_HANG','DA_KIEM_CHO_DUYET','DA_DUYET_CHO_HOAN_TIEN','CHO_HOAN_TIEN','CHO_KHACH_XAC_NHAN','HOAN_THANH','TU_CHOI','CANCELLED'
    )),
    ngay_yeu_cau DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE doi_tra_chi_tiet (
    id_doi_tra_ct INT IDENTITY PRIMARY KEY,
    id_doi_tra INT NOT NULL FOREIGN KEY REFERENCES doi_tra(id_doi_tra),
    id_hdct INT NOT NULL FOREIGN KEY REFERENCES hoa_don_chi_tiet(id_hdct),
    so_luong_tra INT NOT NULL CHECK (so_luong_tra > 0),
    gia_tri_hoan DECIMAL(18,2) NOT NULL CHECK (gia_tri_hoan >= 0),
    sku_doi_chieu NVARCHAR(100),
    ket_qua_kiem NVARCHAR(20) NOT NULL DEFAULT 'CHUA_KIEM',
    checklist_json NVARCHAR(MAX),
    anh_kiem NVARCHAR(MAX),
    nguoi_kiem NVARCHAR(100),
    thoi_gian_kiem DATETIME2,
    nguoi_duyet NVARCHAR(100),
    thoi_gian_duyet DATETIME2,
    ghi_chu_kiem NVARCHAR(500)
);
GO

-- ==============================================================================
-- PHẦN 5: TẠO TRIGGER NGHIỆP VỤ LÕI (CORE LOGIC)
-- ==============================================================================

-- 1. Giỏ hàng: Gộp chung và check kho
GO
CREATE TRIGGER trg_cart_insert ON gio_hang_chi_tiet INSTEAD OF INSERT AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    DECLARE @grouped TABLE (id_gio_hang INT, id_spct INT, total_qty INT);
    INSERT INTO @grouped SELECT id_gio_hang, id_spct, SUM(so_luong) FROM inserted GROUP BY id_gio_hang, id_spct;

    IF EXISTS (SELECT 1 FROM @grouped g JOIN san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK) ON g.id_spct = spct.id_spct LEFT JOIN gio_hang_chi_tiet ghct ON ghct.id_spct = g.id_spct AND ghct.id_gio_hang = g.id_gio_hang WHERE ISNULL(ghct.so_luong,0) + g.total_qty > spct.so_luong)
    BEGIN RAISERROR(N'Vượt quá tồn kho',16,1); ROLLBACK TRANSACTION; RETURN; END

    UPDATE ghct SET so_luong = ghct.so_luong + g.total_qty, don_gia = spct.gia_ban FROM gio_hang_chi_tiet ghct JOIN @grouped g ON ghct.id_gio_hang = g.id_gio_hang AND ghct.id_spct = g.id_spct JOIN san_pham_chi_tiet spct ON spct.id_spct = g.id_spct;

    INSERT INTO gio_hang_chi_tiet (id_gio_hang, id_spct, ma_gio_hang_chi_tiet, so_luong, don_gia, chon)
    SELECT g.id_gio_hang, g.id_spct, CONCAT('GHCT_', NEWID()), g.total_qty, spct.gia_ban, 1 FROM @grouped g JOIN san_pham_chi_tiet spct ON spct.id_spct = g.id_spct WHERE NOT EXISTS (SELECT 1 FROM gio_hang_chi_tiet ghct WHERE ghct.id_gio_hang = g.id_gio_hang AND ghct.id_spct = g.id_spct);
END
GO

-- 2. Hóa đơn: Tự động tính tổng tiền
CREATE TRIGGER trg_update_tong_tien_hd ON hoa_don_chi_tiet AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    DECLARE @hd TABLE (id_hoa_don INT);
    INSERT INTO @hd SELECT id_hoa_don FROM inserted UNION SELECT id_hoa_don FROM deleted;

    UPDATE hd SET tong_tien_hang = ISNULL(x.total,0) FROM hoa_don hd LEFT JOIN (SELECT id_hoa_don, SUM(thanh_tien) total FROM hoa_don_chi_tiet GROUP BY id_hoa_don) x ON hd.id_hoa_don = x.id_hoa_don WHERE hd.id_hoa_don IN (SELECT id_hoa_don FROM @hd);
    UPDATE hoa_don SET tong_thanh_toan = ISNULL(tong_tien_hang,0) - ISNULL(tien_giam,0) + ISNULL(phi_van_chuyen,0) WHERE id_hoa_don IN (SELECT id_hoa_don FROM @hd);
END
GO

-- 3. Hóa đơn: Validate Trạng thái & Set Ngày giao
CREATE TRIGGER trg_validate_trang_thai ON hoa_don AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON; IF NOT UPDATE(trang_thai_don) RETURN;

    IF EXISTS (
        SELECT 1 FROM inserted i JOIN deleted d ON i.id_hoa_don = d.id_hoa_don 
        WHERE d.trang_thai_don = 'DA_HUY' 
          AND i.trang_thai_don <> 'DA_HUY'
    )
    BEGIN RAISERROR(N'Không thể thay đổi trạng thái của đơn hàng đã hủy',16,1); ROLLBACK TRANSACTION; RETURN; END

    IF EXISTS (
        SELECT 1 FROM inserted i JOIN deleted d ON i.id_hoa_don = d.id_hoa_don 
        WHERE i.loai_don_hang = 'ONLINE'
          AND (
            (d.trang_thai_don = 'CHO_XAC_NHAN' AND i.trang_thai_don NOT IN ('DA_XAC_NHAN','DA_HUY')) OR 
            (d.trang_thai_don = 'DA_XAC_NHAN' AND i.trang_thai_don NOT IN ('DANG_GIAO','DA_HUY')) OR 
            (d.trang_thai_don = 'DANG_GIAO' AND i.trang_thai_don <> 'DA_GIAO') OR 
            (d.trang_thai_don = 'DA_GIAO' AND i.trang_thai_don NOT IN ('DA_GIAO','YEU_CAU_TRA_HANG','HOAN_TRA_MOT_PHAN','HOAN_TRA')) OR 
            (d.trang_thai_don = 'YEU_CAU_TRA_HANG' AND i.trang_thai_don NOT IN ('YEU_CAU_TRA_HANG','DA_GIAO','HOAN_TRA_MOT_PHAN','HOAN_TRA')) OR 
            (d.trang_thai_don = 'HOAN_TRA_MOT_PHAN' AND i.trang_thai_don NOT IN ('HOAN_TRA_MOT_PHAN','YEU_CAU_TRA_HANG','HOAN_TRA')) OR 
            (d.trang_thai_don = 'HOAN_TRA' AND i.trang_thai_don <> 'HOAN_TRA')
          )
    )
    BEGIN RAISERROR(N'Cập nhật trạng thái không hợp lệ cho đơn hàng Online',16,1); ROLLBACK TRANSACTION; RETURN; END

    IF EXISTS (
        SELECT 1 FROM inserted i JOIN deleted d ON i.id_hoa_don = d.id_hoa_don 
        WHERE i.loai_don_hang = 'TAI_QUAY'
          AND (
            (d.trang_thai_don = 'DA_GIAO' AND i.trang_thai_don NOT IN ('DA_GIAO','YEU_CAU_TRA_HANG','HOAN_TRA_MOT_PHAN','HOAN_TRA')) OR 
            (d.trang_thai_don = 'YEU_CAU_TRA_HANG' AND i.trang_thai_don NOT IN ('YEU_CAU_TRA_HANG','DA_GIAO','HOAN_TRA_MOT_PHAN','HOAN_TRA')) OR 
            (d.trang_thai_don = 'HOAN_TRA_MOT_PHAN' AND i.trang_thai_don NOT IN ('HOAN_TRA_MOT_PHAN','YEU_CAU_TRA_HANG','HOAN_TRA')) OR 
            (d.trang_thai_don = 'HOAN_TRA' AND i.trang_thai_don <> 'HOAN_TRA')
          )
    )
    BEGIN RAISERROR(N'Cập nhật trạng thái không hợp lệ cho đơn hàng tại quầy',16,1); ROLLBACK TRANSACTION; RETURN; END

    UPDATE hoa_don SET ngay_giao = GETDATE() WHERE id_hoa_don IN (SELECT id_hoa_don FROM inserted WHERE trang_thai_don = 'DA_GIAO' AND ngay_giao IS NULL);
END
GO

-- 4. Hóa đơn: Ghi Log Lịch sử
CREATE TRIGGER trg_log_hoa_don ON hoa_don AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON; IF NOT UPDATE(trang_thai_don) RETURN;
    DECLARE @user_id INT = CAST(SESSION_CONTEXT(N'user_id') AS INT);
    INSERT INTO lich_su_hoa_don (ma_lich_su, id_hoa_don, trang_thai_cu, trang_thai_moi, loai_hanh_dong, hanh_dong, id_nguoi_thuc_hien)
    SELECT CONCAT('LSHD_', NEWID()), i.id_hoa_don, d.trang_thai_don, i.trang_thai_don, 'UPDATE_STATUS', N'Trạng thái: ' + d.trang_thai_don + N' → ' + i.trang_thai_don, @user_id FROM inserted i JOIN deleted d ON i.id_hoa_don = d.id_hoa_don WHERE i.trang_thai_don <> d.trang_thai_don;
END
GO
EXEC sp_settriggerorder @triggername = 'trg_validate_trang_thai', @order = 'FIRST', @stmttype = 'UPDATE';
GO
EXEC sp_settriggerorder @triggername = 'trg_log_hoa_don', @order = 'LAST', @stmttype = 'UPDATE';
GO

-- 5. Hóa đơn: Trừ kho khi Xác nhận
CREATE TRIGGER trg_hoa_don_xac_nhan ON hoa_don AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON; IF NOT UPDATE(trang_thai_don) RETURN;
    DECLARE @valid TABLE (id_hoa_don INT);
    -- Trừ kho khi chuyển từ trạng thái chờ sang trạng thái (Xác nhận/Đang giao/Đã giao)
    INSERT INTO @valid SELECT i.id_hoa_don FROM inserted i JOIN deleted d ON i.id_hoa_don = d.id_hoa_don 
    WHERE (i.trang_thai_don IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO')) 
      AND (d.trang_thai_don NOT IN ('DA_XAC_NHAN', 'DANG_GIAO', 'DA_GIAO', 'DA_HUY'));
    
    IF NOT EXISTS (SELECT 1 FROM @valid) RETURN;

    DECLARE @grouped TABLE (id_spct INT, total_qty INT);
    INSERT INTO @grouped SELECT hdct.id_spct, SUM(hdct.so_luong) FROM hoa_don_chi_tiet hdct JOIN @valid v ON hdct.id_hoa_don = v.id_hoa_don GROUP BY hdct.id_spct;
    IF EXISTS (SELECT 1 FROM @grouped g JOIN san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK) ON g.id_spct = spct.id_spct WHERE g.total_qty > spct.so_luong)
    BEGIN RAISERROR(N'Không đủ tồn kho',16,1); ROLLBACK TRANSACTION; RETURN; END

    UPDATE spct SET so_luong = spct.so_luong - g.total_qty, so_luong_da_ban = so_luong_da_ban + g.total_qty FROM san_pham_chi_tiet spct JOIN @grouped g ON spct.id_spct = g.id_spct;
END
GO

-- 6. Hóa đơn: Hoàn kho khi Hủy
CREATE TRIGGER trg_hoa_don_huy ON hoa_don AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON; IF NOT UPDATE(trang_thai_don) RETURN;
    DECLARE @cancel TABLE (id_hoa_don INT);
    -- Hoàn kho khi hủy từ các trạng thái đã trừ kho
    INSERT INTO @cancel SELECT i.id_hoa_don FROM inserted i JOIN deleted d ON i.id_hoa_don = d.id_hoa_don 
    WHERE i.trang_thai_don = 'DA_HUY' AND d.trang_thai_don IN ('DA_XAC_NHAN','DANG_GIAO', 'DA_GIAO');
    IF NOT EXISTS (SELECT 1 FROM @cancel) RETURN;

    UPDATE spct SET spct.so_luong = spct.so_luong + hdct.so_luong, spct.so_luong_da_ban = CASE WHEN spct.so_luong_da_ban - hdct.so_luong < 0 THEN 0 ELSE spct.so_luong_da_ban - hdct.so_luong END
    FROM san_pham_chi_tiet spct JOIN hoa_don_chi_tiet hdct ON spct.id_spct = hdct.id_spct JOIN @cancel c ON hdct.id_hoa_don = c.id_hoa_don;
END
GO

-- 7. Đổi trả: Validate khắt khe
CREATE TRIGGER trg_check_so_luong_tra ON doi_tra_chi_tiet INSTEAD OF INSERT AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don WHERE hd.trang_thai_don NOT IN ('DA_GIAO','YEU_CAU_TRA_HANG','HOAN_TRA_MOT_PHAN')) BEGIN RAISERROR(N'Chỉ được đổi trả khi đơn đã giao hoặc đang xử lý đổi trả',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don WHERE (hd.loai_don_hang = 'ONLINE' AND (hd.ngay_giao IS NULL OR DATEDIFF(DAY, hd.ngay_giao, GETDATE()) > 7)) OR (hd.loai_don_hang = 'TAI_QUAY' AND DATEDIFF(DAY, hd.ngay_tao, GETDATE()) > 3)) BEGIN RAISERROR(N'Quá thời gian đổi trả',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don WHERE (hd.loai_don_hang = 'ONLINE' AND dt.loai_doi_tra <> 'HOAN_TIEN') OR (hd.loai_don_hang = 'TAI_QUAY' AND dt.loai_doi_tra <> 'DOI_HANG')) BEGIN RAISERROR(N'Loại đổi trả không hợp lệ',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra WHERE dt.tinh_trang_hang = 'DA_SU_DUNG') BEGIN RAISERROR(N'Hàng đã sử dụng không được đổi trả',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra WHERE dt.loai_doi_tra = 'DOI_HANG' AND dt.id_spct_moi IS NULL) BEGIN RAISERROR(N'Phải chọn sản phẩm đổi',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra JOIN san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK) ON dt.id_spct_moi = spct.id_spct WHERE dt.loai_doi_tra = 'DOI_HANG' AND spct.so_luong < i.so_luong_tra) BEGIN RAISERROR(N'Sản phẩm đổi không đủ hàng',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra JOIN hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct JOIN san_pham_chi_tiet spct_cu ON hdct.id_spct = spct_cu.id_spct JOIN san_pham_chi_tiet spct_moi ON dt.id_spct_moi = spct_moi.id_spct WHERE dt.loai_doi_tra = 'DOI_HANG' AND spct_cu.id_san_pham <> spct_moi.id_san_pham) BEGIN RAISERROR(N'Chỉ được đổi cùng loại sản phẩm',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct WHERE hdct.da_doi_tra = 1) BEGIN RAISERROR(N'Sản phẩm đã được đổi trả',16,1); ROLLBACK; RETURN; END
    IF EXISTS (SELECT 1 FROM inserted i JOIN hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct LEFT JOIN (SELECT id_hdct, SUM(so_luong_tra) tong_da_tra FROM doi_tra_chi_tiet GROUP BY id_hdct) d ON d.id_hdct = i.id_hdct WHERE ISNULL(d.tong_da_tra,0) + i.so_luong_tra > hdct.so_luong) BEGIN RAISERROR(N'Vượt quá số lượng mua',16,1); ROLLBACK; RETURN; END

    INSERT INTO doi_tra_chi_tiet (id_doi_tra, id_hdct, so_luong_tra, gia_tri_hoan) SELECT i.id_doi_tra, i.id_hdct, i.so_luong_tra, i.gia_tri_hoan FROM inserted i;
END
GO

-- 8. Đổi trả: Tính tổng tiền hoàn tự động
CREATE TRIGGER trg_update_tien_hoan ON doi_tra_chi_tiet AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dt SET tong_tien_hoan = ISNULL(x.total,0) FROM doi_tra dt LEFT JOIN (SELECT id_doi_tra, SUM(gia_tri_hoan) total FROM doi_tra_chi_tiet GROUP BY id_doi_tra) x ON dt.id_doi_tra = x.id_doi_tra WHERE dt.id_doi_tra IN (SELECT id_doi_tra FROM inserted UNION SELECT id_doi_tra FROM deleted);
    IF EXISTS (SELECT 1 FROM doi_tra dt JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don WHERE dt.tong_tien_hoan > hd.tong_tien_hang) BEGIN RAISERROR(N'Hoàn vượt giá trị đơn hàng',16,1); ROLLBACK; RETURN; END
END
GO

-- 9. Đổi trả: Trigger Tổng Hợp (Thực thi Hoàn trả, Trừ kho mới, Ghi log)
CREATE TRIGGER trg_xu_ly_doi_tra ON doi_tra AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON; IF NOT UPDATE(trang_thai) RETURN;

    -- A. Chặn chỉnh sửa
    IF EXISTS (SELECT 1 FROM inserted i JOIN deleted d ON i.id_doi_tra = d.id_doi_tra WHERE d.trang_thai IN ('HOAN_THANH','TU_CHOI'))
    BEGIN RAISERROR(N'Không thể chỉnh sửa yêu cầu đã hoàn tất',16,1); ROLLBACK; RETURN; END

    -- B. Xử lý khi HOAN_THANH
    IF EXISTS (SELECT 1 FROM inserted i JOIN deleted d ON i.id_doi_tra = d.id_doi_tra WHERE i.trang_thai = 'HOAN_THANH' AND d.trang_thai <> 'HOAN_THANH')
    BEGIN
        IF EXISTS (SELECT 1 FROM inserted i LEFT JOIN doi_tra_chi_tiet dtct ON i.id_doi_tra = dtct.id_doi_tra WHERE i.trang_thai = 'HOAN_THANH' AND dtct.id_doi_tra IS NULL) BEGIN RAISERROR(N'Không có sản phẩm để đổi/hoàn',16,1); ROLLBACK; RETURN; END

        -- Cập nhật cờ hóa đơn
        UPDATE hdct SET da_doi_tra = 1 FROM hoa_don_chi_tiet hdct JOIN doi_tra_chi_tiet dtct ON dtct.id_hdct = hdct.id_hdct JOIN inserted i ON dtct.id_doi_tra = i.id_doi_tra WHERE i.trang_thai = 'HOAN_THANH';

        -- Hoàn kho cũ
        UPDATE spct SET spct.so_luong = spct.so_luong + dtct.so_luong_tra FROM san_pham_chi_tiet spct JOIN hoa_don_chi_tiet hdct ON spct.id_spct = hdct.id_spct JOIN doi_tra_chi_tiet dtct ON dtct.id_hdct = hdct.id_hdct JOIN inserted i ON dtct.id_doi_tra = i.id_doi_tra WHERE i.trang_thai = 'HOAN_THANH';

        -- Trừ kho mới (nếu ĐỔI)
        IF EXISTS (SELECT 1 FROM inserted WHERE trang_thai = 'HOAN_THANH' AND loai_doi_tra = 'DOI_HANG') BEGIN
            UPDATE spct SET spct.so_luong = spct.so_luong - dtct.so_luong_tra FROM san_pham_chi_tiet spct JOIN inserted i ON spct.id_spct = i.id_spct_moi JOIN doi_tra_chi_tiet dtct ON dtct.id_doi_tra = i.id_doi_tra WHERE i.trang_thai = 'HOAN_THANH' AND i.loai_doi_tra = 'DOI_HANG';
        END
    END

    -- C. Ghi log nếu TỪ CHỐI
    DECLARE @user_id INT = CAST(SESSION_CONTEXT(N'user_id') AS INT);
    INSERT INTO lich_su_hoa_don (ma_lich_su, id_hoa_don, trang_thai_cu, trang_thai_moi, loai_hanh_dong, hanh_dong, id_nguoi_thuc_hien, thoi_gian)
    SELECT CONCAT('LSHD_', REPLACE(NEWID(),'-','')), i.id_hoa_don, 'DOI_TRA_' + d.trang_thai, 'DOI_TRA_' + i.trang_thai, 'DOI_TRA', N'Từ chối đổi trả: ' + ISNULL(i.ly_do_tu_choi, N'Không có lý do'), ISNULL(@user_id, 0), GETDATE() FROM inserted i JOIN deleted d ON i.id_doi_tra = d.id_doi_tra WHERE i.trang_thai = 'TU_CHOI' AND d.trang_thai <> 'TU_CHOI';
END
GO
IF COL_LENGTH('dbo.tinh', 'phi_ship_mac_dinh') IS NULL
BEGIN
    ALTER TABLE tinh ADD phi_ship_mac_dinh DECIMAL(18,2) NULL;
END
GO

-- 2. Cập nhật dữ liệu mẫu (Giả sử shop ở Hà Nội)
-- Nội thành rẻ nhất
UPDATE tinh SET phi_ship_mac_dinh = 20000 WHERE ten_tinh LIKE N'%Hà Nội%';

-- Tỉnh lân cận
UPDATE tinh SET phi_ship_mac_dinh = 25000 WHERE ten_tinh IN (N'Bắc Ninh', N'Hưng Yên', N'Hà Nam');

-- Các tỉnh xa ở Miền Nam
UPDATE tinh SET phi_ship_mac_dinh = 40000 WHERE ten_tinh LIKE N'%Hồ Chí Minh%' OR ten_tinh LIKE N'%Cần Thơ%' OR ten_tinh LIKE N'%Đà Nẵng%';
UPDATE tinh 
SET phi_ship_mac_dinh = 30000 
WHERE phi_ship_mac_dinh IS NULL;
GO

select * from tinh
select * from san_pham
select *  from san_pham_chi_tiet 
ALTER TABLE pt_thanh_toan 
ADD CONSTRAINT uq_ma_pt_thanh_toan UNIQUE (ma_pt_thanh_toan);
GO
