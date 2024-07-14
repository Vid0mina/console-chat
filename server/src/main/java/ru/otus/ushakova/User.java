package ru.otus.ushakova;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    private final String login;
    private int id;
    private String password;
    private String nickname;
    private boolean banFlag;
    private List<Role> roles = new ArrayList<>();

    public User(int id, String login, String password, String nickname, boolean banFlag) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.banFlag = false;
    }

    public User(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.banFlag = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public boolean getBanFlag() {
        return banFlag;
    }

    public boolean isBanFlag() {
        return banFlag;
    }

    public void setBanFlag(boolean banFlag) {
        this.banFlag = banFlag;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return id + "," + login + "," + password + "," + nickname + "," + banFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

}
