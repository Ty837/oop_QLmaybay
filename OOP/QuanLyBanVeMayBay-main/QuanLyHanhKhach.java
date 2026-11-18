import java.util.Locale;

/** Quản lý hành khách bằng mảng đối tượng. */
public class QuanLyHanhKhach {
    private HanhKhach[] danhSach;
    private int soLuong;
    public QuanLyHanhKhach(){
        danhSach=new HanhKhach[1000];
        soLuong=0;
    }
    public boolean themHanhKhach(HanhKhach hanhKhach){
        if(hanhKhach==null||soLuong>=danhSach.length) return false;
        String maHanhKhach=hanhKhach.getMaSo();
        if(maHanhKhach==null||maHanhKhach.trim().isEmpty()) return false;
        if(timViTriHanhKhachTheoMa(maHanhKhach)!=-1) return false;
        danhSach[soLuong]=hanhKhach;
        soLuong++;
        return true;
    }
    public boolean[] themNhieuHanhKhach(HanhKhach[] cacHanhKhach){
        if(cacHanhKhach==null) return new boolean[0];
        boolean[] ketQua=new boolean[cacHanhKhach.length];
        for(int i=0;i<cacHanhKhach.length;i++) ketQua[i]=themHanhKhach(cacHanhKhach[i]);
        return ketQua;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public HanhKhach[] getDanhSach(){
        HanhKhach[] ketQua = new HanhKhach[soLuong];
        System.arraycopy(danhSach, 0, ketQua, 0, soLuong);
        return ketQua;
    }
    public int timViTriHanhKhach(HanhKhach canTim){
        for(int i=0;i<soLuong;i++){
            if(danhSach[i]==canTim) return i;
        }
        return -1;
    }
    public int timViTriHanhKhachTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()){
            return -1;
        }
        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            String maHanhKhach=danhSach[i].getMaSo();
            if(maHanhKhach!=null&&maHanhKhach.equalsIgnoreCase(ma)) return i;
        }
        return -1;
    }
    public void xuatDanhSach(){
        xuatDanhSach(getDanhSach());
    }
    public void xuatDanhSach(HanhKhach[] danhSachCanIn){
        if(danhSachCanIn==null||danhSachCanIn.length==0){
            System.out.println("Danh sach khach hang trong");
            return;
        }
        for(int i=0;i<danhSachCanIn.length;i++){
            System.out.print((i+1)+". ");
            danhSachCanIn[i].hienThiThongTin();
        }
    }
    public HanhKhach timHanhKhachTheoMa(String maCanTim){
        int viTri=timViTriHanhKhachTheoMa(maCanTim);
        if(viTri==-1){
            return null;
        }
        return danhSach[viTri];
    }
    public boolean xoaHanhKhachTheoMa(String maHanhKhach,QuanLyVe quanLyVe){
        int viTri=timViTriHanhKhachTheoMa(maHanhKhach);
        if(viTri==-1 || !danhSach[viTri].isStatus()) return false;
        danhSach[viTri].setStatus(false);
        return true;
    }
    public boolean khoiPhucHanhKhach(String maHanhKhach){
        HanhKhach hanhKhach=timHanhKhachTheoMa(maHanhKhach);
        if(hanhKhach==null || hanhKhach.isStatus()) return false;
        hanhKhach.setStatus(true);
        return true;
    }
    public HanhKhach[] timHanhKhachGanDungTheoTen(String tuKhoa){
        return timHanhKhachGanDung(tuKhoa);
    }
    public HanhKhach[] timHanhKhachGanDung(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new HanhKhach[0];
        }

        String khoa=tuKhoa.trim().toLowerCase(Locale.ROOT);
        HanhKhach[] tam=new HanhKhach[soLuong];
        int dem=0;

        for(int i=0;i<soLuong;i++){
            HanhKhach hanhKhach=danhSach[i];
            if(khopTatCaTuKhoa(khoa, hanhKhach.getMaSo(), hanhKhach.getHoTen(),
                    hanhKhach.getSoDienThoai(), hanhKhach.getHochieu())){
                tam[dem]=danhSach[i];
                dem++;
            }
        }

        HanhKhach[] ketQua=new HanhKhach[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=tam[i];
        }
        return ketQua;
    }
    private boolean khopTatCaTuKhoa(String khoa, String... cacTruong){
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
    public HanhKhach layHanhKhachTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return null;
        return danhSach[viTri];
    }
    public boolean suaTenHanhKhachTheoMa(String maHanhKhach,String hoTenMoi){
        if(hoTenMoi==null||hoTenMoi.trim().isEmpty()) return false;
        HanhKhach hanhKhach=timHanhKhachTheoMa(maHanhKhach);
        if(hanhKhach==null) return false;
        hanhKhach.setHoTen(hoTenMoi.trim());
        return true;
    }
    public boolean suaThongTinHanhKhach(String maHanhKhach, String hoTen,
                                        String soDienThoai, String hoChieu){
        HanhKhach hanhKhach = timHanhKhachTheoMa(maHanhKhach);
        if (hanhKhach == null || !hanhKhach.isStatus() || hoTen == null || hoTen.trim().isEmpty()
                || soDienThoai == null || soDienThoai.trim().isEmpty()
                || hoChieu == null || hoChieu.trim().isEmpty()) return false;
        hanhKhach.setHoTen(hoTen.trim());
        hanhKhach.setSoDienThoai(soDienThoai.trim());
        hanhKhach.setHochieu(hoChieu.trim());
        return true;
    }
}
