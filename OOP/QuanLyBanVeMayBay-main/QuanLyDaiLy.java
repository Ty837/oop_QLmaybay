import java.util.Locale;

/** Quản lý đại lý bằng mảng đối tượng. */
public class QuanLyDaiLy {
    private DaiLy[] danhSach;
    private int soLuong;
    public QuanLyDaiLy(){
        danhSach=new DaiLy[1000];
        soLuong=0;
    }
    public int timViTriDaiLyTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()){
            return -1;
        }

        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            String maDaiLy=danhSach[i].getMaSo();
            if(maDaiLy!=null&&maDaiLy.equalsIgnoreCase(ma)){
                return i;
            }
        }
        return -1;
    }
    public boolean themDaiLy(DaiLy daiLy){
        if(daiLy==null||soLuong>=danhSach.length){
            return false;
        }

        String maDaiLy=daiLy.getMaSo();
        if(maDaiLy==null||maDaiLy.trim().isEmpty()){
            return false;
        }
        if(timViTriDaiLyTheoMa(maDaiLy)!=-1){
            return false;
        }

        danhSach[soLuong]=daiLy;
        soLuong++;
        return true;
    }
    public boolean[] themNhieuDaiLy(DaiLy[] cacDaiLy){
        if(cacDaiLy==null) return new boolean[0];
        boolean[] ketQua=new boolean[cacDaiLy.length];
        for(int i=0;i<cacDaiLy.length;i++) ketQua[i]=themDaiLy(cacDaiLy[i]);
        return ketQua;
    }
    public boolean xoaDaiLyTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong){
            return false;
        }
        if(!danhSach[viTri].isStatus()) return false;
        danhSach[viTri].setStatus(false);
        return true;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public DaiLy[] getDanhSach(){
        DaiLy[] ketQua = new DaiLy[soLuong];
        System.arraycopy(danhSach, 0, ketQua, 0, soLuong);
        return ketQua;
    }
    public void xuatDanhSach(){
        xuatDanhSach(getDanhSach());
    }
    public void xuatDanhSach(DaiLy[] danhSachCanIn){
        if(danhSachCanIn==null||danhSachCanIn.length==0){
            System.out.println("Danh sach dai ly trong");
            return;
        }
        for(int i=0;i<danhSachCanIn.length;i++){
            System.out.print((i+1)+". ");
            danhSachCanIn[i].hienThiThongTin();
        }
    }
    public DaiLy timDaiLyTheoMa(String maCanTim){
        int viTri=timViTriDaiLyTheoMa(maCanTim);
        if(viTri==-1){
            return null;
        }
        return danhSach[viTri];
    }
    public boolean xoaDaiLyTheoMa(String maDaiLy){
        int viTri=timViTriDaiLyTheoMa(maDaiLy);
        if(viTri==-1 || !danhSach[viTri].isStatus()){
            return false;
        }
        danhSach[viTri].setStatus(false);
        return true;
    }
    public boolean khoiPhucDaiLy(String maDaiLy){
        DaiLy daiLy=timDaiLyTheoMa(maDaiLy);
        if(daiLy==null || daiLy.isStatus()) return false;
        daiLy.setStatus(true);
        return true;
    }
    public DaiLy[] timDaiLyGanDungTheoMa(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new DaiLy[0];
        }

        String khoa=tuKhoa.trim().toUpperCase();
        DaiLy[] temp=new DaiLy[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            String maDaiLy=danhSach[i].getMaSo();
            if(maDaiLy!=null&&maDaiLy.toUpperCase().contains(khoa)){
                temp[dem]=danhSach[i];
                dem++;
            }
        }

        DaiLy[] ketQua=new DaiLy[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
        }
        return ketQua;
    }
    public DaiLy[] timDaiLyGanDungTheoTen(String tuKhoa){
        return timDaiLyGanDung(tuKhoa);
    }
    public DaiLy[] timDaiLyGanDung(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new DaiLy[0];
        }

        String khoa=tuKhoa.trim().toLowerCase(Locale.ROOT);
        DaiLy[] temp=new DaiLy[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            DaiLy daiLy=danhSach[i];
            if(khopTatCaTuKhoa(khoa, daiLy.getMaSo(), daiLy.getHoTen(),
                    daiLy.getSoDienThoai(), daiLy.getLoaiDaiLy())){
                temp[dem]=danhSach[i];
                dem++;
            }
        }

        DaiLy[] ketQua=new DaiLy[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
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
    public DaiLy layDaiLyTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return null;
        return danhSach[viTri];
    }
     public boolean suaTenDaiLyTheoMa(String maDaiLy,String hoTenMoi){
        if(hoTenMoi==null||hoTenMoi.trim().isEmpty()) return  false;
        DaiLy daiLy=timDaiLyTheoMa(maDaiLy);
        if(daiLy==null || !daiLy.isStatus()) return false;
        daiLy.setHoTen(hoTenMoi.trim());
        return true;
    }

    public boolean suaThongTinDaiLyTheoMa(String maDaiLy, String hoTen,
                                           String soDienThoai, String loaiDaiLy, double duNo) {
        DaiLy daiLy = timDaiLyTheoMa(maDaiLy);
        if (daiLy == null || !daiLy.isStatus() || hoTen == null || hoTen.trim().isEmpty()
                || soDienThoai == null || soDienThoai.trim().isEmpty()
                || loaiDaiLy == null || loaiDaiLy.trim().isEmpty()
                || !Double.isFinite(duNo) || duNo < 0) return false;
        daiLy.setHoTen(hoTen.trim());
        daiLy.setSoDienThoai(soDienThoai.trim());
        daiLy.setLoaiDaiLy(loaiDaiLy.trim());
        daiLy.setDuNo(duNo);
        return true;
    }
}
