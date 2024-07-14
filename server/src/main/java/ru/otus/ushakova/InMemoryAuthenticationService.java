package ru.otus.ushakova;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.otus.ushakova.JDBSConnection.*;

public class InMemoryAuthenticationService implements AuthenticationService {

    List<User> users = returnUsersList();
    private final List<String> roles = new ArrayList<>(List.of("USER", "ADMIN"));

    public InMemoryAuthenticationService() throws SQLException {
        returnUsersList();
    }

    @Override
    public void setRole(String currentLogin, String currentRole, String login, String role) {
        List<String> currentRoleRes = returnRoleByLogin(currentLogin);
        List<String> roleRes = returnRoleByLogin(login);
        for (String cr : currentRoleRes) {
            if (cr.equals("ADMIN")) {
                if (roles.contains(role) && roleRes != null) {
                    for (String rr : roleRes) {
                        if (!rr.contains(role)) {
                            int userId = getUserId(login);
                            int roleId = getRoleId(role, users);
                            updateRole(userId, roleId);
                            System.out.println("Пользователю " + login + " задана новая роль " + role + ".");
                        } else {
                            System.out.println("У пользователя уже имеется такая роль.");
                        }
                    }
                } else System.out.println("Такой роли нет в списке.");
            } else {
                System.out.println("Данная операция может выполняться только пользователям с ролью ADMIN.");
            }
        }
    }

    @Override
    public boolean setBanFlag(String currentRole, String kickLogin, boolean banFlag) {
        if (currentRole.equals("ADMIN")) {
            updateBanflag(kickLogin, banFlag);
            return true;
        } else {
            System.out.println("Данная операция может выполняться только пользователям с ролью ADMIN.");
            return false;
        }
    }

    @Override
    public boolean getBanFlagByNickname(String nickname) {
        return getBanFlag(nickname);
    }

    @Override
    public String getLoginByNickname(String nickname) {
        String login = getLoginByNick(nickname);
        System.out.println(login);
        return login != null ? login : "Такого логина нет.";
    }

    @Override
    public List<String> getRolesByLogin(String login) {
        return returnRoleByLogin(login);
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return getNicknameByLoginAndPswd(login, password);
    }

    @Override
    public boolean register(String login, String password, String nickname) {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        insertUser(login, password, nickname);
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        return getUserId(login) != 0 ? true : false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        return (getNick(nickname)) > 0 ? true : false;
    }

}