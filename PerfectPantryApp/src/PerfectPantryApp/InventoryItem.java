/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PerfectPantryApp;

/**
 *
 * @author Josh
 * This class defines an inventory Item object for use in the GUI
 * It is used in PerfecPantyGUI and in InventoryTableModel
 */
public class InventoryItem {
    String upcDisplay;
    String name;
    String sizeDisplay; //quantity
    String uomDisplay;
    String category;
    String expiration;
    
    public InventoryItem(String upcDisplay, String name, String sizeDisplay,
                    String uomDisplay, String category, String expiration,
                    String quantityDisplay) {
        this.upcDisplay = upcDisplay;
        this.name = name;
        this.sizeDisplay = sizeDisplay; //quantity
        this.uomDisplay = uomDisplay;
        this.category = category;
        this.expiration = expiration;
    }
    //Constructor for shopping list items
    public InventoryItem(String name, String sizeDisplay, String category) {
        this.name = name;
        this.sizeDisplay = sizeDisplay; //quantity
        this.category = category;
    }
}
