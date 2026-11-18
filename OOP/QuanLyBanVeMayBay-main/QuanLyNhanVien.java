import java.util.Locale;

public class QuanLyNhanVien {
    private NhanVien[] danhsach;
    private int soluong;

    public QuanLyNhanVien() {
        this.danhsach = new NhanVien[1000];
        this.soluong = 0;
    }

    private NhanVien layma (String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            return null;
        }
        String ma = maNV.trim();
        for (int i = 0; i < soluong; i++) {
            if (danhsach[i].getMaNhanVien().equalsIgnoreCase(ma)) {
                return danhsach[i];
            }
        }
        return null;
    }
    
    public boolean themNhanVIen( NhanVien nv) {
        if (nv == null || soluong >= danhsach.length) {
            return false;
        }
        String maNhanVien = nv.getMaNhanVien();
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return false;
        }
        if (layma(maNhanVien) != null) {
            return false;
        }
        danhsach[soluong] = nv;
        soluong++;
        return true;
    }

    /** Tên phương thức chuẩn; giữ themNhanVIen để tương thích mã cũ. */
    public boolean themNhanVien(NhanVien nv) {
        return themNhanVIen(nv);
    }

    public boolean[] themNhieuNhanVien(NhanVien[] cacNhanVien) {
        if (cacNhanVien == null) return new boolean[0];
        boolean[] ketQua = new boolean[cacNhanVien.length];
        for (int i = 0; i < cacNhanVien.length; i++) ketQua[i] = themNhanVien(cacNhanVien[i]);
        return ketQua;
    }

    public int getSoLuong() {
        return soluong;
    }

    public NhanVien[] getDanhSach() {
        NhanVien[] ketQua = new NhanVien[soluong];
        System.arraycopy(danhsach, 0, ketQua, 0, soluong);
        return ketQua;
    }

    public void xuatDanhSach(NhanVien[] danhSachCanIn) {
        if (danhSachCanIn == null || danhSachCanIn.length == 0) {
            System.out.println("Danh sach nhan vien trong.");
            return;
        }
        for (int i = 0; i < danhSachCanIn.length; i++) {
            NhanVien nv = danhSachCanIn[i];
            System.out.printf("%d. %s | %s | %s | %s%n", i + 1,
                    nv.getMaNhanVien(), nv.getHoTen(), nv.getSoDienThoai(),
                    nv.isStatus() ? "Dang lam viec" : "Da ngung");
        }
    }

    public NhanVien[] timNhanVienGanDung(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return new NhanVien[0];
        String khoa = tuKhoa.trim().toLowerCase(Locale.ROOT);
        NhanVien[] tam = new NhanVien[soluong];
        int dem = 0;
        for (int i = 0; i < soluong; i++) {
            NhanVien nv = danhsach[i];
            if (khopTatCaTuKhoa(khoa, nv.getMaNhanVien(), nv.getHoTen(), nv.getSoDienThoai())) {
                tam[dem++] = nv;
            }
        }
        NhanVien[] ketQua = new NhanVien[dem];
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

    public boolean suaNhanVien(String maNV, String hoTen, String soDienThoai, String matKhau) {
        NhanVien nv = timNhanVien(maNV);
        if (nv == null || hoTen == null || hoTen.trim().isEmpty()
                || soDienThoai == null || soDienThoai.trim().isEmpty()
                || matKhau == null || matKhau.trim().isEmpty()) return false;
        nv.setHoTen(hoTen.trim());
        nv.setSoDienThoai(soDienThoai.trim());
        nv.setMK(matKhau.trim());
        return true;
    }
    public boolean xoaNhanVien(String maNV) {
        NhanVien nv = layma(maNV);
        if (nv == null|| !nv.isStatus()) {
            return false;
        }
        nv.setStatus(false);
        return true;
    }
    public boolean khoiPhucNhanVien(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return false;
        for (int i = 0; i < soluong; i++) {
            if (danhsach[i].getMaNhanVien().equalsIgnoreCase(maNV.trim())
                    && !danhsach[i].isStatus()) {
                danhsach[i].setStatus(true);
                return true;
            }
        }
        return false;
    }
    public NhanVien timNhanVien(String maNV) {
        NhanVien nv = layma(maNV);
        if (nv == null || !nv.isStatus()) {
            return null;
        }
        return nv;
    }
}
