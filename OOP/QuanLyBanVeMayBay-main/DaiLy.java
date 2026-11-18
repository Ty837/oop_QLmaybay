public class DaiLy extends Nguoi {
    private static final long serialVersionUID = 1L;
    private String loaiDaiLy;
    private Double duNo;
    public DaiLy() {
        super(); 
        this.loaiDaiLy = "";
        this.duNo = 0.0;
    }
    public DaiLy(boolean status, String maSo, String hoTen, String soDienThoai, String loaiDaiLy, Double duNo) {
        super(status, maSo, hoTen, soDienThoai);
        this.loaiDaiLy = loaiDaiLy;
        this.duNo = duNo;
    }
    public DaiLy(String maSo, String hoTen, String soDienThoai,
                 String loaiDaiLy, double duNo) {
        this(true, maSo, hoTen, soDienThoai, loaiDaiLy, duNo);
    }
      /**
     *  soTien > 0 : đại lý lấy vé / phát sinh nợ  -> duNo tăng
     *  soTien < 0 : đại lý thanh toán công nợ     -> duNo giảm (không được trả vượt dư nợ)
     */
    public void capNhatCongNo(boolean status,Double soTien) {
        if (!status)
            return;
        if (soTien > 0) {
            duNo += soTien;
        } else if (soTien < 0) {
            if (duNo + soTien >= 0) {
                duNo += soTien;
            } else {
                System.out.println("So tien thanh toan vuot du no. Khong the cap nhat cong no.");
            }
        }
    }
    @Override
    public void hienThiThongTin() {
        System.out.println(" | Trang thai: " + (status ? "Dang hoat dong" : "Ngung hoat dong")
                + " |[Dai Ly] Ma so: " + maSo + " | Ho ten: " + hoTen
                + " | SDT: " + soDienThoai + " | Loai dai ly: " + loaiDaiLy + " | Du no: " + duNo);
    }
    public String getLoaiDaiLy() {
        return loaiDaiLy;
    }
    public void setLoaiDaiLy(String loaiDaiLy) {
        this.loaiDaiLy = loaiDaiLy;
    }
    public Double getDuNo() {
        return duNo;
    }
    public void setDuNo(Double duNo) {
        this.duNo = duNo;
    }
}
