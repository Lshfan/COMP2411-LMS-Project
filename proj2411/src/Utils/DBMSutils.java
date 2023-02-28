package Utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DBMSutils {
	static Connection connection;
	
	
	static {
		try{
			Properties properties = new Properties();
			properties.load(new FileInputStream("src\\Config.properties"));
			String user = properties.getProperty("username");
			String password = properties.getProperty("password");
			String url = properties.getProperty("url");

			connection = DriverManager.getConnection(url,user,password);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException {
		connection.setAutoCommit(false);
		return connection;
	}

	public static void close(Connection con) {
		close(null,null,con);
	}
	public static void close(ResultSet rs, Statement sm, Connection con) {
		try {
			if(rs != null)rs.close();
			if(sm != null)sm.close();
			if(con != null)con.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
