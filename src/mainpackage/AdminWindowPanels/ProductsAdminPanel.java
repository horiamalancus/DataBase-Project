/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage.AdminWindowPanels;

/**
 *
 * @author cosmi
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mainpackage.ApplicationWindow;

public class ProductsAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form ProductsAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public ProductsAdminPanel(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
        initComponents();
        initFilter();

        initActionListeners();
    }

    private void initActionListeners() {
        insertButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteBoxesButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        updateButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteType.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
    }

    private void initFilter() {
        tr = new TableRowSorter<>((DefaultTableModel) dataTable.getModel());
        dataTable.setRowSorter(tr);

        sortKeys = new ArrayList<>();
        for (int i = 0; i < dataTable.getColumnCount(); ++i) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.UNSORTED));
        }
    }

    public void fillComboBoxes() {

        int idx = (productTypeCB.getItemCount() == -1) ? -1 : productTypeCB.getSelectedIndex();
        int idx2 = (foodTypeCB.getItemCount() == -1) ? -1 : foodTypeCB.getSelectedIndex();
        int idx3 = (stateCB.getItemCount() == -1) ? -1 : stateCB.getSelectedIndex();

        productTypeCB.removeAllItems();
        foodTypeCB.removeAllItems();
        stateCB.removeAllItems();

        try {

            ResultSet rs2 = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_tip FROM tipuri_aliment");

            productTypeCB.addItem("preparat");
            productTypeCB.addItem("bautura");

            while (rs2.next()) {
                foodTypeCB.addItem(rs2.getString(1));
            }

            stateCB.addItem("ACTIV");
            stateCB.addItem("INACTIV");

        } catch (SQLException ex) {
            Logger.getLogger(CategoriesAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        productTypeCB.setSelectedIndex((productTypeCB.getItemCount() == 0) ? -1 : (idx == -1) ? 0 : idx);
        foodTypeCB.setSelectedIndex((foodTypeCB.getItemCount() == 0) ? -1 : (idx2 == -1) ? 0 : idx2);
        stateCB.setSelectedIndex((stateCB.getItemCount() == 0) ? -1 : (idx3 == -1) ? 0 : idx3);

    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String nr_produs;
        String nume_produs;
        String tip_produs;
        String tip_aliment;
        String pret;
        String stare;
        String detalii_suplimentare;

        switch (tmpEventButton.getText()) {
            /////////////// INSERARE ///////////////
            case "Inserare":

                try {

                    ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM produse");
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String currentDate = formatter.format(date);

                tr.setSortKeys(sortKeys);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_produs = numeProdusTextField.getText();
                tip_produs = productTypeCB.getSelectedItem().toString();
                tip_aliment = foodTypeCB.getSelectedItem().toString();
                pret = priceTextField.getText();
                stare = stateCB.getSelectedItem().toString();
                detalii_suplimentare = detaliiSuplimentareTextField.getSelectedText();

                try {
                    PreparedStatement prepSt = conn.prepareStatement("INSERT INTO Produse(nume_produs, tip_produs, pret, tipuri_aliment_id_tip, stare, detalii_suplimentare_produs) VALUES(?, ?, ?, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = ?), ?, ?)");
                    prepSt.setString(1, nume_produs);
                    prepSt.setString(2, tip_produs);
                    prepSt.setFloat(3, Float.parseFloat(pret));
                    prepSt.setString(4, tip_aliment);
                    prepSt.setString(5, stare);
                    prepSt.setString(6, detalii_suplimentare);
                    prepSt.execute();

                    ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(nr_produs) FROM Produse");
                    rs.next();
                    nr_produs = rs.getString(1);

                    Object tfData[] = {Short.parseShort(nr_produs), nume_produs, tip_produs, tip_aliment, Float.parseFloat(pret), stare, detalii_suplimentare, currentDate};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Inserare realizata cu succes");
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                            JOptionPane.showMessageDialog(this, "Produs deja existent.");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Numele produselor trebuie sa contina cuvinte sau numere (opțional și în virgula mobilă), primul cuvânt începând cu majusculă, opțional delimitate prin '-'.\nPretul trebuie sa fie pozitiv.");
                        } else if (ex.getMessage().contains("ORA-01438")) {
                            JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???.??");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }

                    } catch (NumberFormatException ex2) {
                        JOptionPane.showMessageDialog(this, "Pretul produselor trebuie sa contina doar cifre.");
                    }
                Refresh();
                break;

            /////////////// STERGEREA CASTETELOR TEXT FIELD ///////////////
            case "Sterge casetele":
                numeProdusTextField.setText("");
                productTypeCB.setSelectedIndex((productTypeCB.getItemCount() == 0) ? -1 : 0);
                foodTypeCB.setSelectedIndex((foodTypeCB.getItemCount() == 0) ? -1 : 0);
                stateCB.setSelectedIndex((stateCB.getItemCount() == 0) ? -1 : 0);
                priceTextField.setText("");
                detaliiSuplimentareTextField.setText("");
                break;

            case "Sterge tip_aliment":

                tblModel = (DefaultTableModel) this.dataTable.getModel();
                nr_produs = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                tip_aliment = (dataTable.getValueAt(dataTable.getSelectedRow(), 3) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();
                
                if (!tip_aliment.equals("")) {
                    PreparedStatement prepUpdateSt2;
                    try {
                        prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET tipuri_aliment_id_tip = NULL WHERE nr_produs = ?");
                        prepUpdateSt2.setShort(1, Short.parseShort(nr_produs));
                        prepUpdateSt2.execute();
                        
                        tblModel.setValueAt("", dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3);
                        JOptionPane.showMessageDialog(this, "Stergere tip_aliment realizata cu succes");
                    } catch (SQLException ex) {
                        Logger.getLogger(ProductsAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    
                }
                
                
                
                break;

            /////////////// MODIFICAREA IN BAZA DE DATE ///////////////
            case "Modificare":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nr_produs = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();

                    String nume_produs_mod = numeProdusTextField.getText();
                    String tip_produs_mod = productTypeCB.getSelectedItem().toString();
                    String tip_aliment_mod = foodTypeCB.getSelectedItem().toString();
                    String pret_mod = priceTextField.getText();
                    String stare_mod = stateCB.getSelectedItem().toString();
                    String detalii_suplimentare_mod = detaliiSuplimentareTextField.getText();

                    Savepoint sp = null;

                    try {
                        
                        conn.setAutoCommit(false);
                        sp = conn.setSavepoint("sp");
                        
                        PreparedStatement prepSelectSt = conn.prepareStatement("SELECT * FROM Produse WHERE nr_produs = ?");
                        prepSelectSt.setShort(1, Short.parseShort(nr_produs));
                        ResultSet resultSelectSet = prepSelectSt.executeQuery();
                        resultSelectSet.next();

                        if (!nume_produs_mod.equals(resultSelectSet.getString(2))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET nume_produs = ? WHERE nr_produs = ?");
                            prepUpdateSt2.setString(1, nume_produs_mod);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_produs));
                            prepUpdateSt2.execute();
                        }

                        if (!tip_produs_mod.equals(resultSelectSet.getString(3))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET tip_produs = ? WHERE nr_produs = ?");
                            prepUpdateSt2.setString(1, tip_produs_mod);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_produs));
                            prepUpdateSt2.execute();
                        }

                        if (Float.parseFloat(pret_mod) != resultSelectSet.getFloat(4)) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET pret = ? WHERE nr_produs = ?");
                            prepUpdateSt2.setFloat(1, Float.parseFloat(pret_mod));
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_produs));
                            prepUpdateSt2.execute();
                        }

                        if (!stare_mod.equals(resultSelectSet.getString(5))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET stare = ? WHERE nr_produs = ?");
                            prepUpdateSt2.setString(1, stare_mod);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_produs));
                            prepUpdateSt2.execute();
                        }

                        if (!detalii_suplimentare_mod.equals(resultSelectSet.getString(7))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET detalii_suplimentare_produs = ? WHERE nr_produs = ?");
                            prepUpdateSt2.setString(1, detalii_suplimentare_mod);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_produs));
                            prepUpdateSt2.execute();
                        }

                        if (!tip_aliment_mod.equals(resultSelectSet.getString(8))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Produse SET tipuri_aliment_id_tip = (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = ?) WHERE nr_produs = ?");
                            prepUpdateSt2.setString(1, tip_aliment_mod);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_produs));
                            prepUpdateSt2.execute();
                        }

                        tblModel.setValueAt(Short.parseShort(nr_produs), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0);
                        tblModel.setValueAt(nume_produs_mod, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1);
                        tblModel.setValueAt(tip_produs_mod, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 2);
                        tblModel.setValueAt(tip_aliment_mod, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3);
                        tblModel.setValueAt(Float.parseFloat(pret_mod), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 4);
                        tblModel.setValueAt(stare_mod, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 5);
                        tblModel.setValueAt(detalii_suplimentare_mod, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 6);

                        conn.createStatement().execute("commit");
                        if(!conn.getAutoCommit()) conn.setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, "Modificare realizata cu succes");
                    } catch (SQLException ex) {

                        try {
                            conn.rollback(sp);
                            conn.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            Logger.getLogger(IngredientsAdminPanel.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        
                        if (ex.getMessage().contains("ORA-00001")) {
                            JOptionPane.showMessageDialog(this, "Produs deja existent.");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Numele produselor trebuie sa contina cuvinte sau numere (opțional și în virgula mobilă), primul cuvânt începând cu majusculă, opțional delimitate prin '-'.\nPretul trebuie sa fie pozitiv.");
                        } else if (ex.getMessage().contains("ORA-01438")) {
                            JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???.??");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }

                    } catch (NumberFormatException ex2) {
                        JOptionPane.showMessageDialog(this, "Pretul produselor trebuie sa contina doar cifre.");
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

                    nr_produs = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();

                    try {
                        PreparedStatement prepSt = conn.prepareStatement("DELETE FROM Produse WHERE nr_produs = ?");
                        prepSt.setString(1, nr_produs);
                        prepSt.execute();

                        conn.createStatement().execute("commit");
                        
                        tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));
                        JOptionPane.showMessageDialog(this, "Stergere realizata cu succes");

                        
                    } catch (SQLException ex) {
                        
                        if(ex.getMessage().contains("ORA-02292"))
                        {
                            JOptionPane.showMessageDialog(this, "Nu puteti sterge un produs existent intr-o comanda.");
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(this, ex.getMessage());
                        }
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
        fillComboBoxes();

        try {
            try (ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nr_produs, nume_produs, tip_produs, pret, stare, data_crearii, detalii_suplimentare_produs, tipuri_aliment_id_tip FROM Produse ORDER BY nr_produs")) {
                DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
                tblModel.setRowCount(0);
                
                while (rs.next()) {
                    String nr_produs = rs.getString(1);
                    String nume_produs = rs.getString(2);
                    String tip_produs = rs.getString(3);
                    String pret = rs.getString(4);
                    String stare = rs.getString(5);
                    String data_crearii = rs.getDate(6).toString();
                    String detalii_suplimentare = rs.getString(7);
                    String tip_aliment = rs.getString(8);
                    
                    if (tip_aliment != null) {
                        PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT nume_tip FROM tipuri_aliment WHERE id_tip = ?");
                        ps.setShort(1, Short.valueOf(tip_aliment));
                        try (ResultSet rs2 = ps.executeQuery()) {
                            rs2.next();
                            tip_aliment = rs2.getString(1);
                        }
                    }
                    
                    Object tblData[] = {Short.parseShort(nr_produs), nume_produs, tip_produs, tip_aliment, Float.parseFloat(pret), stare, detalii_suplimentare, data_crearii};
                    tblModel = (DefaultTableModel) this.dataTable.getModel();
                    tblModel.addRow(tblData);
                }
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
        numeProdusTextField = new javax.swing.JTextField();
        detaliiSuplimentareTextField = new javax.swing.JTextField();
        insertButton = new javax.swing.JButton();
        deleteBoxesButton = new javax.swing.JButton();
        productTypeCB = new javax.swing.JComboBox<>();
        foodTypeCB = new javax.swing.JComboBox<>();
        stateCB = new javax.swing.JComboBox<>();
        tipProdusLabel = new javax.swing.JLabel();
        tipAlimentLabel = new javax.swing.JLabel();
        stareLabel = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        priceTextField = new javax.swing.JTextField();
        deleteType = new javax.swing.JButton();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nr_produs", "nume_produs", "tip_produs", "tip_aliment", "pret", "stare", "detalii_suplimentare", "data_creare"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Short.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Short.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
        deleteButton.setActionCommand("ButoaneProduseAdmin");
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
        showButton.setActionCommand("ButoaneProduseAdmin");
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
        updateButton.setActionCommand("ButoaneProduseAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(updateButton, gridBagConstraints);

        numeMeniuLabel.setText("nume_produs:");

        detaliiSuplimentareLabel.setText("detalii_suplimentare:");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneProduseAdmin");

        deleteBoxesButton.setText("Sterge casetele");
        deleteBoxesButton.setActionCommand("ButoaneProduseAdmin");

        tipProdusLabel.setText("tip_produs:");

        tipAlimentLabel.setText("tip_aliment:");

        stareLabel.setText("stare:");

        priceLabel.setText("pret:");

        deleteType.setText("Sterge tip_aliment");
        deleteType.setActionCommand("ButoaneProduseAdmin");

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addComponent(tipProdusLabel)
                        .addGap(1, 1, 1)
                        .addComponent(productTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tipAlimentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(foodTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(stareLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stateCB, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detaliiSuplimentareLabel)
                            .addComponent(numeMeniuLabel)
                            .addComponent(priceLabel))
                        .addGap(18, 18, 18)
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(priceTextField)
                            .addComponent(detaliiSuplimentareTextField)
                            .addComponent(numeProdusTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(deleteType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))))
                        .addGap(87, 87, 87))))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeProdusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeMeniuLabel)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(priceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(priceLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(detaliiSuplimentareTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(detaliiSuplimentareLabel))))
                .addGap(33, 33, 33)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(stateCB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(stareLabel)
                        .addComponent(deleteType, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(productTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(foodTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tipProdusLabel)
                        .addComponent(tipAlimentLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
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
                .addGap(0, 62, Short.MAX_VALUE)
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

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked

        String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
        String tip_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
        String tip_aliment = (dataTable.getValueAt(dataTable.getSelectedRow(), 3) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();
        String pret = dataTable.getValueAt(dataTable.getSelectedRow(), 4).toString();
        String stare = dataTable.getValueAt(dataTable.getSelectedRow(), 5).toString();
        String detalii_suplimentare = (dataTable.getValueAt(dataTable.getSelectedRow(), 6) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 6).toString();

        numeProdusTextField.setText(nume_produs);
        productTypeCB.setSelectedItem(tip_produs);
        foodTypeCB.setSelectedItem(tip_aliment);
        stateCB.setSelectedItem(stare);
        priceTextField.setText(pret);
        detaliiSuplimentareTextField.setText(detalii_suplimentare);
    }//GEN-LAST:event_dataTableMouseClicked

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
            String tip_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
            String tip_aliment = (dataTable.getValueAt(dataTable.getSelectedRow(), 3) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();
            String pret = dataTable.getValueAt(dataTable.getSelectedRow(), 4).toString();
            String stare = dataTable.getValueAt(dataTable.getSelectedRow(), 5).toString();
            String detalii_suplimentare = (dataTable.getValueAt(dataTable.getSelectedRow(), 6) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 6).toString();

            numeProdusTextField.setText(nume_produs);
            productTypeCB.setSelectedItem(tip_produs);
            foodTypeCB.setSelectedItem(tip_aliment);
            stateCB.setSelectedItem(stare);
            priceTextField.setText(pret);
            detaliiSuplimentareTextField.setText(detalii_suplimentare);
        }
    }//GEN-LAST:event_dataTableKeyReleased

    private void filterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextFieldKeyReleased
        startFilter();
    }//GEN-LAST:event_filterTextFieldKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteBoxesButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteType;
    private javax.swing.JLabel detaliiSuplimentareLabel;
    private javax.swing.JTextField detaliiSuplimentareTextField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JComboBox<String> foodTypeCB;
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JLabel numeMeniuLabel;
    private javax.swing.JTextField numeProdusTextField;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTextField priceTextField;
    private javax.swing.JComboBox<String> productTypeCB;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    private javax.swing.JLabel stareLabel;
    private javax.swing.JComboBox<String> stateCB;
    private javax.swing.JLabel tipAlimentLabel;
    private javax.swing.JLabel tipProdusLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
