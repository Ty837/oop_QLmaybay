import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

class XuLyFile {
    private static final Path THU_MUC = Paths.get("data");

    // API theo so do UML
    public static boolean ghiVe(VeMayBay[] dsVe, int n) {
        return ghiMang(dsVe, n, "ve.txt");
    }
    public static VeMayBay[] docVe() { return docMang("ve.txt", VeMayBay.class); }
    public static boolean ghiDonDat(DonDatVe[] dsDonDat, int n) {
        return ghiMang(dsDonDat, n, "dondat.txt");
    }
    public static DonDatVe[] docDonDat() { return docMang("dondat.txt", DonDatVe.class); }

    public static boolean ghiLichSu(QuanLyLichSu lichSu) {
        return lichSu != null && ghiMang(lichSu.getDanhSach(), lichSu.getSoLuong(), "lichsu.txt");
    }

    public static LichSuThaoTac[] docLichSu() {
        return docMang("lichsu.txt", LichSuThaoTac.class);
    }

    private static boolean ghiTrangThaiHuyVe(QuanLyVe quanLyVe) {
        VeMayBay[] dsVe = quanLyVe.getDanhSach();
        String[] dong = new String[dsVe.length];
        for (int i = 0; i < dsVe.length; i++) {
            dong[i] = Boolean.toString(quanLyVe.isDaHuy(dsVe[i].getMaVe()));
        }
        try {
            Files.createDirectories(THU_MUC);
            Files.write(THU_MUC.resolve("ve_huy.txt"), Arrays.asList(dong), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Loi ghi ve_huy.txt: " + e.getMessage());
            return false;
        }
    }

    private static boolean[] docTrangThaiHuyVe(int soVe) {
        boolean[] ketQua = new boolean[soVe];
        Path tep = THU_MUC.resolve("ve_huy.txt");
        if (!Files.exists(tep)) return ketQua;
        try {
            List<String> dong = Files.readAllLines(tep, StandardCharsets.UTF_8);
            for (int i = 0; i < Math.min(soVe, dong.size()); i++) {
                ketQua[i] = Boolean.parseBoolean(dong.get(i));
            }
        } catch (IOException e) {
            System.err.println("Loi doc ve_huy.txt: " + e.getMessage());
        }
        return ketQua;
    }

    // Luu/nap cac lop du lieu khac de ung dung co the mo lai trang thai.
    public static boolean ghiChuyenBay(ChuyenBay[] ds, int n) { return ghiMang(ds, n, "chuyenbay.txt"); }
    public static ChuyenBay[] docChuyenBay() { return docMang("chuyenbay.txt", ChuyenBay.class); }
    public static boolean ghiHanhKhach(HanhKhach[] ds, int n) { return ghiMang(ds, n, "hanhkhach.txt"); }
    public static HanhKhach[] docHanhKhach() { return docMang("hanhkhach.txt", HanhKhach.class); }
    public static boolean ghiNhanVien(NhanVien[] ds, int n) { return ghiMang(ds, n, "nhanvien.txt"); }
    public static NhanVien[] docNhanVien() { return docMang("nhanvien.txt", NhanVien.class); }
    public static boolean ghiDaiLy(DaiLy[] ds, int n) { return ghiMang(ds, n, "daily.txt"); }
    public static DaiLy[] docDaiLy() { return docMang("daily.txt", DaiLy.class); }

    private static boolean ghiMang(Object[] ds, int n, String tenTep) {
        if (ds == null || n < 0 || n > ds.length) return false;
        String[] dong = new String[n];
        try {
            for (int i = 0; i < n; i++) {
                if (ds[i] == null) return false;
                dong[i] = maHoa(ds[i]);
            }
            Files.createDirectories(THU_MUC);
            Files.write(THU_MUC.resolve(tenTep), Arrays.asList(dong), StandardCharsets.UTF_8);
            return true;
        } catch (IOException | RuntimeException e) {
            System.err.println("Loi ghi " + tenTep + ": " + e.getMessage());
            return false;
        }
    }

    private static <T> T[] docMang(String tenTep, Class<T> loai) {
        try {
            Path tep = THU_MUC.resolve(tenTep);
            if (!Files.exists(tep)) return mangRong(loai);
            List<String> dong = Files.readAllLines(tep, StandardCharsets.UTF_8);
            T[] ketQua = mang(loai, dong.size());
            for (int i = 0; i < dong.size(); i++) {
                Object obj = giaiMa(dong.get(i));
                if (!loai.isInstance(obj)) throw new IOException("Sai kieu du lieu tai dong " + (i + 1));
                chuanHoaTenHienThi(obj);
                ketQua[i] = loai.cast(obj);
            }
            return ketQua;
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            System.err.println("Loi doc " + tenTep + ": " + e.getMessage());
            return mangRong(loai);
        }
    }

    private static void chuanHoaTenHienThi(Object obj) {
        if (obj instanceof Nguoi) {
            Nguoi nguoi = (Nguoi) obj;
            nguoi.setHoTen(nguoi.getHoTen());
        } else if (obj instanceof ChuyenBay) {
            ChuyenBay chuyenBay = (ChuyenBay) obj;
            chuyenBay.setDiemDi(chuyenBay.getDiemDi());
            chuyenBay.setDiemDen(chuyenBay.getDiemDen());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] mang(Class<T> loai, int n) {
        return (T[]) java.lang.reflect.Array.newInstance(loai, n);
    }
    private static <T> T[] mangRong(Class<T> loai) { return mang(loai, 0); }

    private static String maHoa(Object obj) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(obj); }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }
    private static Object giaiMa(String text) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(text);
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        }
    }

    public static void luuTatCa(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (!ghiChuyenBay(cb.getDanhSach(), cb.getSoLuong())
                || !ghiHanhKhach(hk.getDanhSach(), hk.getSoLuong())
                || !ghiNhanVien(nv.getDanhSach(), nv.getSoLuong())
                || !ghiDaiLy(dl.getDanhSach(), dl.getSoLuong())
                || !ghiVe(ve.getDanhSach(), ve.getSoLuong())
                || !ghiTrangThaiHuyVe(ve)
                || !ghiDonDat(don.getDanhSach(), don.getSoLuong())) {
            throw new IOException("Khong the ghi mot hoac nhieu tep du lieu.");
        }
    }

    public static void luuTatCa(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve,
            QuanLyDonDatVe don, QuanLyLichSu lichSu) throws IOException {
        luuTatCa(cb, hk, nv, dl, ve, don);
        if (!ghiLichSu(lichSu)) throw new IOException("Khong ghi duoc nhat ky thao tac.");
    }

    public static boolean napDuLieu(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve,
            QuanLyDonDatVe don, QuanLyLichSu lichSu) throws IOException {
        boolean daNapDuLieu = napDuLieu(cb, hk, nv, dl, ve, don);
        if (lichSu != null) lichSu.nap(docLichSu());
        return daNapDuLieu;
    }

    public static boolean napDuLieu(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (!Files.exists(THU_MUC)) return false;

        ChuyenBay[] dsCB = docChuyenBay();
        HanhKhach[] dsHK = docHanhKhach();
        NhanVien[] dsNV = docNhanVien();
        DaiLy[] dsDL = docDaiLy();
        VeMayBay[] dsVe = docVe();
        DonDatVe[] dsDon = docDonDat();

        for (ChuyenBay item : dsCB) {
            item.chuanHoaTrangThaiCu();
            if (!cb.themChuyenBay(item)) throw new IOException("Chuyen bay bi trung ma hoac khong hop le.");
        }
        for (HanhKhach item : dsHK) {
            if (!hk.themHanhKhach(item)) throw new IOException("Hanh khach bi trung ma hoac khong hop le.");
        }
        for (NhanVien item : dsNV) {
            if (!nv.themNhanVien(item)) throw new IOException("Nhan vien bi trung ma hoac khong hop le.");
        }
        for (DaiLy item : dsDL) {
            if (!dl.themDaiLy(item)) throw new IOException("Dai ly bi trung ma hoac khong hop le.");
        }

        boolean[] trangThaiVe = docTrangThaiHuyVe(dsVe.length);
        for (int i = 0; i < dsVe.length; i++) {
            VeMayBay item = dsVe[i];
            item.chuanHoaTrangThaiCu(trangThaiVe[i]);
            boolean veDaNgung = trangThaiVe[i] || !item.isStatus();
            ChuyenBay flight = item.getChuyenBay() == null ? null
                    : cb.timChuyenBayTheoMa(item.getChuyenBay().getSoHieuChuyenBay());
            if (flight == null) throw new IOException("Ve tham chieu chuyen bay khong ton tai: " + item.getMaVeHeThong());
            item.setChuyenBay(flight);
            if (item.getHanhKhach() != null) {
                HanhKhach passenger = hk.timHanhKhachTheoMa(item.getHanhKhach().getMaHanhKhach());
                if (passenger == null) throw new IOException("Ve tham chieu hanh khach khong ton tai: " + item.getMaVeHeThong());
                item.setHanhKhach(passenger);
            }
            if (!ve.themVe(item)) throw new IOException("Ma ve trung hoac ve khong hop le: " + item.getMaVeHeThong());
            ve.capNhatTrangThaiHuy(item.getMaVeHeThong(), veDaNgung);
        }

        boolean daBoSungBangGia = false;
        for (ChuyenBay flight : cb.getDanhSach()) {
            if (!flight.coGiaPhoThong()) {
                VeMayBay veCu = timVeGiaTheoHang(ve, flight, 1);
                if (veCu instanceof VePhoThong) {
                    flight.setGiaPhoThong(veCu.getGiaGoc(), ((VePhoThong) veCu).getPhiHanhLy());
                }
            }
            if (!flight.coGiaThuongGia()) {
                VeMayBay veCu = timVeGiaTheoHang(ve, flight, 2);
                if (veCu instanceof VeThuongGia) {
                    flight.setGiaThuongGia(veCu.getGiaGoc(), ((VeThuongGia) veCu).getPhiPhongCho());
                }
            }
            daBoSungBangGia |= flight.boSungBangGiaMacDinhNeuThieu();
        }
        if (daBoSungBangGia && !ghiChuyenBay(cb.getDanhSach(), cb.getSoLuong())) {
            throw new IOException("Khong luu duoc bang gia moi cho chuyen bay.");
        }

        for (DonDatVe item : dsDon) {
            VeMayBay ticket = item.getVe() == null ? null : ve.timVeTheoMa(item.getVe().getMaVeHeThong());
            HanhKhach passenger = item.getHanhKhach() == null ? null : hk.timHanhKhachTheoMa(item.getHanhKhach().getMaHanhKhach());
            NhanVien employee = item.getNhanVienLap() == null ? null
                    : timNhanVienTheoMa(nv, item.getNhanVienLap().getMaNhanVien());
            if (ticket == null || passenger == null) throw new IOException("Don dat co lien ket khong hop le: " + item.getMaDonDat());
            item.setVe(ticket);
            item.setHanhKhach(passenger);
            item.setNguoiDat(passenger);
            item.setNhanVienLap(employee);
            int soDonCu = don.getSoLuong();
            don.themDon(item);
            if (don.getSoLuong() == soDonCu) throw new IOException("Khong nap duoc don: " + item.getMaDonDat());
            if (item.isDaHuy()) ve.capNhatTrangThaiHuy(ticket.getMaVe(), true);
        }
        return true;
    }

    private static VeMayBay timVeGiaTheoHang(QuanLyVe quanLyVe, ChuyenBay flight, int loaiVe) {
        VeMayBay ketQua = null;
        for (VeMayBay ve : quanLyVe.getDanhSach()) {
            if (ve == null || ve.getChuyenBay() == null
                    || !flight.getSoHieuChuyenBay().equalsIgnoreCase(ve.getChuyenBay().getSoHieuChuyenBay())) {
                continue;
            }
            if ((loaiVe == 1 && ve instanceof VePhoThong)
                    || (loaiVe == 2 && ve instanceof VeThuongGia)) {
                ketQua = ve;
            }
        }
        return ketQua;
    }

    private static NhanVien timNhanVienTheoMa(QuanLyNhanVien quanLy, String ma) {
        if (ma == null) return null;
        for (NhanVien nhanVien : quanLy.getDanhSach()) {
            if (nhanVien.getMaNhanVien() != null && nhanVien.getMaNhanVien().equalsIgnoreCase(ma)) {
                return nhanVien;
            }
        }
        return null;
    }

    public static void napDuLieuDemo(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl) {
        LocalDate start = LocalDate.now().plusDays(3);
        String[][] route = {
            {"Ha Noi", "TP. Ho Chi Minh"}, {"TP. Ho Chi Minh", "Da Nang"},
            {"Ha Noi", "Da Nang"}, {"Da Nang", "Ha Noi"},
            {"TP. Ho Chi Minh", "Phu Quoc"}, {"Ha Noi", "Nha Trang"},
            {"Can Tho", "Ha Noi"}, {"Hai Phong", "TP. Ho Chi Minh"},
            {"TP. Ho Chi Minh", "Hue"}, {"Nha Trang", "Ha Noi"}
        };
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = 0; i < route.length; i++) {
            LocalDate day = start.plusDays(i * 2L);
            LocalDateTime departure = day.atTime(6 + (i % 7) * 2, 15);
            LocalDateTime arrival = departure.plusHours(2).plusMinutes(20);
            cb.themChuyenBay(new ChuyenBay(true, String.format("CB%04d", i + 1),
                    route[i][0], route[i][1], formatter.format(departure),
                    formatter.format(arrival), 60 + (i % 3) * 20, 60 + (i % 3) * 20));
        }

        String[] tenHanhKhach = {
            "Nguyen Minh Anh", "Tran Quoc Bao", "Le Thu Ha", "Pham Gia Huy",
            "Vo Ngoc Mai", "Dang Hoang Nam", "Bui Thanh Truc", "Do Nhat Minh",
            "Ho Khanh Linh", "Phan Duc Thanh", "Vu Thao Vy", "Ngo Tuan Kiet",
            "Duong Hai Yen", "Ly Anh Khoa", "Mai Phuong Thao", "Cao Minh Quan",
            "Ta Bao Ngoc", "Huynh Thanh Dat", "Truong My Duyen", "Dinh Gia Bao"
        };
        for (int i = 0; i < tenHanhKhach.length; i++) {
            hk.themHanhKhach(new HanhKhach(String.format("HK%04d", i + 1), tenHanhKhach[i],
                    String.format("09%08d", 10000000 + i),
                    String.format("C%08d", 10000000 + i)));
        }
        nv.themNhanVien(new NhanVien("NV0001", "NV0001", "Nguyen Hoang Long", "0901111001", "demo123"));
        nv.themNhanVien(new NhanVien("NV0002", "NV0002", "Tran Ngoc Phuong", "0901111002", "demo123"));
        dl.themDaiLy(new DaiLy("DL0001", "Dai ly Ha Noi", "0902222001", "Cap 1", 2500000));
        dl.themDaiLy(new DaiLy("DL0002", "Dai ly Sai Gon", "0902222002", "Cap 1", 4800000));
        dl.themDaiLy(new DaiLy("DL0003", "Dai ly Da Nang", "0902222003", "Cap 2", 1250000));
    }

    /** Tạo bộ dữ liệu demo đầy đủ để các màn hình có nội dung minh họa. */
    public static void napDuLieuDemo(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) {
        napDuLieuDemo(cb, hk, nv, dl);
        HanhKhach[] hanhKhach = hk.getDanhSach();
        ChuyenBay[] chuyenBay = cb.getDanhSach();
        NhanVien[] nhanVien = nv.getDanhSach();
        int soDonMau = 6;

        for (int i = 0; i < soDonMau; i++) {
            int loaiVe = i % 2 + 1;
            double giaGoc = 1850000 + i * 325000;
            double phuPhi = loaiVe == 1 ? 180000 + i * 15000 : 420000 + i * 20000;
            double tongTien = (giaGoc + phuPhi) * (1 + VeMayBay.THUE_VAT);
            IThanhToan thanhToan = i % 3 == 1
                    ? new ThanhToanThe("DEMO-THE-" + (i + 1))
                    : new ThanhToanTienMat(tongTien);
            String tenPhuongThuc = thanhToan instanceof ThanhToanThe ? "The" : "Tien mat";

            DonDatVe donMau = don.datVeTuDong(hanhKhach[i], chuyenBay[i], loaiVe,
                    giaGoc, phuPhi, thanhToan, tenPhuongThuc);
            if (donMau == null) {
                throw new IllegalStateException("Khong tao duoc don demo: " + don.getThongBaoGanNhat());
            }
            donMau.setNhanVienLap(nhanVien[i % nhanVien.length]);
            if (!ve.themVe(donMau.getVe())) {
                don.xoaDon(donMau.getMaDonDat());
                throw new IllegalStateException("Khong them duoc ve demo " + donMau.getVe().getMaVe());
            }
        }
    }
}
