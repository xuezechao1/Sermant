package org.apache.dubbo.demo.repository;

import com.huawei.dubbo.common.domain.User;

import org.apache.dubbo.demo.utils.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author luanwenfei
 * @version 0.0.1
 * @since 2021-04-29
 */
public class UserDao {
    private static final String FILE_NAME = "C:\\Users\\26750\\Desktop\\webResultSet.xml";

    public User findByName(String name) throws SQLException {
        User user = new User();
        Connection connection = DbUtil.getConnection();
        if (connection == null) {
            return user;
        }
        String sql = "select * from user where name=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            user.setName(resultSet.getString("name"));
            user.setAge(resultSet.getString("age"));
            user.setDate(resultSet.getString("number"));
            resultSet.close();
        }
        return user;
    }

    public boolean addUser(User user) throws SQLException {
        Connection connection = DbUtil.getConnection();
        if (connection == null) {
            System.out.println("Connection is null");
            return false;
        }
        String sql = "insert into user(name,age,number) value(?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getAge());
        preparedStatement.setString(3, user.getDate());
        return preparedStatement.execute();
    }

    public boolean deleteUser(String name) throws SQLException {
        Connection connection = DbUtil.getConnection();
        if (connection == null) {
            System.out.println("Connection is null");
            return false;
        }
        String sql = "delete from user where name=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        return preparedStatement.execute();
    }
}
