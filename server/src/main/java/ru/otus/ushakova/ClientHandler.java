package ru.otus.ushakova;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    @Getter
    private String nickname;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            boolean isDisconnected = false;
            try {
                System.out.println("Подключился новый клиент.");
                if (tryToAuthenticate()) {
                    communicate();

                }
            } catch (IOException e) {
                isDisconnected = true;
                e.printStackTrace();
            } finally {
                if (!isDisconnected) disconnect();
            }
        }).start();
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nickname);
    }

    public void communicate() throws IOException {
        while (true) {
            String msg = in.readUTF();
            String[] part = msg.split("\\s");
            String currentLogin = server.getAuthenticationService().getLoginByNickname(nickname);
            List<String> currentRoles = server.getAuthenticationService().getRolesByLogin(currentLogin);
            if (msg.startsWith("/")) {
                switch (part[0]) {
                    case "/exit":
                        break;
                    case "/w":
                        part = msg.split("\\s", 3);
                        String recipient = part[1];
                        String letter = part[2];
                        System.out.println("\nКому: " + recipient + "\nТекст сообщения: " + letter + ".");
                        server.sendPrivateMsg(recipient, nickname, letter);
                        break;
                    case "/cr":
                        for (String r : currentRoles) {
                            if (r.equals("ADMIN")) {
                                server.getAuthenticationService().setRole(currentLogin, r, part[1], part[2]);
                            } else {
                                System.out.println("Нет нужной роли для выполнения операции.");
                            }
                        }
                        break;
                    case "/kick":
                        for (String r : currentRoles) {
                            if (r.equals("ADMIN")) {
                                if (server.getAuthenticationService().setBanFlag(r, part[1], true)) {
                                    server.kick(part[1]);
                                }
                            } else {
                                System.out.println("Нет нужной роли для выполнения операции.");
                            }
                        }
                        break;
                }
                continue;
            }
            server.broadcastMessage(nickname + ": " + msg);
        }
    }

    private boolean tryToAuthenticate() throws IOException {
        while (true) {
            String msg = in.readUTF();
            if (msg.startsWith("/auth ")) {
                String[] tokens = msg.split(" ");
                if (tokens.length != 4) {
                    sendMessage("Некорректный формат запроса.");
                    continue;
                }
                String login = tokens[1];
                String password = tokens[2];
                String nickname = server.getAuthenticationService().getNicknameByLoginAndPassword(login, password);
                if (nickname == null) {
                    sendMessage("Неправильный логин/пароль или такого пользователя не существет.");
                    continue;
                }
                if (server.isNicknameBusy(nickname)) {
                    sendMessage("Указанная учетная запись уже занята. Попробуйте зайти позднее.");
                    continue;
                }
                this.nickname = nickname;
                server.subscribe(this);
                sendMessage(nickname + ", добро пожаловать в чат!");
                return true;
            } else if (msg.startsWith("/register ")) {
                String[] tokens = msg.split(" ");
                if (tokens.length != 4) {
                    sendMessage("Некорректный формат запроса.");
                    continue;
                }
                String login = tokens[1];
                String password = tokens[2];
                String nickname = tokens[3];
                if (server.getAuthenticationService().isLoginAlreadyExist(login)) {
                    sendMessage("Указанный логин уже занят.");
                    continue;
                }
                if (server.getAuthenticationService().isNicknameAlreadyExist(nickname)) {
                    sendMessage("Указанный никнейм уже занят.");
                    continue;
                }
                if (!server.getAuthenticationService().register(login, password, nickname)) {
                    sendMessage("Не удалось пройти регистрацию.");
                    continue;
                }
                this.nickname = nickname;
                server.subscribe(this);
                sendMessage("Вы успешно зарегистрировались! " + nickname + ", добро пожаловать в чат!");
                return true;
            } else if (msg.equals("/exit")) {
                return false;
            } else {
                sendMessage("Вам необходимо авторизоваться.");
            }
        }
    }

}