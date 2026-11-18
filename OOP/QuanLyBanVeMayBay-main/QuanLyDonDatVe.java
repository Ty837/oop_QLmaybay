import java.util.Scanner;
import java.util.Locale;
import java.time.LocalDateTime;

/** Quản lý danh sách đơn đặt vé bằng mảng đối tượng DonDatVe[]. */
public class QuanLyDonDatVe {
    private DonDatVe[] danhSachDon;
    private int soLuongDon;
    private int soDonTiepTheo;
    private int soVeTiepTheo;
    private String thongBaoGanNhat;
    private static final Scanner INPUT = new Scanner(System.in);

    public QuanLyDonDatVe() {
        danhSachDon = new DonDatVe[100];
        soLuongDon = 0;
        soDonTiepTheo = 1;
        soVeTiepTheo = 1;
        thongBaoGanNhat = "";
    }

    /** Tạo đơn với mã đơn và mã vé tự sinh sau khi thanh toán thành công. */
    public DonDatVe datVeTuDong(HanhKhach hanhKhach, ChuyenBay chuyenBay,
                                int loaiVe, double giaGoc, double phuPhi,
                                IThanhToan thanhToan, String tenPhuongThuc) {
        String maDonDat = taoMaDonTiepTheo();
        String maVe = taoMaVeTiepTheo();
        return datVe(maDonDat, maVe, hanhKhach, chuyenBay, loaiVe,
                giaGoc, phuPhi, thanhToan, tenPhuongThuc);
    }

    /** Đặt vé; ghế chỉ được giữ nếu thanh toán thành công. */
    public DonDatVe datVe(String maDonDat, String maVe, HanhKhach hanhKhach,
                          ChuyenBay chuyenBay, int loaiVe, double giaGoc,
                          double phuPhi, IThanhToan thanhToan,
                          String tenPhuongThuc) {
        thongBaoGanNhat = "";
        if (maDonDat == null || maDonDat.trim().isEmpty()
                || maVe == null || maVe.trim().isEmpty()) {
            return loi("Ma don va ma ve khong duoc de trong.");
        }
        if (hanhKhach == null || chuyenBay == null || thanhToan == null) {
            return loi("Can co hanh khach, chuyen bay va phuong thuc thanh toan.");
        }
        if (!hanhKhach.isStatus()) {
            return loi("Hanh khach da ngung hoat dong.");
        }
        if (!chuyenBay.isStatus()) {
            return loi("Chuyen bay da ngung khai thac.");
        }
        if (loaiVe != 1 && loaiVe != 2) {
            return loi("Loai ve khong hop le.");
        }
        if (!Double.isFinite(giaGoc) || !Double.isFinite(phuPhi)
                || giaGoc <= 0 || phuPhi < 0) {
            return loi("Gia ve phai lon hon 0 va phu phi khong duoc am.");
        }
        if (timDonTheoMa(maDonDat) != null || daCoMaVe(maVe)) {
            return loi("Ma don hoac ma ve da duoc su dung.");
        }

        String maDonSach = maDonDat.trim();
        String maVeSach = maVe.trim();
        VeMayBay ve = loaiVe == 1
                ? new VePhoThong(maVeSach, giaGoc, phuPhi)
                : new VeThuongGia(maVeSach, giaGoc, phuPhi);
        ve.setChuyenBay(chuyenBay);
        ve.setHanhKhach(hanhKhach);
        double tongTien = ve.tinhGiaVe();
        if (!Double.isFinite(tongTien) || tongTien <= 0) {
            return loi("Tong tien ve khong hop le.");
        }
        if (!chuyenBay.DatGhe()) {
            return loi("Chuyen bay da het ghe.");
        }

        boolean daThanhToan;
        try {
            daThanhToan = thanhToan.thanhToan(tongTien);
        } catch (RuntimeException e) {
            chuyenBay.HuyGhe();
            return loi("Khong the thuc hien thanh toan: " + e.getMessage());
        }
        if (!daThanhToan) {
            chuyenBay.HuyGhe();
            return loi("Thanh toan khong thanh cong; ghe da duoc tra lai.");
        }

        DonDatVe don = new DonDatVe(maDonSach, hanhKhach, hanhKhach, null,
                chuyenBay, ve, thanhToan, ngayHomNay());
        don.setThoiGianDat(LocalDateTime.now());
        don.xacNhanDatVe();
        if (!luuDonNoiBo(don)) {
            chuyenBay.HuyGhe();
            return loi("Khong the luu don; ghe da duoc tra lai.");
        }
        thongBaoGanNhat = "Dat ve thanh cong.";
        return don;
    }

