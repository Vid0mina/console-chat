package ru.otus.ushakova;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryAuthenticationService implements AuthenticationService {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASS = "admin123";
    private final List<User> users;
    private final List<String> roles = new ArrayList<>(List.of("USER", "ADMIN"));

    public InMemoryAuthenticationService() {
        this.users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            this.users.add(new User("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public void setRole(String currentLogin, String currentRole, String login, String role) {
        if (currentRole.equals("ADMIN")) {
            for (String r : roles) {
                if (roles.contains(role)) {
                    for (User u : users) {
                        if (u.getLogin().equals(login)) {
                            u.setRole(role);
                            System.out.println("user = " + u);
                        }
                    }
                } else System.out.println("Такой роли нет в списке.");
            }
        } else {
            System.out.println("Данная операция может выполняться только пользователям с ролью ADMIN.");
        }
    }

    @Override
    public boolean setBanFlag(String currentRole, String kickLogin, boolean banFlag) {
        if (currentRole.equals("ADMIN")) {
            for (User u : users) {
                if (u.login.equals(kickLogin)) {
                    u.setBanFlag(true);
                    System.out.println(u);
                }
            }
            return true;
        } else {
            System.out.println("Данная операция может выполняться только пользователям с ролью ADMIN.");
            return false;
        }
    }

    @Override
    public boolean getBanFlagByNickname(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                return u.isBanFlag();
            }
        }
        return false;
    }

    @Override
    public String getLoginByNickname(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                return u.login;
            }
        }
        return "Такого логина нет.";
    }

    @Override
    public String getRoleByLogin(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return u.getRole();
            }
        }
        return "Не удалось получить роль.";
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean register(String login, String password, String nickname) {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        users.add(new User(login, password, nickname));
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    private class User {

        private final String login;
        private final String password;
        private final String nickname;
        private String role;
        private boolean banFlag;

        public User(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
            if (login.equals(ADMIN_LOGIN) && password.equals(ADMIN_PASS)) this.role = "ADMIN";
            else this.role = "USER";
            this.banFlag = false;
        }

        public boolean isBanFlag() {
            return banFlag;
        }

        public void setBanFlag(boolean banFlag) {
            this.banFlag = banFlag;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getLogin() {
            return login;
        }

        @Override
        public String toString() {
            return "User{" +
                    "login='" + login + '\'' +
                    ", password='" + password + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", role='" + role + '\'' +
                    ", banFlag=" + banFlag +
                    '}';
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

}
