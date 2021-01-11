/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventHandling;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import mainpackage.ApplicationWindow;
import mainpackage.ClientWindow;

/**
 * s
 *
 * @author cosmi
 */
public class ButtonClickListener implements ActionListener {

    private ApplicationWindow appWindow;

    ButtonClickListener(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        CardLayout cardLayout;

        switch (command) {
            case "Admin":
                appWindow.setVisible(false);
                appWindow.startAdminWindow();
                break;

            case "Client":
                appWindow.setVisible(false);
                appWindow.startClientWindow();
                break;

            //////////////////////////// Meniu logare ////////////////////////////
            case "Creare cont nou":
                cardLayout = (CardLayout) appWindow.getAdminWindow().adminMainPanel.loginPanels.getLayout();
                cardLayout.show(appWindow.getAdminWindow().adminMainPanel.loginPanels, "card2");
                break;
            
            case "Creare cont":
                appWindow.getAdminWindow().adminMainPanel.newAccount();
                break;

            case "Înapoi":
                cardLayout = (CardLayout) appWindow.getAdminWindow().adminMainPanel.loginPanels.getLayout();
                cardLayout.show(appWindow.getAdminWindow().adminMainPanel.loginPanels, "card1");
                break;

            case "Logare":
                int check = 0;
                check = appWindow.getAdminWindow().adminMainPanel.personalCheckFunction(check);
                if(check == 1)
                {
                    cardLayout = (CardLayout) appWindow.getAdminWindow().adminMainPanel.getLayout();
                    cardLayout.show(appWindow.getAdminWindow().adminMainPanel, "card2");
                    
                }
                break;

            case "ButonMeniuAdmin":
                cardLayout = (CardLayout) appWindow.getAdminWindow().adminMainPanel.rightSideAdminPanel.getLayout();
                javax.swing.JButton tmpJButton = (javax.swing.JButton) e.getSource();
                cardLayout.show(appWindow.getAdminWindow().adminMainPanel.rightSideAdminPanel, tmpJButton.getText());
                break;

            ///////////// BUTOANE PANOURI ADMIN /////////////
                
            case "ButoaneMeniuriAdmin":
                appWindow.getAdminWindow().adminMainPanel.menusAdminPanel.startAction(e);
                break;

            case "ButoaneCategoriiAdmin":
                appWindow.getAdminWindow().adminMainPanel.categoriesAdminPanel.startAction(e);
                break;

            case "ButoaneCategoriiProduseAdmin":
                appWindow.getAdminWindow().adminMainPanel.categoriesProductsAdminPanel.startAction(e);
                break;

            case "ButoaneIngredienteAdmin":
                appWindow.getAdminWindow().adminMainPanel.ingredientsAdminPanel.startAction(e);
                break;

            case "ButoaneComenziAdmin":
                appWindow.getAdminWindow().adminMainPanel.ordersAdminPanel.startAction(e);
                break;

            case "ButoaneStocuriAdmin":
                appWindow.getAdminWindow().adminMainPanel.productStockPanel.startAction(e);
                break;

            case "ButoaneTipuriProdusAdmin":
                appWindow.getAdminWindow().adminMainPanel.productTypeAdminPanel.startAction(e);
                break;

            case "ButoaneProduseAdmin":
                appWindow.getAdminWindow().adminMainPanel.productsAdminPanel.startAction(e);
                break;

            case "ButoaneProduseComenziAdmin":
                appWindow.getAdminWindow().adminMainPanel.productsOrdersAdminPanel.startAction(e);
                break;

            case "ButoaneReteteAdmin":
                appWindow.getAdminWindow().adminMainPanel.recipesAdminPanel.startAction(e);
                break;
                
            //////////////////////////////////////////////////

            case "SchimbarePanouMeniuClient":
                appWindow.clientWindow.startAction(e);
                break;
            case "Back":
                appWindow.clientWindow.BackFunction();
                break;
            case "vizualize":
                appWindow.clientWindow.vizualizeOrder();
                break;
            case "finalize":
                appWindow.clientWindow.finalizeOrder(e);
                appWindow.getClientWindow().refreshStocuri();
                System.out.println("Comanda inregistrata cu succes");
                break;
            case "Add":
                appWindow.clientWindow.addProducts(e);
                break;
            case "MINUS":
                appWindow.clientWindow.decreaseTextArea(e);
                break;
            case "PLUS":
                appWindow.clientWindow.increaseTextArea(e);
                break;
            case "emptyOrder":
                System.out.println("Am golit cosul de comanda");
                appWindow.clientWindow.emptyOrderBag();
                appWindow.clientWindow.vizualizeOrder();
                break;
            case "Delogare":
                System.out.println("ceva text de test");
                break;
                
            case "Refresh stocuri":
                appWindow.getClientWindow().refreshStocuri();
                break;
            
        }

    }

}
