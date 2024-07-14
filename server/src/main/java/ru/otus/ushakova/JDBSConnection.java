package ru.otus.ushakova;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBSConnection {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/console-chat";
    private static final String DB_USER = "postgres";
    private static final String DB_PSWD = "AdminEdu";

    private static final String U_Q_BY_LOGIN = "SELECT * FROM users where login=?";
    private static final String ROLES_Q = "select * from roles where role=?";
    private static final String U_Q_BY_NICKNAME = "SELECT * FROM users where nickname=?";
    private static final String U_Q_AMOUNT = "SELECT count(*) FROM users where nickname=?";
    private static final String U_Q_USERS = "SELECT * FROM users";
    private static final String U_UPD_USER = """
            UPDATE users
            SET banflag =?
            WHERE login=?""";
    private static final String U_Q_USER_ROLE = """
            select r.role
            from roles r join userrole ur
            on r.id = ur.role_id
            where ur.user_id=?;
            """;
    private static final String ROLE_UPD = """
            UPDATE userrole SET role_id=? where user_id =?;""";
    private static final String ROLE_INS = """
            INSERT INTO userrole (role_id,user_id)
            VALUES
              (?,?) ;""";
    private static final String USERS_INS = """
            INSERT INTO Users (login,password,nickname)
            VALUES
              (?,?,?) returning id;""";
    private static final String USERS_Q_NICKNAME = "select * from users where login=? and password=? ";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSWD);
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLoginByNick(String nickname) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(U_Q_BY_NICKNAME)) {
            String login = null;
            ps.setString(1, nickname);
            try (ResultSet usersResultSet = ps.executeQuery()) {
                while (usersResultSet.next()) {
                    login = usersResultSet.getString("login");
                }
            }
            return login;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNicknameByLoginAndPswd(String login, String password) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(USERS_Q_NICKNAME)) {
            String nickname = null;
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet usersResultSet = ps.executeQuery()) {
                while (usersResultSet.next()) {
                    nickname = usersResultSet.getString("nickname");
                }
            }
            return nickname;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer getRoleId(String role, List<User> users) {
        Connection connection = getConnection();
        Role r = null;
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(ROLES_Q)) {
            for (User user : users) {
                ps.setString(1, role);
                try (ResultSet usersResultSet = ps.executeQuery()) {
                    while (usersResultSet.next()) {
                        int id = usersResultSet.getInt("id");
                        String roleRes = usersResultSet.getString("role");
                        r = new Role(id, roleRes);
                        roles.add(r);
                    }
                    user.setRoles(roles);
                }
            }
            return r.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getBanFlag(String nickname) {
        Connection connection = getConnection();
        boolean banFlag = false;
        try (PreparedStatement ps = connection.prepareStatement(U_Q_BY_NICKNAME)) {
            ps.setString(1, nickname);
            try (ResultSet usersResultSet = ps.executeQuery()) {
                while (usersResultSet.next()) {
                    banFlag = usersResultSet.getBoolean("banflag");
                }
            }
            return banFlag;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> returnRoleByLogin(String login) {
        Connection connection = getConnection();
        List<String> roles = new ArrayList<>();
        Role r = null;
        int user_id = getUserId(login);
        try (PreparedStatement ps = connection.prepareStatement(U_Q_USER_ROLE)) {
            String roleRes = null;
            ps.setInt(1, user_id);
            try (ResultSet usersResultSet = ps.executeQuery()) {
                while (usersResultSet.next()) {
                    roleRes = usersResultSet.getString("role");
                    roles.add(roleRes);
                }
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getUserId(String login) {
        Connection connection = getConnection();
        int id = 0;
        try (PreparedStatement ps = connection.prepareStatement(U_Q_BY_LOGIN)) {
            ps.setString(1, login);
            try (ResultSet usersResultSet = ps.executeQuery()) {
                while (usersResultSet.next()) {
                    id = usersResultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static void updateRole(int user_id, int role_id) {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            try (PreparedStatement ps = connection.prepareStatement(ROLE_UPD)) {
                ps.setInt(1, role_id);
                ps.setInt(2, user_id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRole(int user_id, int role_id) {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            try (PreparedStatement ps = connection.prepareStatement(ROLE_INS)) {
                ps.setInt(1, user_id);
                ps.setInt(2, role_id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int insertUser(String login, String password, String nickname) {
        List<User> users = new ArrayList<>();
        Connection connection = getConnection();
        int id = 0;
        try (Statement statement = connection.createStatement()) {
            try (PreparedStatement ps = connection.prepareStatement(USERS_INS)) {
                ps.setString(1, login);
                ps.setString(2, password);
                ps.setString(3, nickname);
                ps.execute();
                ResultSet userEntity = ps.getResultSet();
                userEntity.next();
                id = userEntity.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateRole(id, 2);
        return id;
    }

    public static void updateBanflag(String login, boolean banflag) {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            try (PreparedStatement ps = connection.prepareStatement(U_UPD_USER)) {
                ps.setBoolean(1, banflag);
                ps.setString(2, login);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List returnUsersList() {
        List<User> users = new ArrayList<>();
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet usersResultSet = statement.executeQuery(U_Q_USERS)) {
                while (usersResultSet.next()) {
                    int id = usersResultSet.getInt("id");
                    String login = usersResultSet.getString("login");
                    String password = usersResultSet.getString("password");
                    String nickname = usersResultSet.getString("nickname");
                    boolean banFlag = usersResultSet.getBoolean("banflag");
                    User user = new User(id, login, password, nickname, banFlag);
                    users.add(user);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public static int getNick(String nickname) {
        Connection connection = getConnection();
        int amount = 0;
        try (PreparedStatement ps = connection.prepareStatement(U_Q_AMOUNT)) {
            ps.setString(1, nickname);
            try (ResultSet usersResultSet = ps.executeQuery()) {
                while (usersResultSet.next()) {
                    amount = usersResultSet.getInt("count");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return amount;
    }

}