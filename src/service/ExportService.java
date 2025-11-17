package service;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import model.Agenda;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class ExportService {

    // =========================
    // EXPORT TXT
    // =========================
    public void exportTXT(List<Agenda> data, File file) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (Agenda a : data) {
            writer.write(a.getNamaAgenda() + "|" + a.getTanggal() + "|" + a.getDeskripsi() + "|" + a.getStatus());
            writer.newLine();
        }
        writer.close();
    }

    // =========================
    // EXPORT JSON
    // =========================
    public void exportJSON(List<Agenda> data, File file) throws Exception {
        JSONArray arr = new JSONArray();

        for (Agenda a : data) {
            JSONObject obj = new JSONObject();
            obj.put("nama", a.getNamaAgenda());
            obj.put("tanggal", a.getTanggal());
            obj.put("deskripsi", a.getDeskripsi());
            obj.put("status", a.getStatus());
            arr.add(obj);
        }

        FileWriter writer = new FileWriter(file);
        writer.write(arr.toJSONString());
        writer.close();
    }

    // =========================
    // EXPORT CSV
    // =========================
    public void exportCSV(List<Agenda> data, File file) throws Exception {
        FileWriter writer = new FileWriter(file);

        writer.write("Nama Agenda,Tanggal,Deskripsi,Status\n");

        for (Agenda a : data) {
            writer.write(
                    "\"" + a.getNamaAgenda() + "\"," +
                    "\"" + a.getTanggal() + "\"," +
                    "\"" + a.getDeskripsi() + "\"," +
                    "\"" + a.getStatus() + "\"\n"
            );
        }

        writer.close();
    }

    // =========================
    // EXPORT PDF
    // =========================
    public void exportPDF(List<Agenda> data, File file) throws Exception {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        doc.add(new Paragraph("Daftar Agenda Harian"));
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Nama");
        table.addCell("Tanggal");
        table.addCell("Deskripsi");
        table.addCell("Status");

        for (Agenda a : data) {
            table.addCell(a.getNamaAgenda());
            table.addCell(a.getTanggal());
            table.addCell(a.getDeskripsi());
            table.addCell(a.getStatus());
        }

        doc.add(table);
        doc.close();
    }
}
