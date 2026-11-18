/** Vai trò đăng nhập và quyền truy cập các màn hình của SkyDesk. */
public enum VaiTro {
    CLIENT("Khach hang", "Xem chuyen bay, tao don dat ve"),
    STAFF("Nhan vien ban ve", "Tra cuu va xu ly dat ve"),
    WAREHOUSE("Quan ly kho", "Quan ly lich bay va so ghe"),
    MANAGER("Quan ly", "Quan ly toan bo chuc nang"),
    ADMIN("Quan tri vien", "Quan ly toan bo chuc nang");

    private final String tenHienThi;
    private final String moTaQuyen;

    VaiTro(String tenHienThi, String moTaQuyen) {
        this.tenHienThi = tenHienThi;
        this.moTaQuyen = moTaQuyen;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public String getMoTaQuyen() {
        return moTaQuyen;
    }

    public boolean coToanQuyen() {
        return this == MANAGER || this == ADMIN;
    }

    public boolean duocMoTrang(String khoaTrang) {
        if (coToanQuyen()) return true;
        if (khoaTrang == null) return false;

        switch (this) {
            case CLIENT:
                return "home".equals(khoaTrang)
                        || "flights".equals(khoaTrang)
                        || "booking".equals(khoaTrang);
            case STAFF:
                return "home".equals(khoaTrang)
                        || "flights".equals(khoaTrang)
                        || "passengers".equals(khoaTrang)
                        || "tickets".equals(khoaTrang)
                        || "booking".equals(khoaTrang)
                        || "orders".equals(khoaTrang);
            case WAREHOUSE:
                return "home".equals(khoaTrang)
                        || "flights".equals(khoaTrang)
                        || "history".equals(khoaTrang);
            default:
                return false;
        }
    }

    public boolean duocQuanLyChuyenBay() {
        return this == WAREHOUSE || coToanQuyen();
    }

    public boolean duocSuaHanhKhach() {
        return this == STAFF || coToanQuyen();
    }

    public boolean duocXoaDuLieu() {
        return coToanQuyen();
    }

    public boolean duocHuyDon() {
        return this == STAFF || coToanQuyen();
    }
}
