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

public class RecipesAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form recipesAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public RecipesAdminPanel(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
        initComponents();
        initFilter();

        initActionListeners();
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

        int idx = (numeProdusCB.getItemCount() == -1) ? -1 : numeProdusCB.getSelectedIndex();
        int idx2 = (numeIngredientCB.getItemCount() == -1) ? -1 : numeIngredientCB.getSelectedIndex();

        numeProdusCB.removeAllItems();
        numeIngredientCB.removeAllItems();

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_produs FROM Produse");

            while (rs.next()) {
                numeProdusCB.addItem(rs.getString(1));
            }

            ResultSet rs2 = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_ingredient, producator FROM Ingrediente");

            while (rs2.next()) {
                if (rs2.getString(2) == null) {
                    numeIngredientCB.addItem(rs2.getString(1) + " | X");
                } else {
                    numeIngredientCB.addItem(rs2.getString(1) + " | " + rs2.getString(2));
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriesAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        numeProdusCB.setSelectedIndex((numeProdusCB.getItemCount() == 0) ? -1 : (idx == -1) ? 0 : idx);
        numeIngredientCB.setSelectedIndex((numeIngredientCB.getItemCount() == 0) ? -1 : (idx2 == -1) ? 0 : idx2);

    }

    private void initActionListeners() {
        insertButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteBoxesButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        updateButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String nume_produs;
        String nume_ingredient;
        String producator;
        String cantitate_ingredient;

        switch (tmpEventButton.getText()) {
            /////////////// INSERARE ///////////////
            case "Inserare":

                try {

                    nume_produs = numeProdusCB.getSelectedItem().toString();
                    nume_ingredient = numeIngredientCB.getSelectedItem().toString();

                    ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Retete");
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Ingredient deja introdus in reteta.");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Cantitatea ingredientului trebuie sa fie pozitiva.");
                    } else if (ex.getMessage().contains("ORA-01438")) {
                        JOptionPane.showMessageDialog(this, "Cantitate prea mare. Introduceti un numar de forma ??.??");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }

                } catch (NumberFormatException ex2) {
                    JOptionPane.showMessageDialog(this, "Cantitate ingredientelor trebuie sa contina doar cifre.");
                }

                if (cantitateIngredientTF.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Casetele sunt goale.");
                    break;
                }

                tr.setSortKeys(sortKeys);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_produs = numeProdusCB.getSelectedItem().toString();
                nume_ingredient = numeIngredientCB.getSelectedItem().toString().split(" | ", 0)[0];
                producator = numeIngredientCB.getSelectedItem().toString().split(" | ", 0)[2];
                cantitate_ingredient = cantitateIngredientTF.getText();

                try {

                    PreparedStatement prepSt;

                    if (producator.equals("X")) {
                        prepSt = conn.prepareStatement("INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = ?), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = ? and producator IS NULL), ?)");
                        prepSt.setFloat(3, Float.parseFloat(cantitate_ingredient));
                        producator = "";
                    } else {
                        prepSt = conn.prepareStatement("INSERT INTO Retete (Produse_nr_produs, Ingrediente_id_ingredient, cantitate_ingredient) VALUES((SELECT nr_produs FROM Produse WHERE nume_produs = ?), (SELECT id_ingredient FROM Ingrediente WHERE nume_ingredient = ? and producator = ?), ?)");
                        prepSt.setString(3, producator);
                        prepSt.setFloat(4, Float.parseFloat(cantitate_ingredient));
                    }

                    prepSt.setString(1, nume_produs);
                    prepSt.setString(2, nume_ingredient);

                    prepSt.execute();

                    Object tfData[] = {nume_produs, nume_ingredient, producator, Float.parseFloat(cantitate_ingredient)};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Inserare realizata cu succes");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
                Refresh();
                break;

            /////////////// STERGEREA CASTETELOR TEXT FIELD ///////////////
            case "Sterge casetele":
                cantitateIngredientTF.setText("");
                numeProdusCB.setSelectedIndex((numeProdusCB.getItemCount() == 0) ? -1 : 0);
                numeIngredientCB.setSelectedIndex((numeProdusCB.getItemCount() == 0) ? -1 : 0);
                break;

            /////////////// MODIFICAREA IN BAZA DE DATE ///////////////
            case "Modificare":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nume_produs = numeProdusCB.getSelectedItem().toString();
                    nume_ingredient = numeIngredientCB.getSelectedItem().toString().split(" | ", 0)[0];
                    producator = numeIngredientCB.getSelectedItem().toString().split(" | ", 0)[2];
                    cantitate_ingredient = cantitateIngredientTF.getText();

                    Savepoint sp = null;

                    try {
                        
                        conn.setAutoCommit(false);
                        sp = conn.setSavepoint("sp");

                        PreparedStatement prepSelectSt;

                        if (producator.equals("X")) {
                            prepSelectSt = conn.prepareStatement("SELECT produse_nr_produs, ingrediente_id_ingredient, cantitate_ingredient FROM Retete WHERE produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) AND ingrediente_id_ingredient = (SELECT id_ingredient FROM ingrediente WHERE nume_ingredient = ? AND producator IS NULL)");
                        } else {
                            prepSelectSt = conn.prepareStatement("SELECT produse_nr_produs, ingrediente_id_ingredient, cantitate_ingredient FROM Retete WHERE produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) AND ingrediente_id_ingredient = (SELECT id_ingredient FROM ingrediente WHERE nume_ingredient = ? AND producator = ?)");
                            prepSelectSt.setString(3, producator);
                        }

                        prepSelectSt.setString(1, nume_produs);
                        prepSelectSt.setString(2, nume_ingredient);
                        ResultSet resultSelectSet = prepSelectSt.executeQuery();
                        resultSelectSet.next();

                        if (Float.parseFloat(cantitate_ingredient) != resultSelectSet.getFloat("cantitate_ingredient")) {

                            PreparedStatement prepUpdateSt2;

                            if (producator.equals("X")) {
                                prepUpdateSt2 = conn.prepareStatement("UPDATE Retete SET cantitate_ingredient = ? WHERE produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) AND ingrediente_id_ingredient = (SELECT id_ingredient FROM ingrediente WHERE nume_ingredient = ? AND producator IS NULL)");
                            } else {
                                prepUpdateSt2 = conn.prepareStatement("UPDATE Retete SET cantitate_ingredient = ? WHERE produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) AND ingrediente_id_ingredient = (SELECT id_ingredient FROM ingrediente WHERE nume_ingredient = ? AND producator = ?)");
                                prepSelectSt.setString(4, producator);
                            }

                            prepUpdateSt2.setFloat(1, Float.parseFloat(cantitate_ingredient));
                            prepUpdateSt2.setString(2, nume_produs);
                            prepUpdateSt2.setString(3, nume_ingredient);
                            prepUpdateSt2.execute();
                        }

                        tblModel.setValueAt(nume_produs, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0);
                        tblModel.setValueAt(nume_ingredient, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1);
                        tblModel.setValueAt(producator, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 2);
                        tblModel.setValueAt(Float.parseFloat(cantitate_ingredient), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3);

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
                            JOptionPane.showMessageDialog(this, "Ingredient deja introdus in reteta.");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Cantitatea ingredientului trebuie sa fie pozitiva.");
                        } else if (ex.getMessage().contains("ORA-01438")) {
                            JOptionPane.showMessageDialog(this, "Cantitate prea mare. Introduceti un numar de forma ??.??");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }

                    } catch (NumberFormatException ex2) {
                        JOptionPane.showMessageDialog(this, "Cantitate ingredientelor trebuie sa contina doar cifre.");
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

                    nume_produs = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                    nume_ingredient = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1).toString();
                    producator = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "X" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();

                    try {
                        PreparedStatement prepSt;

                        if (producator.equals("X")) {
                            prepSt = conn.prepareStatement("DELETE FROM Retete WHERE produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) AND ingrediente_id_ingredient = (SELECT id_ingredient FROM ingrediente WHERE nume_ingredient = ? AND producator IS NULL)");
                        } else {
                            prepSt = conn.prepareStatement("DELETE FROM Retete WHERE produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) AND ingrediente_id_ingredient = (SELECT id_ingredient FROM ingrediente WHERE nume_ingredient = ? AND producator = ?)");
                            prepSt.setString(3, producator);
                        }

                        prepSt.setString(1, nume_produs);
                        prepSt.setString(2, nume_ingredient);
                        prepSt.execute();

                        conn.createStatement().execute("commit");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }

                    tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));
                    JOptionPane.showMessageDialog(this, "Stergere realizata cu succes");

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
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT * FROM Retete ORDER BY Produse_nr_produs");

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                String nume_produs = rs.getString(1);
                String nume_ingredient = rs.getString(2);
                String producator;
                Float cantitate_ingredient = rs.getFloat(3);

                PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT nume_produs FROM Produse WHERE nr_produs = ?");
                ps.setShort(1, Short.valueOf(nume_produs));
                ResultSet rs2 = ps.executeQuery();
                rs2.next();
                nume_produs = rs2.getString(1);
                rs2.close();

                PreparedStatement ps2 = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT nume_ingredient, producator FROM Ingrediente WHERE id_ingredient = ?");
                ps2.setShort(1, Short.valueOf(nume_ingredient));
                ResultSet rs3 = ps2.executeQuery();
                rs3.next();
                nume_ingredient = rs3.getString("nume_ingredient");
                producator = rs3.getString("producator");
                rs3.close();

                Object tblData[] = {nume_produs, nume_ingredient, producator, cantitate_ingredient};
                tblModel = (DefaultTableModel) this.dataTable.getModel();
                tblModel.addRow(tblData);
            }
            
            rs.close();

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
        detaliiSuplimentareLabel1 = new javax.swing.JLabel();
        insertButton = new javax.swing.JButton();
        deleteBoxesButton = new javax.swing.JButton();
        numeProdusCB = new javax.swing.JComboBox<>();
        numeIngredientCB = new javax.swing.JComboBox<>();
        cantitateIngredientTF = new javax.swing.JTextField();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nume_produs", "nume_ingredient", "producator", "cantitate_ingredient"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, true
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
        deleteButton.setActionCommand("ButoaneReteteAdmin");
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
        showButton.setActionCommand("ButoaneReteteAdmin");
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
        updateButton.setActionCommand("ButoaneReteteAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(updateButton, gridBagConstraints);

        numeMeniuLabel.setText("nume_produs");

        detaliiSuplimentareLabel.setText("nume_ingredient");

        detaliiSuplimentareLabel1.setText("cantitate_ingredient");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneReteteAdmin");

        deleteBoxesButton.setText("Sterge casetele");
        deleteBoxesButton.setActionCommand("ButoaneReteteAdmin");

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, insertUpdatePanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numeMeniuLabel)
                    .addComponent(detaliiSuplimentareLabel)
                    .addComponent(detaliiSuplimentareLabel1))
                .addGap(30, 30, 30)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cantitateIngredientTF, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                    .addComponent(numeProdusCB, 0, 237, Short.MAX_VALUE)
                    .addComponent(numeIngredientCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(insertButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(87, 87, 87))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeMeniuLabel)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeProdusCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, insertUpdatePanelLayout.createSequentialGroup()
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(detaliiSuplimentareLabel)
                            .addComponent(numeIngredientCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)))
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detaliiSuplimentareLabel1)
                    .addComponent(cantitateIngredientTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked

        String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
        String nume_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
        String producator = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
        String cantitate_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();

        numeProdusCB.setSelectedItem(nume_produs);

        if (producator.equals("")) {
            numeIngredientCB.setSelectedItem(nume_ingredient + " | X");
        } else {
            numeIngredientCB.setSelectedItem(nume_ingredient + " | " + producator);
        }

        cantitateIngredientTF.setText(cantitate_ingredient);
    }//GEN-LAST:event_dataTableMouseClicked

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
            String nume_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
            String producator = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
            String cantitate_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();

            numeProdusCB.setSelectedItem(nume_produs);

            if (producator.equals("")) {
                numeIngredientCB.setSelectedItem(nume_ingredient + " | X");
            } else {
                numeIngredientCB.setSelectedItem(nume_ingredient + " | " + producator);
            }

            cantitateIngredientTF.setText(cantitate_ingredient);
        }
    }//GEN-LAST:event_dataTableKeyReleased

    private void filterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextFieldKeyReleased
        startFilter();
    }//GEN-LAST:event_filterTextFieldKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTextField cantitateIngredientTF;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteBoxesButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel detaliiSuplimentareLabel;
    private javax.swing.JLabel detaliiSuplimentareLabel1;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JComboBox<String> numeIngredientCB;
    private javax.swing.JLabel numeMeniuLabel;
    private javax.swing.JComboBox<String> numeProdusCB;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
