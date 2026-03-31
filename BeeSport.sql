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
	so_dien_thoai NVARCHAR(20),
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

CREATE TABLE san_pham (
    id_san_pham INT IDENTITY PRIMARY KEY,
    id_danh_muc INT NOT NULL,
    ma_san_pham NVARCHAR(50) UNIQUE,
    ten_san_pham NVARCHAR(200) NOT NULL,
    gia_goc DECIMAL(18,2) NOT NULL,
    trang_thai BIT DEFAULT 1,

    FOREIGN KEY (id_danh_muc) REFERENCES danh_muc(id_danh_muc)
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

CREATE TABLE chat_lieu (
    id_chat_lieu INT IDENTITY PRIMARY KEY,
	ma_chat_lieu NVARCHAR(50),
    ten_chat_lieu NVARCHAR(50),
	trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE san_pham_chi_tiet (
    id_spct INT IDENTITY PRIMARY KEY,
    id_san_pham INT NOT NULL,
    id_kich_thuoc INT NOT NULL,
    id_mau_sac INT NOT NULL,
    id_chat_lieu INT NOT NULL,

    ma_san_pham_chi_tiet NVARCHAR(50) UNIQUE,
    so_luong INT NOT NULL CHECK (so_luong >= 0),
    gia_ban DECIMAL(18,2) NOT NULL,
    trang_thai BIT DEFAULT 1,

    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham),
    FOREIGN KEY (id_kich_thuoc) REFERENCES kich_thuoc(id_kich_thuoc),
    FOREIGN KEY (id_mau_sac) REFERENCES mau_sac(id_mau_sac),
    FOREIGN KEY (id_chat_lieu) REFERENCES chat_lieu(id_chat_lieu)
);
GO

CREATE TABLE gio_hang (
    id_gio_hang INT IDENTITY PRIMARY KEY,
    ma_gio_hang NVARCHAR(50) UNIQUE,
    id_nguoi_dung INT NOT NULL,

    trang_thai NVARCHAR(30)
    CHECK (trang_thai IN ('DANG_SU_DUNG','DA_THANH_TOAN'))
    DEFAULT 'DANG_SU_DUNG',

    ngay_tao DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung)
);
GO

CREATE TABLE gio_hang_chi_tiet (
    id_ghct INT IDENTITY PRIMARY KEY,
    id_gio_hang INT NOT NULL,
    id_spct INT NOT NULL,

    ma_gio_hang_chi_tiet NVARCHAR(50) UNIQUE,
    so_luong INT NOT NULL CHECK (so_luong > 0),

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
	ma_dot_giam_gia NVARCHAR(50),
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
	ma_giam_gia_san_pham NVARCHAR(50),
    id_dot_giam_gia INT,
    id_san_pham INT,
    trang_thai BIT DEFAULT 1,
    FOREIGN KEY (id_dot_giam_gia) REFERENCES dot_giam_gia(id_dot_giam_gia),
    FOREIGN KEY (id_san_pham) REFERENCES san_pham(id_san_pham)
);
GO

DROP TABLE IF EXISTS hoa_don;
GO

CREATE TABLE hoa_don (
    id_hoa_don INT IDENTITY PRIMARY KEY,
    ma_hoa_don NVARCHAR(50) UNIQUE,

    id_nguoi_dung INT NOT NULL,
    id_ma_giam_gia INT NULL,

    ten_nguoi_nhan NVARCHAR(150) NOT NULL,
    so_dien_thoai NVARCHAR(20) NOT NULL,
    tinh NVARCHAR(100),
    huyen NVARCHAR(100),
    xa NVARCHAR(100),
    dia_chi_chi_tiet NVARCHAR(255),

    tong_tien_hang DECIMAL(18,2) NOT NULL,
    tien_giam DECIMAL(18,2) DEFAULT 0,
    phi_van_chuyen DECIMAL(18,2) DEFAULT 0,
    tong_thanh_toan DECIMAL(18,2) NOT NULL,

    trang_thai_don NVARCHAR(30)
    CHECK (trang_thai_don IN 
    ('CHO_XAC_NHAN','DA_XAC_NHAN','DANG_GIAO','DA_GIAO','DA_HUY','HOAN_TRA'))
    DEFAULT 'CHO_XAC_NHAN',

    ghi_chu NVARCHAR(255),

    ngay_tao DATETIME DEFAULT GETDATE(),
    ngay_cap_nhat DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (id_nguoi_dung) REFERENCES nguoi_dung(id_nguoi_dung),
    FOREIGN KEY (id_ma_giam_gia) REFERENCES ma_giam_gia(id_ma_giam_gia)
);
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

    so_luong_da_tra INT DEFAULT 0,

    ngay_tao DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id_hoa_don),
    FOREIGN KEY (id_spct) REFERENCES san_pham_chi_tiet(id_spct)
);
GO

CREATE TABLE pt_thanh_toan (
    id_pttt INT IDENTITY PRIMARY KEY,
	ma_pt_thanh_toan NVARCHAR(50),
    ten_pttt NVARCHAR(100),
    trang_thai BIT DEFAULT 1
);
GO

