package PerfectPantryApp;

import java.util.Vector;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Josh
 */
public class SLTableModel extends AbstractTableModel {
    String[] columnNames = {"Name", "Quantity", "Category", "Edit", "Delete", };
    Class[] columnClasses = {String.class, String.class, String.class, 
        JButton.class, JButton.class};
    Vector inventory; //shopping list

    
    public SLTableModel() {
        this.inventory = new Vector();
    }
    
    public Vector getInventory() {
        return inventory;
    }
    
    public void addInventoryItem(InventoryItem item) {
        inventory.addElement(item);
        fireTableRowsInserted(inventory.size()-1, inventory.size()-1);
    }
    
    public void update(InventoryItem item) {
        int index = inventory.indexOf(item);
        if (index != -1){
            fireTableRowsUpdated(index, index);
        }
    }
    
    @Override
    public int getRowCount() {
        return inventory.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
     }

    @Override
    public String getColumnName(int columnIndex) {
      return columnNames[columnIndex];
    }

    @Override
    public Class getColumnClass(int columnIndex){
      return columnClasses[columnIndex];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InventoryItem item = (InventoryItem) inventory.elementAt(rowIndex);
        if (columnIndex == 0) {return item.name;}
        else if (columnIndex == 1) {return item.sizeDisplay;}
        else if (columnIndex == 2) {return item.category;}
        else if (columnIndex == 3) {return "Edit";}
        else if (columnIndex == 4) {return "Delete";}
        else return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col){

      if (col == 3 || col == 4) {
        return true;
      } else {
        return false;
      }
    }
}
