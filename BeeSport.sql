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

	CREATE TABLE nguoi_dung (
		id_nguoi_dung INT IDENTITY PRIMARY KEY,
		id_vai_tro INT,
		ma_nguoi_dung NVARCHAR(50),
		ho_ten NVARCHAR(150),
		so_dien_thoai NVARCHAR(20) UNIQUE,
		email NVARCHAR(150) UNIQUE NOT NULL,
		mat_khau NVARCHAR(255),
		trang_thai BIT DEFAULT 1,
		ngay_tao DATETIME DEFAULT GETDATE(),
		FOREIGN KEY (id_vai_tro) REFERENCES vai_tro(id_vai_tro)
	);
	GO

	CREATE TABLE xac_thuc (
		id_xac_thuc INT IDENTITY PRIMARY KEY,
		id_nguoi_dung INT,
		ma_xac_thuc NVARCHAR(50),
		loai_xac_thuc NVARCHAR(50),
		trang_thai BIT DEFAULT 1,
		FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
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

	CREATE TABLE san_pham (
    id_san_pham INT IDENTITY PRIMARY KEY,

    id_danh_muc INT NOT NULL,
    id_thuong_hieu INT,
    id_chat_lieu INT, 

    ma_san_pham NVARCHAR(50) UNIQUE,
    ten_san_pham NVARCHAR(200) NOT NULL,

    gia_goc DECIMAL(18,2) NOT NULL,

    trang_thai BIT DEFAULT 1,

    FOREIGN KEY (id_danh_muc) REFERENCES danh_muc(id_danh_muc),
    FOREIGN KEY (id_thuong_hieu) REFERENCES thuong_hieu(id_thuong_hieu),
    FOREIGN KEY (id_chat_lieu) REFERENCES chat_lieu(id_chat_lieu) 
);
GO

	CREATE TABLE hinh_anh_san_pham (
		id_hinh_anh INT IDENTITY PRIMARY KEY,
		id_san_pham INT,
		ma_hinh_anh_san_pham NVARCHAR(50),
		url NVARCHAR(255),
		trang_thai BIT DEFAULT 1,
		FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham)
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




	CREATE TABLE gio_hang (
		id_gio_hang INT IDENTITY PRIMARY KEY,
		ma_gio_hang NVARCHAR(50) UNIQUE,
		id_nguoi_dung INT NOT NULL,

		loai_gio_hang NVARCHAR(20) DEFAULT 'ONLINE'
		CHECK (loai_gio_hang IN ('ONLINE', 'TAI_QUAY')),

		trang_thai NVARCHAR(30)
		CHECK (trang_thai IN ('DANG_SU_DUNG','DA_THANH_TOAN'))
		DEFAULT 'DANG_SU_DUNG',

		ngay_tao DATETIME DEFAULT GETDATE(),

		FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
	);
	GO
	CREATE UNIQUE INDEX idx_unique_active_cart
	ON gio_hang(id_nguoi_dung)
	WHERE trang_thai = 'DANG_SU_DUNG' AND loai_gio_hang = 'ONLINE';

CREATE TABLE san_pham_chi_tiet (
    id_spct INT IDENTITY PRIMARY KEY,

    id_san_pham INT NOT NULL,
    id_kich_thuoc INT NOT NULL,
    id_mau_sac INT NOT NULL,
    id_thuong_hieu INT NOT NULL,

    ma_san_pham_chi_tiet NVARCHAR(50) UNIQUE,

    so_luong INT NOT NULL CHECK (so_luong >= 0),
    so_luong_da_ban INT DEFAULT 0,

    gia_ban DECIMAL(18,2) NOT NULL,

    trang_thai BIT DEFAULT 1,

    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham),
    FOREIGN KEY (id_kich_thuoc) REFERENCES kich_thuoc(id_kich_thuoc),
    FOREIGN KEY (id_mau_sac) REFERENCES mau_sac(id_mau_sac),
    FOREIGN KEY (id_thuong_hieu) REFERENCES thuong_hieu(id_thuong_hieu),

    CONSTRAINT unique_spct 
    UNIQUE (id_san_pham, id_kich_thuoc, id_mau_sac, id_thuong_hieu)
);
GO
	ALTER TABLE san_pham_chi_tiet
	ADD CONSTRAINT chk_sold_qty 
	CHECK (so_luong_da_ban >= 0);



	CREATE TABLE danh_gia_san_pham (
		id_danh_gia INT IDENTITY PRIMARY KEY,
		ma_danh_gia NVARCHAR(50) UNIQUE,

		id_nguoi_dung INT NOT NULL,
		id_san_pham INT NOT NULL,

		so_sao INT CHECK (so_sao BETWEEN 1 AND 5),

		noi_dung NVARCHAR(500),

		ngay_danh_gia DATETIME DEFAULT GETDATE(),

		trang_thai BIT DEFAULT 1,

		FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
		FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham),

		CONSTRAINT unique_review
		UNIQUE (id_nguoi_dung, id_san_pham)
	);
	GO

	CREATE TABLE san_pham_yeu_thich (
		id_yeu_thich INT IDENTITY PRIMARY KEY,

		ma_san_pham_yeu_thich NVARCHAR(50) UNIQUE,

		id_nguoi_dung INT NOT NULL,
		id_san_pham INT NOT NULL,

		ngay_them DATETIME DEFAULT GETDATE(),

		FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
		FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham),

		CONSTRAINT unique_wishlist
		UNIQUE (id_nguoi_dung, id_san_pham)
	);
	GO

	CREATE TABLE gio_hang_chi_tiet (
		id_ghct INT IDENTITY PRIMARY KEY,
		id_gio_hang INT NOT NULL,
		id_spct INT NOT NULL,

		ma_gio_hang_chi_tiet NVARCHAR(50) UNIQUE,

		so_luong INT NOT NULL CHECK (so_luong > 0),

		don_gia DECIMAL(18,2),      -- giá tại thời điểm thêm vào giỏ
		chon BIT DEFAULT 1,         -- checkbox chọn sản phẩm thanh toán

		ngay_them DATETIME DEFAULT GETDATE(),

		FOREIGN KEY (id_gio_hang) REFERENCES gio_hang(id_gio_hang),
		FOREIGN KEY (id_spct) REFERENCES san_pham_chi_tiet(id_spct),

		CONSTRAINT unique_cart_product 
		UNIQUE (id_gio_hang, id_spct)
	);
	GO

	CREATE TABLE ma_giam_gia (
		id_ma_giam_gia INT IDENTITY PRIMARY KEY,
		ma_code NVARCHAR(50) UNIQUE,
		kieu_giam_gia NVARCHAR(20)
		CHECK (kieu_giam_gia IN ('PERCENT','AMOUNT')), -- PERCENT | AMOUNT
		gia_tri_giam DECIMAL(18,2),
		gia_tri_giam_toi_da DECIMAL(18,2),
		gia_tri_toi_thieu DECIMAL(18,2),
		so_luong INT,
		so_luong_da_dung INT DEFAULT 0,
		ngay_bat_dau DATETIME,
		ngay_ket_thuc DATETIME,
		trang_thai BIT DEFAULT 1
	);
	GO
	ALTER TABLE ma_giam_gia
	ADD CONSTRAINT chk_discount_value 
	CHECK (gia_tri_giam > 0);
	ALTER TABLE ma_giam_gia
	ADD CONSTRAINT chk_usage 
	CHECK (so_luong_da_dung <= so_luong);
	ALTER TABLE ma_giam_gia
	ADD CONSTRAINT chk_discount_range
	CHECK (
		(kieu_giam_gia = 'PERCENT' AND gia_tri_giam <= 100)
		OR (kieu_giam_gia = 'AMOUNT')
	);

	CREATE TABLE dot_giam_gia (
		id_dot_giam_gia INT IDENTITY PRIMARY KEY,

		ma_dot_giam_gia NVARCHAR(50) UNIQUE,

		ten_dot NVARCHAR(150),

		kieu_giam_gia NVARCHAR(20)
		CHECK (kieu_giam_gia IN ('PERCENT','AMOUNT')),

		gia_tri_giam DECIMAL(18,2),

		ngay_bat_dau DATETIME,
		ngay_ket_thuc DATETIME,

		trang_thai BIT DEFAULT 1
	);
	GO

	CREATE TABLE giam_gia_san_pham (
    id_giam_gia_san_pham INT IDENTITY PRIMARY KEY,

    ma_giam_gia_san_pham NVARCHAR(50),

    id_dot_giam_gia INT,
    id_spct INT,

    trang_thai BIT DEFAULT 1,

    FOREIGN KEY (id_dot_giam_gia) REFERENCES dot_giam_gia(id_dot_giam_gia),
    FOREIGN KEY (id_spct) REFERENCES san_pham_chi_tiet(id_spct),

    CONSTRAINT unique_discount_spct
    UNIQUE (id_dot_giam_gia, id_spct)
);
	GO

	CREATE TABLE pt_thanh_toan (
		id_pttt INT IDENTITY PRIMARY KEY,
		ma_pt_thanh_toan NVARCHAR(50),
		ten_pttt NVARCHAR(100),
		trang_thai BIT DEFAULT 1
	);
	GO