CREATE TABLE lich_su_thanh_toan (
    id_lstt INT IDENTITY PRIMARY KEY,
	ma_lich_su_thanh_toan NVARCHAR(50),
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
    ngay_su_dung DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE lich_su_hoa_don (
    id_ls_hd INT IDENTITY PRIMARY KEY,
	ma_lich_su_hoa_don NVARCHAR(50),
    id_hoa_don INT,
    trang_thai NVARCHAR(30),
    ngay_cap_nhat DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE doi_tra (
    id_doi_tra INT IDENTITY PRIMARY KEY,
	ma_doi_tra NVARCHAR(50),
    id_hoa_don INT,
    ly_do NVARCHAR(255),
    trang_thai NVARCHAR(30),
    ngay_yeu_cau DATETIME DEFAULT GETDATE()
);
GO

INSERT INTO vai_tro (ma_vai_tro, ten_vai_tro)
VALUES 
('ADMIN','Quản trị viên'),
('STAFF','Nhân viên'),
('USER','Khách hàng');

INSERT INTO nguoi_dung (id_vai_tro, ma_nguoi_dung, ho_ten, so_dien_thoai, email, mat_khau)
VALUES
(1,'ND001','Nguyễn Văn A','0342806730','admin@gmail.com','123456'),
(2,'ND002','Nguyễn Văn B','0901234567','staff@gmail.com','123456'),
(3,'ND003','Trần Văn C','0912345678','user1@gmail.com','123456');

INSERT INTO danh_muc (ma_danh_muc, ten_danh_muc)
VALUES
('DM001','Áo thể thao'),
('DM002','Giày thể thao');

INSERT INTO san_pham (id_danh_muc, ma_san_pham, ten_san_pham, gia_goc)
VALUES
(1,'SP001','Áo Nike Sport',300000),
(2,'SP002','Giày Adidas Run',1200000);

INSERT INTO kich_thuoc (ma_kich_thuoc, ten_kich_thuoc)
VALUES
('S','S'),
('M','M'),
('L','L');

INSERT INTO mau_sac (ma_mau_sac, ten_mau)
VALUES
('DEN','Đen'),
('TRANG','Trắng');

INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu)
VALUES
('COTTON','Cotton'),
('VAI_THUN','Vải thun');

INSERT INTO san_pham_chi_tiet 
(id_san_pham, id_kich_thuoc, id_mau_sac, id_chat_lieu, ma_san_pham_chi_tiet, so_luong, gia_ban)
VALUES
(1,2,1,1,'SPCT001',50,350000), -- Áo size M đen
(2,3,2,2,'SPCT002',30,1300000); -- Giày size L trắng

INSERT INTO tinh (ma_tinh, ten_tinh)
VALUES ('HCM','TP Hồ Chí Minh');

INSERT INTO huyen (ma_huyen, id_tinh, ten_huyen)
VALUES ('Q1',1,'Quận 1');

INSERT INTO xa (ma_xa, id_huyen, ten_xa)
VALUES ('PX1',1,'Phường Bến Nghé');

INSERT INTO dia_chi_van_chuyen
(ma_dia_chi_van_chuyen, id_nguoi_dung, id_xa, ten_nguoi_nhan, so_dien_thoai, dia_chi_chi_tiet, loai_dia_chi, la_mac_dinh)
VALUES
('DC001',3,1,'Trần Văn C','0912345678','123 Lê Lợi','NHA_RIENG',1);

INSERT INTO gio_hang (ma_gio_hang, id_nguoi_dung)
VALUES ('GH001',2);

INSERT INTO gio_hang_chi_tiet (id_gio_hang, id_spct, ma_gio_hang_chi_tiet, so_luong)
VALUES (1,1,'GHCT001',2);

INSERT INTO ma_giam_gia
(ma_code, kieu_giam_gia, gia_tri_giam, gia_tri_giam_toi_da, gia_tri_toi_thieu, so_luong, ngay_bat_dau, ngay_ket_thuc)
VALUES
('SALE10','PERCENT',10,100000,200000,100,GETDATE(),DATEADD(DAY,30,GETDATE()));

INSERT INTO hoa_don
(ma_hoa_don, id_nguoi_dung, id_ma_giam_gia,
ten_nguoi_nhan, so_dien_thoai, tinh, huyen, xa, dia_chi_chi_tiet,
tong_tien_hang, tien_giam, phi_van_chuyen, tong_thanh_toan)
VALUES
('HD001',2,1,
'Trần Văn B','0912345678','TP Hồ Chí Minh','Quận 1','Phường Bến Nghé','123 Lê Lợi',
700000,70000,30000,660000);

INSERT INTO hoa_don_chi_tiet
(ma_hoa_don_chi_tiet, id_hoa_don, id_spct,
ten_san_pham, kich_thuoc, mau_sac, chat_lieu,
don_gia, so_luong, thanh_tien)
VALUES
('HDCT001',1,1,
'Áo Nike Sport','M','Đen','Cotton',
350000,2,700000);

INSERT INTO pt_thanh_toan (ma_pt_thanh_toan, ten_pttt)
VALUES
('COD','Thanh toán khi nhận hàng'),
('BANK','Chuyển khoản ngân hàng');

INSERT INTO lich_su_thanh_toan
(ma_lich_su_thanh_toan, id_hoa_don, id_pttt, so_tien, trang_thai_thanh_toan)
VALUES
('LSTT001',1,1,660000,'DA_THANH_TOAN');



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

SELECT * FROM tinh;
SELECT * FROM huyen;
SELECT * FROM xa;
SELECT * FROM dia_chi_van_chuyen;

SELECT * FROM lich_su_su_dung_ma_giam_gia;
SELECT * FROM lich_su_hoa_don;
SELECT * FROM lich_su_thanh_toan;
SELECT * FROM doi_tra;

