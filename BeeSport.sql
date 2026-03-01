CREATE DATABASE BeeSport;
GO
USE BeeSport;
GO

CREATE TABLE vai_tro (
    id_vai_tro INT IDENTITY PRIMARY KEY,
    ten_vai_tro NVARCHAR(50),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE nguoi_dung (
    id_nguoi_dung INT IDENTITY PRIMARY KEY,
    id_vai_tro INT,
    ho_ten NVARCHAR(150),
    email NVARCHAR(150) UNIQUE,
    mat_khau NVARCHAR(255),
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_vai_tro) REFERENCES vai_tro(id_vai_tro)
);
GO

CREATE TABLE xac_thuc (
    id_xac_thuc INT IDENTITY PRIMARY KEY,
    id_nguoi_dung INT,
    loai_xac_thuc NVARCHAR(50),
    trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
);
GO

CREATE TABLE danh_muc (
    id_danh_muc INT IDENTITY PRIMARY KEY,
    ten_danh_muc NVARCHAR(150),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE san_pham (
    id_san_pham INT IDENTITY PRIMARY KEY,
    id_danh_muc INT,
    ten_san_pham NVARCHAR(200),
    gia_goc DECIMAL(18,2),
    trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_danh_muc) REFERENCES danh_muc(id_danh_muc)
);
GO

CREATE TABLE hinh_anh_san_pham (
    id_hinh_anh INT IDENTITY PRIMARY KEY,
    id_san_pham INT,
    url NVARCHAR(255),
    trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham)
);
GO

CREATE TABLE kich_thuoc (
    id_kich_thuoc INT IDENTITY PRIMARY KEY,
    ten_kich_thuoc NVARCHAR(50),
	trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE mau_sac (
    id_mau_sac INT IDENTITY PRIMARY KEY,
    ten_mau NVARCHAR(50),
	trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE chat_lieu (
    id_chat_lieu INT IDENTITY PRIMARY KEY,
    ten_chat_lieu NVARCHAR(50),
	trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE san_pham_chi_tiet (
    id_spct INT IDENTITY PRIMARY KEY,
    id_san_pham INT,
    id_kich_thuoc INT,
    id_mau_sac INT,
    id_chat_lieu INT,
    so_luong INT,
    gia_ban DECIMAL(18,2),
    trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham),
	FOREIGN KEY (id_kich_thuoc) REFERENCES kich_thuoc(id_kich_thuoc),
	FOREIGN KEY (id_mau_sac) REFERENCES mau_sac(id_mau_sac),
	FOREIGN KEY (id_chat_lieu) REFERENCES chat_lieu(id_chat_lieu)
);
GO

CREATE TABLE gio_hang (
    id_gio_hang INT IDENTITY PRIMARY KEY,
    id_nguoi_dung INT,
    trang_thai NVARCHAR(30) DEFAULT 'DANG_SU_DUNG',
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
);
GO

CREATE TABLE gio_hang_chi_tiet (
    id_ghct INT IDENTITY PRIMARY KEY,
    id_gio_hang INT,
    id_spct INT,
    so_luong INT,
    FOREIGN KEY (id_gio_hang) REFERENCES gio_hang(id_gio_hang),
    FOREIGN KEY (id_spct) REFERENCES san_pham_chi_tiet(id_spct)
);
GO

CREATE TABLE ma_giam_gia (
    id_ma_giam_gia INT IDENTITY PRIMARY KEY,
    ma_code NVARCHAR(50) UNIQUE,
    kieu_giam_gia NVARCHAR(20), -- PERCENT | AMOUNT
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

CREATE TABLE dot_giam_gia (
    id_dot_giam_gia INT IDENTITY PRIMARY KEY,
    ten_dot NVARCHAR(150),
    kieu_giam_gia NVARCHAR(20),
    gia_tri_giam DECIMAL(18,2),
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE giam_gia_san_pham (
    id_giam_gia_san_pham INT IDENTITY PRIMARY KEY,
    id_dot_giam_gia INT,
    id_san_pham INT,
    trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_dot_giam_gia) REFERENCES dot_giam_gia(id_dot_giam_gia),
    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham)
);
GO

CREATE TABLE hoa_don (
    id_hoa_don INT IDENTITY PRIMARY KEY,
    id_nguoi_dung INT,
    id_ma_giam_gia INT NULL,
    tong_tien DECIMAL(18,2),
    trang_thai_don NVARCHAR(30) DEFAULT 'CHO_XAC_NHAN',
    ngay_tao DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
    FOREIGN KEY (id_ma_giam_gia) REFERENCES ma_giam_gia(id_ma_giam_gia)
);
GO

CREATE TABLE hoa_don_chi_tiet (
    id_hdct INT IDENTITY PRIMARY KEY,
    id_hoa_don INT,
    id_spct INT,
    so_luong INT,
    gia DECIMAL(18,2),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don)
);
GO

CREATE TABLE pt_thanh_toan (
    id_pttt INT IDENTITY PRIMARY KEY,
    ten_pttt NVARCHAR(100),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE lich_su_thanh_toan (
    id_lstt INT IDENTITY PRIMARY KEY,
    id_hoa_don INT,
    id_pttt INT,
    so_tien DECIMAL(18,2),
    trang_thai_thanh_toan NVARCHAR(30),
    ngay_thanh_toan DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don),
    FOREIGN KEY (id_pttt) REFERENCES pt_thanh_toan(id_pttt)
);
GO

CREATE TABLE tinh (
    id_tinh INT IDENTITY PRIMARY KEY,
    ten_tinh NVARCHAR(100),
	trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE huyen (
    id_huyen INT IDENTITY PRIMARY KEY,
    id_tinh INT,
    ten_huyen NVARCHAR(100),
	trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_tinh) REFERENCES tinh(id_tinh)
);
GO

CREATE TABLE xa (
    id_xa INT IDENTITY PRIMARY KEY,
    id_huyen INT,
    ten_xa NVARCHAR(100),
	trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_huyen) REFERENCES huyen(id_huyen)
);
GO

