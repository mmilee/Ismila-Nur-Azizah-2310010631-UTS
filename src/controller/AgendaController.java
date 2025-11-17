package controller;

import java.sql.SQLException;
import java.util.List;
import model.Agenda;
import model.AgendaDAO;

public class AgendaController {

    private AgendaDAO dao;

    public AgendaController() {
        dao = new AgendaDAO();
    }

    public List<Agenda> getAllAgenda() throws SQLException {
        return dao.getAllAgenda();
    }

    public void addAgenda(String nama, String tanggal, String deskripsi, String status) throws SQLException {
        Agenda a = new Agenda(0, nama, tanggal, deskripsi, status);
        dao.addAgenda(a);
    }

    public void updateAgenda(int id, String nama, String tanggal, String deskripsi, String status) throws SQLException {
        Agenda a = new Agenda(id, nama, tanggal, deskripsi, status);
        dao.updateAgenda(a);
    }

    public void deleteAgenda(int id) throws SQLException {
        dao.deleteAgenda(id);
    }

    public List<Agenda> searchAgenda(String keyword) throws SQLException {
        return dao.searchAgenda(keyword);
    }

}
