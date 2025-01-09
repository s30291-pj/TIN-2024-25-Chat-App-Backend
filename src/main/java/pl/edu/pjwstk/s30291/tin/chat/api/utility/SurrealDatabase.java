package pl.edu.pjwstk.s30291.tin.chat.api.utility;

import java.util.List;

import com.surrealdb.connection.SurrealConnection;
import com.surrealdb.connection.SurrealWebSocketConnection;
import com.surrealdb.driver.SyncSurrealDriver;

import lombok.Getter;

public class SurrealDatabase {
	@Getter private static SurrealDatabase instance;
	
	private SurrealConnection connection;
	private SyncSurrealDriver driver;
			
	private SurrealDatabase(SurrealConnection connection, SyncSurrealDriver driver) {
		this.driver = driver;
	}
	
	public static void connect(String server, String username, String password) {
		SurrealConnection connection = new SurrealWebSocketConnection(server, 8000, false);
        connection.connect(30);

        SyncSurrealDriver driver = new SyncSurrealDriver(connection);

        driver.signIn(username, password);
        driver.use("tin", "chat");
        
        SurrealDatabase.instance = new SurrealDatabase(connection, driver);
	}
	
	public static void disconnect() {
		SurrealDatabase.getInstance().connection.disconnect();
	}
	
	public static SyncSurrealDriver getDriver() {
		return SurrealDatabase.getInstance().driver;
	}
	
	public static <T> T selectOne(String thing, String id, Class<T> clazz) {
		List<T> tmp = getDriver().select(thing + ":" + id, clazz);
		return (tmp.size() > 0) ? tmp.get(0) : null;
	}
	
	public static void updateOne(String thing, String id, Object object) {
		getDriver().update(thing + ":" + id, object);
	}
	
	public static void createOne(String thing, String id, Object object) {
		getDriver().create(thing + ":" + id, object);
	}
}
