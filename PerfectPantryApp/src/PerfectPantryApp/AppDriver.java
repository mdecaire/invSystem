package PerfectPantryApp;

import java.sql.SQLException;
import javax.swing.*;
import java.util.logging.*;
/**
 *
 * @author Hira, Josh
 */
public class AppDriver {
    
    
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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(PerfectPantryGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        //</editor-fold>

        /* Create and display the form */
    	
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new PerfectPantryGUI().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(AppDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); 
       
    }
    
}
