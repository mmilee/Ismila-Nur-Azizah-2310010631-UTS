/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import controller.AgendaController;
import java.io.File;
import model.Agenda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import service.ExportService;
import service.ImportService;

public class PengelolaanAgendaFrame extends javax.swing.JFrame {

    private AgendaController controller;
    private DefaultTableModel model;
    private ImportService importService;
    private ExportService exportService;

    
    public PengelolaanAgendaFrame() {
        initComponents();
        
        controller = new AgendaController();
        model = new DefaultTableModel(new String[]{
        "No", "ID", "Nama Agenda", "Tanggal", "Deskripsi", "Status"
        }, 0);
        
        importService = new ImportService();
        exportService = new ExportService();
        

        jTable1.setModel(model);

        // SEMBUNYIKAN KOLOM ID
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);
        jTable1.getColumnModel().getColumn(1).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(1).setWidth(0);


        jTable1.setModel(model);

        buttonGroup1.add(cbBelum);
        buttonGroup1.add(cbSelesai);
        cbBelum.setSelected(true);

        loadAgenda();
        
        // ============= EVENT EXPORT =============
        btnExport.addActionListener(e -> handleExport());

        // ============= EVENT IMPORT =============
        btnImport.addActionListener(e -> handleImport());

    }
    
    // ==========================
    // LOAD DATA KE TABEL
    // ==========================
    private void loadAgenda() {
        try {
            model.setRowCount(0);
            List<Agenda> list = controller.getAllAgenda();
            int no = 1;

            for (Agenda a : list) {
                model.addRow(new Object[]{
                        no++,              // Kolom No
                        a.getId(),         // Kolom ID (hidden)
                        a.getNamaAgenda(),
                        a.getTanggal(),
                        a.getDeskripsi(),
                        a.getStatus()
                });
            }

        } catch (SQLException e) {
            showError("Gagal memuat data: " + e.getMessage());
        }
    }


    // ==========================
    // TAMBAH DATA
    // ==========================
    private void addAgenda() {

        String nama = txtNamaAgenda.getText().trim();
        String deskripsi = jTextArea1.getText().trim();
        String status = cbBelum.isSelected() ? "Belum" : "Selesai";

        // Format tanggal
        Date date = jDateTanggalAgenda.getDate();
        if (date == null) {
            showError("Tanggal harus diisi!");
            return;
        }

        String tanggal = new SimpleDateFormat("yyyy-MM-dd").format(date);

        try {
            controller.addAgenda(nama, tanggal, deskripsi, status);
            loadAgenda();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Agenda berhasil ditambahkan!");

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ==========================
    // EDIT DATA
    // ==========================
    private void editAgenda() {
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            showError("Pilih data yang ingin diubah!");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(selectedRow, 1).toString());
        String nama = txtNamaAgenda.getText().trim();
        String deskripsi = jTextArea1.getText().trim();
        String status = cbBelum.isSelected() ? "Belum" : "Selesai";

        Date date = jDateTanggalAgenda.getDate();
        if (date == null) {
            showError("Tanggal harus diisi!");
            return;
        }
        String tanggal = new SimpleDateFormat("yyyy-MM-dd").format(date);

        try {
            controller.updateAgenda(id, nama, tanggal, deskripsi, status);
            loadAgenda();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Agenda berhasil diperbarui!");

        } catch (SQLException e) {
            showError("Gagal update: " + e.getMessage());
        }
    }

    // ==========================
    // HAPUS DATA
    // ==========================
    private void deleteAgenda() {
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            showError("Pilih data yang ingin dihapus!");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(selectedRow, 1).toString());

        try {
            controller.deleteAgenda(id);
            loadAgenda();
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Agenda berhasil dihapus!");
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ==========================
    // PENCARIAN
    // ==========================
    private void searchAgenda() {
        String keyword = txtPencarian.getText().trim();

        if (keyword.isEmpty()) {
            loadAgenda();
            return;
        }

        try {
            List<Agenda> list = controller.searchAgenda(keyword);
            model.setRowCount(0); // Kosongkan tabel dulu

            int no = 1;

            for (Agenda a : list) {
                model.addRow(new Object[]{
                        no++,              // Kolom No
                        a.getId(),         // Kolom ID (hidden)
                        a.getNamaAgenda(),
                        a.getTanggal(),
                        a.getDeskripsi(),
                        a.getStatus()
                });
            }

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // ==========================
    // TAMPILKAN DATA KE INPUT
    // ==========================
    private void populateInputFields(int row) {

        // Nama Agenda
        txtNamaAgenda.setText(model.getValueAt(row, 2).toString());

        // Deskripsi
        jTextArea1.setText(model.getValueAt(row, 4).toString());

        // Tanggal
        try {
            String tgl = model.getValueAt(row, 3).toString();
            Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
            jDateTanggalAgenda.setDate(parsed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Status
        String status = model.getValueAt(row, 5).toString();
        if (status.equals("Belum")) {
            cbBelum.setSelected(true);
        } else {
            cbSelesai.setSelected(true);
        }
    }


    // ==========================
    // CLEAR INPUT
    // ==========================
    private void clearInputFields() {
        txtNamaAgenda.setText("");
        jTextArea1.setText("");
        txtPencarian.setText("");
        jDateTanggalAgenda.setDate(null);
        cbBelum.setSelected(true);
    }

    // ==========================
    // ERROR POPUP
    // ==========================
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void handleExport() {
        try {
            String type = cmbExport.getSelectedItem().toString();

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan File " + type);

            if (type.equals("TXT")) chooser.setSelectedFile(new File("agenda.txt"));
            if (type.equals("JSON")) chooser.setSelectedFile(new File("agenda.json"));
            if (type.equals("CSV")) chooser.setSelectedFile(new File("agenda.csv"));
            if (type.equals("PDF")) chooser.setSelectedFile(new File("agenda.pdf"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                List<Agenda> data = controller.getAllAgenda();

                switch (type) {
                    case "TXT":
                        exportService.exportTXT(data, file);
                        break;
                    case "JSON":
                        exportService.exportJSON(data, file);
                        break;
                    case "CSV":
                        exportService.exportCSV(data, file);
                        break;
                    case "PDF":
                        exportService.exportPDF(data, file);
                        break;
                }

                JOptionPane.showMessageDialog(this, "Export berhasil!");
            }

        } catch (Exception ex) {
            showError("Gagal Export: " + ex.getMessage());
        }
    }

    private void handleImport() {
        try {
            String type = cmbImport.getSelectedItem().toString();

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Pilih File " + type);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                List<Agenda> imported = new ArrayList<>();

                switch (type) {
                    case "TXT":
                        imported = importService.importTXT(file);
                        break;
                    case "JSON":
                        imported = importService.importJSON(file);
                        break;
                    case "CSV":
                        imported = importService.importCSV(file);
                        break;
                }

                // Simpan ke database
                for (Agenda a : imported) {
                    controller.addAgenda(a.getNamaAgenda(), a.getTanggal(), a.getDeskripsi(), a.getStatus());
                }

                loadAgenda();
                JOptionPane.showMessageDialog(this, "Import berhasil!");
            }

        } catch (Exception ex) {
            showError("Gagal Import: " + ex.getMessage());
        }
    }


    // ==========================
    // EVENT HANDLER (Dipanggil dari tombol)
    // ==========================


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jDateTanggalAgenda = new com.toedter.calendar.JDateChooser();
        lblDeskripsi = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        cbBelum = new javax.swing.JCheckBox();
        cbSelesai = new javax.swing.JCheckBox();
        lblNamaAgenda = new javax.swing.JLabel();
        txtNamaAgenda = new javax.swing.JTextField();
        lblTanggalAgenda = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnHapus = new javax.swing.JButton();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtPencarian = new javax.swing.JTextField();
        lblPencarianAgenda = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbImport = new javax.swing.JComboBox<>();
        btnImport = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cmbExport = new javax.swing.JComboBox<>();
        btnExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Aplikasi Agenda Harian");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 153, 255)));

        lblDeskripsi.setText("Deskripsi");

        lblStatus.setText("Status");

        buttonGroup1.add(cbBelum);
        cbBelum.setSelected(true);
        cbBelum.setText("Belum");

        buttonGroup1.add(cbSelesai);
        cbSelesai.setText("Selesai");

        lblNamaAgenda.setText("Nama Agenda");

        lblTanggalAgenda.setText("Tanggal Agenda");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnBersihkan.setText("Bersihkan");
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDeskripsi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTanggalAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNamaAgenda, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jDateTanggalAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNamaAgenda)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cbBelum, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cbSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(btnTambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBersihkan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNamaAgenda)
                    .addComponent(txtNamaAgenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTanggalAgenda)
                    .addComponent(jDateTanggalAgenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDeskripsi)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(cbBelum)
                    .addComponent(cbSelesai))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnBersihkan)
                    .addComponent(btnHapus))
                .addGap(37, 37, 37))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));
        jPanel4.setForeground(new java.awt.Color(255, 102, 255));

        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        lblPencarianAgenda.setText("Pencarian Agenda");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblPencarianAgenda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPencarianAgenda))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));

        jLabel2.setText("Pilih import dari");

        cmbImport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TXT", "JSON", "CSV" }));

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(cmbImport, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImport))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 255)));

        jLabel3.setText("Pilih export ke");

        cmbExport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TXT", "JSON", "PDF", "CSV" }));

        btnExport.setText("Export");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbExport, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExport))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addAgenda();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editAgenda();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        clearInputFields();
    }//GEN-LAST:event_btnBersihkanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteAgenda();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        populateInputFields(row);
    }//GEN-LAST:event_jTable1MouseClicked

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
        searchAgenda();
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        if (cmbImport.getSelectedItem() == null) {
            showError("Pilih jenis file import!");
            return;
        }

        String jenis = cmbImport.getSelectedItem().toString();
        JFileChooser fc = new JFileChooser();

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fc.getSelectedFile();
        List<Agenda> imported = new ArrayList<>();

        try {
            // ======================
            // PILIH FORMAT IMPORT
            // ======================
            switch (jenis) {
                case "TXT": imported = importService.importTXT(file); break;
                case "JSON": imported = importService.importJSON(file); break;
                case "CSV": imported = importService.importCSV(file); break;
                default:
                    showError("Format tidak dikenali!");
                    return;
            }

            // ======================
            // DATA EXIST DALAM DATABASE
            // ======================
            List<Agenda> existing = controller.getAllAgenda();

            int success = 0;
            StringBuilder duplicateList = new StringBuilder();

            // ======================
            // PROSES IMPORT
            // ======================
            for (Agenda a : imported) {

                boolean isDuplicate = false;

                for (Agenda ex : existing) {

                    // CEK DUPLIKAT BERDASARKAN: Nama + Tanggal + Deskripsi + Status
                    if (a.getNamaAgenda().equalsIgnoreCase(ex.getNamaAgenda())
                        && a.getTanggal().equalsIgnoreCase(ex.getTanggal())
                        && a.getDeskripsi().equalsIgnoreCase(ex.getDeskripsi())
                        && a.getStatus().equalsIgnoreCase(ex.getStatus())) {

                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {
                    duplicateList.append(
                            "- Nama: " + a.getNamaAgenda()
                            + " | Tanggal: " + a.getTanggal()
                            + " | Status: " + a.getStatus() + "\n"
                    );
                } else {
                    controller.addAgenda(a.getNamaAgenda(), a.getTanggal(), a.getDeskripsi(), a.getStatus());
                    success++;
                }
            }

            loadAgenda();

            // ======================
            // TAMPILKAN POPUP DUPLIKAT
            // ======================
            if (duplicateList.length() > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Beberapa data tidak di-import karena sudah ada:\n\n" + duplicateList.toString(),
                        "Duplikasi Ditemukan",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            // ======================
            // POPUP HASIL IMPORT
            // ======================
            JOptionPane.showMessageDialog(
                    this,
                    "Import selesai!\nBerhasil ditambahkan: " + success + " data.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }//GEN-LAST:event_btnImportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanAgendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanAgendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanAgendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanAgendaFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanAgendaFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbBelum;
    private javax.swing.JCheckBox cbSelesai;
    private javax.swing.JComboBox<String> cmbExport;
    private javax.swing.JComboBox<String> cmbImport;
    private com.toedter.calendar.JDateChooser jDateTanggalAgenda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblDeskripsi;
    private javax.swing.JLabel lblNamaAgenda;
    private javax.swing.JLabel lblPencarianAgenda;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTanggalAgenda;
    private javax.swing.JTextField txtNamaAgenda;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables

    private void loadTabel() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
