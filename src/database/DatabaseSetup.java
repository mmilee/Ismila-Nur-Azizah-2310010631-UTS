// lib sqlite-jdbc: https://github.com/xerial/sqlite-jdbc/
package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void main(String[] args) {

        // Membuat tabel agenda sesuai komponen GUI
        String sql = "CREATE TABLE IF NOT EXISTS agenda_pribadi ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama_agenda TEXT NOT NULL, "
                + "tanggal TEXT NOT NULL, "
                + "deskripsi TEXT, "
                + "status TEXT NOT NULL"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Tabel 'agenda_pribadi' berhasil dibuat atau sudah tersedia.");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