CREATE TABLE dia_chi_van_chuyen (
    id_dia_chi INT IDENTITY PRIMARY KEY,
    id_nguoi_dung INT,
    id_xa INT,
    dia_chi_chi_tiet NVARCHAR(255),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE lich_su_su_dung_ma_giam_gia (
    id_ls INT IDENTITY PRIMARY KEY,
    id_ma_giam_gia INT,
    id_hoa_don INT,
    ngay_su_dung DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE lich_su_hoa_don (
    id_ls_hd INT IDENTITY PRIMARY KEY,
    id_hoa_don INT,
    trang_thai NVARCHAR(30),
    ngay_cap_nhat DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE doi_tra (
    id_doi_tra INT IDENTITY PRIMARY KEY,
    id_hoa_don INT,
    ly_do NVARCHAR(255),
    trang_thai NVARCHAR(30),
    ngay_yeu_cau DATETIME DEFAULT GETDATE()
);
GO

INSERT INTO vai_tro (ten_vai_tro)
VALUES 
(N'ADMIN'),
(N'NHÂN VIÊN'),
(N'KHÁCH HÀNG');
GO

INSERT INTO nguoi_dung (id_vai_tro, ho_ten, email, mat_khau)
VALUES
(1, N'Admin A', 'adminA@gmail.com', '$2a$10$GTTiodYaKVlVT4ct4a3UX.lB5zjeLjAoESaTEUF1ZVf5PPYNH6mT2'), 
(2, N'Nhân viên B', 'nhanvienb@gmail.com', '$2a$10$GTTiodYaKVlVT4ct4a3UX.lB5zjeLjAoESaTEUF1ZVf5PPYNH6mT2'),
(3, N'Khách hàng C', 'khachhangc@gmail.com', '$2a$10$GTTiodYaKVlVT4ct4a3UX.lB5zjeLjAoESaTEUF1ZVf5PPYNH6mT2');
GO

INSERT INTO xac_thuc (id_nguoi_dung, loai_xac_thuc)
VALUES
(1, N'EMAIL'),
(2, N'EMAIL');
GO

INSERT INTO danh_muc (ten_danh_muc)
VALUES
(N'Áo thể thao'),
(N'Giày thể thao');
GO

INSERT INTO san_pham (id_danh_muc, ten_san_pham, gia_goc)
VALUES
(1, N'Áo chạy bộ BeeSport', 300000),
(2, N'Giày chạy bộ BeeSport', 1200000);
GO

INSERT INTO kich_thuoc (ten_kich_thuoc)
VALUES (N'S'), (N'M'), (N'L');
GO

INSERT INTO mau_sac (ten_mau)
VALUES (N'Đen'), (N'Trắng');
GO

INSERT INTO chat_lieu (ten_chat_lieu)
VALUES (N'Cotton'), (N'Polyester');
GO

INSERT INTO san_pham_chi_tiet
(id_san_pham, id_kich_thuoc, id_mau_sac, id_chat_lieu, so_luong, gia_ban)
VALUES
(1, 2, 1, 1, 50, 280000), -- Áo M Đen Cotton
(1, 3, 2, 2, 30, 290000), -- Áo L Trắng Poly
(2, 2, 1, 2, 20, 1150000); -- Giày M Đen Poly
GO

INSERT INTO gio_hang (id_nguoi_dung)
VALUES (2);
GO

INSERT INTO gio_hang_chi_tiet (id_gio_hang, id_spct, so_luong)
VALUES
(1, 1, 2),
(1, 2, 1);
GO

INSERT INTO ma_giam_gia
(ma_code, kieu_giam_gia, gia_tri_giam, gia_tri_giam_toi_da, gia_tri_toi_thieu, so_luong, ngay_bat_dau, ngay_ket_thuc)
VALUES
('BEE10', 'PERCENT', 10, 100000, 500000, 100, GETDATE(), DATEADD(DAY, 30, GETDATE())),
('BEE50K', 'AMOUNT', 50000, 50000, 300000, 50, GETDATE(), DATEADD(DAY, 15, GETDATE()));
GO

INSERT INTO hoa_don (id_nguoi_dung, id_ma_giam_gia, tong_tien)
VALUES
(2, 1, 830000);
GO

INSERT INTO hoa_don_chi_tiet (id_hoa_don, id_spct, so_luong, gia)
VALUES
(1, 1, 2, 280000),
(1, 2, 1, 290000);
GO

INSERT INTO pt_thanh_toan (ten_pttt)
VALUES (N'Thanh toán khi nhận hàng'), (N'Chuyển khoản');
GO

INSERT INTO lich_su_thanh_toan
(id_hoa_don, id_pttt, so_tien, trang_thai_thanh_toan)
VALUES
(1, 1, 830000, N'CHUA_THANH_TOAN');
GO

INSERT INTO lich_su_su_dung_ma_giam_gia (id_ma_giam_gia, id_hoa_don)
VALUES (1, 1);
GO

INSERT INTO lich_su_hoa_don (id_hoa_don, trang_thai)
VALUES
(1, N'CHO_XAC_NHAN'),
(1, N'DANG_GIAO');
GO



SELECT * FROM vai_tro;
SELECT * FROM nguoi_dung;
SELECT * FROM xac_thuc;

SELECT * FROM danh_muc;
SELECT * FROM san_pham;
SELECT * FROM hinh_anh_san_pham;

SELECT * FROM kich_thuoc;
SELECT * FROM mau_sac;
SELECT * FROM chat_lieu;
SELECT * FROM san_pham_chi_tiet;

SELECT * FROM gio_hang;
SELECT * FROM gio_hang_chi_tiet;

SELECT * FROM ma_giam_gia;
SELECT * FROM dot_giam_gia;
SELECT * FROM giam_gia_san_pham;

SELECT * FROM hoa_don;
SELECT * FROM hoa_don_chi_tiet;

SELECT * FROM pt_thanh_toan;
SELECT * FROM pt_thanh_toan_hoa_don;

SELECT * FROM tinh;
SELECT * FROM huyen;
SELECT * FROM xa;
SELECT * FROM dia_chi_van_chuyen;

SELECT * FROM lich_su_su_dung_ma_giam_gia;
SELECT * FROM lich_su_hoa_don;
SELECT * FROM lich_su_thanh_toan;
SELECT * FROM doi_tra;

