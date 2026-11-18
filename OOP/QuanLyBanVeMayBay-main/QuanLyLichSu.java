/** Lưu nhật ký thao tác bằng mảng đối tượng, theo yêu cầu của đồ án. */
public class QuanLyLichSu {
    private LichSuThaoTac[] danhSach = new LichSuThaoTac[100];
    private int soLuong;

    public void ghi(LichSuThaoTac muc) {
        if (muc == null) return;
        if (soLuong == danhSach.length) {
            LichSuThaoTac[] moi = new LichSuThaoTac[danhSach.length * 2];
            System.arraycopy(danhSach, 0, moi, 0, soLuong);
            danhSach = moi;
        }
        danhSach[soLuong++] = muc;
    }

    public void nap(LichSuThaoTac[] cacMuc) {
        if (cacMuc == null) return;
        for (LichSuThaoTac muc : cacMuc) ghi(muc);
    }

    public LichSuThaoTac[] getDanhSach() {
        LichSuThaoTac[] ketQua = new LichSuThaoTac[soLuong];
        System.arraycopy(danhSach, 0, ketQua, 0, soLuong);
        return ketQua;
    }

    public int getSoLuong() { return soLuong; }
}
