/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage.AdminWindowPanels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mainpackage.ApplicationWindow;

/**
 *
 * @author cosmi
 */
public class MenusAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form menusAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public MenusAdminPanel(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
        initComponents();
        initFilter();

        currMenuTextField.setText(appWindow.getCurrentMenu());

        initActionListeners();

       if (appWindow.getCurrentMenu() == null || appWindow.getCurrentMenu().equals(""))  {
            currMenuTextField.setText("Nu este setat.");
        }

    }

    private void initActionListeners() {
        insertButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteBoxesButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        updateButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        currMenuButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());

    }

    private void initFilter() {
        tr = new TableRowSorter<>((DefaultTableModel) dataTable.getModel());
        dataTable.setRowSorter(tr);

        sortKeys = new ArrayList<>();
        for (int i = 0; i < dataTable.getColumnCount(); ++i) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.UNSORTED));
        }
    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String nr_meniu;
        String nume_meniu;
        String detalii_suplimentare_meniu;
        String data_crearii;

        switch (tmpEventButton.getText()) {

            case "Setare ca meniu curent":

                try {
                    nume_meniu = dataTable.getModel().getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1).toString();
                } catch (java.lang.IndexOutOfBoundsException ex2) {
                    JOptionPane.showMessageDialog(this, "Nu aveti selectat nici un meniu.");
                    break;
                }

                try {

                    try (Writer fileWriter = new FileWriter("res/Fisiere text/Meniul curent.txt", false)) {
                        fileWriter.write(nume_meniu);
                        this.appWindow.setCurrentMenu(nume_meniu);
                        currMenuTextField.setText(nume_meniu);
                        System.out.println("Noul meniu curent este: " + nume_meniu);
                        fileWriter.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }

                break;

            /////////////// INSERARE ///////////////
            case "Inserare":

                try {
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Meniuri");
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }

                if (numeMeniuTextField.getText().equals("") && detaliiSuplimentareTextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Casetele sunt goale.");
                    break;
                }

                tr.setSortKeys(sortKeys);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String currentDate = formatter.format(date);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_meniu = numeMeniuTextField.getText();
                detalii_suplimentare_meniu = detaliiSuplimentareTextField.getText();

                ////////////////////// Modificare baza de date //////////////////////
                try {
                    PreparedStatement prepSt = conn.prepareStatement("INSERT INTO Meniuri(nume_meniu, detalii_suplimentare_meniu) VALUES(?, ?)");
                    prepSt.setString(1, nume_meniu);
                    prepSt.setString(2, detalii_suplimentare_meniu);
                    prepSt.execute();

                    ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(nr_meniu) FROM Meniuri");
                    rs.next();
                    nr_meniu = rs.getString(1);
                    Object tfData[] = {Short.parseShort(nr_meniu), nume_meniu, detalii_suplimentare_meniu, currentDate};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Meniu inserat cu succes");
                } catch (SQLException ex) {

                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Meniu deja existent");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Meniurile trebuie sa contina cuvinte sau numere, primul cuvânt începând cu majusculă, opțional delimitate prin ‘-‘");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }

                }
                Refresh();
                break;

            /////////////// STERGEREA CASTETELOR TEXT FIELD ///////////////
            case "Sterge casetele":
                numeMeniuTextField.setText("");
                detaliiSuplimentareTextField.setText("");
                break;

            /////////////// MODIFICAREA IN BAZA DE DATE ///////////////
            case "Modificare":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nr_meniu = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                    nume_meniu = numeMeniuTextField.getText();
                    detalii_suplimentare_meniu = detaliiSuplimentareTextField.getText();
                    data_crearii = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3).toString();

                    Savepoint sp = null;

                    try {

                        conn.setAutoCommit(false);
                        sp = conn.setSavepoint("sp");

                        PreparedStatement prepSelectSt = conn.prepareStatement("SELECT * FROM Meniuri WHERE nr_meniu = ?");
                        prepSelectSt.setShort(1, Short.parseShort(nr_meniu));
                        ResultSet resultSelectSet = prepSelectSt.executeQuery();
                        resultSelectSet.next();

                        if (!nume_meniu.equals(resultSelectSet.getString("nume_meniu"))) {
                            PreparedStatement prepUpdateSt1 = conn.prepareStatement("UPDATE Meniuri SET nume_meniu = ? WHERE nr_meniu = ?");
                            prepUpdateSt1.setString(1, nume_meniu);
                            prepUpdateSt1.setShort(2, Short.parseShort(nr_meniu));
                            prepUpdateSt1.execute();
                        }

                        if (!detalii_suplimentare_meniu.equals(resultSelectSet.getString("detalii_suplimentare_meniu"))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Meniuri SET detalii_suplimentare_meniu = ? WHERE nr_meniu = ?");
                            prepUpdateSt2.setString(1, detalii_suplimentare_meniu);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_meniu));
                            prepUpdateSt2.execute();
                        }

                        tblModel.setValueAt(Short.parseShort(nr_meniu), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0);
                        tblModel.setValueAt(nume_meniu, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1);
                        tblModel.setValueAt(detalii_suplimentare_meniu, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 2);
                        tblModel.setValueAt(data_crearii, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3);

                        conn.createStatement().execute("commit");
                        if (!conn.getAutoCommit()) {
                            conn.setAutoCommit(true);
                        }
                        JOptionPane.showMessageDialog(this, "Meniu modificat cu succes");
                    } catch (SQLException ex) {

                        try {
                            conn.rollback(sp);
                            conn.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            Logger.getLogger(IngredientsAdminPanel.class.getName()).log(Level.SEVERE, null, ex1);
                        }

                        if (ex.getMessage().contains("ORA-00001")) {
                            JOptionPane.showMessageDialog(this, "Meniu deja existent");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Meniurile trebuie sa contina cuvinte sau numere, primul cuvânt începând cu majusculă, opțional delimitate prin ‘-‘");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Selecteaza un singur rand pentru a modifica.");

                }
                Refresh();
                break;

            /////////////// STERGEREA DIN BAZA DE DATE ///////////////
            case "Sterge":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nr_meniu = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();

                    try {
                        PreparedStatement prepSt = conn.prepareStatement("DELETE FROM Meniuri WHERE nr_meniu = ?");
                        prepSt.setString(1, nr_meniu);
                        prepSt.execute();

                        tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));

                        conn.createStatement().execute("commit");
                        JOptionPane.showMessageDialog(this, "Meniu sters cu succes");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Selecteaza un singur rand pentru a sterge.");
                }
                break;

            /////////////// AFISARE/REFRESH JTABLE ///////////////
            case "Refresh":
                Refresh();
                break;

        }
    }

    private void Refresh() {
        filterTextField.setText("");
        startFilter();
        tr.setSortKeys(sortKeys);

        numeMeniuTextField.setText("");
        detaliiSuplimentareTextField.setText("");

        try {
            Statement st = appWindow.getDataBaseConnection().getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT nr_meniu, nume_meniu, detalii_suplimentare_meniu, data_crearii FROM Meniuri ORDER BY nr_meniu");

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                String nr_meniu = rs.getString("nr_meniu");
                String nume_meniu = rs.getString("nume_meniu");
                String detalii_suplimentare_meniu = rs.getString("detalii_suplimentare_meniu");
                String data_crearii = rs.getDate("data_crearii").toString();

                Object tblData[] = {Short.parseShort(nr_meniu), nume_meniu, detalii_suplimentare_meniu, data_crearii};
                tblModel = (DefaultTableModel) this.dataTable.getModel();
                tblModel.addRow(tblData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MenusAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPanel = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        deleteButton = new javax.swing.JButton();
        showButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        insertUpdatePanel = new javax.swing.JPanel();
        numeMeniuLabel = new javax.swing.JLabel();
        detaliiSuplimentareLabel = new javax.swing.JLabel();
        numeMeniuTextField = new javax.swing.JTextField();
        detaliiSuplimentareTextField = new javax.swing.JTextField();
        insertButton = new javax.swing.JButton();
        deleteBoxesButton = new javax.swing.JButton();
        currMenuButton = new javax.swing.JButton();
        currMenuTextField = new javax.swing.JTextField();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nr_meniu", "nume_meniu", "detalii_suplimentare", "data_creare"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Short.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.getTableHeader().setReorderingAllowed(false);
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataTableMouseClicked(evt);
            }
        });
        dataTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataTableKeyReleased(evt);
            }
        });
        scrollPanel.setViewportView(dataTable);

        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        deleteButton.setText("Sterge");
        deleteButton.setActionCommand("ButoaneMeniuriAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(deleteButton, gridBagConstraints);

        showButton.setText("Refresh");
        showButton.setActionCommand("ButoaneMeniuriAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(showButton, gridBagConstraints);

        updateButton.setText("Modificare");
        updateButton.setActionCommand("ButoaneMeniuriAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(updateButton, gridBagConstraints);

        numeMeniuLabel.setText("nume_menu:");

        detaliiSuplimentareLabel.setText("detalii_suplimentare:");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneMeniuriAdmin");

        deleteBoxesButton.setText("Sterge casetele");
        deleteBoxesButton.setActionCommand("ButoaneMeniuriAdmin");

        currMenuButton.setText("Setare ca meniu curent");
        currMenuButton.setActionCommand("ButoaneMeniuriAdmin");

        currMenuTextField.setEditable(false);

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(detaliiSuplimentareLabel)
                        .addComponent(numeMeniuLabel))
                    .addComponent(currMenuButton))
                .addGap(18, 18, 18)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detaliiSuplimentareTextField)
                            .addComponent(numeMeniuTextField))
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(87, 87, 87))
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addComponent(currMenuTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currMenuButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currMenuTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeMeniuTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeMeniuLabel)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detaliiSuplimentareTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detaliiSuplimentareLabel)
                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(142, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(insertUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(insertUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(bottomPanelLayout.createSequentialGroup()
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTextFieldKeyReleased(evt);
            }
        });

        filterLabel.setText("Filter:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanel)
            .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1045, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(scrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void filterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextFieldKeyReleased
        startFilter();
    }//GEN-LAST:event_filterTextFieldKeyReleased

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_meniu = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
            String detalii_suplimentare_meniu = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();

            numeMeniuTextField.setText(nume_meniu);
            detaliiSuplimentareTextField.setText(detalii_suplimentare_meniu);
        }
    }//GEN-LAST:event_dataTableKeyReleased

    public JTextField getCurrentMenuTF() {
        return this.currMenuTextField;
    }

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked

        String nume_meniu = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
        String detalii_suplimentare_meniu = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();

        numeMeniuTextField.setText(nume_meniu);
        detaliiSuplimentareTextField.setText(detalii_suplimentare_meniu);

    }//GEN-LAST:event_dataTableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton currMenuButton;
    private javax.swing.JTextField currMenuTextField;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteBoxesButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel detaliiSuplimentareLabel;
    private javax.swing.JTextField detaliiSuplimentareTextField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JLabel numeMeniuLabel;
    private javax.swing.JTextField numeMeniuTextField;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
