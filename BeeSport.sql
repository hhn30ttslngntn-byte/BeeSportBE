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

    hoan_tra DATETIME NULL,

    ghi_chu NVARCHAR(255),

    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_cap_nhat DATETIME DEFAULT GETDATE(),

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
    'CHO_THANH_TOAN',
    'DA_THANH_TOAN',
    'DANG_GIAO',
    'DA_GIAO',
    'DA_HUY',
    'HOAN_TRA'
));
GO
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
		trang_thai_thanh_toan NVARCHAR(30)
		CHECK (trang_thai_thanh_toan IN ('CHO_THANH_TOAN','DA_THANH_TOAN','THAT_BAI')),
		vnp_TransactionNo NVARCHAR(50) NULL,
		vnp_PayDate NVARCHAR(14) NULL,
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
		ly_do NVARCHAR(255),
		danh_sach_anh NVARCHAR(MAX) NULL,
		tong_tien_hoan DECIMAL(18,2) DEFAULT 0,
		trang_thai NVARCHAR(30)
		CHECK (trang_thai IN ('CHO_XAC_NHAN_HOAN','CHO_GIAO_HANG','DA_NHAN_HANG_KIEM_TRA','HOAN_THANH','TU_CHOI')),
		ngay_yeu_cau DATETIME DEFAULT GETDATE(),
		FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don)
	);
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


CREATE INDEX idx_spct_sanpham 
ON san_pham_chi_tiet(id_san_pham);
GO

CREATE INDEX idx_giohang_user 
ON gio_hang(id_nguoi_dung);
GO

CREATE INDEX idx_hoadon_user 
ON hoa_don(id_nguoi_dung);
GO

CREATE INDEX idx_diachinguoidung
ON dia_chi_van_chuyen(id_nguoi_dung);
GO

CREATE INDEX idx_review_product
ON danh_gia_san_pham(id_san_pham);
GO

CREATE INDEX idx_wishlist_user
ON san_pham_yeu_thich(id_nguoi_dung);
GO

CREATE INDEX idx_hoadon_trangthai
ON hoa_don(trang_thai_don);
GO

CREATE INDEX idx_hoadon_ngay
ON hoa_don(ngay_tao);
GO

CREATE INDEX idx_ghct_spct
ON gio_hang_chi_tiet(id_spct);
GO

CREATE INDEX idx_ghct_giohang_spct
ON gio_hang_chi_tiet(id_gio_hang, id_spct);
GO

CREATE INDEX idx_hdct_spct
ON hoa_don_chi_tiet(id_spct);
GO


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

    IF NOT UPDATE(trang_thai_don)
        RETURN;

    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN deleted d ON i.id_hoa_don = d.id_hoa_don
        WHERE

        -- 1. CHO_XAC_NHAN → chỉ được sang DA_XAC_NHAN hoặc DA_HUY
        (d.trang_thai_don = 'CHO_XAC_NHAN' 
         AND i.trang_thai_don NOT IN ('DA_XAC_NHAN','DA_HUY'))

        OR

        -- 2. DA_XAC_NHAN → CHO_THANH_TOAN hoặc DA_HUY
        (d.trang_thai_don = 'DA_XAC_NHAN' 
         AND i.trang_thai_don NOT IN ('CHO_THANH_TOAN','DA_HUY'))

        OR

        -- 3. CHO_THANH_TOAN → DA_THANH_TOAN hoặc DA_HUY
        (d.trang_thai_don = 'CHO_THANH_TOAN' 
         AND i.trang_thai_don NOT IN ('DA_THANH_TOAN','DA_HUY'))

        OR

        -- 4. DA_THANH_TOAN → DANG_GIAO hoặc DA_HUY
        (d.trang_thai_don = 'DA_THANH_TOAN' 
         AND i.trang_thai_don NOT IN ('DANG_GIAO','DA_HUY'))

        OR

        -- 5. DANG_GIAO → DA_GIAO (KHÔNG cho hủy nữa)
        (d.trang_thai_don = 'DANG_GIAO' 
         AND i.trang_thai_don <> 'DA_GIAO')

        OR

        -- 6. DA_GIAO → chỉ cho HOAN_TRA
        (d.trang_thai_don = 'DA_GIAO' 
		AND i.trang_thai_don NOT IN ('DA_GIAO','HOAN_TRA'))

        OR

        -- 7. DA_HUY → KHÔNG được update nữa
        (d.trang_thai_don = 'DA_HUY')

        OR

        -- 8. HOAN_TRA → KHÔNG update nữa
        (d.trang_thai_don = 'HOAN_TRA')
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
SET trang_thai_don = 'DA_GIAO'
WHERE id_hoa_don = 1;
GO

-- Tạo thêm HD002 giao thành công để test dữ liệu bảng doi_tra cho Admin
INSERT INTO hoa_don
(ma_hoa_don,id_nguoi_dung,ten_nguoi_nhan,so_dien_thoai,trang_thai_don,loai_don_hang)
VALUES
('HD002',3,N'Trần Văn C','0912345678','DA_GIAO','ONLINE');
GO

INSERT INTO hoa_don_chi_tiet
(ma_hoa_don_chi_tiet,id_hoa_don,id_spct,ten_san_pham,don_gia,so_luong,thanh_tien)
VALUES
('HDCT002',2,3,N'Quần Adidas',200000,2,400000);
GO

-- Cập nhật tổng tiền cho HD002 để khớp với chi tiết
UPDATE hoa_don SET tong_tien_hang=400000, tong_thanh_toan=400000 WHERE id_hoa_don=2;
GO

-- Tạo 1 request Đổi trả đang chờ xác nhận cho HD002
INSERT INTO doi_tra (ma_doi_tra, id_hoa_don, ly_do, danh_sach_anh, tong_tien_hoan, trang_thai)
VALUES
('DT-TEST-001', 2, N'Sản phẩm bị lỗi đường chỉ', '["https://placehold.co/600x400/png?text=LoiSanPham"]', 200000, 'CHO_XAC_NHAN_HOAN');
GO

INSERT INTO doi_tra_chi_tiet (id_doi_tra, id_hdct, so_luong_tra, gia_tri_hoan)
VALUES
(1, 2, 1, 200000);
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
-- ==========================================
-- DU LIEU MAU DE TEST LUONG HOAN TRA
-- ==========================================
DECLARE @test_hd_id INT;

INSERT INTO hoa_don (ma_hoa_don, id_nguoi_dung, loai_don_hang, ten_nguoi_nhan, so_dien_thoai, trang_thai_don, tong_tien_hang, tong_thanh_toan, phi_van_chuyen, tien_giam)
VALUES ('HD-REFUND-001', 3, 'ONLINE', N'Kh�ch Hang Test Refund', '0912345678', 'DA_GIAO', 510000, 490000, 30000, 50000);

SET @test_hd_id = SCOPE_IDENTITY();

INSERT INTO hoa_don_chi_tiet (ma_hoa_don_chi_tiet, id_hoa_don, id_spct, ten_san_pham, don_gia, so_luong, thanh_tien, kich_thuoc, mau_sac)
VALUES 
('HDCT-REF1-1', @test_hd_id, 1, N'San pham Test Hoan Tra 1', 250000, 1, 250000, 'L', 'DO'),
('HDCT-REF1-2', @test_hd_id, 2, N'San pham Test Hoan Tra 2', 260000, 1, 260000, 'M', 'XANH');

GO
