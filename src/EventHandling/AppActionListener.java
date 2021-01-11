/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventHandling;

import javafx.application.Application;
import mainpackage.ApplicationWindow;
import mainpackage.ClientWindow;

/**
 *
 * @author cosmi
 */
public class AppActionListener {

    private final ApplicationWindow applicationWindow;
    private final ButtonClickListener buttonClickListener;
    
    public AppActionListener(ApplicationWindow applicationWindow)
    {
        this.applicationWindow = applicationWindow;
        
        buttonClickListener = new ButtonClickListener(this.applicationWindow);
        //
    }
    
    public ButtonClickListener getButtonClickListener()
    {
        return buttonClickListener;
    }
    
}