    /** Thêm một đơn đã tạo sẵn theo chữ ký trong sơ đồ lớp. */
    public void themDon(DonDatVe don) {
        if (!luuDonNoiBo(don)) {
            thongBaoGanNhat = "Khong them duoc don: du lieu khong hop le hoac ma da ton tai.";
        }
    }

    /** Sửa tên, điện thoại hoặc hộ chiếu của hành khách trong đơn đã chọn. */
    public boolean suaDon(String maDon) {
        DonDatVe don = timDonTheoMa(maDon);
        if (don == null || don.isDaHuy() || don.getHanhKhach() == null) {
            return false;
        }
        HanhKhach cu = don.getHanhKhach();
        System.out.println("Nhan Enter neu muon giu nguyen thong tin hien tai.");
        String hoTen = nhapChuoi("Ho ten moi [" + cu.getHoTen() + "]: ");
        String dienThoai = nhapChuoi("So dien thoai moi [" + cu.getSoDienThoai() + "]: ");
        String hoChieu = nhapChuoi("So ho chieu moi [" + cu.getHochieu() + "]: ");
        if (hoTen == null || dienThoai == null || hoChieu == null) {
            return false;
        }
        HanhKhach moi = new HanhKhach(cu.isStatus(), cu.getMaSo(),
                hoTen.isEmpty() ? cu.getHoTen() : hoTen,
                dienThoai.isEmpty() ? cu.getSoDienThoai() : dienThoai,
                hoChieu.isEmpty() ? cu.getHochieu() : hoChieu);
        don.setHanhKhach(moi);
        return true;
    }

    /** Giữ đơn trong lịch sử và chuyển sang trạng thái đã hủy. */
    public boolean xoaDon(String maDon) {
        return huyDonTheoMa(maDon);
    }

    /** Hủy đơn nhưng vẫn lưu lại để tra cứu lịch sử. */
    public boolean huyDonTheoMa(String maDonDat) {
        DonDatVe don = timDonTheoMa(maDonDat);
        if (don == null || don.isDaHuy() || don.getChuyenBay() == null) {
            return false;
        }
        if (!don.getChuyenBay().HuyGhe()) {
            return false;
        }
        don.setDaHuy(true);
        return true;
    }

    public DonDatVe timDonTheoMa(String maDonDat) {
        int viTri = timViTriDonTheoMa(maDonDat);
        return viTri < 0 ? null : danhSachDon[viTri];
    }

    public int demDonTheoMaChuyenBay(String maChuyenBay) {
        return demDonTheoMaChuyenBay(maChuyenBay, false);
    }

    public int demDonConHieuLucTheoMaChuyenBay(String maChuyenBay) {
        return demDonTheoMaChuyenBay(maChuyenBay, true);
    }

    public int getSoLuongDonConHieuLuc() {
        int dem = 0;
        for (int i = 0; i < soLuongDon; i++) {
            if (!danhSachDon[i].isDaHuy()) dem++;
        }
        return dem;
    }

