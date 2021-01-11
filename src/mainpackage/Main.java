/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

/**
 *
 * @author cosmi
 */
public class Main {
        public static void main(String args[]) {

            java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ApplicationWindow app = new ApplicationWindow();
                app.setVisible(true);
            }
            
        });
            
    }
        
};

