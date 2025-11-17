package service;

import model.Agenda;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class ImportService {

    // =========================
    // IMPORT TXT
    // =========================
    public List<Agenda> importTXT(File file) throws Exception {
        List<Agenda> list = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] p = line.split("\\|");

            if (p.length == 4) {
                list.add(new Agenda(0, p[0], p[1], p[2], p[3]));
            }
        }
        br.close();
        return list;
    }

    // =========================
    // IMPORT JSON
    // =========================
    public List<Agenda> importJSON(File file) throws Exception {
        List<Agenda> list = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONArray arr = (JSONArray) parser.parse(new FileReader(file));

        for (Object obj : arr) {
            JSONObject o = (JSONObject) obj;

            list.add(new Agenda(
                    0,
                    (String) o.get("nama"),
                    (String) o.get("tanggal"),
                    (String) o.get("deskripsi"),
                    (String) o.get("status")
            ));
        }
        return list;
    }

    // =========================
    // IMPORT CSV
    // =========================
    public List<Agenda> importCSV(File file) throws Exception {
        List<Agenda> list = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");

            if (p.length == 4) {
                list.add(new Agenda(
                        0,
                        p[0].replace("\"",""),
                        p[1].replace("\"",""),
                        p[2].replace("\"",""),
                        p[3].replace("\"","")
                ));
            }
        }

        br.close();
        return list;
    }
}
