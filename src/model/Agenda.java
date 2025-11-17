package model;

public class Agenda {
    private int id;
    private String namaAgenda;
    private String tanggal;
    private String deskripsi;
    private String status;

    public Agenda(int id, String namaAgenda, String tanggal, String deskripsi, String status) {
        this.id = id;
        this.namaAgenda = namaAgenda;
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaAgenda() {
        return namaAgenda;
    }

    public void setNamaAgenda(String namaAgenda) {
        this.namaAgenda = namaAgenda;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