CREATE TABLE hoa_don (
    id_hoa_don INT IDENTITY PRIMARY KEY,
    ma_hoa_don NVARCHAR(50) UNIQUE,

    id_nguoi_dung INT NOT NULL,
    id_ma_giam_gia INT NULL,
    id_pttt INT NULL,
    loai_don_hang NVARCHAR(20) DEFAULT 'ONLINE',

    ten_nguoi_nhan NVARCHAR(150) NOT NULL,
    so_dien_thoai NVARCHAR(20) NOT NULL,
    tinh NVARCHAR(100),
    huyen NVARCHAR(100),
    xa NVARCHAR(100),
    dia_chi_chi_tiet NVARCHAR(255),

    tong_tien_hang DECIMAL(18,2) DEFAULT 0,
    tien_giam DECIMAL(18,2) DEFAULT 0,
    phi_van_chuyen DECIMAL(18,2) DEFAULT 0,
    tong_thanh_toan DECIMAL(18,2) DEFAULT 0,

    trang_thai_don NVARCHAR(30) DEFAULT 'CHO_XAC_NHAN',

    ghi_chu NVARCHAR(255),

    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_cap_nhat DATETIME DEFAULT GETDATE(),
	ngay_giao DATETIME,
    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
    FOREIGN KEY (id_ma_giam_gia) REFERENCES ma_giam_gia(id_ma_giam_gia),
    FOREIGN KEY (id_pttt) REFERENCES pt_thanh_toan(id_pttt)
);
GO

ALTER TABLE hoa_don
ADD CONSTRAINT chk_loai_don_hang
CHECK (loai_don_hang IN ('ONLINE','TAI_QUAY'));
GO

