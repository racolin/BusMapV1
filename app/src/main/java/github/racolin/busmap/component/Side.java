package github.racolin.busmap.component;

//Side thì chỉ gồm tên và image là cờ của quốc gia
public class Side {
    private String name;
    private int image;

    public Side() {

    }

    public Side(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
