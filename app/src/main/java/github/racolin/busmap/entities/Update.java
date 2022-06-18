package github.racolin.busmap.entities;

import java.util.Date;

//update được lấy giống như ở dưới sqlite
//update cho biết các phiên bản giữ liệu người dùng đã cập hay chưa
public class Update {
    private int id;
    private Date date;
    private boolean uploaded;

    public Update(int id, Date date, boolean uploaded) {
        this.id = id;
        this.date = date;
        this.uploaded = uploaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}
