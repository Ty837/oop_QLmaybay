# Quan ly ban ve may bay

Ung dung Java chay tren terminal, dung mang doi tuong de quan ly chuyen bay, hanh khach, nhan vien, dai ly, ve va don dat ve.

## Chay chuong trinh

Mo terminal tai thu muc nay va chay:

```powershell
javac -encoding UTF-8 -d out *.java
java -cp out Main
```

Ma `.class` duoc tao trong thu muc `out`, khong nam canh cac file `.java`. Tai khoan minh hoa: `client`, `staff`, `warehouse`, `manager`, `admin`; mat khau: `123456`.

## So do lop

```mermaid
classDiagram
    class Nguoi {
        <<abstract>>
        -boolean status
        -String maSo
        -String hoTen
        -String soDienThoai
        +hienThiThongTin()*
    }
    class HanhKhach
    class NhanVien
    class DaiLy
    Nguoi <|-- HanhKhach
    Nguoi <|-- NhanVien
    Nguoi <|-- DaiLy

    class VeMayBay {
        <<abstract>>
        -boolean status
        -String maVe
        -double giaGoc
        +tinhGiaVe()* double
    }
    class VePhoThong
    class VeThuongGia
    VeMayBay <|-- VePhoThong
    VeMayBay <|-- VeThuongGia

    class IThanhToan {
        <<interface>>
        +thanhToan(double) boolean
    }
    class ThanhToanTienMat
    class ThanhToanThe
    IThanhToan <|.. ThanhToanTienMat
    IThanhToan <|.. ThanhToanThe

    class ChuyenBay {
        -boolean status
        -String soHieuChuyenBay
        -String diemDi
        -String diemDen
        -int soGheTrong
        -double giaGocPhoThong
        -double phiHanhLy
        -double giaGocThuongGia
        -double phiPhongCho
    }
    class DonDatVe {
        -String maDonDat
        -boolean daHuy
        -LocalDateTime thoiGianDat
    }
    DonDatVe --> HanhKhach
    DonDatVe --> NhanVien
    DonDatVe --> ChuyenBay
    DonDatVe --> VeMayBay
    DonDatVe --> IThanhToan
    VeMayBay --> HanhKhach
    VeMayBay --> ChuyenBay

    class QuanLyChuyenBay
    class QuanLyHanhKhach
    class QuanLyDonDatVe
    class QuanLyVe
    class QuanLyNhanVien
    class QuanLyDaiLy
    class QuanLyLichSu
    QuanLyChuyenBay ..> ChuyenBay : mang
    QuanLyHanhKhach ..> HanhKhach : mang
    QuanLyDonDatVe ..> DonDatVe : mang
    QuanLyVe ..> VeMayBay : mang
    QuanLyNhanVien ..> NhanVien : mang
    QuanLyDaiLy ..> DaiLy : mang
    QuanLyLichSu ..> LichSuThaoTac : mang

    class Main
    class TaiKhoan
    class VaiTro {
        <<enumeration>>
        CLIENT
        STAFF
        WAREHOUSE
        MANAGER
        ADMIN
    }
    class NgayThang
    class LichSuThaoTac
    class XuLyFile
    Main ..> QuanLyChuyenBay
    Main ..> QuanLyHanhKhach
    Main ..> QuanLyDonDatVe
    Main ..> QuanLyVe
    Main ..> QuanLyNhanVien
    Main ..> QuanLyDaiLy
    Main ..> QuanLyLichSu
    Main ..> TaiKhoan
    TaiKhoan --> VaiTro
    DonDatVe --> NgayThang
    XuLyFile ..> ChuyenBay
    XuLyFile ..> DonDatVe
    XuLyFile ..> LichSuThaoTac
```

## Doi chieu yeu cau do an

- Co hon 15 lop, cac lop du lieu co constructor va co lop truu tuong `Nguoi`, `VeMayBay`.
- Ke thua the hien qua `Nguoi` va `VeMayBay`; da hinh the hien qua `hienThiThongTin()` va cac lop ve; thanh toan dung interface `IThanhToan`.
- Cac danh sach nghiep vu dung mang, khong dung `ArrayList`, `Vector` hay `LinkedList`.
- `XuLyFile` doc/ghi du lieu UTF-8 vao cac tep `.txt` trong `data`. Moi dong du lieu doi tuong hien duoc ma hoa Base64 de luu va nap lai.
- Thuc the co trang thai de ngung/khoi phuc ma van giu lich su; don dat ve duoc huy mem, khong xoa khoi tep.
- Menu phan quyen theo vai tro; co nhat ky thao tac, tim kiem gan dung, nhap nhieu ban ghi, gia ve theo hang va bao cao thang/quy/nam.
- Bao cao gom doanh thu theo nhan vien, chuyen bay ban chay/it khach va thang ban chay/it khach.

Luu y: du lieu `.txt` la van ban UTF-8 nhung phan noi dung doi tuong duoc ma hoa Base64; mo tep truc tiep se khong doc duoc cac truong nhu ten, ma va gia.
