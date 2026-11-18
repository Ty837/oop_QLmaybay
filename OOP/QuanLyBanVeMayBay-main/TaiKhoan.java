/** Tài khoản mẫu dùng để đăng nhập và phân quyền trong đồ án. */
public final class TaiKhoan {
    private final String tenDangNhap;
    private final String matKhau;
    private final VaiTro vaiTro;
    private final String maNhanVien;

    public TaiKhoan(String tenDangNhap, String matKhau, VaiTro vaiTro) {
        this(tenDangNhap, matKhau, vaiTro, null);
    }

    public TaiKhoan(String tenDangNhap, String matKhau, VaiTro vaiTro, String maNhanVien) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten dang nhap khong duoc de trong.");
        }
        if (matKhau == null || vaiTro == null) {
            throw new IllegalArgumentException("Mat khau va vai tro la bat buoc.");
        }
        this.tenDangNhap = tenDangNhap.trim();
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.maNhanVien = maNhanVien == null ? null : maNhanVien.trim();
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public boolean khopThongTin(String ten, char[] matKhauNhap) {
        return ten != null
                && matKhauNhap != null
                && tenDangNhap.equalsIgnoreCase(ten.trim())
                && matKhau.equals(new String(matKhauNhap));
    }
}
