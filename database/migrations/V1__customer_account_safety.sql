-- Customer account safety migration.
-- Preconditions:
--   * Run against hotel_booking_db after taking a database backup.
--   * The duplicate checks below must return zero rows before adding UNIQUE indexes.
-- This migration does not modify dat_phong or chi_tiet_dat_phong.

START TRANSACTION;

UPDATE khach_hang
SET so_dien_thoai = NULL
WHERE so_dien_thoai IS NOT NULL AND TRIM(so_dien_thoai) = '';

UPDATE khach_hang
SET cccd = NULL
WHERE cccd IS NOT NULL AND TRIM(cccd) = '';

UPDATE khach_hang
SET dia_chi = NULL
WHERE dia_chi IS NOT NULL AND TRIM(dia_chi) = '';

ALTER TABLE khach_hang
    MODIFY COLUMN so_dien_thoai VARCHAR(15) NULL;

ALTER TABLE khach_hang
    ADD UNIQUE KEY uk_khach_hang_ma_tk (ma_tk),
    ADD UNIQUE KEY uk_khach_hang_so_dien_thoai (so_dien_thoai),
    ADD UNIQUE KEY uk_khach_hang_cccd (cccd);

CREATE TABLE password_reset_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ma_tk INT NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_password_reset_token_hash (token_hash),
    KEY idx_password_reset_token_account (ma_tk),
    KEY idx_password_reset_token_expiry (expires_at),
    CONSTRAINT fk_password_reset_token_account
        FOREIGN KEY (ma_tk) REFERENCES tai_khoan (ma_tk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMIT;
