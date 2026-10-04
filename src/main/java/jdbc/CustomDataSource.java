package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {

    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            Properties props = new Properties();
            try (InputStream is = CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties")) {
                props.load(is);
                // Обратите внимание на порядок параметров в вашем конструкторе: driver, url, password, name
                instance = new CustomDataSource(
                        props.getProperty("jdbc.driver"),
                        props.getProperty("jdbc.url"),
                        props.getProperty("jdbc.password"),
                        props.getProperty("jdbc.user")
                );
            } catch (Exception e) {
                // Если файла нет (например, при локальном запуске), оставляем ваш вариант как запасной
                instance = new CustomDataSource("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/myfirstdb", "123", "postgres");
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
