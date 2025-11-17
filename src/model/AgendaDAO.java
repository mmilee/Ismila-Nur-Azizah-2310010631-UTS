package model;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {

    // ================================
    // GET ALL — AMBIL SEMUA DATA
    // ================================
    public List<Agenda> getAllAgenda() throws SQLException {
        List<Agenda> list = new ArrayList<>();
        String sql = "SELECT * FROM agenda_pribadi ORDER BY tanggal ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Agenda a = new Agenda(
                        rs.getInt("id"),
                        rs.getString("nama_agenda"),
                        rs.getString("tanggal"),
                        rs.getString("deskripsi"),
                        rs.getString("status")
                );
                list.add(a);
            }
        }
        return list;
    }

    // ================================
    // INSERT — TAMBAH DATA
    // ================================
    public void addAgenda(Agenda a) throws SQLException {

        // Cek duplikat
        if (isDuplicate(a)) {
            System.out.println("Data duplikat ditemukan, skip insert: " + a.getNamaAgenda());
            return; // STOP, tidak usah insert
        }

        String sql = "INSERT INTO agenda_pribadi (nama_agenda, tanggal, deskripsi, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getNamaAgenda());
            pstmt.setString(2, a.getTanggal());
            pstmt.setString(3, a.getDeskripsi());
            pstmt.setString(4, a.getStatus());

            pstmt.executeUpdate();
        }
    }


    // ================================
    // UPDATE — EDIT DATA
    // ================================
    public void updateAgenda(Agenda a) throws SQLException {

        String sql = "UPDATE agenda_pribadi SET nama_agenda = ?, tanggal = ?, deskripsi = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getNamaAgenda());
            pstmt.setString(2, a.getTanggal());
            pstmt.setString(3, a.getDeskripsi());
            pstmt.setString(4, a.getStatus());
            pstmt.setInt(5, a.getId());

            pstmt.executeUpdate();
        }
    }

    // ================================
    // DELETE — HAPUS DATA
    // ================================
    public void deleteAgenda(int id) throws SQLException {

        String sql = "DELETE FROM agenda_pribadi WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ================================
    // SEARCH — PENCARIAN DATA
    // ================================
    public List<Agenda> searchAgenda(String keyword) throws SQLException {
        List<Agenda> list = new ArrayList<>();

        String sql = "SELECT * FROM agenda_pribadi WHERE nama_agenda LIKE ? OR status LIKE ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Agenda a = new Agenda(
                        rs.getInt("id"),
                        rs.getString("nama_agenda"),
                        rs.getString("tanggal"),
                        rs.getString("deskripsi"),
                        rs.getString("status")
                );
                list.add(a);
            }
        }

        return list;
    }
    
    public boolean isDuplicate(Agenda a) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agenda_pribadi "
                + "WHERE nama_agenda = ? AND tanggal = ? AND deskripsi = ? AND status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getNamaAgenda());
            pstmt.setString(2, a.getTanggal());
            pstmt.setString(3, a.getDeskripsi());
            pstmt.setString(4, a.getStatus());

            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0; // true jika ada duplikasi
        }
    }

}
