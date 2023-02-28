package DAOs;

import java.sql.*;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import Utils.DBMSutils;
import Utils.druidUtils;

public class BasicDAO<T> {
	Connection connection = null;
	
	private QueryRunner qr = new QueryRunner();
	
	public int update(String sql, Object...parameters) {
		
		try {
			connection = druidUtils.getConnection();
			return qr.update(connection,sql,parameters);
			
		}catch(SQLException e) {throw new RuntimeException(e);
		}
	}
	
//	public int update(String sql, Object...parameters) {
//		
//		try {
//			connection = DBMSutils.getConnection();
//			return qr.update(connection,sql,parameters);
//			
//		}catch(SQLException e) {throw new RuntimeException(e);
//		}finally{DBMSutils.close(null,null,connection);}	
//	}
	
	public List<T> getMultiQuery(String sql, Class<T> clazz, Object...parameters){

		try {
			connection = druidUtils.getConnection();
			return qr.query(connection,sql,	new BeanListHandler<T>(clazz),parameters);
			

		}catch(SQLException e) {throw new RuntimeException(e);}
	}
	
//	public List<T> getMultiQuery(String sql, Class<T> clazz, Object...parameters){
//
//		try {
//			connection = DBMSutils.getConnection();
//			return qr.query(connection,sql,	new BeanListHandler<T>(clazz),parameters);
//			
//		}catch(SQLException e) {throw new RuntimeException(e);
//		}finally{DBMSutils.close(null,null,connection);}	
//	}
	
	public T getQuery(String sql, Class<T> clazz, Object...parameters){

		try {
			connection = druidUtils.getConnection();

			return qr.query(connection,sql,new BeanHandler<T>(clazz),parameters);
			
		}catch(SQLException e) {throw new RuntimeException(e);
		}
	}
	
//	public T getQuery(String sql, Class<T> clazz, Object...parameters){
//
//		try {
//			connection = DBMSutils.getConnection();
//
//			return qr.query(connection,sql,new BeanHandler<T>(clazz),parameters);
//			
//		}catch(SQLException e) {throw new RuntimeException(e);
//		}finally{DBMSutils.close(null,null,connection);}	
//	}
	
	public Object queryScalar(String sql, Object...parameters) {
		
		try {
			connection = druidUtils.getConnection();
			return qr.query(connection,sql,new ScalarHandler<>(),parameters);
			
		}catch(SQLException e) {throw new RuntimeException(e);
		}
	}
	
//	public Object queryScalar(String sql, Object...parameters) {
//		
//		try {
//			connection = DBMSutils.getConnection();
//			return qr.query(connection,sql,new ScalarHandler<>(),parameters);
//			
//		}catch(SQLException e) {throw new RuntimeException(e);
//		}finally{DBMSutils.close(null,null,connection);}
//	}


	public void commit(){
		try {
			connection = druidUtils.getConnection();
			qr.execute(connection,"commit");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void rollback(){
		try {
			connection = druidUtils.getConnection();
			qr.execute(connection,"rollback");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
//	public void commit(){
//		try {
//			connection = DBMSutils.getConnection();
//			qr.execute(connection,"commit");
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}finally{DBMSutils.close(null,null,connection);}
//	}
//	public void rollback(){
//		try {
//			connection = DBMSutils.getConnection();
//			qr.execute(connection,"rollback");
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}finally{DBMSutils.close(null,null,connection);}
//	}
}
