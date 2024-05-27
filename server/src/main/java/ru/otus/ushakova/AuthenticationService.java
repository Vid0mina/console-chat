package ru.otus.ushakova;

public interface AuthenticationService {

    String getNicknameByLoginAndPassword(String login, String password);

    boolean register(String login, String password, String nickname);

    boolean isLoginAlreadyExist(String login);

    boolean isNicknameAlreadyExist(String nickname);

    void setRole(String currentLogin, String currentRole, String login, String role);

    boolean setBanFlag(String currentRole, String kickLogin, boolean banFlag);

    String getLoginByNickname(String nickname);

    String getRoleByLogin(String login);

    boolean getBanFlagByNickname(String nickname);

}