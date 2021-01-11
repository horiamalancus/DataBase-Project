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

public class IngredientsAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form IngredientsAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public IngredientsAdminPanel(ApplicationWindow appWindow) {
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

        int idx = (foodTypeCB.getItemCount() == -1) ? -1 : foodTypeCB.getSelectedIndex();

        foodTypeCB.removeAllItems();

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_tip FROM tipuri_aliment");

            while (rs.next()) {
                foodTypeCB.addItem(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriesAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        foodTypeCB.setSelectedIndex((foodTypeCB.getItemCount() == 0) ? -1 : (idx == -1) ? 0 : idx);

    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String id_ingredient;
        String nume_ingredient;
        String tip_aliment;
        String stoc_ingredient;
        String producator;

        switch (tmpEventButton.getText()) {
            /////////////// INSERARE ///////////////
            case "Inserare":

                try {
                    tip_aliment = foodTypeCB.getSelectedItem().toString();

                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Ingrediente");
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Ingredient deja existent");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Ingredientele trebuie sa contina cuvinte fără majuscule.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }
                }

                if (numeIngredientTextField.getText().equals("") && stocIngredientTextField.getText().equals("") && producatorTextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Casetele sunt goale.");
                    break;
                }

                tr.setSortKeys(sortKeys);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_ingredient = numeIngredientTextField.getText();
                stoc_ingredient = stocIngredientTextField.getText();
                producator = producatorTextField.getText();
                tip_aliment = foodTypeCB.getSelectedItem().toString();

                try {
                    PreparedStatement prepSt = conn.prepareStatement("INSERT INTO Ingrediente(nume_ingredient, stoc_ingredient, producator, tipuri_aliment_id_tip) VALUES(?, ?, ?, (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = ?))");
                    prepSt.setString(1, nume_ingredient);
                    prepSt.setFloat(2, Float.valueOf(stoc_ingredient));
                    prepSt.setString(3, producator);
                    prepSt.setString(4, tip_aliment);
                    prepSt.execute();

                    ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(id_ingredient) FROM Ingrediente");
                    rs.next();
                    id_ingredient = rs.getString(1);
                    Object tfData[] = {Short.parseShort(id_ingredient), nume_ingredient, tip_aliment, Float.parseFloat(stoc_ingredient), producator};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Ingredient inserat cu succes");
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Ingredient deja existent.");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Numele ingredientelor trebuie sa contina cuvinte fără majuscule.\nStocul trebuie sa fie pozitiv.");
                    } else if (ex.getMessage().contains("ORA-01438")) {
                        JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???.??");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }

                } catch (NumberFormatException ex2) {
                    JOptionPane.showMessageDialog(this, "Stocul ingredientelor trebuie sa contina doar cifre.");
                }
                Refresh();
                break;

            /////////////// STERGEREA CASTETELOR TEXT FIELD ///////////////
            case "Sterge casetele":
                numeIngredientTextField.setText("");
                stocIngredientTextField.setText("");
                producatorTextField.setText("");
                foodTypeCB.setSelectedIndex((foodTypeCB.getItemCount() == 0) ? -1 : 0);
                break;

            /////////////// MODIFICAREA IN BAZA DE DATE ///////////////
            case "Modificare":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    id_ingredient = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                    nume_ingredient = numeIngredientTextField.getText();
                    stoc_ingredient = stocIngredientTextField.getText();
                    producator = producatorTextField.getText();
                    tip_aliment = foodTypeCB.getSelectedItem().toString();

                    Savepoint sp = null;

                    try {
                        
                        conn.setAutoCommit(false);
                        sp = conn.setSavepoint("sp");
                        
                        PreparedStatement prepSelectSt = conn.prepareStatement("SELECT * FROM Ingrediente WHERE id_ingredient = ?");
                        prepSelectSt.setShort(1, Short.parseShort(id_ingredient));
                        ResultSet resultSelectSet = prepSelectSt.executeQuery();
                        resultSelectSet.next();

                        if (!nume_ingredient.equals(resultSelectSet.getString("nume_ingredient"))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Ingrediente SET nume_ingredient = ? WHERE id_ingredient = ?");
                            prepUpdateSt2.setString(1, nume_ingredient);
                            prepUpdateSt2.setShort(2, Short.parseShort(id_ingredient));
                            prepUpdateSt2.execute();
                        }

                        if (Float.parseFloat(stoc_ingredient) != (resultSelectSet.getFloat("stoc_ingredient"))) {
                            PreparedStatement prepUpdateSt1 = conn.prepareStatement("UPDATE Ingrediente SET stoc_ingredient = ? WHERE id_ingredient = ?");
                            prepUpdateSt1.setFloat(1, Float.parseFloat(stoc_ingredient));
                            prepUpdateSt1.setShort(2, Short.parseShort(id_ingredient));
                            prepUpdateSt1.execute();
                        }

                        if (!producator.equals(resultSelectSet.getString("producator"))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Ingrediente SET producator = ? WHERE id_ingredient = ?");
                            prepUpdateSt2.setString(1, producator);
                            prepUpdateSt2.setShort(2, Short.parseShort(id_ingredient));
                            prepUpdateSt2.execute();
                        }

                        if (!tip_aliment.equals(resultSelectSet.getString("tipuri_aliment_id_tip"))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Ingrediente SET tipuri_aliment_id_tip = (SELECT id_tip FROM tipuri_aliment WHERE nume_tip = ?) WHERE id_ingredient = ?");
                            prepUpdateSt2.setString(1, tip_aliment);
                            prepUpdateSt2.setShort(2, Short.parseShort(id_ingredient));
                            prepUpdateSt2.execute();
                        }

                        tblModel.setValueAt(Short.parseShort(id_ingredient), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0);
                        tblModel.setValueAt(nume_ingredient, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1);
                        tblModel.setValueAt(tip_aliment, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 2);
                        tblModel.setValueAt(Float.parseFloat(stoc_ingredient), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3);
                        tblModel.setValueAt(producator, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 4);

                        conn.createStatement().execute("commit");
                        if(!conn.getAutoCommit()) conn.setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, "Ingredient modificat cu succes");
                    } catch (SQLException ex) {

                        try {
                            conn.rollback(sp);
                            conn.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            Logger.getLogger(IngredientsAdminPanel.class.getName()).log(Level.SEVERE, null, ex1);
                        }

                        if (ex.getMessage().contains("ORA-00001")) {
                            JOptionPane.showMessageDialog(this, "Ingredient deja existent.");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Numele ingredientelor trebuie sa contina cuvinte fără majuscule.\nStocul trebuie sa fie pozitiv.");
                        } else if (ex.getMessage().contains("ORA-01438")) {
                            JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???.??");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }

                    } catch (NumberFormatException ex2) {
                        JOptionPane.showMessageDialog(this, "Stocul ingredientelor trebuie sa contina doar cifre.");
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

                    id_ingredient = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();

                    try {
                        PreparedStatement prepSt = conn.prepareStatement("DELETE FROM Ingrediente WHERE id_ingredient = ?");
                        prepSt.setString(1, id_ingredient);
                        prepSt.execute();

                        conn.createStatement().execute("commit");

                        tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));
                        JOptionPane.showMessageDialog(this, "Ingredient inlaturat cu succes");
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
        fillComboBoxes();

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT * FROM Ingrediente ORDER BY id_ingredient");

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                String id_ingredient = rs.getString(1);
                String nume_ingredient = rs.getString(2);
                String stoc_ingredient = rs.getString(3);
                String producator = rs.getString(4);
                String tip_aliment = rs.getString(5);

                if (tip_aliment != null) {
                    PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT nume_tip FROM tipuri_aliment WHERE id_tip = ?");
                    ps.setShort(1, Short.valueOf(tip_aliment));
                    ResultSet rs2 = ps.executeQuery();
                    rs2.next();
                    tip_aliment = rs2.getString(1);
                }

                Object tblData[] = {Short.parseShort(id_ingredient), nume_ingredient, tip_aliment, Float.parseFloat(stoc_ingredient), producator};
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
        numeIngredientLabel = new javax.swing.JLabel();
        prodIngredientLabel = new javax.swing.JLabel();
        numeIngredientTextField = new javax.swing.JTextField();
        producatorTextField = new javax.swing.JTextField();
        insertButton = new javax.swing.JButton();
        deleteBoxesButton = new javax.swing.JButton();
        foodTypeCB = new javax.swing.JComboBox<>();
        tipAlimentLabel = new javax.swing.JLabel();
        stocIngredientLabel1 = new javax.swing.JLabel();
        stocIngredientTextField = new javax.swing.JTextField();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id_ingredient", "nume_ingredient", "tip_aliment", "stoc_ingredient", "producator"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Short.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
        deleteButton.setActionCommand("ButoaneIngredienteAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(deleteButton, gridBagConstraints);

        showButton.setText("Refresh");
        showButton.setActionCommand("ButoaneIngredienteAdmin");
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
        updateButton.setActionCommand("ButoaneIngredienteAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(updateButton, gridBagConstraints);

        numeIngredientLabel.setText("nume_ingredient:");

        prodIngredientLabel.setText("producator:");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneIngredienteAdmin");

        deleteBoxesButton.setText("Sterge casetele");
        deleteBoxesButton.setActionCommand("ButoaneIngredienteAdmin");

        tipAlimentLabel.setText("tip_aliment");

        stocIngredientLabel1.setText("stoc_ingredient:");

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addComponent(numeIngredientLabel)
                        .addGap(18, 18, 18)
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(stocIngredientTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                            .addComponent(producatorTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(numeIngredientTextField))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(87, 87, 87))
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(stocIngredientLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tipAlimentLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(prodIngredientLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(foodTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeIngredientTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeIngredientLabel)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stocIngredientTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stocIngredientLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(producatorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodIngredientLabel)
                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(foodTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tipAlimentLabel))
                .addContainerGap(94, Short.MAX_VALUE))
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
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
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

        String nume_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
        String tip_aliment = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
        String stoc_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();
        String producator = (dataTable.getValueAt(dataTable.getSelectedRow(), 4) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 4).toString();

        numeIngredientTextField.setText(nume_ingredient);
        foodTypeCB.setSelectedItem(tip_aliment);
        stocIngredientTextField.setText(stoc_ingredient);
        producatorTextField.setText(producator);
    }//GEN-LAST:event_dataTableMouseClicked

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
            String tip_aliment = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
            String stoc_ingredient = dataTable.getValueAt(dataTable.getSelectedRow(), 3).toString();
            String producator = (dataTable.getValueAt(dataTable.getSelectedRow(), 4) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 4).toString();

            numeIngredientTextField.setText(nume_ingredient);
            foodTypeCB.setSelectedItem(tip_aliment);
            stocIngredientTextField.setText(stoc_ingredient);
            producatorTextField.setText(producator);
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
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JComboBox<String> foodTypeCB;
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JLabel numeIngredientLabel;
    private javax.swing.JTextField numeIngredientTextField;
    private javax.swing.JLabel prodIngredientLabel;
    private javax.swing.JTextField producatorTextField;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    private javax.swing.JLabel stocIngredientLabel1;
    private javax.swing.JTextField stocIngredientTextField;
    private javax.swing.JLabel tipAlimentLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
