package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;

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
    private static final String deleteUserSQL = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM MYUSERS WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM MYUSERS WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM MYUSERS";

    public Long createUser(User user) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    public User findUserById(Long userId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setFirstName(rs.getString("firstname"));
                    user.setLastName(rs.getString("lastname"));
                    user.setAge(rs.getInt("age"));
                    return user;
                }
            }
        }
        return null;
    }

    public User findUserByName(String userName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setFirstName(rs.getString("firstname"));
                    user.setLastName(rs.getString("lastname"));
                    user.setAge(rs.getInt("age"));
                    return user;
                }
            }
        }
        return null;
    }

    public List<User> findAllUser() throws SQLException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(findAllUserSQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setAge(rs.getInt("age"));
                users.add(user);
            }
        }
        return users;
    }

    public User updateUser(User user) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        }
        return user;
    }

    public void deleteUser(Long userId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(deleteUserSQL)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}