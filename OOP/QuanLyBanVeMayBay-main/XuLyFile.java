import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

class XuLyFile {
    private static final Path THU_MUC = Paths.get("data");
    private static final String HANG_CHUYEN_BAY = "status,maChuyenBay,diemDi,diemDen,ngayDi,ngayDen,tongGhe,gheTrong,giaPhoThong,phiHanhLy,giaThuongGia,phiPhongCho";
    private static final String HANG_HANH_KHACH = "status,maHanhKhach,hoTen,dienThoai,hoChieu";
    private static final String HANG_NHAN_VIEN = "status,maSo,hoTen,dienThoai,maNhanVien,matKhau";
    private static final String HANG_DAI_LY = "status,maSo,hoTen,dienThoai,loaiDaiLy,duNo";
    private static final String HANG_VE = "loai,maVe,giaGoc,phuPhi,maChuyenBay,maHanhKhach,status,duocDoiTra,tongSoVe";
    private static final String HANG_TRANG_THAI_VE = "maVe,daHuy";
    private static final String HANG_DON_DAT = "maDon,maHanhKhach,maNhanVien,maChuyenBay,maVe,phuongThuc,thongTinThanhToan,ngayDat,thoiGianDat,daHuy,daXacNhan";
    private static final String HANG_LICH_SU = "thoiGian,taiKhoan,vaiTro,hanhDong,doiTuong,maDoiTuong,ghiChu";

    // API theo so do UML
    public static boolean ghiVe(VeMayBay[] dsVe, int n) {
        if (dsVe == null || n < 0 || n > dsVe.length) return false;
        String[][] dong = new String[n][9];
        for (int i = 0; i < n; i++) {
            VeMayBay ve = dsVe[i];
            String loai = ve instanceof VePhoThong ? "PHO_THONG" : "THUONG_GIA";
            double phuPhi = ve instanceof VePhoThong ? ((VePhoThong) ve).getPhiHanhLy()
                    : ve instanceof VeThuongGia ? ((VeThuongGia) ve).getPhiPhongCho() : 0;
            dong[i] = new String[]{loai, ve.getMaVe(), Double.toString(ve.getGiaGoc()),
                    Double.toString(phuPhi), maChuyenBay(ve.getChuyenBay()), maHanhKhach(ve.getHanhKhach()),
                    Boolean.toString(ve.isStatus()), Boolean.toString(ve.isDuocDoiTra()),
                    Integer.toString(ve.getTongSoVe())};
        }
        return ghiCsv("ve.txt", HANG_VE, dong);
    }
    public static VeMayBay[] docVe() throws IOException {
        if (!laBangCsv("ve.txt", HANG_VE)) return docLegacy("ve.txt", VeMayBay.class);
        String[][] rows = docCsv("ve.txt", HANG_VE);
        VeMayBay[] ketQua = new VeMayBay[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            String ma = row[1];
            double gia = Double.parseDouble(row[2]);
            double phi = Double.parseDouble(row[3]);
            VeMayBay ve = "PHO_THONG".equals(row[0])
                    ? new VePhoThong(ma, gia, phi) : new VeThuongGia(ma, gia, phi);
            ve.setChuyenBay(stubChuyenBay(row[4]));
            ve.setHanhKhach(stubHanhKhach(row[5]));
            ve.setStatus(Boolean.parseBoolean(row[6]));
            ve.setDuocDoiTra(Boolean.parseBoolean(row[7]));
            ve.setTongSoVe(Integer.parseInt(row[8]));
            ketQua[i] = ve;
        }
        return ketQua;
    }
    public static boolean ghiDonDat(DonDatVe[] dsDonDat, int n) {
        if (dsDonDat == null || n < 0 || n > dsDonDat.length) return false;
        String[][] dong = new String[n][11];
        for (int i = 0; i < n; i++) {
            DonDatVe don = dsDonDat[i];
            String phuongThuc = "";
            String thongTin = "";
            if (don.getPhuongThuc() instanceof ThanhToanTienMat) {
                phuongThuc = "TIEN_MAT";
                thongTin = Double.toString(((ThanhToanTienMat) don.getPhuongThuc()).getTienKhachDua());
            } else if (don.getPhuongThuc() instanceof ThanhToanThe) {
                phuongThuc = "THE";
                thongTin = ((ThanhToanThe) don.getPhuongThuc()).getSoThe();
            }
            NgayThang ngayDat = don.getNgayDat();
            dong[i] = new String[]{don.getMaDonDat(), maHanhKhach(don.getHanhKhach()),
                    don.getNhanVienLap() == null ? "" : don.getNhanVienLap().getMaNhanVien(),
                    maChuyenBay(don.getChuyenBay()), don.getVe() == null ? "" : don.getVe().getMaVe(),
                    phuongThuc, thongTin, ngayDat == null ? "" : ngayDat.toString(),
                    don.getThoiGianDat() == null ? "" : don.getThoiGianDat().toString(),
                    Boolean.toString(don.isDaHuy()), Boolean.toString(don.isDaXacNhan())};
        }
        return ghiCsv("dondat.txt", HANG_DON_DAT, dong);
    }
    public static DonDatVe[] docDonDat() throws IOException {
        if (!laBangCsv("dondat.txt", HANG_DON_DAT)) return docLegacy("dondat.txt", DonDatVe.class);
        String[][] rows = docCsv("dondat.txt", HANG_DON_DAT);
        DonDatVe[] ketQua = new DonDatVe[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            HanhKhach khach = stubHanhKhach(row[1]);
            NhanVien nhanVien = stubNhanVien(row[2]);
            ChuyenBay chuyenBay = stubChuyenBay(row[3]);
            VeMayBay ve = stubVe(row[4]);
            IThanhToan phuongThuc = null;
            if ("TIEN_MAT".equals(row[5])) {
                phuongThuc = new ThanhToanTienMat(Double.parseDouble(row[6]));
            } else if ("THE".equals(row[5])) {
                phuongThuc = new ThanhToanThe(row[6].isEmpty() ? "THE-CU" : row[6]);
            }
            NgayThang ngayDat = parseNgay(row[7]);
            DonDatVe don = new DonDatVe(row[0], khach, khach, nhanVien,
                    chuyenBay, ve, phuongThuc, ngayDat);
            if (!row[8].isEmpty()) don.setThoiGianDat(LocalDateTime.parse(row[8]));
            don.setDaHuy(Boolean.parseBoolean(row[9]));
            if (Boolean.parseBoolean(row[10])) don.xacNhanDatVe();
            ketQua[i] = don;
        }
        return ketQua;
    }

