package mainpackage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.*;

public class ClientWindow extends javax.swing.JFrame {

    private ApplicationWindow appWindow;
    public ArrayList<ArrayList<String>> produseleMele = new ArrayList<>();
    public ArrayList<JTextField> quantityTF = new ArrayList<>();
    public ArrayList<Pair<JTextField, String>> nr_produse_ramase = new ArrayList<>();
    public PreparedStatement selectProduct;
    private JComboBox tableComboBox = new JComboBox();
    private JComboBox detailsComboBox = new JComboBox();

    public ClientWindow(ApplicationWindow appWindow) {

        this.appWindow = appWindow;
        initComponents();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Client window closed.");
                e.getWindow().setVisible(false);
                appWindow.setVisible(true);
                e.getWindow().dispose();
            }
        });

        int ct = 0;

        try {
            Connection conn = appWindow.getDataBaseConnection().getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nume_categorie FROM CATEGORII WHERE Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = '" + appWindow.getCurrentMenu() + "')");
            selectProduct = conn.prepareStatement("SELECT p.nume_produs, p.pret FROM Categorii c, Produse p, categorii_produse cp  WHERE c.nume_categorie = ? AND c.Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = ?) AND p.nr_produs = cp.Produse_nr_produs AND c.nr_categorie = cp.Categorii_nr_categorie AND p.stare = 'ACTIV'");
            selectProduct.setString(2, appWindow.getCurrentMenu());
                
            while (rs.next()) {
                //button settings
                //

                String buttonText = rs.getString(1);

                selectProduct.setString(1, buttonText);
                ResultSet rs2 = selectProduct.executeQuery();

                JButton btn = new JButton();
                btn.setText(buttonText);
                btn.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
                btn.setActionCommand("SchimbarePanouMeniuClient");

                GridBagConstraints gridBagConstraints;
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.fill = GridBagConstraints.BOTH;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = ct++;
                gridBagConstraints.ipadx = 32;
                gridBagConstraints.ipady = 27;
                CtgPanel.setBackground(new Color(174, 40, 37));
                CtgPanel.add(btn, gridBagConstraints);

                //panel settings
                ////////////////
                // VARIABILA panel REPREZINTA PANELUL ADAUGAT IN DYNAMIC PANEL SI IN CARE SE VOR ADAUGA PRODUSELE
                JPanel panel = new JPanel();
                panel.setBackground(new Color(201, 184, 56));
                panel.setName(btn.getText());
                DynamicPanel.add(panel, btn.getText());

                //PANEL CARE CONTINE FIECARE PRODUS
                /////////////////////////////////////
                while (rs2.next()) {
                    String nume_produs = rs2.getString(1);
                    String pret = rs2.getString(2);

                    //ADAUGARE PANEL PENTRU FIECARE PRODUS DIN CATEGORIA SELECTATA
                    JPanel produsPanel = new JPanel(new BorderLayout());
                    produsPanel.setPreferredSize(new Dimension(300, 250));
                    produsPanel.setBackground(new Color(180, 114, 71));
                    produsPanel.setName(nume_produs);
                    panel.add(produsPanel, nume_produs);
                    ///////////////////////////////////

                    //ADAUGARE TEXT AREA IN FIECARE PANEL AL UNUI PRODUS
                    JTextArea descriereProdus = new javax.swing.JTextArea();
                    descriereProdus.setBounds(0, 75, 250, 75);
                    descriereProdus.setColumns(10);
                    descriereProdus.setRows(3);
                    descriereProdus.setVisible(true);
                    descriereProdus.setText(nume_produs + " - " + pret + " lei");
                    descriereProdus.setFont(new Font("TimesRoman", Font.PLAIN, 16));
                    descriereProdus.setLineWrap(true);
                    descriereProdus.setEditable(false);
                    produsPanel.add(descriereProdus, BorderLayout.CENTER);

                    ////////////////////////////////////////////////////
                    //ADAUGAREA UNEI POZE PENTRU PRODUS
                    JLabel foodPicture = new javax.swing.JLabel();
                    foodPicture.setBackground(new Color(255, 255, 255));
                    foodPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    foodPicture.setSize(new Dimension(200, 150));
                    ImageIcon img = null;
                    try {
                        img = new ImageIcon(new ImageIcon(getClass().getResource("/Poze/" + nume_produs + ".jpg")).getImage().getScaledInstance(foodPicture.getWidth(), foodPicture.getHeight(), Image.SCALE_SMOOTH));
                    } catch (Exception e) {
                        img = new ImageIcon(new ImageIcon(getClass().getResource("/Poze/empty.jpg")).getImage().getScaledInstance(foodPicture.getWidth(), foodPicture.getHeight(), Image.SCALE_SMOOTH));
                    }
                    foodPicture.setIcon(img);
                    foodPicture.setVerifyInputWhenFocusTarget(false);
                    produsPanel.add(foodPicture, BorderLayout.NORTH);

                    ///////////////////////////////////
                    //ADAUGARE BUTON ADD FIECARUI PRODUS
                    JButton button = new JButton("Add");
                    button.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
                    button.setActionCommand("Add");

                    gridBagConstraints = new java.awt.GridBagConstraints();
                    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.gridy = ct;
                    gridBagConstraints.ipadx = 75;
                    gridBagConstraints.ipady = 160;
                    produsPanel.add(button, BorderLayout.EAST);

                    /////////////////////////////////////////
                    ///ADAUGAREA BUTOANE + - PENTRU SELECTAREA CANTITATII DIN FIECARE PRODUS
                    JPanel numarProdusePanel = new JPanel(new BorderLayout());
                    JButton leftB = new JButton("-");
                    JButton leftR = new JButton("+");
                    leftB.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
                    leftR.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
                    leftB.setActionCommand("MINUS");
                    leftR.setActionCommand("PLUS");
                    JTextField numarProduseTextField = new JTextField("0");
                    numarProduseTextField.setEditable(false);

                    numarProdusePanel.add(leftB, BorderLayout.WEST);
                    numarProdusePanel.add(numarProduseTextField, BorderLayout.CENTER);
                    numarProdusePanel.add(leftR, BorderLayout.EAST);

                    ////////////////////////////////////////////////////////////////
                    JPanel sud = new JPanel(new BorderLayout());
                    produsPanel.add(sud, BorderLayout.SOUTH);
                    sud.add(numarProdusePanel, BorderLayout.CENTER);

                    PreparedStatement ps_reteta = conn.prepareStatement("SELECT COUNT(Produse_nr_produs) FROM Retete r WHERE r.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");
                    PreparedStatement ps_stoc = conn.prepareStatement("SELECT COUNT(Produse_nr_produs) FROM stocuri_produs sp WHERE sp.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");

                    ps_reteta.setString(1, nume_produs);
                    ps_stoc.setString(1, nume_produs);

                    ResultSet rs_reteta = ps_reteta.executeQuery();
                    rs_reteta.next();
                    int reteta = rs_reteta.getInt(1);
                    ResultSet rs_stoc = ps_stoc.executeQuery();
                    rs_stoc.next();
                    int stoc = rs_stoc.getInt(1);

                    PreparedStatement ps;
                    ResultSet rs3;

                    if (reteta != 0) {
                        ps = conn.prepareStatement(
                                "SELECT MIN(FLOOR((SELECT i.stoc_ingredient/r.cantitate_ingredient FROM DUAL))) as nr_preparate_disponibile\n"
                                + "FROM Produse p, Retete r, Ingrediente i \n"
                                + "WHERE p.nr_produs = r.Produse_nr_produs and i.id_ingredient = r.Ingrediente_id_ingredient and p.nume_produs = ?\n"
                                + "GROUP BY p.nume_produs");
                        ps.setString(1, nume_produs);
                        rs3 = ps.executeQuery();
                        rs3.next();
                    } else if (stoc != 0) {
                        ps = conn.prepareStatement("SELECT stoc_produs FROM stocuri_produs WHERE Produse_nr_produs = (SELECT nr_produs FROM produse WHERE nume_produs = ?)");
                        ps.setString(1, nume_produs);
                        rs3 = ps.executeQuery();
                        rs3.next();
                    } else {
                        ps = null;
                        rs3 = null;
                    }

                    JLabel jLabel = new JLabel("Stoc disponibil:");
                    String nr_produse_disponibile = (rs3 != null) ? rs3.getString(1) : "0";
                    JTextField nr_produseTF = new JTextField(nr_produse_disponibile);
                    nr_produseTF.setEditable(false);

                    JPanel dreapta = new JPanel(new BorderLayout());
                    dreapta.add(jLabel, BorderLayout.WEST);
                    dreapta.add(nr_produseTF, BorderLayout.EAST);

                    nr_produse_ramase.add(new Pair<>(nr_produseTF, nume_produs));

                    sud.add(dreapta, BorderLayout.EAST);

                }
            }

            //ADAUGARE BUTON PENTRU FINALIZARE COMANDA
            javax.swing.JButton button = new JButton();
            button.setText("Vizualizare Comanda");
            java.awt.GridBagConstraints gridBagConstraints;
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = ct++;
            gridBagConstraints.ipadx = 32;
            gridBagConstraints.ipady = 27;
            CtgPanel.add(button, gridBagConstraints);
            button.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
            button.setActionCommand("vizualize");

            //ADAUGARE BUTON PENTRU REFRESH STOC
            javax.swing.JButton button2 = new JButton();
            button2.setText("Refresh stocuri");
            java.awt.GridBagConstraints gridBagConstraints2;
            gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = ct++;
            gridBagConstraints2.ipadx = 32;
            gridBagConstraints2.ipady = 27;
            CtgPanel.add(button2, gridBagConstraints2);
            button2.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
            button2.setActionCommand("Refresh stocuri");

            //////////////////////////////////////////
        } catch (SQLException ex) {
            Logger.getLogger(ApplicationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void refreshTF() {
        quantityTF.forEach((jTF) -> {
            jTF.setText("0");
        });
    }

    public void refreshStocuri() {

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        try {

            for (Pair pair : this.nr_produse_ramase) {
                PreparedStatement ps_reteta = conn.prepareStatement("SELECT COUNT(Produse_nr_produs) FROM Retete r WHERE r.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");
                PreparedStatement ps_stoc = conn.prepareStatement("SELECT COUNT(Produse_nr_produs) FROM stocuri_produs sp WHERE sp.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");

                ps_reteta.setString(1, pair.getValue().toString());
                ps_stoc.setString(1, pair.getValue().toString());

                ResultSet rs_reteta = ps_reteta.executeQuery();
                rs_reteta.next();
                int reteta = rs_reteta.getInt(1);
                ps_reteta.close();
                
                ResultSet rs_stoc = ps_stoc.executeQuery();
                rs_stoc.next();
                int stoc = rs_stoc.getInt(1);
                ps_stoc.close();

               
                
                
                PreparedStatement ps;
                ResultSet rs3;

                if (reteta != 0) {
                    ps = conn.prepareStatement(
                            "SELECT MIN(FLOOR((SELECT i.stoc_ingredient/r.cantitate_ingredient FROM DUAL))) as nr_preparate_disponibile\n"
                            + "FROM Produse p, Retete r, Ingrediente i \n"
                            + "WHERE p.nr_produs = r.Produse_nr_produs and i.id_ingredient = r.Ingrediente_id_ingredient and p.nume_produs = ?\n"
                            + "GROUP BY p.nume_produs");
                    ps.setString(1, pair.getValue().toString());
                    rs3 = ps.executeQuery();
                    rs3.next();
                } else if (stoc != 0) {
                    ps = conn.prepareStatement("SELECT stoc_produs FROM stocuri_produs WHERE Produse_nr_produs = (SELECT nr_produs FROM produse WHERE nume_produs = ?)");
                    ps.setString(1, pair.getValue().toString());
                    rs3 = ps.executeQuery();
                    rs3.next();
                } else {
                    ps = null;
                    rs3 = null;
                }

                JTextField TF = (JTextField) pair.getKey();
                String nr_produse_disponibile = (rs3 != null) ? rs3.getString(1) : "0";
                TF.setText(nr_produse_disponibile);

                if (rs3 != null) rs3.close();
                
            }

        } catch (SQLException ex) {

          Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex);

            
        }
    }

    public void increaseTextArea(ActionEvent e) {
        JButton tmpJButton = (JButton) e.getSource();
        Component[] components = tmpJButton.getParent().getComponents();
        JTextField textField = new JTextField();
        textField = (JTextField) components[1];
        int i = Integer.parseInt(textField.getText());
        String s = String.valueOf(i + 1);
        textField.setText(s);
    }

    public void decreaseTextArea(ActionEvent e) {
        JButton tmpJButton = (JButton) e.getSource();
        Component[] components = tmpJButton.getParent().getComponents();
        JTextField textField = new JTextField();
        textField = (JTextField) components[1];

        int i = Integer.parseInt(textField.getText());
        String s = "";
        if (i > 0) {
            s = String.valueOf(i - 1);
        } else {
            s = "0";
        }
        textField.setText(s);
    }

    public void finalizeOrder(ActionEvent e) {
        try {
            Connection conn = appWindow.getDataBaseConnection().getConnection();
            int index = 3;
            String str =
                      "DECLARE\n"
                    + "    no_stock EXCEPTION;\n"
                    + "    error_order EXCEPTION;\n"
                    + "    produse_comandate produse_comenzi.nr_produse_comandate%TYPE;\n"
                    + "    nr_masa_insert Comenzi.nr_masa%TYPE;\n"
                    + "    detalii_suplimentare comenzi.detalii_suplimentare_comanda%TYPE;\n"
                    + "    \n"
                    + "    produs_in_reteta Ingrediente.id_ingredient%TYPE;\n"
                    + "    produs_in_stoc stocuri_produs.stoc_produs%TYPE;\n"
                    + "BEGIN\n"
                    + "\n"
                    + "    SAVEPOINT sp;\n"
                    + "\n"
                    + "    nr_masa_insert := ?;\n"
                    + "    detalii_suplimentare := ?;\n"
                    + "    INSERT INTO Comenzi(id_comanda, data_comanda, nr_masa, detalii_suplimentare_comanda) VALUES(NULL,SYSDATE,nr_masa_insert,detalii_suplimentare);";

            for (int i = 0; i < produseleMele.size(); i++) {
                str = str
                        + "    BEGIN\n"
                        + "        produse_comandate := ?;\n"
                        + "        INSERT INTO produse_comenzi(nr_produse_comandate, Produse_nr_produs, Comenzi_id_comanda) VALUES(produse_comandate, (SELECT nr_produs FROM Produse WHERE nume_produs = ?), (SELECT MAX(id_comanda) FROM Comenzi));\n"
                        + "\n"
                        + "        SELECT COUNT(Produse_nr_produs) INTO produs_in_reteta FROM Retete r WHERE r.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?);\n"
                        + "        SELECT COUNT(Produse_nr_produs) INTO produs_in_stoc FROM stocuri_produs sp WHERE sp.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?);\n"
                        + "\n"
                        + "        IF (produs_in_reteta > 0) THEN\n"
                        + "            UPDATE Ingrediente i\n"
                        + "            SET stoc_ingredient = stoc_ingredient - produse_comandate * (SELECT r.cantitate_ingredient FROM Retete r WHERE r.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) and r.Ingrediente_id_ingredient = i.id_ingredient)\n"
                        + "            WHERE EXISTS (SELECT 1 FROM Retete r WHERE Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?) and r.Ingrediente_id_ingredient = i.id_ingredient);\n"
                        + "        ELSIF (produs_in_stoc > 0) THEN\n"
                        + "            UPDATE stocuri_produs sp\n"
                        + "            SET stoc_produs = stoc_produs - produse_comandate\n"
                        + "            WHERE sp.Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?);\n"
                        + "        ELSE \n"
                        + "            RAISE no_stock;\n"
                        + "        END IF;\n"
                        + "    END;\n";
            }
            str +=     
                      "COMMIT;\n"
                    + "\n"
                    + "    EXCEPTION\n"
                    + "        WHEN OTHERS THEN\n"
                    + "            ROLLBACK TO sp;\n"
                    + "            RAISE error_order;"
                    + "END;";
            selectProduct = conn.prepareStatement(str);
            short val = Short.valueOf(String.valueOf(tableComboBox.getSelectedIndex() + 1));
            selectProduct.setShort(1, val);
            selectProduct.setString(2, (String) detailsComboBox.getSelectedItem());
            for (int i = 0; i < produseleMele.size(); i++) {
                String nume = produseleMele.get(i).get(0);
                String cantitate = produseleMele.get(i).get(2);
                if (index % 7 == 3) {
                    selectProduct.setString(index, cantitate);
                }
                selectProduct.setString(index + 1, nume);
                selectProduct.setString(index + 2, nume);
                selectProduct.setString(index + 3, nume);
                selectProduct.setString(index + 4, nume);
                selectProduct.setString(index + 5, nume);
                selectProduct.setString(index + 6, nume);
                index += 7;
            }

            selectProduct.execute();
            JOptionPane.showMessageDialog(this, "Comanda inregistrata cu succes");
            emptyOrderBag();
            vizualizeOrder();
            refreshTF();

        } catch (SQLException ex) {
            if(ex.getMessage().contains("ORA-06550") || ex.getMessage().contains("ORA-06510"))
                JOptionPane.showMessageDialog(this, "Eroare la gestionarea comenzii.\nStoc indisponibil pentru unul dintre produse.");
            else
                JOptionPane.showMessageDialog(this, "Eroare la gestionarea comenzii.");
                //Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }

    public void vizualizeOrder() {
        float costComanda = 0;
        int par = 1;
        JPanel commandPanel = new JPanel(new BorderLayout());

        commandPanel.setBackground(new Color(115, 89, 105));
        commandPanel.setName("Finalizare Comanda");
        commandPanel.setVisible(true);
        DynamicPanel.add(commandPanel, "Finalizare comanda");

        JLabel foodPicture = new JLabel();
        JLabel foodPicture2 = new JLabel();

        foodPicture.setBackground(new Color(255, 255, 255));
        foodPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        foodPicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Poze/food1.png"))); // NOI18N
        foodPicture.setVerifyInputWhenFocusTarget(false);
        commandPanel.add(foodPicture, BorderLayout.NORTH);

        foodPicture2.setBackground(new Color(255, 255, 255));
        foodPicture2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        foodPicture2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Poze/food1.png"))); // NOI18N
        foodPicture2.setVerifyInputWhenFocusTarget(false);
        commandPanel.add(foodPicture2, BorderLayout.SOUTH);

        //Adaugare text box in vizualizare comanda
        JTextArea jTextArea1 = new javax.swing.JTextArea();
        JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1.setBounds(0, 0, 1000, 1000);
        jScrollPane2.setBounds(0, 0, 400, 400);

        jScrollPane2.setPreferredSize(new Dimension(500, 500));

        jTextArea1.setColumns(10);
        jTextArea1.setRows(3);
        jTextArea1.setVisible(true);
        jScrollPane2.setViewportView(jTextArea1);

        for (ArrayList<String> list : produseleMele) {
            float pret = 0;
            for (String str : list) {
                if (par % 3 == 1) {
                    jTextArea1.append(">>> ");
                    jTextArea1.append(str + "              ");
                }
                if (par % 3 == 2) {
                    float i = Float.parseFloat(str);
                    pret = i;
                    jTextArea1.append(str + " lei");
                }
                if (par % 3 == 0) {
                    float i = Float.parseFloat(str);
                    costComanda = costComanda + pret * i;
                    jTextArea1.append("  x  " + str + "\n");
                }
                par++;
            }
        }
        jTextArea1.append("\nCost total comanda: " + costComanda + " lei\n");

        jTextArea1.setFont(new Font("TimesRoman", Font.PLAIN, 16));

        jTextArea1.setLineWrap(true);
        jTextArea1.setEditable(false);

        commandPanel.add(jScrollPane2, BorderLayout.CENTER);

        //ADAUGARE BUTON PENTRU FINALIZARE COMANDA
        JButton button = new JButton();
        button.setText("Finalizare Comanda");
        GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.ipady = 27;
        button.setSize(new Dimension(20, 20));
        commandPanel.add(button, BorderLayout.WEST);
        button.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        button.setActionCommand("finalize");

        //////////////////////////////////////////
        //ADAUGARE BUTON PENTRU GOLIRE COS COMANDA
        JButton emptyButton = new JButton();
        emptyButton.setText("Golire Cos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.ipady = 27;
        emptyButton.setSize(new Dimension(20, 20));
        commandPanel.add(emptyButton, BorderLayout.EAST);
        emptyButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        emptyButton.setActionCommand("emptyOrder");

        JPanel panouDreapta = new JPanel(new BorderLayout());
        //JComboBox tableComboBox = new JComboBox();
        //JComboBox detailsComboBox = new JComboBox();

        tableComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Masa 1", "Masa 2", "Masa 3", "Masa 4", "Masa 5", "Masa 6"}));
        tableComboBox.setSelectedItem("Masa 1");
        panouDreapta.add(tableComboBox, BorderLayout.SOUTH);

        detailsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Plata numerar", "Plata card"}));
        detailsComboBox.setSelectedItem("Plata numerar");
        panouDreapta.add(detailsComboBox, BorderLayout.NORTH);

        panouDreapta.add(emptyButton, BorderLayout.CENTER);
        panouDreapta.setBackground(new Color(100, 100, 100));
        commandPanel.add(panouDreapta, BorderLayout.EAST);

        //////////////////////////////////////////
        CardLayout cardLayout;
        cardLayout = (CardLayout) DynamicPanel.getLayout();
        cardLayout.show(DynamicPanel, "Finalizare comanda");

    }

    public void emptyOrderBag() {
        produseleMele.removeAll(produseleMele);
    }

    public void BackFunction() {
        this.setVisible(false);
        appWindow.setVisible(true);
    }

    public void startAction(ActionEvent e) {
        CardLayout cardLayout;
        cardLayout = (CardLayout) DynamicPanel.getLayout();
        javax.swing.JButton tmpJButton = (javax.swing.JButton) e.getSource();
        cardLayout.show(DynamicPanel, tmpJButton.getText());
    }

    public void addProducts(ActionEvent e) {
        JButton tmpJButton = (JButton) e.getSource();

        Component[] components = tmpJButton.getParent().getComponents();

        //DEPLASARE PRIN COMPONENTELE PRODUSULUI MEU
        JPanel panouJPanel = new JPanel();
        panouJPanel = (JPanel) components[3]; // Panou din SUD

        components = panouJPanel.getComponents();

        panouJPanel = (JPanel) components[0]; // Panou din STANGA

        components = panouJPanel.getComponents();

        //DEPLASARE PRIN ELEMENTELE DIN PANELUL CARE FACE PARTE DIN PRODUSUL MEU
        JTextField textField = new JTextField();
        textField = (JTextField) components[1];
        quantityTF.add(textField);

        int ct = Integer.parseInt(textField.getText());
        int ok;
        int ctok;

        try {
            Connection conn = appWindow.getDataBaseConnection().getConnection();
            Statement st = conn.createStatement();
            String str = "SELECT p.pret FROM Produse p WHERE p.nume_produs = ?";
            selectProduct = conn.prepareStatement(str);
            selectProduct.setString(1, tmpJButton.getParent().getName());
            ResultSet rs = selectProduct.executeQuery();
            while (rs.next()) {
                String y = rs.getString(1);
                String a[] = new String[]{tmpJButton.getParent().getName(), y, String.valueOf(ct)};

                ok = 0;
                if (ct == 0) {
                    ctok = 0;
                } else {
                    ctok = 1;
                }
                int index = 0;
                for (int i = 0; i < produseleMele.size(); i++) {
                    if (produseleMele.get(i).get(0) == a[0]) {
                        ok = 1;
                        index = i;
                    }
                }
                if (ok == 0 && ctok == 0) {
                    JOptionPane.showMessageDialog(this, "Pentru a adauga produsul trebuie selectata cantitatea");
                }
                if (ok == 0 && ctok == 1) {
                    produseleMele.add(new ArrayList<String>(Arrays.asList(a)));
                }
                if (ok == 1 && ctok == 0) {
                    produseleMele.remove(index);
                }
                if (ok == 1 && ctok == 1) {
                    produseleMele.set(index, new ArrayList<String>(Arrays.asList(a)));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        CtgPanel = new javax.swing.JPanel();
        DynamicPanel = new javax.swing.JPanel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Fereastra meniu client");

        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        CtgPanel.setBackground(new java.awt.Color(204, 51, 0));
        CtgPanel.setLayout(new java.awt.GridBagLayout());
        mainPanel.add(CtgPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, -1));

        DynamicPanel.setBackground(new java.awt.Color(204, 153, 0));
        DynamicPanel.setLayout(new java.awt.CardLayout());
        mainPanel.add(DynamicPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(212, 0, 1260, 720));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CtgPanel;
    public javax.swing.JPanel DynamicPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
