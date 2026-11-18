import java.util.Locale;

/** Quản lý chuyến bay bằng mảng đối tượng. */
public class QuanLyChuyenBay {
    private ChuyenBay[] danhSach;
    private int soLuong;
    public QuanLyChuyenBay(){
        danhSach=new ChuyenBay[1000];
        soLuong=0;
    }
    public boolean themChuyenBay(ChuyenBay chuyenBay){
        if(chuyenBay==null||soLuong>=danhSach.length){
            return false;
        }
        String maChuyenBay=chuyenBay.getSoHieuChuyenBay();
        if(maChuyenBay==null||maChuyenBay.trim().isEmpty()) return false;
        if(timViTriChuyenBayTheoMa(maChuyenBay)!=-1) return false;
        danhSach[soLuong]=chuyenBay;
        soLuong++;
        return true;
    }
    public boolean[] themNhieuChuyenBay(ChuyenBay[] cacChuyenBay){
        if(cacChuyenBay==null) return new boolean[0];
        boolean[] ketQua=new boolean[cacChuyenBay.length];
        for(int i=0;i<cacChuyenBay.length;i++) ketQua[i]=themChuyenBay(cacChuyenBay[i]);
        return ketQua;
    }
    public int timChuyenBay(ChuyenBay canTim){
        for(int i=0;i<soLuong;i++){
            if(danhSach[i]==canTim) return i;
        }
        return -1;
    }
    public int timViTriChuyenBayTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()){
            return -1;
        }

        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            String maChuyenBay=danhSach[i].getSoHieuChuyenBay();
            if(maChuyenBay!=null&&maChuyenBay.equalsIgnoreCase(ma)){
                return i;
            }
        }
        return -1;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public ChuyenBay[] getDanhSach(){
        ChuyenBay[] ketQua = new ChuyenBay[soLuong];
        System.arraycopy(danhSach, 0, ketQua, 0, soLuong);
        return ketQua;
    }
    public void xuatDanhSach(){
        if(soLuong==0){
            System.out.println("Danh sach chuyen bay trong");
            return;
        }

        for(int i=0;i<soLuong;i++){
            System.out.print((i+1)+". ");
            danhSach[i].hienThiThongTin();
        }
    }
    public ChuyenBay[] timChuyenBayGanDungTheoMa(String tuKhoa){
        return timChuyenBayGanDung(tuKhoa);
    }
    public ChuyenBay[] timChuyenBayGanDung(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new ChuyenBay[0];
        }

        String khoa=tuKhoa.trim().toLowerCase(Locale.ROOT);
        ChuyenBay[] temp=new ChuyenBay[soLuong];
        int dem=0;

        for(int i=0;i<soLuong;i++){
            ChuyenBay chuyenBay=danhSach[i];
            if(khopTatCaTuKhoa(khoa, chuyenBay.getSoHieuChuyenBay(), chuyenBay.getDiemDi(),
                    chuyenBay.getDiemDen(), chuyenBay.getNgayDi(), chuyenBay.getNgayDen())){
                temp[dem]=danhSach[i];
                dem++;
            }
        }

        ChuyenBay[] ketQua=new ChuyenBay[dem];
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
    public boolean xoaChuyenBayTheoMa(String maChuyenBay,QuanLyVe quanLyVe){
        int viTri=timViTriChuyenBayTheoMa(maChuyenBay);
        if(viTri==-1 || !danhSach[viTri].isStatus()) return false;
        danhSach[viTri].setStatus(false);
        return true;
    }
    public boolean khoiPhucChuyenBay(String maChuyenBay){
        ChuyenBay chuyenBay=timChuyenBayTheoMa(maChuyenBay);
        if(chuyenBay==null || chuyenBay.isStatus()) return false;
        chuyenBay.setStatus(true);
        return true;
    }
    // Khong thay doi chuyen bay da co ve vi ve cu van tham chieu doi tuong cu.
    public boolean suaChuyenBayTheoMa(String maChuyenBay,ChuyenBay chuyenBayMoi,QuanLyVe quanLyVe){
        if(quanLyVe==null||chuyenBayMoi==null) return false;
        int viTri=timViTriChuyenBayTheoMa(maChuyenBay);
        if(viTri==-1) return false;
        if(!danhSach[viTri].isStatus()) return false;
        String maMoi=chuyenBayMoi.getSoHieuChuyenBay();
        if(maMoi==null||!maMoi.equalsIgnoreCase(danhSach[viTri].getSoHieuChuyenBay())) return false;
        if(quanLyVe.demVeTheoMaChuyenBay(maChuyenBay)>0) return false;
        danhSach[viTri]=chuyenBayMoi;
        return true;
    }
    public ChuyenBay timChuyenBayTheoMa(String maCanTim){
        int viTri=timViTriChuyenBayTheoMa(maCanTim);
        if(viTri==-1) return null;
        return danhSach[viTri];
    }
    public ChuyenBay layChuyenBayTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong){
            return null;
        }
        return danhSach[viTri];
    }
}