ALTER TABLE hoa_don
ADD CONSTRAINT chk_trang_thai_don
CHECK (trang_thai_don IN 
(
    'CHO_XAC_NHAN',
    'DA_XAC_NHAN',
    'DANG_GIAO',
    'DA_GIAO',
    'DA_HUY',
    'HOAN_TRA',
    'YEU_CAU_TRA_HANG',
    'HOAN_TRA_MOT_PHAN'
));
	CREATE TABLE hoa_don_chi_tiet (
		id_hdct INT IDENTITY PRIMARY KEY,
		ma_hoa_don_chi_tiet NVARCHAR(50) UNIQUE,

		id_hoa_don INT NOT NULL,
		id_spct INT NOT NULL,


		ten_san_pham NVARCHAR(200) NOT NULL,
		kich_thuoc NVARCHAR(50),
		mau_sac NVARCHAR(50),
		chat_lieu NVARCHAR(50),

		don_gia DECIMAL(18,2) NOT NULL,
		so_luong INT NOT NULL CHECK (so_luong > 0),

		thanh_tien DECIMAL(18,2) NOT NULL,

		ngay_tao DATETIME DEFAULT GETDATE(),
		da_doi_tra BIT DEFAULT 0,
		FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don),
		FOREIGN KEY (id_spct) REFERENCES san_pham_chi_tiet(id_spct)
	);
	GO
	ALTER TABLE hoa_don_chi_tiet
	ADD CONSTRAINT chk_so_luong_hdct 
	CHECK (so_luong > 0);



	CREATE TABLE lich_su_thanh_toan (
		id_lstt INT IDENTITY PRIMARY KEY,
		ma_lich_su_thanh_toan NVARCHAR(50),
		id_hoa_don INT,
		id_pttt INT,
		so_tien DECIMAL(18,2),
		loai_giao_dich NVARCHAR(20),
		trang_thai_thanh_toan NVARCHAR(30)
		CHECK (trang_thai_thanh_toan IN ('CHO_THANH_TOAN','DA_THANH_TOAN','THAT_BAI')),
		CHECK (loai_giao_dich IN ('THANH_TOAN','HOAN_TIEN')),
		ngay_thanh_toan DATETIME DEFAULT GETDATE(),
		FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don),
		FOREIGN KEY (id_pttt) REFERENCES pt_thanh_toan(id_pttt)
	);
	GO

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
		id_tinh INT,
		ten_huyen NVARCHAR(100),
		trang_thai BIT DEFAULT 1,
		FOREIGN KEY (id_tinh) REFERENCES tinh(id_tinh)
	);
	GO

	CREATE TABLE xa (
		id_xa INT IDENTITY PRIMARY KEY,
		ma_xa NVARCHAR(50),
		id_huyen INT,
		ten_xa NVARCHAR(100),
		trang_thai BIT DEFAULT 1,
		FOREIGN KEY (id_huyen) REFERENCES huyen(id_huyen)
	);
	GO

	CREATE TABLE dia_chi_van_chuyen (
		id_dia_chi INT IDENTITY PRIMARY KEY,
		ma_dia_chi_van_chuyen NVARCHAR(50),
		id_nguoi_dung INT,
		id_xa INT,

		ten_nguoi_nhan NVARCHAR(150),
		so_dien_thoai NVARCHAR(20),
		dia_chi_chi_tiet NVARCHAR(255),

		loai_dia_chi NVARCHAR(30), -- NHA_RIENG | VAN_PHONG
		la_mac_dinh BIT DEFAULT 0,

		trang_thai BIT DEFAULT 1,
		FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
		FOREIGN KEY (id_xa) REFERENCES xa(id_xa)
	);
	GO

	CREATE TABLE lich_su_su_dung_ma_giam_gia (
		id_ls INT IDENTITY PRIMARY KEY,

		ma_lich_su_su_dung_ma_giam_gia NVARCHAR(50),

		id_ma_giam_gia INT,
		id_hoa_don INT,

		ngay_su_dung DATETIME DEFAULT GETDATE(),

		FOREIGN KEY (id_ma_giam_gia) REFERENCES ma_giam_gia(id_ma_giam_gia),
		FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don)
	);
	GO

DROP TABLE IF EXISTS lich_su_hoa_don;
GO

CREATE TABLE lich_su_hoa_don (
    id_ls_hd INT IDENTITY PRIMARY KEY,

    ma_lich_su NVARCHAR(50) UNIQUE,

    id_hoa_don INT NOT NULL,

    trang_thai_cu NVARCHAR(30),
    trang_thai_moi NVARCHAR(30),

    loai_hanh_dong NVARCHAR(50), -- UPDATE_STATUS, CREATE, CANCEL...
    hanh_dong NVARCHAR(255),

    id_nguoi_thuc_hien INT NULL, -- admin/staff/user

    thoi_gian DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don),
    FOREIGN KEY (id_nguoi_thuc_hien) REFERENCES nguoi_dung(id_nguoi_dung)
);
GO

CREATE TABLE doi_tra (
		id_doi_tra INT IDENTITY PRIMARY KEY,
		ma_doi_tra NVARCHAR(50),
		id_hoa_don INT,
		loai_doi_tra NVARCHAR(20),
		ly_do NVARCHAR(255),
		ly_do_tu_choi NVARCHAR(255),
		tinh_trang_hang NVARCHAR(20),
		danh_sach_anh NVARCHAR(MAX) NULL,
		tong_tien_hoan DECIMAL(18,2) DEFAULT 0,
		tien_chenh_lech DECIMAL(18,2) DEFAULT 0,
		phi_ship_hoan DECIMAL(18,2) DEFAULT 0,
		ghi_chu_admin NVARCHAR(500),
		trang_thai_thanh_toan NVARCHAR(30),
		ngay_xu_ly DATETIME,
		trang_thai NVARCHAR(30)
		CHECK (trang_thai IN ('CHO_XAC_NHAN_HOAN','CHO_GIAO_HANG','DA_NHAN_HANG_KIEM_TRA','HOAN_THANH','TU_CHOI','CANCELLED')),
		CHECK (loai_doi_tra IN ('REFUND','EXCHANGE')),
		CHECK (tinh_trang_hang IN ('NGUYEN_VEN','LOI','DA_SU_DUNG')),
		ngay_yeu_cau DATETIME DEFAULT GETDATE(),
		FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don)
	);
	GO
	ALTER TABLE doi_tra
	ADD CONSTRAINT chk_tien_hoan_max
	CHECK (tong_tien_hoan >= 0);
	GO

	CREATE TABLE doi_tra_chi_tiet (
		id_doi_tra_ct INT IDENTITY PRIMARY KEY,
		id_doi_tra INT NOT NULL,
		id_hdct INT NOT NULL,
		so_luong_tra INT NOT NULL CHECK (so_luong_tra > 0),
		gia_tri_hoan DECIMAL(18,2) NOT NULL,
		FOREIGN KEY (id_doi_tra) REFERENCES doi_tra(id_doi_tra),
		FOREIGN KEY (id_hdct) REFERENCES hoa_don_chi_tiet(id_hdct)
	);
	GO
	ALTER TABLE doi_tra_chi_tiet
	ADD CONSTRAINT chk_gia_tri_hoan_positive CHECK (gia_tri_hoan >= 0);
	GO

CREATE INDEX idx_review_product ON danh_gia_san_pham(id_san_pham);

CREATE INDEX idx_hoadon_user ON hoa_don(id_nguoi_dung);
CREATE INDEX idx_hoadon_ngay ON hoa_don(ngay_tao);
CREATE INDEX idx_hoadon_trangthai ON hoa_don(trang_thai_don);
CREATE INDEX idx_hoadon_user_trangthai ON hoa_don(id_nguoi_dung, trang_thai_don);

CREATE INDEX idx_hdct_hoa_don ON hoa_don_chi_tiet(id_hoa_don);
CREATE INDEX idx_hdct_spct ON hoa_don_chi_tiet(id_spct);

CREATE INDEX idx_doi_tra_hoa_don ON doi_tra(id_hoa_don);
CREATE INDEX idx_doi_tra_trang_thai ON doi_tra(trang_thai);
CREATE INDEX idx_dtct_doi_tra ON doi_tra_chi_tiet(id_doi_tra);