    public double thongKeDoanhThuTheoChuyenBay(String maChuyenBay) {
        if (maChuyenBay == null || maChuyenBay.trim().isEmpty()) return 0;
        double tong = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            ChuyenBay chuyenBay = don.getChuyenBay();
            if (!don.isDaHuy() && chuyenBay != null
                    && maChuyenBay.trim().equalsIgnoreCase(chuyenBay.getSoHieuChuyenBay())) {
                tong += don.getSoTienThanhToan();
            }
        }
        return tong;
    }

    private int demDonTheoMaChuyenBay(String maChuyenBay, boolean chiConHieuLuc) {
        if (maChuyenBay == null || maChuyenBay.trim().isEmpty()) {
            return 0;
        }
        int dem = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            ChuyenBay chuyenBay = don.getChuyenBay();
            String maCuaChuyenBay = chuyenBay == null ? null : chuyenBay.getSoHieuCHuyenBay();
            if (maCuaChuyenBay != null
                    && (!chiConHieuLuc || !don.isDaHuy())
                    && maCuaChuyenBay.equalsIgnoreCase(maChuyenBay.trim())) {
                dem++;
            }
        }
        return dem;
    }

    /** Hàm thống kê doanh thu theo đúng tên trong sơ đồ lớp. */
    public double thongKeDoanhThu() {
        double doanhThu = 0;
        for (int i = 0; i < soLuongDon; i++) {
            if (!danhSachDon[i].isDaHuy()) {
                doanhThu += danhSachDon[i].getSoTienThanhToan();
            }
        }
        return doanhThu;
    }

    /** Giữ tên hàm cũ để giao diện hiện tại tiếp tục sử dụng được. */
    public double tinhDoanhThu() {
        return thongKeDoanhThu();
    }

    /** Chuyến có số đơn còn hiệu lực nhiều nhất trong danh sách đang khai thác. */
    public ChuyenBay timChuyenBayBanChayNhat(ChuyenBay[] cacChuyenBay) {
        return timChuyenBayTheoSoDon(cacChuyenBay, true);
    }

    /** Chuyến có số đơn còn hiệu lực ít nhất; tính cả chuyến chưa bán được vé. */
    public ChuyenBay timChuyenBayItKhachNhat(ChuyenBay[] cacChuyenBay) {
        return timChuyenBayTheoSoDon(cacChuyenBay, false);
    }

    private ChuyenBay timChuyenBayTheoSoDon(ChuyenBay[] cacChuyenBay, boolean timMax) {
        if (cacChuyenBay == null) return null;
        ChuyenBay ketQua = null;
        int soDonTotNhat = timMax ? -1 : Integer.MAX_VALUE;
        for (ChuyenBay chuyenBay : cacChuyenBay) {
            if (chuyenBay == null || !chuyenBay.isStatus()) continue;
            int soDon = demDonConHieuLucTheoMaChuyenBay(chuyenBay.getSoHieuChuyenBay());
            if (ketQua == null || (timMax ? soDon > soDonTotNhat : soDon < soDonTotNhat)) {
                ketQua = chuyenBay;
                soDonTotNhat = soDon;
            }
        }
        return ketQua;
    }

    /** Doanh thu theo tháng/năm; truyền 0 ở tháng hoặc năm để không lọc phần đó. */
    public double thongKeDoanhThu(int thang, int nam, String maNhanVien) {
        double tong = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            if (don.isDaHuy() || don.getThoiGianDat() == null) continue;
            LocalDateTime ngayDat = don.getThoiGianDat();
            if (thang > 0 && ngayDat.getMonthValue() != thang) continue;
            if (nam > 0 && ngayDat.getYear() != nam) continue;
            if (!donThuocNhanVien(don, maNhanVien)) continue;
            tong += don.getSoTienThanhToan();
        }
        return tong;
    }

    public double thongKeDoanhThuTheoQuy(int quy, int nam, String maNhanVien) {
        if (quy < 1 || quy > 4) return 0;
        double tong = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            if (don.isDaHuy() || don.getThoiGianDat() == null) continue;
            LocalDateTime ngayDat = don.getThoiGianDat();
            int quyCuaDon = (ngayDat.getMonthValue() - 1) / 3 + 1;
            if (ngayDat.getYear() != nam || quyCuaDon != quy
                    || !donThuocNhanVien(don, maNhanVien)) continue;
            tong += don.getSoTienThanhToan();
        }
        return tong;
    }

    private boolean donThuocNhanVien(DonDatVe don, String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) return true;
        NhanVien nhanVien = don.getNhanVienLap();
        return nhanVien != null && nhanVien.getMaNhanVien() != null
                && nhanVien.getMaNhanVien().equalsIgnoreCase(maNhanVien.trim());
    }

    public int[] thongKeSoDonTheoThang(int nam, String maNhanVien) {
        int[] soDonTheoThang = new int[12];
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            if (don.isDaHuy() || don.getThoiGianDat() == null
                    || don.getThoiGianDat().getYear() != nam
                    || !donThuocNhanVien(don, maNhanVien)) continue;
            soDonTheoThang[don.getThoiGianDat().getMonthValue() - 1]++;
        }
        return soDonTheoThang;
    }

    public int timThangBanChayNhat(int nam) {
        return timThangTheoSoDon(thongKeSoDonTheoThang(nam, null), true);
    }

    public int timThangItKhachNhat(int nam) {
        return timThangTheoSoDon(thongKeSoDonTheoThang(nam, null), false);
    }

    private int timThangTheoSoDon(int[] soDonTheoThang, boolean timMax) {
        int tong = 0;
        for (int soDon : soDonTheoThang) tong += soDon;
        if (tong == 0) return 0;
        int thangTotNhat = 0;
        for (int i = 1; i < soDonTheoThang.length; i++) {
            if (timMax ? soDonTheoThang[i] > soDonTheoThang[thangTotNhat]
                    : soDonTheoThang[i] < soDonTheoThang[thangTotNhat]) {
                thangTotNhat = i;
            }
        }
        return thangTotNhat + 1;
    }

    public double thongKeDoanhThuTheoThang(int thang, int nam) {
        return thongKeDoanhThu(thang, nam, null);
    }

    public double thongKeDoanhThuTheoNam(int nam) {
        return thongKeDoanhThu(0, nam, null);
    }

    public double thongKeDoanhThuTheoNhanVien(String maNhanVien, int thang, int nam) {
        return thongKeDoanhThu(thang, nam, maNhanVien);
    }

    public double thongKeDoanhThuTheoNhanVienTheoQuy(String maNhanVien, int quy, int nam) {
        return thongKeDoanhThuTheoQuy(quy, nam, maNhanVien);
    }

    public void inBaoCaoDoanhThu() {
        int soDonHieuLuc = 0;
        for (int i = 0; i < soLuongDon; i++) {
            if (!danhSachDon[i].isDaHuy()) {
                soDonHieuLuc++;
            }
        }
        System.out.println("\n===== BAO CAO DOANH THU =====");
        System.out.println("So don con hieu luc: " + soDonHieuLuc);
        System.out.printf("Tong doanh thu: %,.0f VND%n", thongKeDoanhThu());

        String[] maChuyenBay = new String[soLuongDon];
        double[] doanhThuTheoChuyen = new double[soLuongDon];
        int soChuyen = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            if (don.isDaHuy() || don.getChuyenBay() == null) {
                continue;
            }
            String ma = don.getChuyenBay().getSoHieuCHuyenBay();
            if (ma == null || ma.trim().isEmpty()) {
                ma = "(khong co ma)";
            }
            int viTri = 0;
            while (viTri < soChuyen && !maChuyenBay[viTri].equalsIgnoreCase(ma)) {
                viTri++;
            }
            if (viTri == soChuyen) {
                maChuyenBay[soChuyen] = ma;
                soChuyen++;
            }
            doanhThuTheoChuyen[viTri] += don.getSoTienThanhToan();
        }
        for (int i = 0; i < soChuyen; i++) {
            System.out.printf("- %s: %,.0f VND%n", maChuyenBay[i], doanhThuTheoChuyen[i]);
        }
        System.out.println("=============================\n");
    }

    public void xuatDanhSach() {
        xuatDanhSach(getDanhSach());
    }

    public void xuatDanhSach(DonDatVe[] danhSachCanIn) {
        if (danhSachCanIn == null || danhSachCanIn.length == 0) {
            System.out.println("Danh sach don dat ve trong.");
            return;
        }
        for (int i = 0; i < danhSachCanIn.length; i++) {
            System.out.print((i + 1) + ". ");
            danhSachCanIn[i].hienThiThongTin();
        }
    }

    public DonDatVe[] timDonGanDung(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return new DonDatVe[0];
        String khoa = tuKhoa.trim().toLowerCase(Locale.ROOT);
        DonDatVe[] tam = new DonDatVe[soLuongDon];
        int dem = 0;
        for (int i = 0; i < soLuongDon; i++) {
            DonDatVe don = danhSachDon[i];
            HanhKhach khach = don.getHanhKhach();
            ChuyenBay chuyenBay = don.getChuyenBay();
            VeMayBay ve = don.getVe();
            if (khopTatCaTuKhoa(khoa, don.getMaDonDat(),
                    khach == null ? null : khach.getMaSo(),
                    khach == null ? null : khach.getHoTen(),
                    chuyenBay == null ? null : chuyenBay.getSoHieuChuyenBay(),
                    ve == null ? null : ve.getMaVe())) {
                tam[dem++] = don;
            }
        }
        DonDatVe[] ketQua = new DonDatVe[dem];
        System.arraycopy(tam, 0, ketQua, 0, dem);
        return ketQua;
    }

    private boolean khopTatCaTuKhoa(String khoa, String... cacTruong) {
        for (String tuKhoa : khoa.split("\\s+")) {
            boolean timThay = false;
            for (String giaTri : cacTruong) {
                if (giaTri != null && giaTri.toLowerCase(Locale.ROOT).contains(tuKhoa)) {
                    timThay = true;
                    break;
                }
            }
            if (!timThay) return false;
        }
        return true;
    }

    public DonDatVe[] getDanhSach() {
        DonDatVe[] ketQua = new DonDatVe[soLuongDon];
        System.arraycopy(danhSachDon, 0, ketQua, 0, soLuongDon);
        return ketQua;
    }

    public int getSoLuong() {
        return soLuongDon;
    }

    public String getThongBaoGanNhat() {
        return thongBaoGanNhat;
    }

    private boolean luuDonNoiBo(DonDatVe don) {
        if (don == null || don.getMaDonDat() == null || don.getMaDonDat().trim().isEmpty()
                || don.getVe() == null || don.getVe().getMaVe() == null
                || don.getVe().getMaVe().trim().isEmpty() || don.getHanhKhach() == null
                || don.getChuyenBay() == null) {
            return false;
        }
        String maHanhKhach = don.getHanhKhach().getMaSo();
        String maChuyenBay = don.getChuyenBay().getSoHieuCHuyenBay();
        double soTien = don.getSoTienThanhToan();
        if (maHanhKhach == null || maHanhKhach.trim().isEmpty()
                || maChuyenBay == null || maChuyenBay.trim().isEmpty()
                || !Double.isFinite(soTien) || soTien <= 0
                || timDonTheoMa(don.getMaDonDat()) != null
                || daCoMaVe(don.getVe().getMaVe())) {
            return false;
        }
        if (soLuongDon == danhSachDon.length) {
            DonDatVe[] danhSachMoi = new DonDatVe[danhSachDon.length * 2];
            System.arraycopy(danhSachDon, 0, danhSachMoi, 0, soLuongDon);
            danhSachDon = danhSachMoi;
        }
        danhSachDon[soLuongDon++] = don;
        capNhatMaTiepTheo(don.getMaDonDat(), "DD", true);
        capNhatMaTiepTheo(don.getVe().getMaVe(), "VE", false);
        return true;
    }

    private void capNhatMaTiepTheo(String ma, String tienTo, boolean laMaDon) {
        if (ma == null || !ma.toUpperCase().startsWith(tienTo)) return;
        try {
            int so = Integer.parseInt(ma.substring(tienTo.length()));
            if (laMaDon) soDonTiepTheo = Math.max(soDonTiepTheo, so + 1);
            else soVeTiepTheo = Math.max(soVeTiepTheo, so + 1);
        } catch (NumberFormatException ignored) {
            // Mã tự đặt không theo dạng DD0001/VE0001 không ảnh hưởng bộ đếm.
        }
    }

    private int timViTriDonTheoMa(String maDonDat) {
        if (maDonDat == null || maDonDat.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < soLuongDon; i++) {
            if (danhSachDon[i].getMaDonDat().equalsIgnoreCase(maDonDat.trim())) {
                return i;
            }
        }
        return -1;
    }

    private boolean daCoMaVe(String maVe) {
        if (maVe == null || maVe.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < soLuongDon; i++) {
            VeMayBay ve = danhSachDon[i].getVe();
            if (ve != null && ve.getMaVe() != null && ve.getMaVe().equalsIgnoreCase(maVe.trim())) {
                return true;
            }
        }
        return false;
    }

    private String taoMaDonTiepTheo() {
        String ma;
        do {
            ma = String.format("DD%04d", soDonTiepTheo++);
        } while (timDonTheoMa(ma) != null);
        return ma;
    }

    private String taoMaVeTiepTheo() {
        String ma;
        do {
            ma = String.format("VE%04d", soVeTiepTheo++);
        } while (daCoMaVe(ma));
        return ma;
    }

    private static String nhapChuoi(String loiNhac) {
        System.out.print(loiNhac);
        return INPUT.hasNextLine() ? INPUT.nextLine().trim() : null;
    }

    private static NgayThang ngayHomNay() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return new NgayThang(today.getDayOfMonth(), today.getMonthValue(), today.getYear());
    }

    private DonDatVe loi(String thongBao) {
        thongBaoGanNhat = thongBao;
        return null;
    }
}