    public static boolean ghiLichSu(QuanLyLichSu lichSu) {
        if (lichSu == null) return false;
        LichSuThaoTac[] ds = lichSu.getDanhSach();
        String[][] dong = new String[ds.length][7];
        for (int i = 0; i < ds.length; i++) {
            LichSuThaoTac muc = ds[i];
            dong[i] = new String[]{muc.getThoiGian().toString(), muc.getTaiKhoan(), muc.getVaiTro(),
                    muc.getHanhDong(), muc.getDoiTuong(), muc.getMaDoiTuong(), muc.getGhiChu()};
        }
        return ghiCsv("lichsu.txt", HANG_LICH_SU, dong);
    }

    public static LichSuThaoTac[] docLichSu() throws IOException {
        if (!laBangCsv("lichsu.txt", HANG_LICH_SU)) return docLegacy("lichsu.txt", LichSuThaoTac.class);
        String[][] rows = docCsv("lichsu.txt", HANG_LICH_SU);
        LichSuThaoTac[] ketQua = new LichSuThaoTac[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            ketQua[i] = new LichSuThaoTac(LocalDateTime.parse(row[0]), row[1], row[2],
                    row[3], row[4], row[5], row[6]);
        }
        return ketQua;
    }

    private static boolean ghiTrangThaiHuyVe(QuanLyVe quanLyVe) {
        VeMayBay[] dsVe = quanLyVe.getDanhSach();
        String[][] dong = new String[dsVe.length][2];
        for (int i = 0; i < dsVe.length; i++) {
            dong[i] = new String[]{dsVe[i].getMaVe(), Boolean.toString(quanLyVe.isDaHuy(dsVe[i].getMaVe()))};
        }
        return ghiCsv("ve_huy.txt", HANG_TRANG_THAI_VE, dong);
    }