CREATE INDEX idx_giohang_user ON gio_hang(id_nguoi_dung);
CREATE INDEX idx_ghct_spct ON gio_hang_chi_tiet(id_spct);
CREATE INDEX idx_ghct_giohang_spct ON gio_hang_chi_tiet(id_gio_hang, id_spct);

CREATE INDEX idx_lstt_hoa_don ON lich_su_thanh_toan(id_hoa_don);
CREATE INDEX idx_lshd_hoa_don ON lich_su_hoa_don(id_hoa_don);



DROP TRIGGER IF EXISTS trg_cart_insert;
GO
CREATE TRIGGER trg_cart_insert
ON gio_hang_chi_tiet
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @grouped TABLE (
        id_gio_hang INT,
        id_spct INT,
        total_qty INT
    );

    INSERT INTO @grouped
    SELECT 
        id_gio_hang,
        id_spct,
        SUM(so_luong)
    FROM inserted
    GROUP BY id_gio_hang, id_spct;

    ----------------------------------
    -- check tồn kho
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM @grouped g
        JOIN san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK)
            ON g.id_spct = spct.id_spct
        LEFT JOIN gio_hang_chi_tiet ghct
            ON ghct.id_spct = g.id_spct
            AND ghct.id_gio_hang = g.id_gio_hang
        WHERE ISNULL(ghct.so_luong,0) + g.total_qty > spct.so_luong
    )
    BEGIN
        RAISERROR(N'Vượt quá tồn kho',16,1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    ----------------------------------
    -- update
    ----------------------------------
    UPDATE ghct
    SET 
        so_luong = ghct.so_luong + g.total_qty,
        don_gia = spct.gia_ban
    FROM gio_hang_chi_tiet ghct
    JOIN @grouped g
        ON ghct.id_gio_hang = g.id_gio_hang
        AND ghct.id_spct = g.id_spct
    JOIN san_pham_chi_tiet spct
        ON spct.id_spct = g.id_spct;

    ----------------------------------
    -- insert
    ----------------------------------
    INSERT INTO gio_hang_chi_tiet
    (
        id_gio_hang,
        id_spct,
        ma_gio_hang_chi_tiet,
        so_luong,
        don_gia,
        chon
    )
    SELECT 
        g.id_gio_hang,
        g.id_spct,
        CONCAT('GHCT_', NEWID()),
        g.total_qty,
        spct.gia_ban,
        1
    FROM @grouped g
    JOIN san_pham_chi_tiet spct 
        ON spct.id_spct = g.id_spct
    WHERE NOT EXISTS (
        SELECT 1
        FROM gio_hang_chi_tiet ghct
        WHERE ghct.id_gio_hang = g.id_gio_hang
        AND ghct.id_spct = g.id_spct
    );

END
GO


DROP TRIGGER IF EXISTS trg_update_tong_tien_hd;
GO
CREATE TRIGGER trg_update_tong_tien_hd
ON hoa_don_chi_tiet
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @hd TABLE (id_hoa_don INT);

    INSERT INTO @hd
    SELECT id_hoa_don FROM inserted
    UNION
    SELECT id_hoa_don FROM deleted;

    -- update tổng tiền hàng
    UPDATE hd
    SET tong_tien_hang = ISNULL(x.total,0)
    FROM hoa_don hd
    LEFT JOIN (
        SELECT id_hoa_don, SUM(thanh_tien) total
        FROM hoa_don_chi_tiet
        GROUP BY id_hoa_don
    ) x ON hd.id_hoa_don = x.id_hoa_don
    WHERE hd.id_hoa_don IN (SELECT id_hoa_don FROM @hd);

    -- update thanh toán
    UPDATE hoa_don
    SET tong_thanh_toan =
        ISNULL(tong_tien_hang,0)
        - ISNULL(tien_giam,0)
        + ISNULL(phi_van_chuyen,0)
    WHERE id_hoa_don IN (SELECT id_hoa_don FROM @hd);

END
GO

DROP TRIGGER IF EXISTS trg_hoa_don_xac_nhan;
GO
CREATE TRIGGER trg_hoa_don_xac_nhan
ON hoa_don
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF NOT UPDATE(trang_thai_don)
        RETURN;

    DECLARE @valid TABLE (id_hoa_don INT);

    INSERT INTO @valid
    SELECT i.id_hoa_don
    FROM inserted i
    JOIN deleted d ON i.id_hoa_don = d.id_hoa_don
    WHERE 
        i.trang_thai_don IN ('DA_XAC_NHAN', 'DA_GIAO')
        AND d.trang_thai_don NOT IN ('DA_XAC_NHAN', 'DA_GIAO');

    IF NOT EXISTS (SELECT 1 FROM @valid)
        RETURN;

    ----------------------------------
    -- gom sản phẩm
    ----------------------------------
    DECLARE @grouped TABLE (
        id_spct INT,
        total_qty INT
    );

    INSERT INTO @grouped
    SELECT 
        hdct.id_spct,
        SUM(hdct.so_luong)
    FROM hoa_don_chi_tiet hdct
    JOIN @valid v ON hdct.id_hoa_don = v.id_hoa_don
    GROUP BY hdct.id_spct;

    ----------------------------------
    -- check tồn kho + lock
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM @grouped g
        JOIN san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK)
            ON g.id_spct = spct.id_spct
        WHERE g.total_qty > spct.so_luong
    )
    BEGIN
        RAISERROR(N'Không đủ tồn kho',16,1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    ----------------------------------
    -- trừ kho
    ----------------------------------
    UPDATE spct
    SET 
        so_luong = so_luong - g.total_qty,
        so_luong_da_ban = so_luong_da_ban + g.total_qty
    FROM san_pham_chi_tiet spct
    JOIN @grouped g ON spct.id_spct = g.id_spct;

END
GO

DROP TRIGGER IF EXISTS trg_hoa_don_huy;
GO
	CREATE TRIGGER trg_hoa_don_huy
	ON hoa_don
	AFTER UPDATE
	AS
	BEGIN
		SET NOCOUNT ON;
		SET XACT_ABORT ON;

		IF NOT UPDATE(trang_thai_don)
			RETURN;

		DECLARE @cancel TABLE (id_hoa_don INT);

		INSERT INTO @cancel
		SELECT i.id_hoa_don
		FROM inserted i
		JOIN deleted d 
			ON i.id_hoa_don = d.id_hoa_don
		WHERE 
			i.trang_thai_don = 'DA_HUY'
			AND d.trang_thai_don IN ('DA_XAC_NHAN','DANG_GIAO');

		IF NOT EXISTS (SELECT 1 FROM @cancel)
			RETURN;

		UPDATE spct
		SET 
			spct.so_luong = spct.so_luong + hdct.so_luong,
			spct.so_luong_da_ban = 
				CASE 
					WHEN spct.so_luong_da_ban - hdct.so_luong < 0 THEN 0
					ELSE spct.so_luong_da_ban - hdct.so_luong
				END
		FROM san_pham_chi_tiet spct
		JOIN hoa_don_chi_tiet hdct 
			ON spct.id_spct = hdct.id_spct
		JOIN @cancel c 
			ON hdct.id_hoa_don = c.id_hoa_don;

	END
GO



DROP TRIGGER IF EXISTS trg_validate_trang_thai;
GO

CREATE TRIGGER trg_validate_trang_thai
ON hoa_don
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF NOT UPDATE(trang_thai_don) RETURN;

    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN deleted d ON i.id_hoa_don = d.id_hoa_don
        WHERE
        (d.trang_thai_don = 'CHO_XAC_NHAN' AND i.trang_thai_don NOT IN ('DA_XAC_NHAN','DA_HUY'))
        OR (d.trang_thai_don = 'DA_XAC_NHAN' AND i.trang_thai_don NOT IN ('DANG_GIAO','DA_HUY'))
        OR (d.trang_thai_don = 'DANG_GIAO' AND i.trang_thai_don NOT IN ('DA_GIAO','DA_HUY'))
        OR (d.trang_thai_don = 'DA_GIAO' AND i.trang_thai_don NOT IN ('HOAN_TRA', 'DA_GIAO', 'YEU_CAU_TRA_HANG', 'HOAN_TRA_MOT_PHAN'))
        OR (d.trang_thai_don = 'YEU_CAU_TRA_HANG' AND i.trang_thai_don NOT IN ('HOAN_TRA', 'HOAN_TRA_MOT_PHAN', 'DA_GIAO'))
    )
    BEGIN
        RAISERROR(N'Cập nhật trạng thái không hợp lệ theo quy trình',16,1);
        ROLLBACK TRANSACTION;
    END
