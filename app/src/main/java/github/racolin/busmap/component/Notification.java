package github.racolin.busmap.component;

import java.util.Date;

//Notification chứa các trường là title, description, time, image, content
public class Notification {
    private String title, description;
    private Date time;
    private int image;
    private int content;

    public Notification() {

    }

    public Notification(String title, String description, Date time, int image, int content) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.image = image;
        this.content = content;
    }

    public int getContent() {
        return content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
