import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public final class Main {
   public static void main(String[] args) {
      new Main().chay();
   }

   private static final TaiKhoan[] TAI_KHOAN_MAU;
   private final Scanner input;
   private final QuanLyChuyenBay chuyenBay;
   private final QuanLyHanhKhach hanhKhach;
   private final QuanLyDonDatVe donDatVe;
   private final QuanLyVe ve;
   private final QuanLyNhanVien nhanVien;
   private final QuanLyDaiLy daiLy;
   private final QuanLyLichSu lichSu;
   private TaiKhoan taiKhoan;

   public Main() {
      this.input = new Scanner(System.in, "UTF-8");
      this.chuyenBay = new QuanLyChuyenBay();
      this.hanhKhach = new QuanLyHanhKhach();
      this.donDatVe = new QuanLyDonDatVe();
      this.ve = new QuanLyVe();
      this.nhanVien = new QuanLyNhanVien();
      this.daiLy = new QuanLyDaiLy();
      this.lichSu = new QuanLyLichSu();
   }

   public void chay() {
      System.out.println("==============================================");
      System.out.println("       SKYDESK - QUAN LY VE MAY BAY");
      System.out.println("               Ban terminal");
      System.out.println("==============================================");
      this.napDuLieu();

      try {
         if (this.dangNhap()) {
            while(true) {
               this.inMenu();
               int var1 = this.nhapSoNguyen("Chon chuc nang: ", 0, 9);
               switch (var1) {
                  case 0:
                     this.luuDuLieu();
                     System.out.println("Da luu du lieu. Tam biet!");
                     return;
                  case 1:
                     this.xemChuyenBay();
                     break;
                  case 2:
                     if (this.coQuyenDatVe()) {
                        this.datVe();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 3:
                     if (this.coQuyenXuLyDon()) {
                        this.xemVaXuLyDon();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 4:
                     if (this.taiKhoan.getVaiTro().duocSuaHanhKhach()) {
                        this.quanLyHanhKhach();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 5:
                     if (this.coQuyenXuLyDon()) {
                        this.xemVe();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 6:
                     if (this.taiKhoan.getVaiTro().duocQuanLyChuyenBay()) {
                        this.quanLyChuyenBay();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 7:
                     if (this.taiKhoan.getVaiTro().coToanQuyen()) {
                        this.quanLyNhanSuVaDaiLy();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 8:
                     if (this.taiKhoan.getVaiTro().coToanQuyen()) {
                        this.baoCaoDoanhThu();
                     } else {
                        this.baoKhongDuQuyen();
                     }
                     break;
                  case 9:
                     if (this.taiKhoan.getVaiTro().duocMoTrang("history")) {
                        this.xemLichSu();
                     } else {
                        this.baoKhongDuQuyen();
                     }
               }
            }
         }
      } catch (HetDuLieuNhap e) {
         this.luuDuLieu();
         System.out.println("\nDa ket thuc phien terminal.");
      }
   }

   private boolean dangNhap() {
      System.out.println("\nTai khoan mau: client, staff, warehouse, manager, admin");
      System.out.println("Mat khau mau: 123456");

      for(int var1 = 1; var1 <= 3; ++var1) {
         String var2 = this.nhapChuoi("Ten dang nhap (go 0 de thoat): ");
         if ("0".equals(var2)) {
            return false;
         }

         String var3 = this.nhapChuoi("Mat khau: ");
         char[] var4 = var3.toCharArray();
         TaiKhoan var5 = null;

         for(TaiKhoan var9 : TAI_KHOAN_MAU) {
            if (var9.khopThongTin(var2, var4)) {
               var5 = var9;
               break;
            }
         }

         Arrays.fill(var4, '\u0000');
         if (var5 != null) {
            this.taiKhoan = var5;
            PrintStream var10000 = System.out;
            String var10001 = this.taiKhoan.getTenDangNhap();
            var10000.println("\nXin chao " + var10001 + " - " + this.taiKhoan.getVaiTro().getTenHienThi() + ".");
            return true;
         }

         System.out.println("Sai ten dang nhap hoac mat khau. Con " + (3 - var1) + " lan thu.");
      }

      System.out.println("Ban da nhap sai qua so lan cho phep.");
      return false;
   }

   private void inMenu() {
      System.out.println("\n---------------- MENU ----------------");
      System.out.println("Vai tro: " + this.taiKhoan.getVaiTro().getTenHienThi()
              + " | Quyen: " + this.taiKhoan.getVaiTro().getMoTaQuyen());
      System.out.println("1. Tra cuu chuyen bay");
      if (this.coQuyenDatVe()) {
         System.out.println("2. Dat ve");
      }

      if (this.coQuyenXuLyDon()) {
         System.out.println("3. Danh sach va xu ly don dat ve");
      }

      if (this.taiKhoan.getVaiTro().duocSuaHanhKhach()) {
         System.out.println("4. Quan ly hanh khach");
      }

      if (this.coQuyenXuLyDon()) {
         System.out.println("5. Danh sach ve");
      }

      if (this.taiKhoan.getVaiTro().duocQuanLyChuyenBay()) {
         System.out.println("6. Quan ly chuyen bay");
      }

      if (this.taiKhoan.getVaiTro().coToanQuyen()) {
         System.out.println("7. Quan ly nhan vien va dai ly");
      }

      if (this.taiKhoan.getVaiTro().coToanQuyen()) {
         System.out.println("8. Bao cao doanh thu");
      }

      if (this.taiKhoan.getVaiTro().duocMoTrang("history")) {
         System.out.println("9. Nhat ky thao tac");
      }

      System.out.println("0. Luu va thoat");
      System.out.println("--------------------------------------");
   }

   private boolean coQuyenDatVe() {
      VaiTro var1 = this.taiKhoan.getVaiTro();
      return var1 == VaiTro.CLIENT || var1 == VaiTro.STAFF || var1.coToanQuyen();
   }

   private boolean coQuyenXuLyDon() {
      VaiTro var1 = this.taiKhoan.getVaiTro();
      return var1 == VaiTro.STAFF || var1.coToanQuyen();
   }

   private void baoKhongDuQuyen() {
      System.out.println("Tai khoan nay khong co quyen dung chuc nang do.");
   }

   private void napDuLieu() {
      try {
         XuLyFile.napDuLieu(this.chuyenBay, this.hanhKhach, this.nhanVien, this.daiLy, this.ve, this.donDatVe, this.lichSu);
         if (this.tatCaDanhSachRong()) {
            XuLyFile.napDuLieuDemo(this.chuyenBay, this.hanhKhach, this.nhanVien, this.daiLy, this.ve, this.donDatVe);
            this.luuDuLieu();
            System.out.println("Da nap du lieu minh hoa lan dau.");
         } else {
            System.out.printf("Da nap %d chuyen bay, %d don dat ve.%n", this.chuyenBay.getSoLuong(), this.donDatVe.getSoLuong());
         }
      } catch (RuntimeException | IOException var2) {
         System.out.println("Khong nap duoc du lieu: " + ((Exception)var2).getMessage());
      }

   }

   private boolean tatCaDanhSachRong() {
      return this.chuyenBay.getSoLuong() == 0 && this.hanhKhach.getSoLuong() == 0 && this.donDatVe.getSoLuong() == 0 && this.ve.getSoLuong() == 0 && this.nhanVien.getSoLuong() == 0 && this.daiLy.getSoLuong() == 0;
   }

   private boolean luuDuLieu() {
      try {
         XuLyFile.luuTatCa(this.chuyenBay, this.hanhKhach, this.nhanVien, this.daiLy, this.ve, this.donDatVe, this.lichSu);
         return true;
      } catch (IOException var2) {
         System.out.println("Khong luu duoc du lieu: " + var2.getMessage());
         return false;
      }
   }

   private void ghiLichSu(String var1, String var2, String var3, String var4) {
      this.lichSu.ghi(new LichSuThaoTac(this.taiKhoan.getTenDangNhap(), this.taiKhoan.getVaiTro().getTenHienThi(), var1, var2, var3, var4));
   }

   private void xemChuyenBay() {
      while (true) {
         String var1 = this.nhapChuoi("Tu khoa (ma chuyen, diem di/den; Enter xem tat ca, 0 ve menu): ");
         if ("0".equals(var1)) {
            return;
         }

         boolean var2 = this.taiKhoan.getVaiTro().coToanQuyen()
                 && this.xacNhan("Hien ca chuyen da ngung khai thac? (y/N): ");
         this.inDanhSachChuyenBay(var1, var2);
      }
   }

   private void inDanhSachChuyenBay(String var1, boolean var2) {
      String tuKhoa = var1 == null ? "" : var1.trim().toLowerCase(Locale.ROOT);
      ChuyenBay[] tam = new ChuyenBay[this.chuyenBay.getSoLuong()];
      int dem = 0;
      for (ChuyenBay flight : this.chuyenBay.getDanhSach()) {
         boolean khop = tuKhoa.isEmpty() || this.chuaKhoa(flight.getSoHieuChuyenBay(), tuKhoa)
                 || this.chuaKhoa(flight.getDiemDi(), tuKhoa) || this.chuaKhoa(flight.getDiemDen(), tuKhoa)
                 || this.chuaKhoa(flight.getNgayDi(), tuKhoa) || this.chuaKhoa(flight.getNgayDen(), tuKhoa);
         if (khop && (var2 || flight.isStatus())) tam[dem++] = flight;
      }
      ChuyenBay[] ketQua = Arrays.copyOf(tam, dem);
      this.xuatTheoTrang("MA CHUYEN | DIEM DI -> DIEM DEN | NGAY DI | GHE TRONG/TONG | TRANG THAI",
              ketQua, flight -> {
                 System.out.printf("%-10s | %s -> %s | %s | %d/%d | %s%n",
                         flight.getSoHieuChuyenBay(), flight.getDiemDi(), flight.getDiemDen(),
                         flight.getNgayDi(), flight.getSoGheTrong(), flight.getTongSoGhe(),
                         flight.isStatus() ? "Dang khai thac" : "Da ngung");
                 System.out.printf("    TONG GIA (DA GOM VAT) | Pho thong: %s | Thuong gia: %s%n",
                         this.giaHienThi(flight, 1), this.giaHienThi(flight, 2));
              });
   }

   private String giaHienThi(ChuyenBay chuyenBay, int loaiVe) {
      return String.format(Locale.ROOT, "%,.0f VND", chuyenBay.tinhGiaVeTheoHang(loaiVe));
   }

   private boolean chuaKhoa(String var1, String var2) {
      return var1 != null && var1.toLowerCase(Locale.ROOT).contains(var2);
   }

   private void datVe() {
      this.inDanhSachChuyenBay("", false);
      String maChuyenBay = this.nhapBatBuoc("Ma chuyen bay: ");
      ChuyenBay chuyenBay = this.chuyenBay.timChuyenBayTheoMa(maChuyenBay);
      if (chuyenBay == null || !chuyenBay.isStatus()) {
         System.out.println("Khong tim thay chuyen bay dang khai thac.");
         return;
      }
      if (!chuyenBay.conGheTrong()) {
         System.out.println("Chuyen bay da het ghe.");
         return;
      }

      int loaiVe = this.nhapSoNguyen("Loai ve (1. Pho thong, 2. Thuong gia): ", 1, 2);
      double giaGoc = chuyenBay.getGiaGocTheoHang(loaiVe);
      double phuPhi = chuyenBay.getPhuPhiTheoHang(loaiVe);
      double tongTien = chuyenBay.tinhGiaVeTheoHang(loaiVe);
      double tienVAT = tongTien - giaGoc - phuPhi;
      System.out.printf("Gia goc: %,.0f VND%n", giaGoc);
      System.out.printf("Phu phi: %,.0f VND%n", phuPhi);
      System.out.printf("VAT (%.0f%%): %,.0f VND%n", VeMayBay.THUE_VAT * 100, tienVAT);
      System.out.printf("TONG THANH TOAN: %,.0f VND%n", tongTien);
      if (!this.xacNhan("Tiep tuc dat ve voi gia nay? (y/N): ")) return;

      String maHanhKhach = this.nhapChuoi("Ma hanh khach (Enter de tao ho so moi): ");
      HanhKhach hanhKhach = maHanhKhach.isEmpty() ? null : this.hanhKhach.timHanhKhachTheoMa(maHanhKhach);
      boolean taoHoSoMoi = hanhKhach == null;
      if (!taoHoSoMoi && !hanhKhach.isStatus()) {
         System.out.println("Ho so hanh khach da ngung hoat dong.");
         return;
      }
      if (taoHoSoMoi) {
         maHanhKhach = this.nextCode("HK", this.maHanhKhachHienCo());
         System.out.println("Ma hanh khach moi: " + maHanhKhach);
         hanhKhach = new HanhKhach(maHanhKhach, this.nhapBatBuoc("Ho ten: "),
                 this.nhapBatBuoc("So dien thoai: "), this.nhapBatBuoc("So ho chieu: "));
      }

      int phuongThuc = this.nhapSoNguyen("Thanh toan (1. Tien mat, 2. The): ", 1, 2);
      IThanhToan thanhToan;
      String tenPhuongThuc;
      if (phuongThuc == 1) {
         thanhToan = new ThanhToanTienMat(this.nhapSoThuc("So tien khach dua (VND): ", 0));
         tenPhuongThuc = "Tien mat";
      } else {
         thanhToan = new ThanhToanThe(this.nhapBatBuoc("So the: "));
         tenPhuongThuc = "The";
      }

      DonDatVe don = this.donDatVe.datVeTuDong(hanhKhach, chuyenBay, loaiVe,
              giaGoc, phuPhi, thanhToan, tenPhuongThuc);
      if (don == null) {
         System.out.println(this.donDatVe.getThongBaoGanNhat());
         return;
      }
      if (this.taiKhoan.getVaiTro() == VaiTro.STAFF) {
         don.setNhanVienLap(this.timNhanVienDangNhap());
      }

      if (taoHoSoMoi && !this.hanhKhach.themHanhKhach(hanhKhach)) {
         this.donDatVe.xoaDon(don.getMaDonDat());
         System.out.println("Khong luu duoc ho so hanh khach; don da duoc hoan tac.");
      } else if (!this.ve.themVe(don.getVe())) {
         this.donDatVe.xoaDon(don.getMaDonDat());
         if (taoHoSoMoi) this.hanhKhach.xoaHanhKhachTheoMa(hanhKhach.getMaSo(), this.ve);
         System.out.println("Khong luu duoc ve; don da duoc hoan tac.");
      } else {
         this.ghiLichSu("Dat ve", "Don dat ve", don.getMaDonDat(), "Ve " + don.getVe().getMaVe());
         this.luuDuLieu();
         System.out.println("Dat ve thanh cong: don " + don.getMaDonDat() + ", ve " + don.getVe().getMaVe() + ".");
         System.out.printf("Tong da thanh toan: %,.0f VND%n", don.getVe().tinhGiaVe());
      }
   }

   private NhanVien timNhanVienDangNhap() {
      String var1 = this.taiKhoan.getMaNhanVien();
      if (var1 == null) {
         return null;
      } else {
         for(NhanVien var5 : this.nhanVien.getDanhSach()) {
            if (var5.getMaNhanVien().equalsIgnoreCase(var1) && var5.isStatus()) {
               return var5;
            }
         }

         return null;
      }
   }

   private void xemVaXuLyDon() {
      while (true) {
         System.out.println("\n===== DANH SACH DON DAT VE =====");
         this.xuatTheoTrang("DANH SACH DON DAT VE", this.donDatVe.getDanhSach(), DonDatVe::hienThiThongTin);
         System.out.println("1. Huy don    2. Sua thong tin hanh khach    3. Tim don    0. Quay lai");
         int thaoTac = this.nhapSoNguyen("Chon thao tac: ", 0, 3);
         if (thaoTac == 0) {
            if (this.xacNhan("Dong y quay lai menu chinh? (y/N): ")) return;
            continue;
         }

         if (thaoTac == 3) {
            String tuKhoa = this.nhapBatBuoc("Tim theo ma don, ma ve, chuyen bay hoac hanh khach: ");
            this.xuatTheoTrang("KET QUA TIM DON", this.donDatVe.timDonGanDung(tuKhoa), DonDatVe::hienThiThongTin);
         } else {
            String maDon = this.nhapBatBuoc("Ma don: ");
            DonDatVe don = this.donDatVe.timDonTheoMa(maDon);
            if (don == null) {
               System.out.println("Khong tim thay don.");
            } else if (thaoTac == 1) {
               if (this.xacNhan("Xac nhan huy don " + don.getMaDonDat() + "? (y/N): ")) {
                  if (this.huyDonDatVe(don)) {
                     System.out.println("Da huy don " + don.getMaDonDat() + ".");
                  } else {
                     System.out.println("Khong the huy don nay.");
                  }
               }
            } else {
               HanhKhach hanhKhach = don.getHanhKhach();
               if (hanhKhach == null || don.isDaHuy()) {
                  System.out.println("Khong the sua thong tin cua don nay.");
               } else {
                  String hoTen = this.nhapChuoi("Ho ten [" + hanhKhach.getHoTen() + "]: ");
                  String dienThoai = this.nhapChuoi("So dien thoai [" + hanhKhach.getSoDienThoai() + "]: ");
                  String hoChieu = this.nhapChuoi("So ho chieu [" + hanhKhach.getHochieu() + "]: ");
                  if (hoTen.isEmpty() && dienThoai.isEmpty() && hoChieu.isEmpty()) {
                     System.out.println("Khong co thong tin nao thay doi.");
                  } else {
                     if (!hoTen.isEmpty()) hanhKhach.setHoTen(hoTen);
                     if (!dienThoai.isEmpty()) hanhKhach.setSoDienThoai(dienThoai);
                     if (!hoChieu.isEmpty()) hanhKhach.setHochieu(hoChieu);
                     this.ghiLichSu("Sua hanh khach", "Don dat ve", don.getMaDonDat(),
                             "Da cap nhat thong tin hanh khach " + hanhKhach.getMaSo());
                     this.luuDuLieu();
                     System.out.println("Da cap nhat thong tin hanh khach.");
                  }
               }
            }
         }

         if (this.xacNhan("Dong y quay lai menu chinh? (y/N): ")) return;
      }
   }

   private void quanLyHanhKhach() {
      System.out.println("\n===== HANH KHACH =====");
      this.xuatTheoTrang("DANH SACH HANH KHACH", this.hanhKhach.getDanhSach(), HanhKhach::hienThiThongTin);
      System.out.println("1. Them 1/nhieu  2. Sua  3. Ngung hoat dong  4. Khoi phuc  5. Tim  0. Quay lai");
      int thaoTac = this.nhapSoNguyen("Chon thao tac: ", 0, 5);
      if (thaoTac == 0) return;
      if (thaoTac == 1) {
         this.themNhieuHanhKhach();
         return;
      }
      if (thaoTac == 5) {
         String tuKhoa = this.nhapBatBuoc("Tim theo ma, ten, dien thoai hoac ho chieu: ");
         this.xuatTheoTrang("KET QUA TIM HANH KHACH", this.hanhKhach.timHanhKhachGanDung(tuKhoa),
                 HanhKhach::hienThiThongTin);
         return;
      }

      String ma = this.nhapBatBuoc("Ma hanh khach: ");
      HanhKhach hanhKhach = this.hanhKhach.timHanhKhachTheoMa(ma);
      if (hanhKhach == null) {
         System.out.println("Khong tim thay hanh khach.");
         return;
      }

      boolean thanhCong;
      if (thaoTac == 2) {
         thanhCong = this.hanhKhach.suaThongTinHanhKhach(ma,
                 this.nhapBatBuoc("Ho ten moi: "), this.nhapBatBuoc("So dien thoai moi: "),
                 this.nhapBatBuoc("So ho chieu moi: "));
      } else if (thaoTac == 3) {
         thanhCong = this.hanhKhach.xoaHanhKhachTheoMa(ma, this.ve);
      } else {
         thanhCong = this.hanhKhach.khoiPhucHanhKhach(ma);
      }
      if (!thanhCong) {
         System.out.println("Khong the cap nhat ho so nay.");
         return;
      }
      String ghiChu = thaoTac == 2 ? "Da cap nhat ho ten, so dien thoai, ho chieu" : "";
      this.ghiLichSu(thaoTac == 2 ? "Sua" : (thaoTac == 3 ? "Ngung hoat dong" : "Khoi phuc"),
              "Hanh khach", ma, ghiChu);
      this.luuDuLieu();
      System.out.println("Da cap nhat danh sach hanh khach.");
   }

   private void themNhieuHanhKhach() {
      int soLuong = this.nhapSoNguyen("So ho so can them (1-1000): ", 1, 1000);
      HanhKhach[] moi = new HanhKhach[soLuong];
      for (int i = 0; i < soLuong; i++) {
         System.out.println("Nhap hanh khach " + (i + 1) + "/" + soLuong + ":");
         moi[i] = new HanhKhach(this.nhapBatBuoc("Ma hanh khach: "),
                 this.nhapBatBuoc("Ho ten: "), this.nhapBatBuoc("So dien thoai: "),
                 this.nhapBatBuoc("So ho chieu: "));
      }
      boolean[] ketQua = this.hanhKhach.themNhieuHanhKhach(moi);
      int daThem = 0;
      for (int i = 0; i < ketQua.length; i++) {
         if (ketQua[i]) {
            daThem++;
            this.ghiLichSu("Them", "Hanh khach", moi[i].getMaSo(), moi[i].getHoTen());
         } else {
            System.out.println("Bo qua ma hanh khach bi trung: " + moi[i].getMaSo());
         }
      }
      if (daThem > 0) this.luuDuLieu();
      System.out.printf("Da them %d/%d ho so hanh khach.%n", daThem, soLuong);
   }

   private void xemVe() {
      while (true) {
         System.out.println("\n===== DANH SACH VE =====");
         this.xuatTheoTrang("DANH SACH VE | MA VE | LOAI | MA CHUYEN | TONG TIEN | TRANG THAI",
                 this.ve.getDanhSach(), this::inDongVe);
         System.out.println("1. Tim ve  2. Huy ve  0. Quay lai");
         int thaoTac = this.nhapSoNguyen("Chon thao tac: ", 0, 2);
         if (thaoTac == 0) {
            if (this.xacNhan("Dong y quay lai menu chinh? (y/N): ")) return;
            continue;
         }
         if (thaoTac == 1) {
            String tuKhoa = this.nhapBatBuoc("Nhap ma ve, ma chuyen bay hoac ten/ma hanh khach: ");
            this.xuatTheoTrang("KET QUA TIM VE", this.ve.timVeGanDung(tuKhoa), this::inDongVe);
         } else {
            this.huyVeTrongDanhSach();
         }
         if (this.xacNhan("Dong y ve menu chinh? (y/N): ")) return;
      }
   }

   private void huyVeTrongDanhSach() {
      String maVe = this.nhapBatBuoc("Ma ve can huy: ");
      VeMayBay veCanHuy = this.ve.timVeTheoMa(maVe);
      if (veCanHuy == null) {
         System.out.println("Khong tim thay ma ve.");
         return;
      }
      if (this.ve.isDaHuy(maVe) || !veCanHuy.isStatus()) {
         System.out.println("Ve nay da huy.");
         return;
      }

      DonDatVe don = this.timDonTheoVe(maVe);
      if (!this.xacNhan("Xac nhan huy ve " + veCanHuy.getMaVe() + "? (y/N): ")) return;
      if (don != null) {
         if (!this.huyDonDatVe(don)) {
            System.out.println("Khong the huy ve nay.");
            return;
         }
      } else {
         ChuyenBay chuyenBay = veCanHuy.getChuyenBay();
         if (chuyenBay == null || !chuyenBay.HuyGhe()) {
            System.out.println("Khong the hoan ghe cho ve nay.");
            return;
         }
         if (!this.ve.capNhatTrangThaiHuy(maVe, true)) {
            chuyenBay.DatGhe();
            System.out.println("Khong cap nhat duoc trang thai ve.");
            return;
         }
         this.ghiLichSu("Huy ve", "Ve may bay", maVe, "");
         this.luuDuLieu();
      }
      System.out.println("Da huy ve " + veCanHuy.getMaVe() + ".");
   }

   private DonDatVe timDonTheoVe(String maVe) {
      for (DonDatVe don : this.donDatVe.getDanhSach()) {
         if (don.getVe() != null && don.getVe().getMaVe() != null
                 && don.getVe().getMaVe().equalsIgnoreCase(maVe)) {
            return don;
         }
      }
      return null;
   }

   private boolean huyDonDatVe(DonDatVe don) {
      if (don == null || !this.donDatVe.huyDonTheoMa(don.getMaDonDat())) return false;
      if (don.getVe() != null) this.ve.capNhatTrangThaiHuy(don.getVe().getMaVe(), true);
      this.ghiLichSu("Huy don", "Don dat ve", don.getMaDonDat(), "");
      this.luuDuLieu();
      return true;
   }

   private void quanLyChuyenBay() {
      System.out.println("\n===== QUAN LY CHUYEN BAY =====");
      VaiTro vaiTro = this.taiKhoan.getVaiTro();
      boolean quanTri = vaiTro.coToanQuyen();
      this.inDanhSachChuyenBay("", quanTri);

      if (vaiTro == VaiTro.WAREHOUSE) {
         System.out.println("Kho chi duoc cap nhat thong tin/gia chuyen bay.");
         System.out.println("1. Sua thong tin  2. Sua bang gia  3. Tim chuyen bay  0. Quay lai");
         int thaoTacKho = this.nhapSoNguyen("Chon thao tac: ", 0, 3);
         if (thaoTacKho == 1) this.suaThongTinChuyenBay();
         else if (thaoTacKho == 2) this.suaBangGiaChuyenBay();
         else if (thaoTacKho == 3) this.timKiemChuyenBay();
         return;
      }

      System.out.println("1. Them 1/nhieu  2. Sua  3. Ngung khai thac  4. Khoi phuc  5. Sua bang gia  6. Tim  0. Quay lai");
      int thaoTac = this.nhapSoNguyen("Chon thao tac: ", 0, 6);
      if (thaoTac == 0) return;
      if (thaoTac == 1) {
         this.themNhieuChuyenBay();
         return;
      }
      if (thaoTac == 2) {
         this.suaThongTinChuyenBay();
         return;
      }
      if (thaoTac == 5) {
         this.suaBangGiaChuyenBay();
         return;
      }
      if (thaoTac == 6) {
         this.timKiemChuyenBay();
         return;
      }

      String ma = this.nhapBatBuoc("Ma chuyen bay: ");
      boolean thanhCong = thaoTac == 3
              ? this.chuyenBay.xoaChuyenBayTheoMa(ma, this.ve)
              : this.chuyenBay.khoiPhucChuyenBay(ma);
      if (!thanhCong) {
         System.out.println("Khong the cap nhat chuyen bay.");
         return;
      }
      this.ghiLichSu(thaoTac == 3 ? "Ngung khai thac" : "Khoi phuc", "Chuyen bay", ma, "");
      this.luuDuLieu();
      System.out.println("Da cap nhat danh sach chuyen bay.");
   }

   private void themNhieuChuyenBay() {
      int soLuong = this.nhapSoNguyen("So chuyen bay can them (1-1000): ", 1, 1000);
      ChuyenBay[] moi = new ChuyenBay[soLuong];
      for (int i = 0; i < soLuong; i++) {
         System.out.println("Nhap chuyen bay " + (i + 1) + "/" + soLuong + ":");
         moi[i] = this.nhapThongTinChuyenBay(this.nhapBatBuoc("Ma chuyen bay: "));
      }
      boolean[] ketQua = this.chuyenBay.themNhieuChuyenBay(moi);
      int daThem = 0;
      for (int i = 0; i < ketQua.length; i++) {
         if (ketQua[i]) {
            daThem++;
            this.ghiLichSu("Them", "Chuyen bay", moi[i].getSoHieuChuyenBay(),
                    moi[i].getDiemDi() + " -> " + moi[i].getDiemDen());
         } else {
            System.out.println("Bo qua ma chuyen bay bi trung: " + moi[i].getSoHieuChuyenBay());
         }
      }
      if (daThem > 0) this.luuDuLieu();
      System.out.printf("Da them %d/%d chuyen bay.%n", daThem, soLuong);
   }

   private void suaThongTinChuyenBay() {
      String ma = this.nhapBatBuoc("Ma chuyen bay: ");
      ChuyenBay cu = this.chuyenBay.timChuyenBayTheoMa(ma);
      if (cu == null || !cu.isStatus()) {
         System.out.println("Khong tim thay chuyen bay dang khai thac.");
         return;
      }
      ChuyenBay moi = this.nhapThongTinChuyenBay(cu.getSoHieuChuyenBay());
      moi.setGiaPhoThong(cu.getGiaGocTheoHang(1), cu.getPhuPhiTheoHang(1));
      moi.setGiaThuongGia(cu.getGiaGocTheoHang(2), cu.getPhuPhiTheoHang(2));
      if (!this.chuyenBay.suaChuyenBayTheoMa(ma, moi, this.ve)) {
         System.out.println(this.ve.demVeTheoMaChuyenBay(ma) > 0
                 ? "Chuyen bay da co ve nen khong the sua thong tin."
                 : "Khong cap nhat duoc chuyen bay.");
         return;
      }
      this.ghiLichSu("Sua", "Chuyen bay", ma,
              moi.getDiemDi() + " -> " + moi.getDiemDen() + " | Di: " + moi.getNgayDi()
                      + " | Den: " + moi.getNgayDen() + " | Ghe trong: " + moi.getSoGheTrong());
      this.luuDuLieu();
      System.out.println("Da cap nhat thong tin chuyen bay.");
   }

   private void timKiemChuyenBay() {
      String tuKhoa = this.nhapBatBuoc("Tim theo ma, diem di/den hoac ngay bay: ");
      this.inDanhSachChuyenBay(tuKhoa, this.taiKhoan.getVaiTro().coToanQuyen());
   }

   private ChuyenBay nhapThongTinChuyenBay(String var1) {
      String var2 = this.nhapBatBuoc("Diem di: ");
      String var3 = this.nhapBatBuoc("Diem den: ");
      String var4 = this.nhapBatBuoc("Ngay/gio di (vi du 20/11/2026 08:30): ");
      String var5 = this.nhapBatBuoc("Ngay/gio den: ");
      int var6 = this.nhapSoNguyen("Tong so ghe: ", 1, 100000);
      int var7 = this.nhapSoNguyen("So ghe trong: ", 0, var6);
      return new ChuyenBay(true, var1, var2, var3, var4, var5, var6, var7);
   }

   private void suaBangGiaChuyenBay() {
      String maChuyenBay = this.nhapBatBuoc("Ma chuyen bay: ");
      ChuyenBay chuyenBay = this.chuyenBay.timChuyenBayTheoMa(maChuyenBay);
      if (chuyenBay == null) {
         System.out.println("Khong tim thay chuyen bay.");
         return;
      }

      System.out.printf("Gia tron goi hien tai | Pho thong: %s | Thuong gia: %s%n",
              this.giaHienThi(chuyenBay, 1), this.giaHienThi(chuyenBay, 2));
      double giaPhoThong = this.nhapSoThuc("Gia goc pho thong (VND): ", 1);
      double phiHanhLy = this.nhapSoThuc("Phi hanh ly pho thong (VND): ", 0);
      double giaThuongGia = this.nhapSoThuc("Gia goc thuong gia (VND): ", 1);
      double phiPhongCho = this.nhapSoThuc("Phi phong cho thuong gia (VND): ", 0);
      chuyenBay.setGiaPhoThong(giaPhoThong, phiHanhLy);
      chuyenBay.setGiaThuongGia(giaThuongGia, phiPhongCho);
      this.ghiLichSu("Sua bang gia", "Chuyen bay", maChuyenBay,
              "Pho thong: " + this.giaHienThi(chuyenBay, 1)
                      + " | Thuong gia: " + this.giaHienThi(chuyenBay, 2));
      this.luuDuLieu();
      System.out.println("Da cap nhat day du gia ve cho chuyen bay.");
   }

   private void quanLyNhanSuVaDaiLy() {
      System.out.println("\n1. Quan ly nhan vien   2. Quan ly dai ly   0. Quay lai");
      int var1 = this.nhapSoNguyen("Chon danh sach: ", 0, 2);
      if (var1 == 1) {
         this.quanLyNhanVien();
      }

      if (var1 == 2) {
         this.quanLyDaiLy();
      }

   }

   private void quanLyNhanVien() {
      System.out.println("\n===== NHAN VIEN =====");
      this.xuatTheoTrang("DANH SACH NHAN VIEN", this.nhanVien.getDanhSach(), nhanVien ->
              System.out.printf("%s | %s | %s | %s%n", nhanVien.getMaNhanVien(),
                      nhanVien.getHoTen(), nhanVien.getSoDienThoai(),
                      nhanVien.isStatus() ? "Dang lam viec" : "Da ngung"));
      System.out.println("1. Them 1/nhieu  2. Sua  3. Ngung hoat dong  4. Khoi phuc  5. Tim  0. Quay lai");
      int thaoTac = this.nhapSoNguyen("Chon thao tac: ", 0, 5);
      if (thaoTac == 0) return;
      if (thaoTac == 1) {
         this.themNhieuNhanVien();
         return;
      }
      if (thaoTac == 5) {
         String tuKhoa = this.nhapBatBuoc("Tim theo ma, ten hoac dien thoai: ");
         this.xuatTheoTrang("KET QUA TIM NHAN VIEN", this.nhanVien.timNhanVienGanDung(tuKhoa), nhanVien ->
                 System.out.printf("%s | %s | %s | %s%n", nhanVien.getMaNhanVien(),
                         nhanVien.getHoTen(), nhanVien.getSoDienThoai(),
                         nhanVien.isStatus() ? "Dang lam viec" : "Da ngung"));
         return;
      }

      String ma = this.nhapBatBuoc("Ma nhan vien: ");
      boolean thanhCong;
      if (thaoTac == 2) {
         thanhCong = this.nhanVien.suaNhanVien(ma, this.nhapBatBuoc("Ho ten moi: "),
                 this.nhapBatBuoc("So dien thoai moi: "), this.nhapBatBuoc("Mat khau moi: "));
      } else if (thaoTac == 3) {
         thanhCong = this.nhanVien.xoaNhanVien(ma);
      } else {
         thanhCong = this.nhanVien.khoiPhucNhanVien(ma);
      }
      if (!thanhCong) {
         System.out.println("Khong cap nhat duoc nhan vien (kiem tra ma/trang thai).");
         return;
      }
      this.ghiLichSu(thaoTac == 2 ? "Sua" : (thaoTac == 3 ? "Ngung hoat dong" : "Khoi phuc"),
              "Nhan vien", ma, thaoTac == 2
                      ? "Da cap nhat ho ten, so dien thoai, mat khau" : "");
      this.luuDuLieu();
      System.out.println("Da cap nhat nhan vien.");
   }

   private void themNhieuNhanVien() {
      int soLuong = this.nhapSoNguyen("So nhan vien can them (1-1000): ", 1, 1000);
      NhanVien[] moi = new NhanVien[soLuong];
      for (int i = 0; i < soLuong; i++) {
         System.out.println("Nhap nhan vien " + (i + 1) + "/" + soLuong + ":");
         String ma = this.nhapBatBuoc("Ma nhan vien: ");
         moi[i] = new NhanVien(ma, ma, this.nhapBatBuoc("Ho ten: "),
                 this.nhapBatBuoc("So dien thoai: "), this.nhapBatBuoc("Mat khau: "));
      }
      boolean[] ketQua = this.nhanVien.themNhieuNhanVien(moi);
      int daThem = 0;
      for (int i = 0; i < ketQua.length; i++) {
         if (ketQua[i]) {
            daThem++;
            this.ghiLichSu("Them", "Nhan vien", moi[i].getMaNhanVien(), moi[i].getHoTen());
         } else {
            System.out.println("Bo qua ma nhan vien bi trung: " + moi[i].getMaNhanVien());
         }
      }
      if (daThem > 0) this.luuDuLieu();
      System.out.printf("Da them %d/%d nhan vien.%n", daThem, soLuong);
   }

   private void quanLyDaiLy() {
      System.out.println("\n===== DAI LY =====");
      this.xuatTheoTrang("DANH SACH DAI LY", this.daiLy.getDanhSach(), DaiLy::hienThiThongTin);
      System.out.println("1. Them 1/nhieu  2. Sua thong tin  3. Ngung hoat dong  4. Khoi phuc  5. Tim  0. Quay lai");
      int thaoTac = this.nhapSoNguyen("Chon thao tac: ", 0, 5);
      if (thaoTac == 0) return;
      if (thaoTac == 1) {
         this.themNhieuDaiLy();
         return;
      }
      if (thaoTac == 5) {
         String tuKhoa = this.nhapBatBuoc("Tim theo ma, ten, dien thoai hoac loai dai ly: ");
         this.xuatTheoTrang("KET QUA TIM DAI LY", this.daiLy.timDaiLyGanDung(tuKhoa), DaiLy::hienThiThongTin);
         return;
      }

      String ma = this.nhapBatBuoc("Ma dai ly: ");
      boolean thanhCong;
      if (thaoTac == 2) {
         thanhCong = this.daiLy.suaThongTinDaiLyTheoMa(ma,
                 this.nhapBatBuoc("Ten moi: "), this.nhapBatBuoc("So dien thoai moi: "),
                 this.nhapBatBuoc("Loai dai ly moi: "), this.nhapSoThuc("Du no moi (VND): ", 0));
      } else if (thaoTac == 3) {
         thanhCong = this.daiLy.xoaDaiLyTheoMa(ma);
      } else {
         thanhCong = this.daiLy.khoiPhucDaiLy(ma);
      }
      if (!thanhCong) {
         System.out.println("Khong cap nhat duoc dai ly (kiem tra ma/trang thai).");
         return;
      }
      this.ghiLichSu(thaoTac == 2 ? "Sua" : (thaoTac == 3 ? "Ngung hoat dong" : "Khoi phuc"),
              "Dai ly", ma, thaoTac == 2
                      ? "Da cap nhat ten, so dien thoai, loai dai ly, du no" : "");
      this.luuDuLieu();
      System.out.println("Da cap nhat dai ly.");
   }

   private void themNhieuDaiLy() {
      int soLuong = this.nhapSoNguyen("So dai ly can them (1-1000): ", 1, 1000);
      DaiLy[] moi = new DaiLy[soLuong];
      for (int i = 0; i < soLuong; i++) {
         System.out.println("Nhap dai ly " + (i + 1) + "/" + soLuong + ":");
         moi[i] = new DaiLy(this.nhapBatBuoc("Ma dai ly: "),
                 this.nhapBatBuoc("Ten dai ly: "), this.nhapBatBuoc("So dien thoai: "),
                 this.nhapBatBuoc("Loai dai ly: "), this.nhapSoThuc("Du no ban dau (VND): ", 0));
      }
      boolean[] ketQua = this.daiLy.themNhieuDaiLy(moi);
      int daThem = 0;
      for (int i = 0; i < ketQua.length; i++) {
         if (ketQua[i]) {
            daThem++;
            this.ghiLichSu("Them", "Dai ly", moi[i].getMaSo(), moi[i].getHoTen());
         } else {
            System.out.println("Bo qua ma dai ly bi trung: " + moi[i].getMaSo());
         }
      }
      if (daThem > 0) this.luuDuLieu();
      System.out.printf("Da them %d/%d dai ly.%n", daThem, soLuong);
   }

   private void baoCaoDoanhThu() {
      while (true) {
         System.out.println("\n===== BAO CAO VA THONG KE =====");
         System.out.printf("Don con hieu luc: %d | Tong doanh thu: %,.0f VND%n",
                 this.donDatVe.getSoLuongDonConHieuLuc(), this.donDatVe.thongKeDoanhThu());
         System.out.println("1. Doanh thu theo thang/quy/nam");
         System.out.println("2. Doanh thu theo nhan vien va thang/quy/nam");
         System.out.println("3. Chuyen bay ban chay nhat va it khach nhat");
         System.out.println("4. Thang ban chay nhat va it khach nhat");
         System.out.println("0. Quay lai menu chinh");
         int thaoTac = this.nhapSoNguyen("Chon bao cao: ", 0, 4);
         if (thaoTac == 0) {
            if (this.xacNhan("Dong y quay lai menu chinh? (y/N): ")) return;
            continue;
         }

         if (thaoTac == 1) {
            this.inBaoCaoDoanhThuTheoKy(null, "toan he thong");
         } else if (thaoTac == 2) {
            this.inBaoCaoDoanhThuNhanVien();
         } else if (thaoTac == 3) {
            this.inBaoCaoChuyenBayBanChay();
         } else {
            this.inBaoCaoThangBanChay();
         }
         if (this.xacNhan("Dong y quay lai menu chinh? (y/N): ")) return;
      }
   }

   private void inBaoCaoDoanhThuTheoKy(String maNhanVien, String tenNguoi) {
      System.out.println("1. Theo thang  2. Theo quy  3. Theo nam  0. Quay lai");
      int loaiKy = this.nhapSoNguyen("Chon ky bao cao: ", 0, 3);
      if (loaiKy == 0) return;
      int nam = this.nhapSoNguyen("Nam: ", 1900, 9999);
      double doanhThu;
      String moTa;
      if (loaiKy == 1) {
         int thang = this.nhapSoNguyen("Thang (1-12): ", 1, 12);
         doanhThu = this.donDatVe.thongKeDoanhThu(thang, nam, maNhanVien);
         moTa = String.format("thang %02d/%d", thang, nam);
      } else if (loaiKy == 2) {
         int quy = this.nhapSoNguyen("Quy (1-4): ", 1, 4);
         doanhThu = this.donDatVe.thongKeDoanhThuTheoQuy(quy, nam, maNhanVien);
         moTa = "quy " + quy + "/" + nam;
      } else {
         doanhThu = this.donDatVe.thongKeDoanhThu(0, nam, maNhanVien);
         moTa = "nam " + nam;
      }
      System.out.printf("Doanh thu %s cua %s: %,.0f VND%n", moTa, tenNguoi, doanhThu);
   }

   private void inBaoCaoDoanhThuNhanVien() {
      System.out.println("1. Theo thang  2. Theo quy  3. Theo nam  0. Quay lai");
      int loaiKy = this.nhapSoNguyen("Chon ky bao cao: ", 0, 3);
      if (loaiKy == 0) return;
      int nam = this.nhapSoNguyen("Nam: ", 1900, 9999);
      int thang = loaiKy == 1 ? this.nhapSoNguyen("Thang (1-12): ", 1, 12) : 0;
      int quy = loaiKy == 2 ? this.nhapSoNguyen("Quy (1-4): ", 1, 4) : 0;
      String moTa = loaiKy == 1 ? String.format("thang %02d/%d", thang, nam)
              : loaiKy == 2 ? "quy " + quy + "/" + nam : "nam " + nam;
      NhanVien[] danhSachNhanVien = this.nhanVien.getDanhSach();
      if (danhSachNhanVien.length == 0) {
         System.out.println("Chua co nhan vien de thong ke.");
         return;
      }
      this.xuatTheoTrang("DOANH THU THEO NHAN VIEN - " + moTa, danhSachNhanVien, nv -> {
         double doanhThu = loaiKy == 2
                 ? this.donDatVe.thongKeDoanhThuTheoQuy(quy, nam, nv.getMaNhanVien())
                 : this.donDatVe.thongKeDoanhThu(thang, nam, nv.getMaNhanVien());
         System.out.printf("%s | %s | %,.0f VND%n", nv.getMaNhanVien(), nv.getHoTen(), doanhThu);
      });
   }

   private void inBaoCaoChuyenBayBanChay() {
      ChuyenBay banChay = this.donDatVe.timChuyenBayBanChayNhat(this.chuyenBay.getDanhSach());
      ChuyenBay itKhach = this.donDatVe.timChuyenBayItKhachNhat(this.chuyenBay.getDanhSach());
      if (banChay == null) {
         System.out.println("Chua co chuyen bay dang khai thac.");
         return;
      }
      this.inDongThongKeChuyenBay("Ban chay nhat", banChay);
      this.inDongThongKeChuyenBay("It khach nhat", itKhach);
   }

   private void inDongThongKeChuyenBay(String nhan, ChuyenBay chuyenBay) {
      if (chuyenBay == null) return;
      System.out.printf("%s: %s | %s -> %s | %d don con hieu luc | %,.0f VND%n",
              nhan, chuyenBay.getSoHieuChuyenBay(), chuyenBay.getDiemDi(), chuyenBay.getDiemDen(),
              this.donDatVe.demDonConHieuLucTheoMaChuyenBay(chuyenBay.getSoHieuChuyenBay()),
              this.donDatVe.thongKeDoanhThuTheoChuyenBay(chuyenBay.getSoHieuChuyenBay()));
   }

   private void inBaoCaoThangBanChay() {
      int nam = this.nhapSoNguyen("Nam can thong ke: ", 1900, 9999);
      int thangBanChay = this.donDatVe.timThangBanChayNhat(nam);
      int thangItKhach = this.donDatVe.timThangItKhachNhat(nam);
      if (thangBanChay == 0) {
         System.out.println("Nam " + nam + " chua co don dat ve con hieu luc.");
         return;
      }
      int[] soDon = this.donDatVe.thongKeSoDonTheoThang(nam, null);
      System.out.printf("Thang ban chay nhat: %02d/%d | %d don | %,.0f VND%n",
              thangBanChay, nam, soDon[thangBanChay - 1],
              this.donDatVe.thongKeDoanhThu(thangBanChay, nam, null));
      System.out.printf("Thang it khach nhat: %02d/%d | %d don | %,.0f VND%n",
              thangItKhach, nam, soDon[thangItKhach - 1],
              this.donDatVe.thongKeDoanhThu(thangItKhach, nam, null));
   }

   private <T> void xuatTheoTrang(String tieuDe, T[] danhSach,
                                  java.util.function.Consumer<T> cachIn) {
      if (danhSach == null || danhSach.length == 0) {
         System.out.println("\n" + tieuDe + ": danh sach trong.");
         return;
      }
      int kichThuocTrang = 10;
      int soTrang = (danhSach.length + kichThuocTrang - 1) / kichThuocTrang;
      int trang = 0;
      while (true) {
         int batDau = trang * kichThuocTrang;
         int ketThuc = Math.min(batDau + kichThuocTrang, danhSach.length);
         System.out.printf("\n===== %s | Trang %d/%d | %d ban ghi =====%n",
                 tieuDe, trang + 1, soTrang, danhSach.length);
         for (int i = batDau; i < ketThuc; i++) {
            System.out.printf("%d. ", i + 1);
            cachIn.accept(danhSach[i]);
         }
         if (soTrang == 1) return;

         System.out.println("N. Trang sau   P. Trang truoc   0. Dong danh sach");
         String luaChon = this.nhapChuoi("Chon trang: ");
         if ("n".equalsIgnoreCase(luaChon) || "t".equalsIgnoreCase(luaChon)) {
            if (trang + 1 < soTrang) trang++;
            else System.out.println("Ban dang o trang cuoi.");
         } else if ("p".equalsIgnoreCase(luaChon) || "l".equalsIgnoreCase(luaChon)) {
            if (trang > 0) trang--;
            else System.out.println("Ban dang o trang dau.");
         } else if ("0".equals(luaChon)) {
            if (this.xacNhan("Dong y dong danh sach? (y/N): ")) return;
         } else {
            System.out.println("Hay chon N, P hoac 0.");
         }
      }
   }

   private void inDongVe(VeMayBay ve) {
      String loaiVe = ve instanceof VePhoThong ? "Pho thong"
              : ve instanceof VeThuongGia ? "Thuong gia" : "Khac";
      String maChuyen = ve.getChuyenBay() == null ? "-" : ve.getChuyenBay().getSoHieuChuyenBay();
      System.out.printf("%s | %s | %s | %,.0f VND | %s%n", ve.getMaVe(), loaiVe,
              maChuyen, ve.tinhGiaVe(), this.ve.isDaHuy(ve.getMaVe()) ? "DA HUY" : "CON HIEU LUC");
   }

   private void inDongLichSu(LichSuThaoTac muc) {
      System.out.printf("%s | %s (%s) | %s | %s %s | %s%n", muc.getThoiGian(),
              muc.getTaiKhoan(), muc.getVaiTro(), muc.getHanhDong(), muc.getDoiTuong(),
              muc.getMaDoiTuong(), muc.getGhiChu());
   }

   private void xemLichSu() {
      LichSuThaoTac[] danhSachCu = this.lichSu.getDanhSach();
      LichSuThaoTac[] moiNhatTruoc = new LichSuThaoTac[danhSachCu.length];
      for (int i = 0; i < danhSachCu.length; i++) {
         moiNhatTruoc[i] = danhSachCu[danhSachCu.length - i - 1];
      }
      this.xuatTheoTrang("NHAT KY THAO TAC", moiNhatTruoc, this::inDongLichSu);
   }

   private String[] maHanhKhachHienCo() {
      HanhKhach[] var1 = this.hanhKhach.getDanhSach();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = var1[var3].getMaSo();
      }

      return var2;
   }

   private String nextCode(String var1, String[] var2) {
      int var3 = 0;

      for(String var7 : var2) {
         if (var7 != null && var7.toUpperCase(Locale.ROOT).startsWith(var1)) {
            try {
               var3 = Math.max(var3, Integer.parseInt(var7.substring(var1.length())));
            } catch (NumberFormatException var9) {
            }
         }
      }

      return var1 + String.format("%04d", var3 + 1);
   }

   private String nhapChuoi(String var1) {
      System.out.print(var1);
      if (!this.input.hasNextLine()) {
         throw new HetDuLieuNhap();
      } else {
         return this.input.nextLine().trim();
      }
   }

   private String nhapBatBuoc(String var1) {
      while(true) {
         String var2 = this.nhapChuoi(var1);
         if (!var2.isEmpty()) {
            return var2;
         }

         System.out.println("Gia tri nay khong duoc de trong.");
      }
   }

   private int nhapSoNguyen(String var1, int var2, int var3) {
      while(true) {
         String var4 = this.nhapChuoi(var1);

         try {
            int var5 = Integer.parseInt(var4);
            if (var5 >= var2 && var5 <= var3) {
               return var5;
            }
         } catch (NumberFormatException var6) {
         }

         System.out.printf("Hay nhap so nguyen tu %d den %d.%n", var2, var3);
      }
   }

   private double nhapSoThuc(String var1, double var2) {
      while(true) {
         String var4 = this.nhapChuoi(var1).replace(',', '.');

         try {
            double var5 = Double.parseDouble(var4);
            if (Double.isFinite(var5) && var5 >= var2) {
               return var5;
            }
         } catch (NumberFormatException var7) {
         }

         System.out.println("Hay nhap so hop le lon hon hoac bang " + var2 + ".");
      }
   }

   private boolean xacNhan(String var1) {
      String var2 = this.nhapChuoi(var1);
      return "y".equalsIgnoreCase(var2) || "yes".equalsIgnoreCase(var2) || "c".equalsIgnoreCase(var2) || "co".equalsIgnoreCase(var2);
   }

   static {
      TAI_KHOAN_MAU = new TaiKhoan[]{new TaiKhoan("client", "123456", VaiTro.CLIENT), new TaiKhoan("staff", "123456", VaiTro.STAFF, "NV0001"), new TaiKhoan("warehouse", "123456", VaiTro.WAREHOUSE), new TaiKhoan("manager", "123456", VaiTro.MANAGER), new TaiKhoan("admin", "123456", VaiTro.ADMIN)};
   }

   private static final class HetDuLieuNhap extends RuntimeException {
      private static final long serialVersionUID = 1L;
   }
}
