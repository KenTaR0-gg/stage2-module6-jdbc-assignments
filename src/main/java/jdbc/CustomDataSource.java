package jdbc;

import javax.sql.DataSource;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties")) {
                if (is != null) {
                    props.load(is);
                }
            } catch (Exception e) {
                // Игнорируем, если файла нет
            }

            String driver = getProp(props, "postgres.driver", "jdbc.driver");
            String url = getProp(props, "postgres.url", "jdbc.url");
            String user = getProp(props, "postgres.name", "jdbc.user", "username");
            String password = getProp(props, "postgres.password", "jdbc.password");

            if (password == null) password = "";

            if (driver == null || url == null) {
                driver = "org.postgresql.Driver";
                url = "jdbc:postgresql://localhost:5432/myfirstdb";
                user = "postgres";
                password = "123";
            }

            instance = new CustomDataSource(driver, url, password, user);
        }
        return instance;
    }

    private static String getProp(Properties props, String... keys) {
        for (String key : keys) {
            String val = props.getProperty(key);
            if (val != null && !val.trim().isEmpty()) {
                return val.trim();
            }
        }
        return null;
    }

    // ИСПРАВЛЕНИЕ: Теперь метод возвращает реальное подключение, а не null
    @Override
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Драйвер не найден: " + this.driver, e);
        }
        return DriverManager.getConnection(this.url, this.name, this.password);
    }

    // ИСПРАВЛЕНИЕ: То же самое для перегруженного метода
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Драйвер не найден: " + this.driver, e);
        }
        return DriverManager.getConnection(this.url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException { return null; }
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {}
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {}
    @Override
    public int getLoginTimeout() throws SQLException { return 0; }
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException { return null; }
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
}