    private static boolean[] docTrangThaiHuyVe(VeMayBay[] dsVe) {
        boolean[] ketQua = new boolean[dsVe.length];
        Path tep = THU_MUC.resolve("ve_huy.txt");
        if (!Files.exists(tep)) return ketQua;
        try {
            if (laBangCsv("ve_huy.txt", HANG_TRANG_THAI_VE)) {
                String[][] rows = docCsv("ve_huy.txt", HANG_TRANG_THAI_VE);
                for (int i = 0; i < dsVe.length; i++) {
                    for (String[] row : rows) {
                        if (dsVe[i].getMaVe().equalsIgnoreCase(row[0])) {
                            ketQua[i] = Boolean.parseBoolean(row[1]);
                            break;
                        }
                    }
                }
            } else {
                String[] dong = new String(Files.readAllBytes(tep), StandardCharsets.UTF_8).split("\\R");
                for (int i = 0; i < Math.min(dsVe.length, dong.length); i++) {
                    ketQua[i] = Boolean.parseBoolean(dong[i]);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Loi doc trang thai ve: " + e.getMessage(), e);
        }
        return ketQua;
    }

    // Luu/nap cac lop du lieu khac de ung dung co the mo lai trang thai.
    public static boolean ghiChuyenBay(ChuyenBay[] ds, int n) {
        if (ds == null || n < 0 || n > ds.length) return false;
        String[][] dong = new String[n][12];
        for (int i = 0; i < n; i++) {
            ChuyenBay cb = ds[i];
            dong[i] = new String[]{Boolean.toString(cb.isStatus()), cb.getSoHieuChuyenBay(),
                    cb.getDiemDi(), cb.getDiemDen(), cb.getNgayDi(), cb.getNgayDen(),
                    Integer.toString(cb.getTongSoGhe()), Integer.toString(cb.getSoGheTrong()),
                    Double.toString(cb.getGiaGocTheoHang(1)), Double.toString(cb.getPhuPhiTheoHang(1)),
                    Double.toString(cb.getGiaGocTheoHang(2)), Double.toString(cb.getPhuPhiTheoHang(2))};
        }
        return ghiCsv("chuyenbay.txt", HANG_CHUYEN_BAY, dong);
    }

    public static ChuyenBay[] docChuyenBay() throws IOException {
        if (!laBangCsv("chuyenbay.txt", HANG_CHUYEN_BAY)) return docLegacy("chuyenbay.txt", ChuyenBay.class);
        String[][] rows = docCsv("chuyenbay.txt", HANG_CHUYEN_BAY);
        ChuyenBay[] ketQua = new ChuyenBay[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            ChuyenBay cb = new ChuyenBay(Boolean.parseBoolean(row[0]), row[1], row[2], row[3],
                    row[4], row[5], Integer.parseInt(row[6]), Integer.parseInt(row[7]));
            cb.setGiaPhoThong(Double.parseDouble(row[8]), Double.parseDouble(row[9]));
            cb.setGiaThuongGia(Double.parseDouble(row[10]), Double.parseDouble(row[11]));
            ketQua[i] = cb;
        }
        return ketQua;
    }

    public static boolean ghiHanhKhach(HanhKhach[] ds, int n) {
        if (ds == null || n < 0 || n > ds.length) return false;
        String[][] dong = new String[n][5];
        for (int i = 0; i < n; i++) {
            HanhKhach hk = ds[i];
            dong[i] = new String[]{Boolean.toString(hk.isStatus()), hk.getMaSo(), hk.getHoTen(),
                    hk.getSoDienThoai(), hk.getHochieu()};
        }
        return ghiCsv("hanhkhach.txt", HANG_HANH_KHACH, dong);
    }

    public static HanhKhach[] docHanhKhach() throws IOException {
        if (!laBangCsv("hanhkhach.txt", HANG_HANH_KHACH)) return docLegacy("hanhkhach.txt", HanhKhach.class);
        String[][] rows = docCsv("hanhkhach.txt", HANG_HANH_KHACH);
        HanhKhach[] ketQua = new HanhKhach[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            ketQua[i] = new HanhKhach(Boolean.parseBoolean(row[0]), row[1], row[2], row[3], row[4]);
        }
        return ketQua;
    }

    public static boolean ghiNhanVien(NhanVien[] ds, int n) {
        if (ds == null || n < 0 || n > ds.length) return false;
        String[][] dong = new String[n][6];
        for (int i = 0; i < n; i++) {
            NhanVien nv = ds[i];
            dong[i] = new String[]{Boolean.toString(nv.isStatus()), nv.getMaSo(), nv.getHoTen(),
                    nv.getSoDienThoai(), nv.getMaNhanVien(), nv.getMK()};
        }
        return ghiCsv("nhanvien.txt", HANG_NHAN_VIEN, dong);
    }

    public static NhanVien[] docNhanVien() throws IOException {
        if (!laBangCsv("nhanvien.txt", HANG_NHAN_VIEN)) return docLegacy("nhanvien.txt", NhanVien.class);
        String[][] rows = docCsv("nhanvien.txt", HANG_NHAN_VIEN);
        NhanVien[] ketQua = new NhanVien[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            ketQua[i] = new NhanVien(Boolean.parseBoolean(row[0]), row[1], row[2], row[3], row[4], row[5]);
        }
        return ketQua;
    }

    public static boolean ghiDaiLy(DaiLy[] ds, int n) {
        if (ds == null || n < 0 || n > ds.length) return false;
        String[][] dong = new String[n][6];
        for (int i = 0; i < n; i++) {
            DaiLy dl = ds[i];
            dong[i] = new String[]{Boolean.toString(dl.isStatus()), dl.getMaSo(), dl.getHoTen(),
                    dl.getSoDienThoai(), dl.getLoaiDaiLy(), dl.getDuNo() == null ? "" : dl.getDuNo().toString()};
        }
        return ghiCsv("daily.txt", HANG_DAI_LY, dong);
    }

    public static DaiLy[] docDaiLy() throws IOException {
        if (!laBangCsv("daily.txt", HANG_DAI_LY)) return docLegacy("daily.txt", DaiLy.class);
        String[][] rows = docCsv("daily.txt", HANG_DAI_LY);
        DaiLy[] ketQua = new DaiLy[rows.length];
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            double duNo = row[5].isEmpty() ? 0 : Double.parseDouble(row[5]);
            ketQua[i] = new DaiLy(Boolean.parseBoolean(row[0]), row[1], row[2], row[3], row[4], duNo);
        }
        return ketQua;
    }

    private static boolean ghiCsv(String tenTep, String tieuDe, String[][] cacDong) {
        if (cacDong == null) return false;
        Path tepTam = THU_MUC.resolve(tenTep + ".tmp");
        try {
            Files.createDirectories(THU_MUC);
            try (java.io.BufferedWriter writer = Files.newBufferedWriter(tepTam, StandardCharsets.UTF_8)) {
                writer.write(tieuDe);
                writer.newLine();
                for (String[] dong : cacDong) {
                    if (dong == null) throw new IOException("Dong CSV rong trong " + tenTep);
                    for (int i = 0; i < dong.length; i++) {
                        if (i > 0) writer.write(',');
                        writer.write(maHoaCsv(dong[i]));
                    }
                    writer.newLine();
                }
            }
            try {
                Files.move(tepTam, THU_MUC.resolve(tenTep), StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tepTam, THU_MUC.resolve(tenTep), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException e) {
            try { Files.deleteIfExists(tepTam); } catch (IOException ignored) { }
            System.err.println("Loi ghi " + tenTep + ": " + e.getMessage());
            return false;
        }
    }

    private static String maHoaCsv(String giaTri) {
        String text = giaTri == null ? "" : giaTri;
        if (text.indexOf(',') < 0 && text.indexOf('"') < 0
                && text.indexOf('\n') < 0 && text.indexOf('\r') < 0) return text;
        return '"' + text.replace("\"", "\"\"") + '"';
    }

    private static String[][] docCsv(String tenTep, String tieuDe) throws IOException {
        Path tep = THU_MUC.resolve(tenTep);
        if (!Files.exists(tep)) throw new IOException("Khong tim thay " + tep);
        String noiDung = new String(Files.readAllBytes(tep), StandardCharsets.UTF_8);
        String[][] dong = new String[16][];
        int soDong = 0;
        String[] truong = new String[16];
        int soTruong = 0;
        StringBuilder giaTri = new StringBuilder();
        boolean trongNgoac = false;
        boolean daDongNgoac = false;
        boolean daBatDauDong = false;
        for (int i = 0; i < noiDung.length(); i++) {
            char c = noiDung.charAt(i);
            if (trongNgoac) {
                if (c == '"') {
                    if (i + 1 < noiDung.length() && noiDung.charAt(i + 1) == '"') {
                        giaTri.append('"');
                        i++;
                    } else {
                        trongNgoac = false;
                        daDongNgoac = true;
                    }
                } else {
                    giaTri.append(c);
                }
            } else if (c == ',' || c == '\n' || c == '\r') {
                if (daDongNgoac && c != ',' && c != '\n' && c != '\r') {
                    throw new IOException("CSV co ky tu sau dau ngoac kep khong hop le.");
                }
                if (c == ',') {
                    truong = themTruong(truong, soTruong++, giaTri.toString());
                    giaTri.setLength(0);
                    daDongNgoac = false;
                    daBatDauDong = true;
                } else {
                    truong = themTruong(truong, soTruong++, giaTri.toString());
                    giaTri.setLength(0);
                    if (soTruong != tieuDe.split(",", -1).length) {
                        throw new IOException("Sai so cot trong " + tenTep + " tai dong " + (soDong + 1));
                    }
                    if (soDong == 0 && noiDung.startsWith("\uFEFF")) {
                        truong[0] = truong[0].replace("\uFEFF", "");
                    }
                    if (soDong == 0) {
                        if (!Arrays.equals(Arrays.copyOf(truong, soTruong), tieuDe.split(",", -1))) {
                            throw new IOException("Tieu de cot khong dung trong " + tenTep);
                        }
                    } else {
                        if (soDong - 1 == dong.length) dong = Arrays.copyOf(dong, dong.length * 2);
                        dong[soDong - 1] = Arrays.copyOf(truong, soTruong);
                    }
                    soDong++;
                    soTruong = 0;
                    truong = new String[16];
                    daDongNgoac = false;
                    daBatDauDong = false;
                    if (c == '\r' && i + 1 < noiDung.length() && noiDung.charAt(i + 1) == '\n') i++;
                }
            } else if (c == '"') {
                if (giaTri.length() != 0 || daDongNgoac) throw new IOException("Dau ngoac kep sai trong " + tenTep);
                trongNgoac = true;
                daBatDauDong = true;
            } else {
                if (daDongNgoac) throw new IOException("CSV co ky tu sau dau ngoac kep khong hop le.");
                giaTri.append(c);
                daBatDauDong = true;
            }
        }
        if (trongNgoac) throw new IOException("CSV chua dong dau ngoac kep trong " + tenTep);
        if (daBatDauDong || soTruong > 0 || giaTri.length() > 0) {
            truong = themTruong(truong, soTruong++, giaTri.toString());
            if (soTruong != tieuDe.split(",", -1).length) {
                throw new IOException("Sai so cot trong " + tenTep + " tai dong " + (soDong + 1));
            }
            if (soDong == 0) {
                if (!Arrays.equals(Arrays.copyOf(truong, soTruong), tieuDe.split(",", -1))) {
                    throw new IOException("Tieu de cot khong dung trong " + tenTep);
                }
            } else {
                if (soDong - 1 == dong.length) dong = Arrays.copyOf(dong, dong.length * 2);
                dong[soDong - 1] = Arrays.copyOf(truong, soTruong);
                soDong++;
            }
        }
        if (soDong == 0) throw new IOException("Tep CSV rong: " + tenTep);
        return Arrays.copyOf(dong, soDong - 1);
    }

    private static String[] themTruong(String[] truong, int viTri, String giaTri) {
        if (viTri == truong.length) truong = Arrays.copyOf(truong, truong.length * 2);
        truong[viTri] = giaTri;
        return truong;
    }

    private static <T> T[] docLegacy(String tenTep, Class<T> loai) throws IOException {
        Path tep = THU_MUC.resolve(tenTep);
        if (!Files.exists(tep)) return mangRong(loai);
        String noiDung = new String(Files.readAllBytes(tep), StandardCharsets.UTF_8).trim();
        if (noiDung.isEmpty()) return mangRong(loai);
        String[] cacDong = noiDung.split("\\R");
        T[] ketQua = mang(loai, cacDong.length);
        try {
            for (int i = 0; i < cacDong.length; i++) {
                Object obj = giaiMa(cacDong[i].trim());
                if (!loai.isInstance(obj)) throw new IOException("Sai kieu du lieu tai dong " + (i + 1));
                chuanHoaTenHienThi(obj);
                ketQua[i] = loai.cast(obj);
            }
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            throw new IOException("Khong doc duoc du lieu cu " + tenTep + ": " + e.getMessage(), e);
        }
        return ketQua;
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

    private static String maChuyenBay(ChuyenBay chuyenBay) {
        return chuyenBay == null ? "" : chuyenBay.getSoHieuChuyenBay();
    }

    private static String maHanhKhach(HanhKhach hanhKhach) {
        return hanhKhach == null ? "" : hanhKhach.getMaHanhKhach();
    }

    private static ChuyenBay stubChuyenBay(String ma) {
        return ma == null || ma.isEmpty() ? null
                : new ChuyenBay(true, ma, "", "", "", "", 0, 0);
    }

    private static HanhKhach stubHanhKhach(String ma) {
        return ma == null || ma.isEmpty() ? null : new HanhKhach(true, ma, "", "", "");
    }

    private static NhanVien stubNhanVien(String ma) {
        return ma == null || ma.isEmpty() ? null : new NhanVien(true, ma, "", "", ma, "");
    }

    private static VeMayBay stubVe(String ma) {
        return ma == null || ma.isEmpty() ? null : new VePhoThong(ma, 0, 0);
    }

    private static NgayThang parseNgay(String text) throws IOException {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            String[] phan = text.trim().split("/");
            if (phan.length == 3) {
                return new NgayThang(Integer.parseInt(phan[0]), Integer.parseInt(phan[1]),
                        Integer.parseInt(phan[2]));
            }
            LocalDate ngay = LocalDate.parse(text.trim());
            return new NgayThang(ngay);
        } catch (RuntimeException e) {
            throw new IOException("Ngay dat khong hop le: " + text, e);
        }
    }

    private static boolean laBangCsv(String tenTep, String tieuDe) {
        Path tep = THU_MUC.resolve(tenTep);
        if (!Files.exists(tep)) return false;
        try (java.io.BufferedReader reader = Files.newBufferedReader(tep, StandardCharsets.UTF_8)) {
            String dongDau = reader.readLine();
            return dongDau != null && dongDau.replace("\uFEFF", "").equals(tieuDe);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean coDuLieuCuCanChuyenDoi() {
        String[][] cacTep = {
            {"chuyenbay.txt", HANG_CHUYEN_BAY}, {"hanhkhach.txt", HANG_HANH_KHACH},
            {"nhanvien.txt", HANG_NHAN_VIEN}, {"daily.txt", HANG_DAI_LY},
            {"ve.txt", HANG_VE}, {"ve_huy.txt", HANG_TRANG_THAI_VE},
            {"dondat.txt", HANG_DON_DAT}, {"lichsu.txt", HANG_LICH_SU}
        };
        boolean coTep = false;
        boolean canChuyenDoi = false;
        for (String[] tep : cacTep) {
            coTep |= Files.exists(THU_MUC.resolve(tep[0]));
            canChuyenDoi |= !laBangCsv(tep[0], tep[1]);
        }
        return coTep && canChuyenDoi;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] mang(Class<T> loai, int n) {
        return (T[]) java.lang.reflect.Array.newInstance(loai, n);
    }
    private static <T> T[] mangRong(Class<T> loai) { return mang(loai, 0); }

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
        boolean canChuyenDoiLichSu = Files.exists(THU_MUC.resolve("lichsu.txt"))
                && !laBangCsv("lichsu.txt", HANG_LICH_SU);
        boolean daNapDuLieu = napDuLieu(cb, hk, nv, dl, ve, don);
        if (lichSu != null) lichSu.nap(docLichSu());
        if (canChuyenDoiLichSu && lichSu != null && !ghiLichSu(lichSu)) {
            throw new IOException("Khong chuyen doi duoc tep lichsu.txt sang bang du lieu.");
        }
        return daNapDuLieu;
    }

    public static boolean napDuLieu(QuanLyChuyenBay cb, QuanLyHanhKhach hk,
            QuanLyNhanVien nv, QuanLyDaiLy dl, QuanLyVe ve, QuanLyDonDatVe don) throws IOException {
        if (!Files.exists(THU_MUC)) return false;
        boolean canChuyenDoi = coDuLieuCuCanChuyenDoi();

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

        boolean[] trangThaiVe = docTrangThaiHuyVe(dsVe);
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
            ChuyenBay flight = item.getChuyenBay() == null
                    ? (ticket == null ? null : ticket.getChuyenBay())
                    : cb.timChuyenBayTheoMa(item.getChuyenBay().getSoHieuChuyenBay());
            if (ticket == null || passenger == null) throw new IOException("Don dat co lien ket khong hop le: " + item.getMaDonDat());
            if (flight == null) throw new IOException("Don dat thieu chuyen bay: " + item.getMaDonDat());
            item.setVe(ticket);
            item.setHanhKhach(passenger);
            item.setNguoiDat(passenger);
            item.setNhanVienLap(employee);
            item.setChuyenBay(flight);
            int soDonCu = don.getSoLuong();
            don.themDon(item);
            if (don.getSoLuong() == soDonCu) throw new IOException("Khong nap duoc don: " + item.getMaDonDat());
            if (item.isDaHuy()) ve.capNhatTrangThaiHuy(ticket.getMaVe(), true);
        }
        if (canChuyenDoi) luuTatCa(cb, hk, nv, dl, ve, don);
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
