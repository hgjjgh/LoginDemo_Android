package idv.ron.logindemo_android;

import java.io.Serializable;

public class User implements Serializable {
    private String userName,password,name;
    private int id;

    public User(int id, String userName, String password, String name) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