END
GO

DROP TRIGGER IF EXISTS trg_log_hoa_don;
GO

CREATE TRIGGER trg_log_hoa_don
ON hoa_don
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT UPDATE(trang_thai_don)
        RETURN;

    DECLARE @user_id INT;
    SET @user_id = CAST(SESSION_CONTEXT(N'user_id') AS INT);
    IF @user_id IS NULL SET @user_id = 1; -- Mặc định là Admin ND001

    INSERT INTO lich_su_hoa_don
    (
        ma_lich_su,
        id_hoa_don,
        trang_thai_cu,
        trang_thai_moi,
        loai_hanh_dong,
        hanh_dong,
        id_nguoi_thuc_hien
    )
    SELECT 
        CONCAT('LSHD_', NEWID()),
        i.id_hoa_don,
        d.trang_thai_don,
        i.trang_thai_don,
        'UPDATE_STATUS',
        N'Trạng thái: ' + d.trang_thai_don + N' → ' + i.trang_thai_don,
        @user_id
    FROM inserted i
    JOIN deleted d 
        ON i.id_hoa_don = d.id_hoa_don
    WHERE i.trang_thai_don <> d.trang_thai_don;
END
GO

--dam bao thu tu trigger validate chay truoc trigger log theo sau
EXEC sp_settriggerorder 
    @triggername = 'trg_validate_trang_thai',
    @order = 'FIRST',
    @stmttype = 'UPDATE';
GO

EXEC sp_settriggerorder 
    @triggername = 'trg_log_hoa_don',
    @order = 'LAST',
    @stmttype = 'UPDATE';
GO

DROP TRIGGER IF EXISTS trg_set_ngay_giao;
GO

CREATE TRIGGER trg_set_ngay_giao
ON hoa_don
AFTER UPDATE
AS
BEGIN
    IF UPDATE(trang_thai_don)
    BEGIN
        UPDATE hoa_don
        SET ngay_giao = GETDATE()
        WHERE id_hoa_don IN (
            SELECT id_hoa_don
            FROM inserted
            WHERE trang_thai_don = 'DA_GIAO'
			AND ngay_giao IS NULL
        )
    END
END
GO


DROP TRIGGER IF EXISTS trg_check_so_luong_tra;
GO

