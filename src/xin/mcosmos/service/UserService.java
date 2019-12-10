package xin.mcosmos.service;

import xin.mcosmos.dao.ProductDao;
import xin.mcosmos.dao.UserDao;
import xin.mcosmos.domain.User;

import java.sql.SQLException;

public class UserService {
    public boolean register(User user) throws SQLException {

        UserDao dao = new UserDao();
        int row = dao.register(user);

        return row > 0 ? true : false;
    }

    public boolean changeState(String pin) throws SQLException {
        UserDao dao = new UserDao();
        int row = dao.changeState(pin);
        return row > 0 ? true : false;
    }

    public boolean checkUsername(String username) {
        UserDao dao = new UserDao();
        Long isExist = 0L;
        try {
            isExist = dao.checkUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExist > 0 ? true : false;
    }

    public User login(String username, String password) {
        UserDao dao = new UserDao();
        User user = null;
        try {
            user = dao.login(username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
