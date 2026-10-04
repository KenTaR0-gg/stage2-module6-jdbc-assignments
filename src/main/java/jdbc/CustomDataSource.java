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
            // Безопасное чтение файла
            try (InputStream is = CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties")) {
                if (is != null) { // Защита от NullPointerException, если файла нет
                    props.load(is);
                }
            } catch (Exception e) {
                // Игнорируем ошибку, ниже применятся дефолтные значения
            }

            // Ищем ключи по всем возможным названиям (Autocode часто использует разные форматы)
            String driver = props.getProperty("jdbc.driver", props.getProperty("driver", "org.postgresql.Driver"));
            String url = props.getProperty("jdbc.url", props.getProperty("url", "jdbc:postgresql://localhost:5432/myfirstdb"));
            String user = props.getProperty("jdbc.user", props.getProperty("user", props.getProperty("username", "postgres")));
            String password = props.getProperty("jdbc.password", props.getProperty("password", "123"));

            instance = new CustomDataSource(driver, url, password, user);
        }
        return instance;
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