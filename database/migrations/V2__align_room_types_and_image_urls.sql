-- Align room type sample data with images/rooms and update image URLs.
-- Safe to run multiple times in dev because all statements are idempotent by key checks.

START TRANSACTION;

UPDATE loai_phong
SET ten_loai = 'Blossom Suite',
    mo_ta = 'Phong am cung, khong gian yen tinh, phu hop cho cap doi va khach cong tac ngan ngay',
    gia_mac_dinh = 1450000,
    suc_chua = 2,
    dien_tich = 32,
    hinh_anh = '/images/rooms/BlossomSuite/Room_6%20%287%29a_6129022bde6a6.jpeg',
    da_xoa = b'0'
WHERE ma_loai = 1;

UPDATE loai_phong
SET ten_loai = 'Signature Suites',
    mo_ta = 'Phong cao cap, noi that go hien dai, bo cuc rong rai voi khu tiep khach rieng',
    gia_mac_dinh = 1850000,
    suc_chua = 2,
    dien_tich = 38,
    hinh_anh = '/images/rooms/SignatureSuites/Room_3%20%285%29a_6129005564f1a.jpeg',
    da_xoa = b'0'
WHERE ma_loai = 2;

UPDATE loai_phong
SET ten_loai = 'Sky Corner Suites',
    mo_ta = 'Phong goc cao cap, nhieu anh sang tu nhien va tam nhin thoang',
    gia_mac_dinh = 2150000,
    suc_chua = 3,
    dien_tich = 42,
    hinh_anh = '/images/rooms/SkyCornerSuites/Room_1%20%2814%29a_612901bc99234.jpeg',
    da_xoa = b'0'
WHERE ma_loai = 3;

INSERT INTO loai_phong (ma_loai, ten_loai, mo_ta, gia_mac_dinh, suc_chua, dien_tich, hinh_anh, da_xoa)
SELECT 4,
       'Family Premium Suites',
       'Phong danh cho gia dinh, dien tich lon, day du tien nghi luu tru dai ngay',
       2650000,
       4,
       55,
       '/images/rooms/FamilyPremiumSuites/Room_2%20%2816%29a_612902bdd57f2.jpeg',
       b'0'
WHERE NOT EXISTS (SELECT 1 FROM loai_phong WHERE ma_loai = 4);

UPDATE loai_phong
SET ten_loai = 'Family Premium Suites',
    mo_ta = 'Phong danh cho gia dinh, dien tich lon, day du tien nghi luu tru dai ngay',
    gia_mac_dinh = 2650000,
    suc_chua = 4,
    dien_tich = 55,
    hinh_anh = '/images/rooms/FamilyPremiumSuites/Room_2%20%2816%29a_612902bdd57f2.jpeg',
    da_xoa = b'0'
WHERE ma_loai = 4;

INSERT INTO phong (so_phong, ma_loai, tang, trang_thai, ghi_chu, da_xoa)
SELECT '401', 4, 4, 'TRONG', NULL, b'0'
WHERE NOT EXISTS (SELECT 1 FROM phong WHERE so_phong = '401');

COMMIT;
