package github.racolin.busmap.entities;

import android.content.ContentValues;

import java.util.Arrays;
import java.util.Date;

import github.racolin.busmap.Support;

//Lớp user gồm tất cả các thông tin của người dùng
public class User {
    private final String email;
    private final String password;
    private String name;
    private String phone;
    private boolean gender;
    private Date date_of_birth;
    private byte[] image;

    public User(String email, String password, String name, String phone,
                boolean gender, Date date_of_birth, byte[] image) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getEmail() {
        return email;
    }

    public ContentValues getContentValuesForInsert() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("gender", gender ? 0 : 1);
        contentValues.put("date_of_birth", Support.dateToString(date_of_birth, "dd/MM/yyyy"));
        contentValues.put("image", image);
        return contentValues;
    }

    public ContentValues getContentValuesForUpdate() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("gender", gender ? 0 : 1);
        contentValues.put("date_of_birth", Support.dateToString(date_of_birth, "dd/MM/yyyy"));
        contentValues.put("image", image);
        return contentValues;
    }

    @Override
    public String toString() {
        return this == null ? "null" : "User{" +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", date_of_birth=" + date_of_birth +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
