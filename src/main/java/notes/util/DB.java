package notes.util;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;

public class DB {
    public final static String name = "notes";
    private final static String url = "jdbc:sqlite:" + name + ".db";

    public static void testConnectiontoDB() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (\n"
                + "	date_and_time text,\n"
                + "	note_text text\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement create = conn.createStatement()) {
            create.execute(sql);
            System.out.println("Table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ResultSet queryDB(String query) throws SQLException {
        CachedRowSetImpl table;
        ResultSet results = null;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stat = conn.createStatement()) {
            System.out.println("Trying to: " + query);
            results = stat.executeQuery(query);
            table = new CachedRowSetImpl();
            table.populate(results);
        } catch (SQLException e) {
            System.out.println("Problem at executeDB : " + e);
            throw e;
        }
        return table;
    }

    public static void updateDB(String query) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stat = conn.createStatement()) {
            System.out.println("Trying to: " + query);
            stat.executeUpdate(query);
            //conn.commit();
        } catch (SQLException e) {
            System.out.println("Problem at updateDB : " + e);
            throw e;
        }
    }
}