CREATE TRIGGER trg_check_so_luong_tra
ON doi_tra_chi_tiet
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    ----------------------------------
    -- 1. CHECK: trạng thái đơn hàng (Cho phép tạo thêm khi đang xử lý hoặc đã hoàn trả 1 phần)
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don
        WHERE hd.trang_thai_don NOT IN ('DA_GIAO', 'YEU_CAU_TRA_HANG', 'HOAN_TRA_MOT_PHAN')
    )
    BEGIN
        RAISERROR(N'Trạng thái đơn hàng không hỗ trợ tạo yêu cầu đổi trả',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 2. CHECK: thời gian (FIX NULL ngay_giao)
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don
        WHERE 
            (hd.loai_don_hang = 'ONLINE'
                AND (hd.ngay_giao IS NULL OR DATEDIFF(DAY, hd.ngay_giao, GETDATE()) > 7))
            OR
            (hd.loai_don_hang = 'TAI_QUAY'
                AND DATEDIFF(DAY, hd.ngay_tao, GETDATE()) > 3)
    )
    BEGIN
        RAISERROR(N'Quá thời gian đổi trả',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 3. CHECK: loại đổi trả (Chấp nhận cả REFUND/EXCHANGE từ Java)
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        JOIN hoa_don hd ON dt.id_hoa_don = hd.id_hoa_don
        WHERE
        (hd.loai_don_hang = 'ONLINE' AND dt.loai_doi_tra NOT IN ('HOAN_TIEN', 'REFUND'))
        OR
        (hd.loai_don_hang = 'TAI_QUAY' AND dt.loai_doi_tra NOT IN ('DOI_HANG', 'EXCHANGE'))
    )
    BEGIN
        RAISERROR(N'Loại đổi trả không hợp lệ',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 4. CHECK: tình trạng hàng
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        WHERE dt.tinh_trang_hang = 'DA_SU_DUNG'
    )
    BEGIN
        RAISERROR(N'Hàng đã sử dụng không được đổi trả',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 5. CHECK: phải chọn sp mới khi đổi
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        WHERE dt.loai_doi_tra IN ('DOI_HANG', 'EXCHANGE')
        AND i.id_spct_moi IS NULL
    )
    BEGIN
        RAISERROR(N'Phải chọn sản phẩm đổi',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 6. CHECK: tồn kho (Sửa tham chiếu i.id_spct_moi)
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        JOIN san_pham_chi_tiet spct WITH (UPDLOCK, ROWLOCK)
            ON i.id_spct_moi = spct.id_spct
        WHERE dt.loai_doi_tra IN ('DOI_HANG', 'EXCHANGE')
        AND spct.so_luong < i.so_luong_tra
    )
    BEGIN
        RAISERROR(N'Sản phẩm đổi không đủ hàng',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 7. CHECK: đổi đúng sản phẩm (Sửa tham chiếu i.id_spct_moi)
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN doi_tra dt ON i.id_doi_tra = dt.id_doi_tra
        JOIN hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct
        JOIN san_pham_chi_tiet spct_cu ON hdct.id_spct = spct_cu.id_spct
        JOIN san_pham_chi_tiet spct_moi ON i.id_spct_moi = spct_moi.id_spct
        WHERE 
            dt.loai_doi_tra IN ('DOI_HANG', 'EXCHANGE')
            AND spct_cu.id_san_pham <> spct_moi.id_san_pham
    )
    BEGIN
        RAISERROR(N'Chỉ được đổi cùng loại sản phẩm',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 8. CHECK: không cho đổi nhiều lần trên cùng 1 chi tiết (Tùy chính sách, hiện tại cho phép nếu chưa hết SL)
    ----------------------------------
    -- IF EXISTS (SELECT 1 FROM inserted i JOIN hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct WHERE hdct.da_doi_tra = 1)
    -- BEGIN RAISERROR(N'Sản phẩm đã được đổi trả trước đó',16,1); ROLLBACK; RETURN; END

    ----------------------------------
    -- 9. CHECK: số lượng cộng dồn (Quan trọng nhất: chỉ tính yêu cầu hữu hiệu)
    ----------------------------------
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN hoa_don_chi_tiet hdct ON i.id_hdct = hdct.id_hdct
        LEFT JOIN (
            SELECT dtct.id_hdct, SUM(dtct.so_luong_tra) tong_da_tra
            FROM doi_tra_chi_tiet dtct
            JOIN doi_tra dt_parent ON dtct.id_doi_tra = dt_parent.id_doi_tra
            WHERE dt_parent.trang_thai NOT IN ('TU_CHOI', 'CANCELLED')
            GROUP BY dtct.id_hdct
        ) d ON d.id_hdct = i.id_hdct
        WHERE ISNULL(d.tong_da_tra,0) + i.so_luong_tra > hdct.so_luong
    )
    BEGIN
        RAISERROR(N'Tổng số lượng trả vượt quá số lượng đã mua',16,1);
        ROLLBACK; RETURN;
    END

    ----------------------------------
    -- 10. INSERT
    ----------------------------------
    INSERT INTO doi_tra_chi_tiet (
        id_doi_tra,
        id_hdct,
        so_luong_tra,
        gia_tri_hoan
    )
    SELECT 
        i.id_doi_tra,
        i.id_hdct,
        i.so_luong_tra,
        i.so_luong_tra * hdct.don_gia
    FROM inserted i
    JOIN hoa_don_chi_tiet hdct 
        ON i.id_hdct = hdct.id_hdct;

END
GO

DROP TRIGGER IF EXISTS trg_set_da_doi_tra;
GO
CREATE TRIGGER trg_set_da_doi_tra
ON doi_tra
AFTER UPDATE
AS
BEGIN
    IF UPDATE(trang_thai)
    BEGIN
        UPDATE hdct
        SET da_doi_tra = 1
        FROM hoa_don_chi_tiet hdct
        JOIN doi_tra_chi_tiet dtct 
            ON dtct.id_hdct = hdct.id_hdct
        JOIN inserted i 
            ON dtct.id_doi_tra = i.id_doi_tra
        JOIN deleted d 
            ON d.id_doi_tra = i.id_doi_tra
        WHERE i.trang_thai = 'HOAN_THANH'
        AND d.trang_thai <> 'HOAN_THANH' -- 🔥 FIX CUỐI
    END
END
GO

-- VÔ HIỆU HÓA TRIGGER XỬ LÝ KHO ĐỔI TRẢ VÌ ĐÃ XỬ LÝ TRONG JAVA (Tránh double-counting và xử lý hàng lỗi)
DROP TRIGGER IF EXISTS trg_hoan_kho_doi_tra;
GO
DROP TRIGGER IF EXISTS trg_tru_kho_doi_hang;
GO

DROP TRIGGER IF EXISTS trg_lock_doi_tra;
GO

CREATE TRIGGER trg_lock_doi_tra
ON doi_tra
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    -- Chỉ chặn nếu có sự thay đổi về trạng thái quy trình (trang_thai)
    IF UPDATE(trang_thai)
    BEGIN
        IF EXISTS (
            SELECT 1
            FROM inserted i
            JOIN deleted d ON i.id_doi_tra = d.id_doi_tra
            WHERE d.trang_thai IN ('HOAN_THANH','TU_CHOI','CANCELLED')
            AND i.trang_thai <> d.trang_thai
        )
        BEGIN
            RAISERROR(N'Không thể thay đổi trạng thái quy trình của yêu cầu đã đóng',16,1);
            ROLLBACK;
            RETURN;
        END
    END
END
GO

DROP TRIGGER IF EXISTS trg_log_tu_choi;
GO

