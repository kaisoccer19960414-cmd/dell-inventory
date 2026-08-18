-- 販売開始日/販売停止日を記録する。時刻までは不要なためDATE型。
-- 既存商品には遡って正しい日付を入れられないため、両方ともNULL(不明)のままにする。
ALTER TABLE products ADD COLUMN activated_at DATE;
ALTER TABLE products ADD COLUMN deactivated_at DATE;
