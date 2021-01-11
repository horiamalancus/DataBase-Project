package mainpackage;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import mainpackage.AdminWindowPanels.AdminMainPanel;

public class AdminWindow extends javax.swing.JFrame {

    private final ApplicationWindow appWindow;
    public mainpackage.AdminWindowPanels.AdminMainPanel adminMainPanel;
    
    public AdminWindow(ApplicationWindow appWindow) {

        this.appWindow = appWindow;
        initComponents();
        initComponents2();
        
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Admin window closed.");
                e.getWindow().setVisible(false);
                appWindow.setVisible(true);
                e.getWindow().dispose();     
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Fereastra Admin");
        setPreferredSize(new java.awt.Dimension(1440, 900));
        setSize(new java.awt.Dimension(0, 0));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private void initComponents2()
    {
        adminMainPanel = new mainpackage.AdminWindowPanels.AdminMainPanel(appWindow);
        getContentPane().add(adminMainPanel, java.awt.BorderLayout.CENTER);
        pack();
    }


};