CREATE TRIGGER trg_log_tu_choi
ON doi_tra
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT UPDATE(trang_thai)
        RETURN;

    DECLARE @user_id INT;
    SET @user_id = CAST(SESSION_CONTEXT(N'user_id') AS INT);
    IF @user_id IS NULL SET @user_id = 1; -- Mặc định là Admin ND001

    INSERT INTO lich_su_hoa_don
    (
        ma_lich_su,
        id_hoa_don,
        trang_thai_cu,
        trang_thai_moi,
        loai_hanh_dong,
        hanh_dong,
        id_nguoi_thuc_hien,
        thoi_gian
    )
    SELECT 
        CONCAT('LSHD_', REPLACE(NEWID(),'-','')),
        i.id_hoa_don,
        'DOI_TRA_' + d.trang_thai,
        'DOI_TRA_' + i.trang_thai,
        'DOI_TRA',
        N'Từ chối đổi trả: ' + ISNULL(i.ly_do_tu_choi, N'Không có lý do'),
        @user_id, -- 🔥 Để NULL nếu không có trong session
        GETDATE()
    FROM inserted i
    JOIN deleted d 
        ON i.id_doi_tra = d.id_doi_tra
    WHERE 
        i.trang_thai = 'TU_CHOI'
        AND d.trang_thai <> 'TU_CHOI';

END
GO


INSERT INTO vai_tro (ma_vai_tro, ten_vai_tro)
VALUES 
('ADMIN','Quản trị viên'),
('STAFF','Nhân viên'),
('USER','Khách hàng');
GO

INSERT INTO nguoi_dung (id_vai_tro, ma_nguoi_dung, ho_ten, so_dien_thoai, email, mat_khau)
VALUES
(1,'ND001','Nguyễn Văn A','0342806730','admin@gmail.com','123456'),
(2,'ND002','Nguyễn Văn B','0901234567','staff@gmail.com','123456'),
(3,'ND003',N'Trần Văn C','0912345678','user1@gmail.com','123456'),
(3,'ND004',N'Trần Văn D','0912345679','user2@gmail.com','123456');
GO

INSERT INTO danh_muc (ma_danh_muc, ten_danh_muc)
VALUES
('DM001',N'Áo thể thao'),
('DM002',N'Quần thể thao');

INSERT INTO thuong_hieu (ma_thuong_hieu, ten_thuong_hieu)
VALUES
('TH001','Nike'),
('TH002','Adidas');
GO

INSERT INTO kich_thuoc VALUES ('S','S',1),('M','M',1);
INSERT INTO mau_sac VALUES ('DEN',N'Đen',1),('TRANG',N'Trắng',1);
INSERT INTO chat_lieu VALUES ('COTTON',N'Cotton',1);
GO

INSERT INTO san_pham
(id_danh_muc,id_thuong_hieu,id_chat_lieu,ma_san_pham,ten_san_pham,gia_goc)
VALUES
(1,1,1,'SP001',N'Áo Nike',200000),
(2,2,1,'SP002',N'Quần Adidas',180000);

INSERT INTO san_pham_chi_tiet
(id_san_pham,id_kich_thuoc,id_mau_sac,id_thuong_hieu,
ma_san_pham_chi_tiet,so_luong,gia_ban)
VALUES
(1,1,1,1,'SPCT001',10,250000),
(1,2,2,1,'SPCT002',8,260000),
(2,1,1,2,'SPCT003',15,200000);

INSERT INTO gio_hang(ma_gio_hang,id_nguoi_dung)
VALUES
('GH001',3),
('GH002',4);
GO

INSERT INTO gio_hang_chi_tiet(id_gio_hang,id_spct,so_luong)
VALUES
(1,1,2),
(1,2,1);
GO

INSERT INTO hoa_don
(ma_hoa_don,id_nguoi_dung,ten_nguoi_nhan,so_dien_thoai)
VALUES
('HD001',3,N'Trần Văn C','0912345678');
GO

INSERT INTO hoa_don_chi_tiet
(ma_hoa_don_chi_tiet,id_hoa_don,id_spct,
ten_san_pham,don_gia,so_luong,thanh_tien)
VALUES
('HDCT001',1,1,N'Áo Nike',250000,2,500000);
GO

UPDATE hoa_don
SET trang_thai_don = 'DA_XAC_NHAN'
WHERE id_hoa_don = 1;
GO

UPDATE hoa_don
SET trang_thai_don = 'DA_HUY'
WHERE id_hoa_don = 1;
GO


SELECT * FROM vai_tro;
SELECT * FROM nguoi_dung;
SELECT * FROM danh_muc;
SELECT * FROM thuong_hieu;
SELECT * FROM san_pham;
SELECT * FROM san_pham_chi_tiet;
SELECT * FROM gio_hang;
SELECT * FROM gio_hang_chi_tiet;
SELECT * FROM hoa_don;
SELECT * FROM hoa_don_chi_tiet;
select * from giam_gia_san_pham
select * from ma_giam_gia
select * from pt_thanh_toan
INSERT INTO pt_thanh_toan (ma_pt_thanh_toan, ten_pttt)
VALUES 
('COD', N'Thanh toán khi nhận hàng (COD)'),
('VNPAY', N'Thanh toán qua cổng VNPay')
GO
-- 1. Xóa cái index cản trở việc tạo nhiều giỏ hàng
DROP INDEX IF EXISTS idx_unique_active_cart ON gio_hang;
GO

-- 2. Thêm loại giỏ hàng để phân biệt: Khách online tự thêm hay Thu ngân tạo Hóa đơn chờ
-- co trong table r

-- 3. (Tùy chọn) Thêm tên để thu ngân dễ nhìn (Ví dụ: "Đơn chờ bàn 1", "Khách VIP")
-- bi lap nen xoa di
-- =============================================
-- C?P NH?T SCHEMA CHO CH?C NANG �?I TR? N�NG CAO
-- =============================================

ALTER TABLE hoa_don ADD ngay_nhan_hang DATETIME;
GO

ALTER TABLE doi_tra ADD loai_doi_tra NVARCHAR(20);
ALTER TABLE doi_tra ADD tien_chenh_lech DECIMAL(18,2) DEFAULT 0;
ALTER TABLE doi_tra ADD phi_ship_hoan DECIMAL(18,2) DEFAULT 0;
ALTER TABLE doi_tra ADD ghi_chu_admin NVARCHAR(500);
ALTER TABLE doi_tra ADD trang_thai_thanh_toan NVARCHAR(30);
GO

ALTER TABLE doi_tra_chi_tiet ADD don_gia DECIMAL(18,2);
ALTER TABLE doi_tra_chi_tiet ADD id_spct_moi INT;
GO

CREATE TABLE hang_loi (
    id_hang_loi INT IDENTITY PRIMARY KEY,
    id_spct INT,
    id_hoa_don INT,
    so_luong INT,
    ly_do NVARCHAR(500),
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_spct) REFERENCES san_pham_chi_tiet(id_spct),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don)
);
GO

