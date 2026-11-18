import java.io.Serializable;
import java.time.LocalDateTime;

/** Một dòng nhật ký để biết ai đã thay đổi dữ liệu và vào lúc nào. */
public class LichSuThaoTac implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime thoiGian;
    private final String taiKhoan;
    private final String vaiTro;
    private final String hanhDong;
    private final String doiTuong;
    private final String maDoiTuong;
    private final String ghiChu;

    public LichSuThaoTac(String taiKhoan, String vaiTro, String hanhDong,
                         String doiTuong, String maDoiTuong, String ghiChu) {
        this.thoiGian = LocalDateTime.now();
        this.taiKhoan = taiKhoan == null ? "" : taiKhoan;
        this.vaiTro = vaiTro == null ? "" : vaiTro;
        this.hanhDong = hanhDong == null ? "" : hanhDong;
        this.doiTuong = doiTuong == null ? "" : doiTuong;
        this.maDoiTuong = maDoiTuong == null ? "" : maDoiTuong;
        this.ghiChu = ghiChu == null ? "" : ghiChu;
    }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public String getTaiKhoan() { return taiKhoan; }
    public String getVaiTro() { return vaiTro; }
    public String getHanhDong() { return hanhDong; }
    public String getDoiTuong() { return doiTuong; }
    public String getMaDoiTuong() { return maDoiTuong; }
    public String getGhiChu() { return ghiChu; }
}
