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
            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties")) {
                if (is == null) {
                    throw new RuntimeException("Файл app.properties не найден в папке resources!");
                }
                props.load(is);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка загрузки файла настроек: " + e.getMessage(), e);
            }

            // Ищем ключи по возможным вариантам без жесткой привязки к Postgres
            String driver = getProp(props, "jdbc.driver", "driver", "db.driver");
            String url = getProp(props, "jdbc.url", "url", "db.url");
            String user = getProp(props, "jdbc.user", "user", "username", "db.user");
            String password = getProp(props, "jdbc.password", "password", "db.password");
            if (password == null) password = "";

            if (driver == null || url == null) {
                throw new RuntimeException("В файле свойств не найдены ключи подключения. Доступные ключи: " + props.keySet());
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

    @Override
    public Connection getConnection() throws SQLException { return null; }
    @Override
    public Connection getConnection(String username, String password) throws SQLException { return null; }
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