CREATE TABLE lich_su_doi_tra (
    id_lsdt INT IDENTITY PRIMARY KEY,
    id_doi_tra INT,
    id_hoa_don INT,
    hanh_dong NVARCHAR(100),
    chi_tiet NVARCHAR(MAX),
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_doi_tra) REFERENCES doi_tra(id_doi_tra),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don)
);
GO

-- =============================================
-- SEED DATA �? TEST CH?C NANG �?I TR?
-- =============================================

-- 1. Th�m Voucher m?u
INSERT INTO ma_giam_gia (ma_code, kieu_giam_gia, gia_tri_giam, gia_tri_toi_thieu, so_luong, so_luong_da_dung, ngay_bat_dau, ngay_ket_thuc, trang_thai)
VALUES ('TEST200K', 'AMOUNT', 200000, 1000000, 100, 1, '2026-01-01', '2026-12-31', 1);

-- 2. Th�m don h�ng ONLINE d� giao (C� Voucher)
-- T?ng h�ng: 1.250.000 (5 c�i SPCT001 * 250k)
-- Gi?m: 200.000
-- Thanh to�n: 1.050.000
INSERT INTO hoa_don (ma_hoa_don, id_nguoi_dung, id_ma_giam_gia, id_pttt, loai_don_hang, ten_nguoi_nhan, so_dien_thoai, tong_tien_hang, tien_giam, tong_thanh_toan, trang_thai_don, ngay_nhan_hang, ngay_tao)
VALUES ('HD_TEST_ONLINE', 3, (SELECT id_ma_giam_gia FROM ma_giam_gia WHERE ma_code='TEST200K'), 2, 'ONLINE', N'Kh�ch Test Online', '0988888888', 1250000, 200000, 1050000, 'DA_GIAO', DATEADD(day, -2, GETDATE()), GETDATE());

INSERT INTO hoa_don_chi_tiet (ma_hoa_don_chi_tiet, id_hoa_don, id_spct, ten_san_pham, don_gia, so_luong, thanh_tien)
VALUES ('HDCT_TEST_1', (SELECT id_hoa_don FROM hoa_don WHERE ma_hoa_don='HD_TEST_ONLINE'), 1, N'�o Nike S Tr?ng', 250000, 5, 1250000);

-- 3. Th�m don h�ng OFFLINE d� giao (T?i qu?y)
INSERT INTO hoa_don (ma_hoa_don, id_nguoi_dung, id_pttt, loai_don_hang, ten_nguoi_nhan, so_dien_thoai, tong_tien_hang, tien_giam, tong_thanh_toan, trang_thai_don, ngay_nhan_hang, ngay_tao)
VALUES ('HD_TEST_OFFLINE', 3, 1, 'TAI_QUAY', N'Kh�ch Test Offline', '0977777777', 400000, 0, 400000, 'DA_GIAO', DATEADD(day, -1, GETDATE()), GETDATE());

INSERT INTO hoa_don_chi_tiet (ma_hoa_don_chi_tiet, id_hoa_don, id_spct, ten_san_pham, don_gia, so_luong, thanh_tien)
VALUES ('HDCT_TEST_2', (SELECT id_hoa_don FROM hoa_don WHERE ma_hoa_don='HD_TEST_OFFLINE'), 3, N'Qu?n Adidas S �en', 200000, 2, 400000);


-- =============================================
-- TH�M D? LI?U S?N PH?M NHI?U BI?N TH? & �ON H�NG TEST
-- =============================================

-- 1. Th�m s?n ph?m Hoodie
INSERT INTO san_pham (id_danh_muc, id_thuong_hieu, id_chat_lieu, ma_san_pham, ten_san_pham, gia_goc, trang_thai)
VALUES (1, 1, 1, 'SP003', N'�o Hoodie Nike Premium', 500000, 1);

-- 2. Th�m c�c bi?n th? cho Hoodie (Size S/M x M�u �en/Tr?ng)
-- Gi? s? ID: KichThuoc S=1, M=2 | MauSac �en=1, Tr?ng=2
INSERT INTO san_pham_chi_tiet (id_san_pham, id_kich_thuoc, id_mau_sac, id_thuong_hieu, ma_san_pham_chi_tiet, so_luong, so_luong_da_ban, gia_ban, trang_thai)
VALUES 
((SELECT id_san_pham FROM san_pham WHERE ma_san_pham='SP003'), 1, 1, 1, 'SPCT004', 20, 0, 550000, 1),
((SELECT id_san_pham FROM san_pham WHERE ma_san_pham='SP003'), 2, 1, 1, 'SPCT005', 20, 0, 550000, 1),
((SELECT id_san_pham FROM san_pham WHERE ma_san_pham='SP003'), 1, 2, 1, 'SPCT006', 20, 0, 550000, 1),
((SELECT id_san_pham FROM san_pham WHERE ma_san_pham='SP003'), 2, 2, 1, 'SPCT007', 20, 0, 550000, 1);

-- 3. T?o h�a don ONLINE d� giao d? test d?i h�ng
INSERT INTO hoa_don (ma_hoa_don, id_nguoi_dung, id_pttt, loai_don_hang, ten_nguoi_nhan, so_dien_thoai, tong_tien_hang, tien_giam, tong_thanh_toan, trang_thai_don, ngay_nhan_hang, ngay_tao)
VALUES ('HD_TEST_EXCHANGE', 3, 2, 'ONLINE', N'Ngu?i Mua Hoodie', '0966666666', 1100000, 0, 1100000, 'DA_GIAO', DATEADD(day, -1, GETDATE()), GETDATE());

-- 4. Chi ti?t h�a don (Mua 2 Hoodie S �en)
INSERT INTO hoa_don_chi_tiet (ma_hoa_don_chi_tiet, id_hoa_don, id_spct, ten_san_pham, kich_thuoc, mau_sac, don_gia, so_luong, thanh_tien)
VALUES ('HDCT_TEST_3', (SELECT id_hoa_don FROM hoa_don WHERE ma_hoa_don='HD_TEST_EXCHANGE'), 
(SELECT id_spct FROM san_pham_chi_tiet WHERE ma_san_pham_chi_tiet='SPCT004'), 
N'�o Hoodie Nike Premium', 'S', N'�en', 550000, 2, 1100000);

