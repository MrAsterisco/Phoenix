package phoenix.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contiene tutte le funzionalità legate al Database in SQLite su cui si appoggia l'applicativo.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class DbLayer {

	/**
	 * La posizione dove registrare il Database.
	 * 
	 * @discussion A causa di alcune incompatibilità con Eclipse su Mac, ho impostato come posizione del Database la cartella /tmp.
	 * Avrei preferito utilizzare un Db già incluso nelle risorse del progetto, ma ho avuto diversi problemi tecnici a far funzionare
	 * la copia custom dei file in Eclipse.
	 */
	private static String DatabasePath =  "/tmp/Phoenix.db";
	
	private Connection connection;
	
	public DbLayer() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + DatabasePath);
	}
	
	public ResultSet executeQuery(String query, Object... variables) throws SQLException {
		PreparedStatement statement = this.connection.prepareStatement(query);
		
		for (int i = 0; i < variables.length; i++) {
			statement.setObject(i+1, variables[i]);
		}
		
		ResultSet set = statement.executeQuery();
		
		return set;
	}
	
	public void executeUpdate(String query, Object... variables) throws SQLException {
		PreparedStatement statement = null;
		
		statement = this.connection.prepareStatement(query);
		
		for (int i = 0; i < variables.length; i++) {
			statement.setObject(i+1, variables[i]);
		}
		
		statement.executeUpdate();
	}
	
}
