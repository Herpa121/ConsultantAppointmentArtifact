package appointment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPDataSource {

	private static HikariDataSource hDataSource;
	
	// Static configuration settings
	static {
		HikariConfig hConfig = new HikariConfig();
		hConfig.setJdbcUrl("jdbc:mysql://localhost:3306/AppointmentSystemDBEnum");
		hConfig.setUsername("root");
		hConfig.setPassword("Tolkien@99");
		hConfig.setMaximumPoolSize(10); // Pool size of 10
		hConfig.setMinimumIdle(2); // Minimum idle connections at 2
		hConfig.setConnectionTimeout(6000); // Connection timeout 60 sec
		hConfig.setIdleTimeout(300000); // Idle timeout 5 min
		hConfig.setMaxLifetime(1200000); // Connection max lifetime 20 min

		// Prepared Statement Cache optimizations
		hConfig.addDataSourceProperty("cachePrepStmts", "true");
		hConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		hDataSource = new HikariDataSource(hConfig);
	}
	
	// Get connection
	public static Connection getConnection() throws SQLException {
		return hDataSource.getConnection();
	}
	
}
