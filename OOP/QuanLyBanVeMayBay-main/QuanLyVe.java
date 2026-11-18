/** Quản lý vé bằng mảng đối tượng VeMayBay[]. */
public class QuanLyVe {
    private VeMayBay[] danhSach;
    private boolean[] daHuy;
    private int soLuong;
    public QuanLyVe(){
        danhSach=new VeMayBay[1000];
        daHuy=new boolean[1000];
        soLuong=0;
    }
    public boolean themVe(VeMayBay ve){
        if(ve==null) return false;
        String maVe=ve.getMaVe();
        if(maVe==null||maVe.trim().isEmpty()) return false;
        if(timViTriVeTheoMa(maVe)!=-1) return false;
        if(soLuong>=danhSach.length){
            VeMayBay[] danhSachMoi=new VeMayBay[danhSach.length*2];
            boolean[] daHuyMoi=new boolean[daHuy.length*2];
            for(int i=0;i<soLuong;i++){
                danhSachMoi[i]=danhSach[i];
                daHuyMoi[i]=daHuy[i];
            }
            danhSach=danhSachMoi;
            daHuy=daHuyMoi;
        }
        danhSach[soLuong]=ve;
        daHuy[soLuong]=false;
        soLuong++;
        return true;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public VeMayBay[] getDanhSach(){
        VeMayBay[] ketQua = new VeMayBay[soLuong];
        System.arraycopy(danhSach, 0, ketQua, 0, soLuong);
        return ketQua;
    }

    public boolean isDaHuy(String maVe){
        int viTri = timViTriVeTheoMa(maVe);
        return viTri >= 0 && daHuy[viTri];
    }

    public boolean capNhatTrangThaiHuy(String maVe, boolean trangThaiHuy){
        int viTri = timViTriVeTheoMa(maVe);
        if (viTri < 0) return false;
        daHuy[viTri] = trangThaiHuy;
        danhSach[viTri].setStatus(!trangThaiHuy);
        return true;
    }
    public boolean xoaVeTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong || daHuy[viTri]) return false;
        daHuy[viTri]=true;
        danhSach[viTri].setStatus(false);
        return true;
    }
    public int demVePhoThong(){
        int d=0;
        for(int i=0;i<soLuong;i++){
            if(danhSach[i] instanceof VePhoThong) d++;
        }
        return d;   
    }
    public int demVeThuongGia(){
        int d=0;
        for(int i=0;i<soLuong;i++){
            if(danhSach[i] instanceof VeThuongGia)
                d++;
        }
        return d;
    }
    public void xuatDanhSach(){
        xuatDanhSach(getDanhSach());
    }

    public void xuatDanhSach(VeMayBay[] danhSachCanIn){
        if(danhSachCanIn==null||danhSachCanIn.length==0){
            System.out.println("Danh sach ve trong");
            return;
        }
        System.out.println("STT | MA VE | LOAI VE | MA CHUYEN | GIA GOC | PHU PHI | TONG THANH TOAN | TRANG THAI");
        for(int i=0;i<danhSachCanIn.length;i++){
            VeMayBay ve = danhSachCanIn[i];
            String trangThai = isDaHuy(ve.getMaVe()) ? "DA HUY" : "CON HIEU LUC";
            String loaiVe = "Khac";
            double phuPhi = 0;
            if (ve instanceof VePhoThong) {
                loaiVe = "Pho thong";
                phuPhi = ((VePhoThong) ve).getPhiHanhLy();
            } else if (ve instanceof VeThuongGia) {
                loaiVe = "Thuong gia";
                phuPhi = ((VeThuongGia) ve).getPhiPhongCho();
            }
            String maChuyen = ve.getChuyenBay() == null ? "-" : ve.getChuyenBay().getSoHieuChuyenBay();
            System.out.printf("%d | %s | %s | %s | %,.0f VND | %,.0f VND | %,.0f VND | %s%n",
                    i + 1, ve.getMaVe(), loaiVe, maChuyen, ve.getGiaGoc(), phuPhi,
                    ve.tinhGiaVe(), trangThai);
        }
    }

    public VeMayBay[] timVeGanDung(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()) return new VeMayBay[0];
        String khoa=tuKhoa.trim().toLowerCase(java.util.Locale.ROOT);
        VeMayBay[] ketQua=new VeMayBay[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            VeMayBay ve=danhSach[i];
            HanhKhach hanhKhach=ve.getHanhKhach();
            ChuyenBay chuyenBay=ve.getChuyenBay();
            String maHanhKhach=hanhKhach==null?null:hanhKhach.getMaSo();
            String hoTen=hanhKhach==null?null:hanhKhach.getHoTen();
            String maChuyen=chuyenBay==null?null:chuyenBay.getSoHieuChuyenBay();
            if(khopTatCaTuKhoa(khoa, ve.getMaVe(), maHanhKhach, hoTen, maChuyen)){
                ketQua[dem++]=ve;
            }
        }
        VeMayBay[] ketQuaGon=new VeMayBay[dem];
        System.arraycopy(ketQua,0,ketQuaGon,0,dem);
        return ketQuaGon;
    }

    private boolean khopTatCaTuKhoa(String khoa, String... cacTruong){
        for(String tuKhoa:khoa.split("\\s+")){
            boolean timThay=false;
            for(String giaTri:cacTruong){
                if(giaTri!=null&&giaTri.toLowerCase(java.util.Locale.ROOT).contains(tuKhoa)){
                    timThay=true;
                    break;
                }
            }
            if(!timThay) return false;
        }
        return true;
    }
    public int timViTriVeTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()) return -1;
        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            if(danhSach[i].getMaVe().equalsIgnoreCase(ma)) return i;
        }
        return -1;
    }
    public boolean veConHieuLuc(String maVe){
        int viTri=timViTriVeTheoMa(maVe);
        return viTri!=-1&&!daHuy[viTri]&&danhSach[viTri].isStatus();
    }
    public boolean suaVeTheoMa(String maVe,VeMayBay veMoi){
        int viTri=timViTriVeTheoMa(maVe);
        if(viTri==-1||daHuy[viTri]||!danhSach[viTri].isStatus()||veMoi==null) return false;
        String maMoi=veMoi.getMaVe();
        if(maMoi==null||!maMoi.equalsIgnoreCase(danhSach[viTri].getMaVe())) return false;
        danhSach[viTri]=veMoi;
        return true;
    }
    public VeMayBay timVeTheoMa(String maVe){
        int viTri=timViTriVeTheoMa(maVe);
        if(viTri==-1){
            return null;
        }
        return danhSach[viTri];
    }
    public VeMayBay[] timVeGanDungTheoMa(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new VeMayBay[0];
        }
        String maKhoa=tuKhoa.trim().toUpperCase();
        VeMayBay[] temp=new VeMayBay[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            String maVe= danhSach[i].getMaVe();
            if(maVe!=null&&maVe.toUpperCase().contains(maKhoa)){
                temp[dem]=danhSach[i];
                dem++;
            }
        }
        VeMayBay[] ketQua=new VeMayBay[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
        }
        return ketQua;
    }
    public VeMayBay[] timVeGanDungTheoHanhKhach(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()) return new VeMayBay[0];
        String maKhoa= tuKhoa.trim().toUpperCase();
        VeMayBay[] temp=new VeMayBay[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            HanhKhach hanhKhach=danhSach[i].getHanhKhach();
            if(hanhKhach!=null){
                String hoTen=hanhKhach.getHoTen();
                if(hoTen!=null&&hoTen.toUpperCase().contains(maKhoa)){
                temp[dem]=danhSach[i];
                dem++;
               }
            }
           
        }
         VeMayBay[] ketQua=new VeMayBay[dem];
         for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
         }
         return ketQua;
    }
    public int demVeTheoMaChuyenBay(String maChuyenBay){
        if(maChuyenBay==null||maChuyenBay.trim().isEmpty()) return 0;
        String maCanDem=maChuyenBay.trim();
        int dem=0;
        for(int i=0;i<soLuong;i++){
            ChuyenBay chuyenBay=danhSach[i].getChuyenBay();
            if(chuyenBay!=null){
                String maCuaChuyen=chuyenBay.getSoHieuChuyenBay();
                if(maCuaChuyen!=null&&maCuaChuyen.equalsIgnoreCase(maCanDem)){
                    dem++;
                }
            }
        }
        return dem;
    }
    public int demVeConHieuLucTheoMaChuyenBay(String maChuyenBay){
        if(maChuyenBay==null||maChuyenBay.trim().isEmpty()) return 0;
        String maCanDem=maChuyenBay.trim();
        int dem=0;
        for(int i=0;i<soLuong;i++){
            if(daHuy[i]) continue;
            ChuyenBay chuyenBay=danhSach[i].getChuyenBay();
            if(chuyenBay!=null){
                String maCuaChuyen=chuyenBay.getSoHieuChuyenBay();
                if(maCuaChuyen!=null&&maCuaChuyen.equalsIgnoreCase(maCanDem)) dem++;
            }
        }
        return dem;
    }
    public VeMayBay layVeTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return null;
        return danhSach[viTri];
    }
    public int demVeTheoMaHanhKhach(String maHanhKhach){
        if(maHanhKhach==null||maHanhKhach.trim().isEmpty()) return 0;
        String maCanDem=maHanhKhach.trim();
        int dem=0;
        for(int i=0;i<soLuong;i++){
            HanhKhach hanhKhach=danhSach[i].getHanhKhach();
            if(hanhKhach!=null){
                String maCuaHanhKhach=hanhKhach.getMaSo();
                if(maCuaHanhKhach!=null&&maCuaHanhKhach.equalsIgnoreCase(maCanDem)) dem++;
            }
        }
        return dem;
    }
}
