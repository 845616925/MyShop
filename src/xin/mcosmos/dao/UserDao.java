package xin.mcosmos.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import xin.mcosmos.domain.User;
import xin.mcosmos.utils.DataSourceUtils;

import java.sql.SQLException;

/**
 * uid
 * username
 * password
 * name
 * email
 * telephone
 * birthday
 * sex
 * state
 * code
 */
public class UserDao {
    public User login(String username, String password) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from user where username=? and password=?";
        return runner.query(sql, new BeanHandler<User>(User.class), username, password);

    }

    public int register(User user) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?)";
        int update = runner.update(sql,
                user.getUid(),
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                user.getTelephone(),
                user.getBirthday(),
                user.getSex(),
                user.getState(),
                user.getCode()
        );
        return update;
    }

    public int changeState(String pin) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update user set state=1 where code=?";
        return runner.update(sql, pin);
    }

    public Long checkUsername(String username) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from user where username=?";
        Long query = runner.query(sql, new ScalarHandler<>(), username);
        return query;
    }
}
