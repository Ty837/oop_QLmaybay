import java.io.Serializable;
import java.text.Normalizer;

public class ChuyenBay implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean status = true;
    // Chuyến bay cũ từng dùng status để báo còn ghế; cờ này giúp nâng dữ liệu cũ an toàn.
    private boolean daChuyenStatusSangHoatDong = true;
    private String soHieuCHuyenBay;
    private String DiemDi, DiemDen;
    private String NgayDi, NgayDen;
    private int tongSoGhe, soGheTrong;
    private double giaGocPhoThong, phiHanhLy;
    private double giaGocThuongGia, phiPhongCho;

    public ChuyenBay() {
        this.soHieuCHuyenBay = "";
        this.DiemDi = "";
        this.DiemDen = "";
        this.NgayDi = "";
        this.NgayDen = "";
        this.tongSoGhe = 0;
        this.soGheTrong = 0;
    }

    public ChuyenBay(boolean status, String soHieuCHuyenBay, String DiemDi, String DiemDen, String NgayDi, String NgayDen, int tongSoGhe, int soGheTrong) {
        this.status = status;
        this.daChuyenStatusSangHoatDong = true;
        this.soHieuCHuyenBay = soHieuCHuyenBay;
        this.DiemDi = boDau(DiemDi);
        this.DiemDen = boDau(DiemDen);
        this.NgayDi = NgayDi;
        this.NgayDen = NgayDen;
        this.tongSoGhe = tongSoGhe;
        this.soGheTrong = soGheTrong;
        boSungBangGiaMacDinhNeuThieu();
    }

    /** Constructor tiện dụng dùng ngày trong lớp NgayThang. */
    public ChuyenBay(String maChuyenBay, String diemDi, String diemDen,
                     int tongSoGhe, NgayThang ngayDi) {
        this(true, maChuyenBay, diemDi, diemDen,
                ngayDi == null ? "" : ngayDi.toString(),
                ngayDi == null ? "" : ngayDi.toString(),
                tongSoGhe, tongSoGhe);
    }

    public boolean DatGhe () {
        if (!status || soGheTrong <= 0) {
            return false;
        }
        soGheTrong--;
        return true;
    }
    public boolean HuyGhe () {
        if (soGheTrong >= tongSoGhe) {
            return false;
        }
        soGheTrong++;
        return true;
    }
    public void hienThiThongTin() {
        System.out.println(" | Trang thai: " + (status ? "Dang hoat dong" : "Ngung hoat dong")
                + " |[Chuyen bay] So hieu: " + soHieuCHuyenBay + " | Diem di: " + DiemDi
                + " | Diem den: " + DiemDen + " | Ngay di: " + NgayDi
                + " | Ngay den: " + NgayDen + " | Tong so ghe: " + tongSoGhe
                + " | So ghe trong: " + soGheTrong);
    }
    
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
        this.daChuyenStatusSangHoatDong = true;
    }
    /** Nâng dữ liệu đã lưu từ phiên bản cũ, khi status còn mang nghĩa còn ghế. */
    public void chuanHoaTrangThaiCu() {
        if (!daChuyenStatusSangHoatDong) {
            status = true;
            daChuyenStatusSangHoatDong = true;
        }
    }
    public boolean conGheTrong() {
        return soGheTrong > 0;
    }
    public String getSoHieuCHuyenBay() {
        return soHieuCHuyenBay;
    }
    /** Alias viết đúng chuẩn camelCase; giữ getter cũ để tương thích code nhóm. */
    public String getSoHieuChuyenBay() {
        return soHieuCHuyenBay;
    }
    public void setSoHieuCHuyenBay(String soHieuCHuyenBay) {
        this.soHieuCHuyenBay = soHieuCHuyenBay;
    }
    public void setSoHieuChuyenBay(String soHieuChuyenBay) {
        this.soHieuCHuyenBay = soHieuChuyenBay;
    }
    public String getDiemDi() {
        return DiemDi;
    }
    public void setDiemDi(String DiemDi) {
        this.DiemDi = boDau(DiemDi);
    }
    public String getDiemDen() {
        return DiemDen;
    }
    public void setDiemDen(String DiemDen) {
        this.DiemDen = boDau(DiemDen);
    }
    public String getNgayDi() {
        return NgayDi;
    }
    public void setNgayDi(String NgayDi) {
        this.NgayDi = NgayDi;
    }
    public String getNgayDen() {
        return NgayDen;
    }
    public void setNgayDen(String NgayDen) {
        this.NgayDen = NgayDen;
    }
    public int getTongSoGhe() {
        return tongSoGhe;
    }
    public void setTongSoGhe(int tongSoGhe) {
        this.tongSoGhe = tongSoGhe;
    }
    public int getSoGheTrong() {
        return soGheTrong;
    }
    public void setSoGheTrong(int soGheTrong) {
        this.soGheTrong = soGheTrong;
    }

    public boolean coGiaPhoThong() {
        return Double.isFinite(giaGocPhoThong) && giaGocPhoThong > 0
                && Double.isFinite(phiHanhLy) && phiHanhLy >= 0;
    }

    public boolean coGiaThuongGia() {
        return Double.isFinite(giaGocThuongGia) && giaGocThuongGia > 0
                && Double.isFinite(phiPhongCho) && phiPhongCho >= 0;
    }

    public double getGiaGocTheoHang(int loaiVe) {
        return loaiVe == 1 ? giaGocPhoThong : giaGocThuongGia;
    }

    public double getPhuPhiTheoHang(int loaiVe) {
        return loaiVe == 1 ? phiHanhLy : phiPhongCho;
    }

    public double tinhGiaVeTheoHang(int loaiVe) {
        return (getGiaGocTheoHang(loaiVe) + getPhuPhiTheoHang(loaiVe))
                * (1 + VeMayBay.THUE_VAT);
    }

    public void setGiaPhoThong(double giaGoc, double phi) {
        kiemTraGia(giaGoc, phi);
        giaGocPhoThong = giaGoc;
        phiHanhLy = phi;
    }

    public void setGiaThuongGia(double giaGoc, double phi) {
        kiemTraGia(giaGoc, phi);
        giaGocThuongGia = giaGoc;
        phiPhongCho = phi;
    }

    /** Bo sung gia mau cho du lieu cu chua co bang gia. */
    public boolean boSungBangGiaMacDinhNeuThieu() {
        int soThuTu = soThuTuGiaMau();
        double giaGocMau = soThuTu > 0 ? 1_850_000 + (soThuTu - 1) * 325_000 : 2_500_000;
        double phiPhoThongMau = soThuTu > 0 ? 180_000 + (soThuTu - 1) * 15_000 : 200_000;
        double phiThuongGiaMau = soThuTu > 0 ? 420_000 + (soThuTu - 1) * 20_000 : 500_000;
        boolean daBoSung = false;

        if (!coGiaPhoThong()) {
            if (!Double.isFinite(giaGocPhoThong) || giaGocPhoThong <= 0) giaGocPhoThong = giaGocMau;
            if (!Double.isFinite(phiHanhLy) || phiHanhLy < 0) phiHanhLy = phiPhoThongMau;
            daBoSung = true;
        }
        if (!coGiaThuongGia()) {
            if (!Double.isFinite(giaGocThuongGia) || giaGocThuongGia <= 0) giaGocThuongGia = giaGocMau;
            if (!Double.isFinite(phiPhongCho) || phiPhongCho < 0) phiPhongCho = phiThuongGiaMau;
            daBoSung = true;
        }
        return daBoSung;
    }

    private int soThuTuGiaMau() {
        if (soHieuCHuyenBay == null || !soHieuCHuyenBay.matches("(?i)CB\\d{4}")) return 0;
        try {
            int so = Integer.parseInt(soHieuCHuyenBay.substring(2));
            return so >= 1 && so <= 10 ? so : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void kiemTraGia(double giaGoc, double phi) {
        if (!Double.isFinite(giaGoc) || giaGoc <= 0 || !Double.isFinite(phi) || phi < 0) {
            throw new IllegalArgumentException("Gia goc phai lon hon 0 va phu phi khong duoc am.");
        }
    }

    private static String boDau(String value) {
        if (value == null) return null;
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }
}
