package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    public SimpleJDBCRepository() {
        try {
            CustomDataSource dataSource = CustomDataSource.getInstance();
            CustomConnector connector = new CustomConnector();

            this.connection = connector.getConnection(
                    dataSource.getUrl(),
                    dataSource.getName(),
                    dataSource.getPassword()
            );
        } catch (Exception e) {
            System.out.println("Не удалось подключиться: " + e.getMessage());
        }
    }

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM MYUSERS WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM MYUSERS WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM MYUSERS";

    public Long createUser(String firstname, String lastname, int age) throws SQLException {

        ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, firstname);
        ps.setString(2, lastname);
        ps.setInt(3, age);

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        }
    }

    public User findUserById(Long userId) throws SQLException {

        ps = connection.prepareStatement(findUserByIdSQL);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("firstname"));
            user.setAge(rs.getInt("age"));
            user.setLastName(rs.getString("lastname"));
            return user;
        }
        return null;

    }

    public User findUserByName(String userName) throws SQLException {

        ps = connection.prepareStatement(findUserByNameSQL);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("firstname"));
            user.setAge(rs.getInt("age"));
            user.setLastName(rs.getString("lastname"));
            return user;
        }
        return null;


    }

    public List<User> findAllUser() throws SQLException {
        List<User> users = new ArrayList<>();
        ps = connection.prepareStatement(findAllUserSQL);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("firstname"));
            user.setAge(rs.getInt("age"));
            user.setLastName(rs.getString("lastname"));
            users.add(user);
        }
        return users;

    }

    public User updateUser(String firstname, String name, int age, Long id) throws SQLException {


        ps = connection.prepareStatement(updateUserSQL);
        ps.setString(1, firstname);
        ps.setString(2, name);
        ps.setInt(3, age);
        ps.setLong(4, id);
        ps.executeUpdate();
        return new User();

    }

    private void deleteUser(Long userId) throws SQLException {
        ps = connection.prepareStatement(deleteUser);
        ps.setLong(1, userId);
         ps.executeUpdate();
    }
}
