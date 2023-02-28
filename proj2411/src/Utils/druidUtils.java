package Utils;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class druidUtils {
	static DataSource ds;
	static Connection connection;
	
	
	static {
		try{
			Properties properties = new Properties();
			properties.load(new FileInputStream("src\\Config.properties"));
			ds = DruidDataSourceFactory.createDataSource(properties);
			connection = ds.getConnection();
			connection.setAutoCommit(false);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return connection;
	}


}
