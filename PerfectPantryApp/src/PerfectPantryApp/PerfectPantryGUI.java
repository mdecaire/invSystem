package PerfectPantryApp;

import java.awt.event.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.TableCellRenderer;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * This class creates the GUI for the Perfect Pantry application.
 * @author Hira Waqas, Josh Gugel, Michelle Decaire
 */
public class PerfectPantryGUI extends JFrame {
    
    private InventoryData invData;
    private ShoppingData shopData;
    private NutritionData nutData;
    static JFrame  thisFrame;
    /**
     * Creates new form PerfectPantryGUI
     */
    public PerfectPantryGUI() throws SQLException {
        //JDBC jdbc = new JDBC();
        Connection conn = JDBC.getConnection();
        if (conn == null) {
            DBPropertiesDialog dialog = new DBPropertiesDialog(null);
            dialog.run();
        }

        invData = new InventoryData();
        shopData=new ShoppingData();
        nutData= new NutritionData();
        thisFrame=this;
        initComponents();
    }

    private void populateShoppingTable(String listName) {
        shopListNameLabel.setText(listName);
        shopListTable.setModel(shopData.setShoppingList(listName));
        JButtonRenderer jbRender = new JButtonRenderer();
        shopListTable.setDefaultRenderer(JButton.class, jbRender);
        EditTableEditor editEditor = new EditTableEditor(new JCheckBox());
        shopListTable.getColumnModel().getColumn(3).setCellEditor(editEditor);
        DeleteTableEditor deleteEditor = new DeleteTableEditor(new JCheckBox());
        shopListTable.getColumnModel().getColumn(4).setCellEditor(deleteEditor);
        shopListTable.repaint();
        shopListTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        shopListTable.getColumnModel().getColumn(1).setPreferredWidth(15);
        shopListTable.getColumnModel().getColumn(2).setPreferredWidth(175);
        shopListTable.getColumnModel().getColumn(3).setPreferredWidth(5);
        shopListTable.getColumnModel().getColumn(4).setPreferredWidth(5);
    }

    class AddInventoryDialog extends JDialog implements ActionListener{
        private String[] data;
        private JComboBox uomComboBox;
        private JLabel upcLabel;
        private JLabel qtyLabel;
        private JLabel uomLabel;
        private JLabel expirationLabel;
        private JTextField upcTextField;
        private JTextField qtyTextField;
        private JTextField expirationTextField;
        private JButton addBtn;
        private JButton cancelBtn;
        private  boolean addSuccess=false;
        //Constructor
        public AddInventoryDialog(Frame frame, InventoryData invData){
            super(frame, "Add Item", true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            data = new String[4];
            for (int i=0; i < data.length; i++) {
                data[i] = null;
            }
            JPanel panel = new JPanel();
            GridBagLayout grid = new GridBagLayout();
            panel.setLayout(grid);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            //UPC
            upcLabel = new JLabel("Item UPC (12 digits)*");
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(upcLabel, gbc);
            upcTextField = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(upcTextField, gbc);
            
            //Quantity
            qtyLabel = new JLabel("Quantity*");
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add( qtyLabel, gbc);
            qtyTextField = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(qtyTextField, gbc);
            
            //Unit of Measurment
            uomLabel = new JLabel("Unit of Measurment");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(uomLabel, gbc);
            String[] uomStrings = {"unit", "pc.", "dozen", "lb.", "oz.", "g", "gal", 
                                    "qt.", "cup"};
            uomComboBox = new JComboBox(uomStrings);
            uomComboBox.setSelectedIndex(0);
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(uomComboBox, gbc);
            
            //Expiration
            expirationLabel = new JLabel("Expiration");
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(expirationLabel, gbc);
            expirationTextField = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(expirationTextField, gbc);
            
            
            //Add and Cancel Buttons
            addBtn = new JButton("Add");
            addBtn.addActionListener(this);
            gbc.gridx = 0;
            gbc.gridy = 4;
            panel.add(addBtn, gbc);
            cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(this);
            gbc.gridx = 1;
            gbc.gridy = 4;
            panel.add(cancelBtn, gbc);
            
            getContentPane().add(panel);
            pack();    
            setLocationRelativeTo(null);
        }
        
        public String [] run() {
            this.setVisible(true);
            return data;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addBtn) {
                data[0] = upcTextField.getText();
                data[1] = qtyTextField.getText();
                data[2] = (String)uomComboBox.getSelectedItem();
                data[3] = expirationTextField.getText();
                //data[4] = usageTextField.getText();
                
                //Validate data
                String upcCheck = invData.ValidateUPC(data[0]);
                switch (upcCheck) {
                    case "valid":
                        //check to see if record already exists in inventory
                        if (invData.CheckExists()) {
                            //increments the quantity and estimated usage
                            if (invData.adjustQuantity(data)){
                                populatePantryList();
                                JOptionPane.showMessageDialog(this, "Quantity added to existing entry");
                                return;
                            } else {
                                JOptionPane.showMessageDialog(this, "Inventory not updated"); //failed to increment
                                return;
                            }
                        } else {
                            //UPC okay to add to Inventory
                            if(invData.AddInventory(data,0)) {
                                populatePantryList();
                                populateNutritionTable();
                                JOptionPane.showMessageDialog(this, "Record has been updated");
                                dispose();
                                break;
                            } else {
                                return;
                            }
                        }
                    case "empty":
                        JOptionPane.showMessageDialog(this, "Invalid Input: UPC must not be empty");
                        return;
                    case "length":
                        JOptionPane.showMessageDialog(this, "UPC must be a 12 digit integer");
                        return;
                    case "notANum":
                        JOptionPane.showMessageDialog(this, "UPC must be a numeric value");
                        return;
                    case "notFound":
                        if(!createDialog(data[0])) {return;}
                        int productID= productInput.getProductID();
                        if(invData.AddInventory(data, productID)) {
                            populatePantryList();
                            populateNutritionTable();
                            JOptionPane.showMessageDialog(this, "Record has been updated");
                            dispose();
                         }else{
                            JOptionPane.showMessageDialog(this, "Product Not Updated"); 
                        }
                    default:
                        break;
                }

            } else if (e.getSource() == cancelBtn) {
                dispose(); 
            }
        }
        ProductDialog productInput = null;

        //dialog box to initliaze product insertion
        private boolean createDialog(String upc) {
            int n = JOptionPane.showOptionDialog(this,
                    "This Product does not exist in "
                    + "our system.\r\n Would you like to add it now?", "Add Product Now?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                productInput = new ProductDialog(thisFrame, upc);
                return productInput.addSuccessful();
            } else {
                JOptionPane.showMessageDialog(this, "Product Addition Cancelled");
                return false;//if no is selected 
            }

        }

    }
    
    class EditInventoryDialog extends JDialog implements ActionListener{
        private String[] data;
        private DefaultComboBoxModel model;
        private JComboBox uomComboBox;
        private JLabel qtyLabel;
        private JLabel uomLabel;
        private JLabel expirationLabel;
        //private JLabel usageLabel;
        private JTextField qtyTextField;
        private JTextField expirationTextField;
        //private JTextField usageTextField;
        private JButton updateBtn;
        private JButton cancelBtn;
        private  boolean editSuccess=false;
        //Constructor
        public EditInventoryDialog(Frame frame, String[] dataIn){
            super(frame, "Edit Item", true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            data = dataIn; // String[5]
            JPanel panel = new JPanel();
            GridBagLayout grid = new GridBagLayout();
            panel.setLayout(grid);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            //Quantity
            qtyLabel = new JLabel("Quantity*");
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add( qtyLabel, gbc);
            qtyTextField = new JTextField(data[1], 10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(qtyTextField, gbc);
            
            //Unit of Measurment
            uomLabel = new JLabel("Unit of Measurment");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(uomLabel, gbc);
            String[] uomStrings = {"unit", "pc.", "dozen", "lb.", "oz.", "g", "gal", 
                                    "qt.", "cup"};
            model = new DefaultComboBoxModel(uomStrings);
            uomComboBox = new JComboBox(model);
            int n = model.getIndexOf(data[2]); 
            if (n == -1) {   //set the uom if it matches
                uomComboBox.setSelectedIndex(0);
            } else {
                uomComboBox.setSelectedIndex(n);
            }
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(uomComboBox, gbc);
            
            //Expiration
            expirationLabel = new JLabel("Expiration");
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(expirationLabel, gbc);
            expirationTextField = new JTextField(data[3], 10);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(expirationTextField, gbc);
            

            
            //Update and Cancel Buttons
            updateBtn = new JButton("Update");
            updateBtn.addActionListener(this);
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel.add(updateBtn, gbc);
            cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(this);
            gbc.gridx = 1;
            gbc.gridy = 5;
            panel.add(cancelBtn, gbc);
            
            getContentPane().add(panel);
            pack();    
            setLocationRelativeTo(null);
        }
        
        public String [] run() {
            this.setVisible(true);
            return data;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == updateBtn) {
                data[1] = qtyTextField.getText();
                data[2] = (String)uomComboBox.getSelectedItem();
                data[3] = expirationTextField.getText();
                //data[4] = usageTextField.getText();
                
                
                //Validate data
                String upcCheck = invData.ValidateUPC(data[0]);
                //check to see if record already exists in inventory
                if (invData.CheckExists()) {
                    //should always get here
                    if (invData.EditInventory(data)){
                        populatePantryList();
                        JOptionPane.showMessageDialog(this, "Item Updated");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Inventory not updated"); //failed to increment
                        return;
                    }
                } else {
                    //should never get here?
                    System.out.println("UPC does not exist in Inventory list");
                    return;
                }
            } else if (e.getSource() == cancelBtn) {
                dispose(); 
            }
        }
    }
    
    class AddItemSLDialog extends JDialog implements ActionListener{
        private String[] data;
        private JLabel nameLabel;
        private JTextField nameTextField;
        private JComboBox catComboBox;
        private JLabel catLabel;
        private JLabel qtyLabel;
        private JTextField qtyTextField;
        private JButton addBtn;
        private JButton cancelBtn;
        private JLabel listLabel;
        private JComboBox listComboBox;
        private  boolean addSuccess=false;
        //Constructor
        public AddItemSLDialog(Frame frame, ShoppingData shopData){
            super(frame, "Add Item", true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            data = new String[4];
            for (int i=0; i < data.length; i++) {
                data[i] = null;
            }
            JPanel panel = new JPanel();
            GridBagLayout grid = new GridBagLayout();
            panel.setLayout(grid);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            //Name
            nameLabel = new JLabel("Item Name*");
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(nameLabel, gbc);
            nameTextField = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(nameTextField, gbc);
            
            //Quantity
            qtyLabel = new JLabel("Quantity");
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add( qtyLabel, gbc);
            qtyTextField = new JTextField(10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(qtyTextField, gbc);
            
            

            //Category
            catLabel = new JLabel("Category");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(catLabel, gbc);
            String[] catStrings = {"Miscellaneous", "Produce", "Meats, Poultry, and Seafood",
                "Dairy and Refrigerated", "Pantry", "Breads and Bakery", "Baking, Herbs, and Spices", 
                "Beverages", "Household Supplies"};
            catComboBox = new JComboBox(catStrings);
            catComboBox.setSelectedIndex(0);
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(catComboBox, gbc);
            
            //List
            listLabel = new JLabel("Shopping List");
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(listLabel, gbc);
            String[] listStrings = shopData.getLists();
            listComboBox = new JComboBox(listStrings);
            listComboBox.setSelectedIndex(0);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(listComboBox, gbc);
            
            
            //Add and Cancel Buttons
            addBtn = new JButton("Add");
            addBtn.addActionListener(this);
            gbc.gridx = 0;
            gbc.gridy = 4;
            panel.add(addBtn, gbc);
            cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(this);
            gbc.gridx = 1;
            gbc.gridy = 4;
            panel.add(cancelBtn, gbc);
            
            getContentPane().add(panel);
            pack();    
            setLocationRelativeTo(null);
        }
        
        public String [] run() {
            this.setVisible(true);
            return data;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addBtn) {
                data[0] = (String)listComboBox.getSelectedItem();
                data[1] = nameTextField.getText();
                data[2] = qtyTextField.getText();
                data[3] = (String)catComboBox.getSelectedItem();
                
                //check if item is in table already
                Vector v = ((SLTableModel)shopListTable.getModel()).getInventory();
                for (int i=0; i<v.size(); i++) {
                    if (((InventoryItem)v.get(i)).name.equals(data[1])) {
                        JOptionPane.showMessageDialog(this, "Item already exists in list");
                        return;
                    }
                }
                
                if(shopData.AddItemSL(data)) { //if(true) {
                    populateShoppingTable(data[0]);
                    JOptionPane.showMessageDialog(this, "Item has been added!");
                    dispose();
                } else {
                    return;
                }
            } else if (e.getSource() == cancelBtn) {
                dispose(); 
            }
        }
    }
    
    class EditSLDialog extends JDialog implements ActionListener{
        private String[] data;
        private DefaultComboBoxModel model;
        private JComboBox catComboBox;
        private JLabel qtyLabel;
        private JLabel nameLabel;
        private JLabel catLabel;
        private JTextField qtyTextField;
        private JTextField nameTextField;
        private JButton updateBtn;
        private JButton cancelBtn;
        private  boolean editSuccess=false;
        //Constructor
        public EditSLDialog(Frame frame, String[] dataIn){
            super(frame, "Edit Item", true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            data = dataIn; // String[5]
            JPanel panel = new JPanel();
            GridBagLayout grid = new GridBagLayout();
            panel.setLayout(grid);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            //Name
            nameLabel = new JLabel("Name*");
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add( nameLabel, gbc);
            nameTextField = new JTextField(data[1], 10);
            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(nameTextField, gbc);
            
            //Quantity
            qtyLabel = new JLabel("Quantity*");
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add( qtyLabel, gbc);
            qtyTextField = new JTextField(data[2], 10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(qtyTextField, gbc);
            
            //Category
            catLabel = new JLabel("Category");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(catLabel, gbc);
            String[] catStrings = {"Miscellaneous", "Produce", "Meats, Poultry, and Seafood",
                "Dairy and Refrigerated", "Pantry", "Breads and Bakery", "Baking, Herbs, and Spices", 
                "Beverages", "Household Supplies"};
            model = new DefaultComboBoxModel(catStrings);
            catComboBox = new JComboBox(model);
            int n = model.getIndexOf(data[3]); 
            if (n == -1) {   //set the uom if it matches
                catComboBox.setSelectedIndex(0);
            } else {
                catComboBox.setSelectedIndex(n);
            }
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(catComboBox, gbc);
            
            //Update and Cancel Buttons
            updateBtn = new JButton("Update");
            updateBtn.addActionListener(this);
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(updateBtn, gbc);
            cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(this);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(cancelBtn, gbc);
            
            getContentPane().add(panel);
            pack();    
            setLocationRelativeTo(null);
        }
        
        public String [] run() {
            this.setVisible(true);
            return data;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == updateBtn) {
         
                data[1] = nameTextField.getText();
                data[2] = qtyTextField.getText();
                data[3] = (String)catComboBox.getSelectedItem();
    
                
                
                if (shopData.EditItemSL(data)){
                    populateShoppingTable(shopListNameLabel.getText());
                    JOptionPane.showMessageDialog(this, "Item Updated");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Item not updated"); //failed to increment
                    return;
                }
            } else if (e.getSource() == cancelBtn) {
                dispose(); 
            }
        }
    }
    
     class DBPropertiesDialog extends JDialog implements ActionListener{
        private String user = "root";
        private String pwd = "admin";
        private JLabel msgLabel;
        private JLabel userLabel;
        private JLabel pwdLabel;
        private JTextField userTextField;
        private JTextField pwdTextField;
        private JButton okBtn;
        private JButton cancelBtn;
        //private  boolean editSuccess=false;
        
        //Constructor
        public DBPropertiesDialog(Frame frame){
            super(frame, "Create db.properties", true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            JPanel panel = new JPanel();
            GridBagLayout grid = new GridBagLayout();
            panel.setLayout(grid);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            //Message
            msgLabel = new JLabel("Enter your MySQL username and password\n");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            panel.add(msgLabel, gbc);
            
            //User
            userLabel = new JLabel("User");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            panel.add(userLabel, gbc);
            userTextField = new JTextField("root", 10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(userTextField, gbc);
            
            //Password
            pwdLabel = new JLabel("Password");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(pwdLabel, gbc);
            pwdTextField = new JTextField("admin", 10);
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(pwdTextField, gbc);
             
            //Update and Cancel Buttons
            okBtn = new JButton("Ok");
            okBtn.addActionListener(this);
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(okBtn, gbc);
            cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(this);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(cancelBtn, gbc);
            
            getContentPane().add(panel);
            pack();    
            setLocationRelativeTo(null);
        }
        
        public void run() {
            this.setVisible(true);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okBtn) {
                user = userTextField.getText();
                pwd = pwdTextField.getText();
                
                try {
                    //create db.properties
                    PrintWriter writer = new PrintWriter("db.properties", "UTF-8");
                    writer.println("# MySQL DB properties");
                    writer.println("user=" + user);
                    writer.println("password=" + pwd);
                    writer.print("url=jdbc:mysql://localhost:3306/inventory_system?"
                            + "zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false");
                    writer.close();
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    //Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
                    //System.out.println("File not found, creating db.properties");
                }
                dispose(); 
            } else if (e.getSource() == cancelBtn) {
                //JOptionPane.showMessageDialog(null, "File db.properties not found");
                dispose(); 
            }
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        homeTabPane = new JTabbedPane();
        inventorySplitPane = new JSplitPane();
        inventoryLeftPanel = new JPanel();
        addInventoryButton = new JButton();
        categoriesPanel = new JPanel();
        bakingCB = new JCheckBox();
        beveragesCB = new JCheckBox();
        breadsBakeryCB = new JCheckBox();
        dairyRefCB = new JCheckBox();
        householdSCB = new JCheckBox();
        meatsPoultryCB = new JCheckBox();
        miscellaneousCB = new JCheckBox();
        ProduceCB = new JCheckBox();
        pantryCB = new JCheckBox();
        inventoryRightPanel = new JPanel();
        sortingPanel = new JPanel();
        sortingLabel = new JLabel();
        sortingComboBox = new JComboBox<>();
        jScrollPane1 = new JScrollPane();
        inventoryTable = new JTable();
        viewInventoryButton = new JButton();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Perfect Pantry");

        addInventoryButton.setText("Add Inventory");
        addInventoryButton.addActionListener(e-> addInventoryButton());
        categoriesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Categories"));

        bakingCB.setText("Baking, Herbs, and Spices");
        bakingCB.addActionListener(e-> populatePantryList());

        beveragesCB.setText("Beverages");
        beveragesCB.addActionListener(e-> populatePantryList());

        breadsBakeryCB.setText("Breads and Bakery");
        breadsBakeryCB.addActionListener(e-> populatePantryList());

        dairyRefCB.setText("Dairy and Refrigerated");
        dairyRefCB.addActionListener(e-> populatePantryList());

        householdSCB.setText("Household Supplies");
        householdSCB.addActionListener(e-> populatePantryList());

        meatsPoultryCB.setText("Meats and Poultry");
        meatsPoultryCB.addActionListener(e-> populatePantryList());

        miscellaneousCB.setText("Miscellaneous");
        miscellaneousCB.addActionListener(e-> populatePantryList());
        
        pantryCB.setText("Pantry");
        pantryCB.addActionListener(e-> populatePantryList());
        
        ProduceCB.setText("Produce");
        ProduceCB.addActionListener(e-> populatePantryList());
        
        javax.swing.GroupLayout categoriesPanelLayout = new GroupLayout(categoriesPanel);
        categoriesPanel.setLayout(categoriesPanelLayout);
        categoriesPanelLayout.setHorizontalGroup(
            categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(categoriesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, categoriesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bakingCB))
                    .addGroup(categoriesPanelLayout.createSequentialGroup()
                        .addGroup(categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(ProduceCB)
                            .addComponent(pantryCB)
                            .addComponent(beveragesCB)
                            .addComponent(breadsBakeryCB)
                            .addComponent(dairyRefCB)
                            .addComponent(householdSCB)
                            .addComponent(meatsPoultryCB)
                            .addComponent(miscellaneousCB))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        categoriesPanelLayout.setVerticalGroup(
            categoriesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(categoriesPanelLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bakingCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(beveragesCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(breadsBakeryCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dairyRefCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(householdSCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(meatsPoultryCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(miscellaneousCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pantryCB)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProduceCB))
        );

        createSearchPanel();
        viewInventoryButton.setText("View Inventory");
        viewInventoryButton.addActionListener(e-> viewInventoryAction());
        GroupLayout inventoryLeftPanelLayout = new GroupLayout(inventoryLeftPanel);
        inventoryLeftPanel.setLayout(inventoryLeftPanelLayout);
        inventoryLeftPanelLayout.setHorizontalGroup(
            inventoryLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inventoryLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inventoryLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(categoriesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addInventoryButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewInventoryButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(searchPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                //.addComponent(viewInventoryButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        inventoryLeftPanelLayout.setVerticalGroup(
            inventoryLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inventoryLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewInventoryButton)
                .addGap(18, 18, 18)
                .addComponent(addInventoryButton)
                .addGap(18, 18, 18)
                .addComponent(categoriesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inventorySplitPane.setLeftComponent(inventoryLeftPanel);

        sortingLabel.setText("Sorting:");

        sortingComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "UPC","Name", "Categories", "Expiration Date" }));
        sortingComboBox.addActionListener(e-> populatePantryList());

        GroupLayout sortingPanelLayout = new GroupLayout(sortingPanel);
        sortingPanel.setLayout(sortingPanelLayout);
        sortingPanelLayout.setHorizontalGroup(
            sortingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(sortingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGap(35, 35, 35)
                .addComponent(sortingLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sortingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sortingPanelLayout.setVerticalGroup(
            sortingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(sortingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sortingPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(sortingLabel)
                    .addComponent(sortingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        populatePantryList();
        jScrollPane1.setViewportView(inventoryTable);

        GroupLayout inventoryRightPanelLayout = new GroupLayout(inventoryRightPanel);
        inventoryRightPanel.setLayout(inventoryRightPanelLayout);
        inventoryRightPanelLayout.setHorizontalGroup(
            inventoryRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inventoryRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inventoryRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(sortingPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(inventoryRightPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 1000, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        inventoryRightPanelLayout.setVerticalGroup(
            inventoryRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inventoryRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sortingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inventorySplitPane.setRightComponent(inventoryRightPanel);

        homeTabPane.addTab("Inventory", inventorySplitPane);

        shopListTab();
        nutritionTab();
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeTabPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeTabPane)
                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(null);
    }
  
    /**
     * This method perform action for add inventory button
     */
    private void addInventoryButton() {
        //check item UPC in database to verify
        AddInventoryDialog dialog = new AddInventoryDialog(this, invData);
        String[] data = dialog.run();
        //switch statement moved to inside dialog
    }
    private void viewInventoryAction() {
        populatePantryList();
    }
    
    /**
     * This method sort selected option
     * @return selected option string
     */
    private String sortedSelectedOption() {
        String sort= (String)sortingComboBox.getSelectedItem();
        String selectedOption = "";
    	if(sort.equals("UPC")){
            selectedOption = "default";
        }    	
        else if(sort.equals("Name") || sort.equals("Categories")){
            selectedOption = sort;
        }
        else if (sort.equals("Expiration Date")) {
            selectedOption = "date";
        }
        return selectedOption;
    }
   /**
    * This method will concat the query
    */
    private String getConcatenatedWhereStatement(String query, String selectedCat) {
        if (query.length() > 0) {
            query += "," + selectedCat;
        } else {
            query = selectedCat;
        }
        return query;
    }
    /**
     * This method first check if any checkbox is checked 
     * then it will get selected categories and sorting option
     * and then populate table
     */ 
    private void populatePantryList() {
        String selectedCategories = "";
        if(bakingCB.isSelected())
        {
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Baking, Herbs, and Spices'");
        }
        if(beveragesCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Beverages'");
        }
        if(breadsBakeryCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Breads and Bakery'");
        }
        if(dairyRefCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Dairy and Refrigerated'");
        }
        if(householdSCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Household Supplies'");
        }
        if(meatsPoultryCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Meats, Poultry, and Seafood'");            
        }
        if(miscellaneousCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Miscellaneous'");            
        }
        if(ProduceCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Produce'");            
        }
        if(pantryCB.isSelected()){
            selectedCategories = getConcatenatedWhereStatement(selectedCategories,"'Pantry'");            
        }
        
        invData.buildQuery(sortedSelectedOption(), selectedCategories);
        setModel();
        
    }
    private void setModel(){
        inventoryTable.setModel(invData.GetModel());
        JButtonRenderer jbRender = new JButtonRenderer();
        inventoryTable.setDefaultRenderer(JButton.class, jbRender);
        EditTableEditor editEditor = new EditTableEditor(new JCheckBox());
        inventoryTable.getColumnModel().getColumn(6).setCellEditor(editEditor);
        DeleteTableEditor deleteEditor = new DeleteTableEditor(new JCheckBox());
        inventoryTable.getColumnModel().getColumn(7).setCellEditor(deleteEditor);
        AddToCartTableEditor addEditor = new AddToCartTableEditor(new JCheckBox());
        inventoryTable.getColumnModel().getColumn(8).setCellEditor(addEditor);
        inventoryTable.repaint();
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(175);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(15);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(10);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(25);
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(5);
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(5);
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(20);
    }

  
    
    //JButtonRenderer provides implementation for a button in a table cell
    public class JButtonRenderer extends JButton implements TableCellRenderer{
        //constructor
        public JButtonRenderer() {
            super();
        }
        public JButtonRenderer(String name) {
            super(name);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((String)value);
            switch ((String) value) {
                case "Edit":
                    setForeground(Color.BLACK);
                    break;
                case "Delete":
                    setForeground(Color.RED);
                    break;
                case "Add to Cart":
                    setForeground(Color.GREEN);
                    break;
            }
            return this;
        }
    }

    //EditTableEditor defines the functionality of the edit table cell
    public class EditTableEditor extends DefaultCellEditor {
        JButton button;
        String label;
        boolean clicked;
        int row, col;
        JTable table;

        public EditTableEditor (JCheckBox checkBox) {
          super(checkBox);
          button = new JButton();
          button.setOpaque(true);
          button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
              fireEditingStopped();
            }
          });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
          this.table = table;
          this.row = row;
          this.col = column;

          button.setForeground(Color.black);
          button.setBackground(UIManager.getColor("Button.background"));
          label = (value == null) ? "" : value.toString();
          button.setText(label);
          clicked = true;
          return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked)
            {
                System.out.println("in getCellEditorValue of Edit");
                //Edit Item Here
                //inventory
                if (table.getModel() instanceof InventoryTableModel) {
                    InventoryItem item = (InventoryItem)((InventoryTableModel)table.getModel()).inventory.get(row);
                    String[] data = {item.upcDisplay, item.sizeDisplay, item.uomDisplay, item.expiration};
                    EditInventoryDialog dialog = new EditInventoryDialog(null, data);
                    data = dialog.run();
                }
                //shopping list
                else if (table.getModel() instanceof SLTableModel) {
                    InventoryItem item = (InventoryItem)((SLTableModel)table.getModel()).inventory.get(row);
                    String[] data = {shopListNameLabel.getText(), item.name, item.sizeDisplay, item.category, item.name};
                    EditSLDialog dialog = new EditSLDialog(null, data);
                    data = dialog.run();
                }
            }
            clicked = false;
            return new String(label);
        }

        @Override
        public boolean stopCellEditing() {
          clicked = false;
          return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
          super.fireEditingStopped();
        }
    }
    
    //DeleteTableEditor defines the functionality of the delete table cell
    public class DeleteTableEditor extends DefaultCellEditor {
        JButton button;
        String label;
        boolean clicked;
        int row, col;
        JTable table;

        public DeleteTableEditor (JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent event){
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            this.col = column;

            button.setForeground(Color.black);
            button.setBackground(UIManager.getColor("Button.background"));
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked)
            {
                int n = JOptionPane.showConfirmDialog(null,
                        "Delete Item?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    if (table.getModel() instanceof InventoryTableModel) {
                        //Inventory
                        InventoryItem item = (InventoryItem)((InventoryTableModel)table.getModel()).inventory.get(row);
                        if( invData.deleteRecord(item.upcDisplay)){
                            populatePantryList();
                           JOptionPane.showMessageDialog(null, "Record deleted successfully.!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Delete Failed");
                        }
                    } else if (table.getModel() instanceof SLTableModel) {
                        //Shopping List
                        InventoryItem item = (InventoryItem)((SLTableModel)table.getModel()).inventory.get(row);
                        String[] data = {shopListNameLabel.getText(), item.name};
                        if (shopData.DeleteItemSL(data)){
                            populateShoppingTable(shopListNameLabel.getText());
                           JOptionPane.showMessageDialog(null, "Record deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Delete Failed");
                        }
                    }
                } else {
                    //do nothing
                }
            }
            clicked = false;
            return new String(label);
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    
    
    //AddToCartTableEditor defines the functionality of the add to cart table cell
    public class AddToCartTableEditor extends DefaultCellEditor {
        JButton button;
        String label;
        boolean clicked;
        int row, col;
        JTable table;

        public AddToCartTableEditor (JCheckBox checkBox) {
          super(checkBox);
          button = new JButton();
          button.setOpaque(true);
          button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
              fireEditingStopped();
            }
          });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
          this.table = table;
          this.row = row;
          this.col = column;

          button.setForeground(Color.black);
          button.setBackground(UIManager.getColor("Button.background"));
          label = (value == null) ? "" : value.toString();
          button.setText(label);
          clicked = true;
          return button;
        }

        @Override
         public Object getCellEditorValue() {
          if (clicked)
          {
               InventoryItem item = (InventoryItem)((InventoryTableModel)table.getModel()).inventory.get(row);
               String name= item.name;
               String category=item.category;
              AddToCartDialog(name, category);
            
            //JOptionPane.showMessageDialog(null, "Coming in Phase Three!");
          }
          clicked = false;
          return new String(label);
        }

        @Override
        public boolean stopCellEditing() {
          clicked = false;
          return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
          super.fireEditingStopped();
        }
        
        //This shows the dialog for how many and what list
        private void AddToCartDialog(String name, String category) {
            ImageIcon icon = new ImageIcon(getClass().getResource("./groceryIcon.png"));
            String[] ShoppingList = shopData.getLists();
            JPanel panel = new JPanel();
            JTextField quantityField = new JTextField();
            JComboBox listBox = new JComboBox(ShoppingList);
            Object[] fields = {
                "Add to List:", listBox,
                "Quantity Needed?", quantityField,};
            String[] data = new String[4];

            int option = JOptionPane.showConfirmDialog(PerfectPantryGUI.thisFrame, fields,
                    name, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    icon);
             String listName=(String) listBox.getSelectedItem();
                data[0] = listName;
                data[1] = name;
                data[2] = quantityField.getText();
                data[3] = category;
            if (option == JOptionPane.OK_OPTION) {
               
               if( shopData.AddItemSL(data)){
                   
               }
                populateShoppingTable(listName);
            }

        }
       
    }
    

    
    //TODO
    //goButton performs a search based on the input in the Search Box
    private void goButton(String searchType, String searchKeyword){
       invData.buildSearchQuery(searchType, searchKeyword);
         setModel();
    }
    
    //this method will create search panel components 
    private void createSearchPanel(){
        searchPanel = new JPanel();
        searchInLabel = new JLabel();
        searchByComboBox = new JComboBox<>();
        enterKeywordLabel = new JLabel();
        keywordTextField = new JTextField();
        goButton = new JButton();
        
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        searchInLabel.setText("Search In:");

        searchByComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Search By Name", "Search by UPC" }));
        /**
         * removed search by nutrition since we have a 
         */
        enterKeywordLabel.setText("Enter Keyword:");

        goButton.setText("Go");
        goButton.addActionListener(e-> goButton((String)
                (searchByComboBox.getSelectedItem()), keywordTextField.getText()));
     
        GroupLayout searchPanelLayout = new GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addComponent(searchInLabel)
                        .addGap(2, 2, 2)
                        .addComponent(searchByComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(searchPanelLayout.createSequentialGroup()
                                .addComponent(enterKeywordLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(keywordTextField))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(goButton))))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchInLabel)
                    .addComponent(searchByComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enterKeywordLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(keywordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(goButton))
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }
    
    //
    private void shopListTab(){
        shopListTab = new JPanel();
        shopListSplitPane = new JSplitPane();
        shopListLeftPanel = new JPanel();
        createShopListButton = new JButton();
        exportCSVButton = new JButton();
        viewShopListPanel = new JPanel();
        selectShopListLabel = new JLabel();
        selectShopListComboBox = new JComboBox<>();
        shopListRightPanel = new JPanel();
        shopListRightTopPanel = new JPanel();
        shopListNameLabel = new JLabel();
        deleteshopListButton = new JButton();
        editShopListButton = new JButton();
        shopListScrollPane = new JScrollPane();
        shopListTable = new JTable();
        addItemShopListButton = new JButton();
        
        createShopListButton.setText("Create New List");
        createShopListButton.addActionListener(e-> createShopListButtonAction());

        exportCSVButton.setText("Export to CSV");
        exportCSVButton.addActionListener(e-> {
            try {
                exportCSVButtonAction();
            } catch (IOException ex) {
                Logger.getLogger(PerfectPantryGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
        viewShopListPanel.setBorder(BorderFactory.createTitledBorder("View List"));

        selectShopListLabel.setText("Select List:");
        if (shopData.getLists()==null){
            shopData.createShoppingList("Default");
        }
        String[] lists = shopData.getLists();
        selectShopListComboBox.setModel(new DefaultComboBoxModel<>(lists));
        selectShopListComboBox.setSelectedItem(lists[0]);
        selectShopListComboBox.addActionListener(e->populateShoppingTable((String)selectShopListComboBox.getSelectedItem()));
        populateShoppingTable((String)selectShopListComboBox.getSelectedItem());
        GroupLayout viewShopListPanelLayout = new GroupLayout(viewShopListPanel);
        viewShopListPanel.setLayout(viewShopListPanelLayout);
        viewShopListPanelLayout.setHorizontalGroup(
            viewShopListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(viewShopListPanelLayout.createSequentialGroup()
                .addGroup(viewShopListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(selectShopListLabel, GroupLayout.DEFAULT_SIZE, 58, GroupLayout.DEFAULT_SIZE)
                    .addComponent(selectShopListComboBox, GroupLayout.DEFAULT_SIZE, 94, GroupLayout.DEFAULT_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        viewShopListPanelLayout.setVerticalGroup(
            viewShopListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(viewShopListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectShopListLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectShopListComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout shopListLeftPanelLayout = new GroupLayout(shopListLeftPanel);
        shopListLeftPanel.setLayout(shopListLeftPanelLayout);
        shopListLeftPanelLayout.setHorizontalGroup(
            shopListLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(shopListLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shopListLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(createShopListButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exportCSVButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(shopListLeftPanelLayout.createSequentialGroup()
                        .addComponent(viewShopListPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        shopListLeftPanelLayout.setVerticalGroup(
            shopListLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(shopListLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewShopListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(createShopListButton, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                .addGap(18 ,18, 18)
                .addComponent(exportCSVButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE)) 
        );

        shopListSplitPane.setLeftComponent(shopListLeftPanel);

        shopListRightTopPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

       
        shopListNameLabel.setText((String)selectShopListComboBox.getSelectedItem());
        deleteshopListButton.setIcon(new ImageIcon(getClass().getResource("delete.png"))); // NOI18N
        deleteshopListButton.setMaximumSize(new java.awt.Dimension(179, 147));
        deleteshopListButton.setMinimumSize(new java.awt.Dimension(179, 147));
        deleteshopListButton.addActionListener(e-> deleteShopListButtonAction());

        editShopListButton.setIcon(new ImageIcon(getClass().getResource("edit.png"))); // NOI18N
        editShopListButton.setMaximumSize(new java.awt.Dimension(44, 44));
        editShopListButton.setMinimumSize(new java.awt.Dimension(44, 44));
        editShopListButton.addActionListener(e-> editShopListButtonAction());

        GroupLayout shopListRightTopPanelLayout = new GroupLayout(shopListRightTopPanel);
        shopListRightTopPanel.setLayout(shopListRightTopPanelLayout);
        shopListRightTopPanelLayout.setHorizontalGroup(
            shopListRightTopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(shopListRightTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shopListNameLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(editShopListButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteshopListButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        shopListRightTopPanelLayout.setVerticalGroup(
            shopListRightTopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(shopListRightTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shopListRightTopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(shopListRightTopPanelLayout.createSequentialGroup()
                        .addComponent(editShopListButton, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(shopListRightTopPanelLayout.createSequentialGroup()
                        .addGroup(shopListRightTopPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(shopListNameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteshopListButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9))))
        );

    
      
        
        shopListScrollPane.setViewportView(shopListTable);

        addItemShopListButton.setText("Add Item");
        addItemShopListButton.addActionListener(e-> addItemSLButtonAction());

        GroupLayout shopListRightPanelLayout = new GroupLayout(shopListRightPanel);
        shopListRightPanel.setLayout(shopListRightPanelLayout);
        shopListRightPanelLayout.setHorizontalGroup(
            shopListRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(shopListRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shopListRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(shopListRightPanelLayout.createSequentialGroup()
                        .addComponent(addItemShopListButton, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(shopListRightTopPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shopListScrollPane, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE))
                .addContainerGap())
        );
        shopListRightPanelLayout.setVerticalGroup(
            shopListRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(shopListRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shopListRightTopPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shopListScrollPane, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addItemShopListButton)
                .addContainerGap(179, Short.MAX_VALUE))
        );

        shopListSplitPane.setRightComponent(shopListRightPanel);

        javax.swing.GroupLayout shopListTabLayout = new javax.swing.GroupLayout(shopListTab);
        shopListTab.setLayout(shopListTabLayout);
        shopListTabLayout.setHorizontalGroup(
            shopListTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(shopListSplitPane)
        );
        shopListTabLayout.setVerticalGroup(
            shopListTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shopListTabLayout.createSequentialGroup()
                .addComponent(shopListSplitPane)
                .addContainerGap())
        );

        homeTabPane.addTab("Shopping List", shopListTab);
    }
    private void nutritionTab(){
        nutritionTab = new JSplitPane();
        nutLeftPanel = new JPanel();
        nutViewListButton = new JButton();
        nutSearchPanel = new JPanel();
        nutTextField = new JTextField();
        nutKeywordLabel = new JLabel();
        nutSearchButton = new JButton();
        nutRightPanel = new JPanel();
        nutTopPanel = new JPanel();
        nutSortingLabel = new JLabel();
        nutSortingComboBox = new JComboBox<>();
        nutTableScrollPane = new JScrollPane();
        nutritionTable = new JTable();
        
        nutViewListButton.setText("View Nutrition List");
        nutViewListButton.addActionListener(e-> populateNutritionTable());

        nutSearchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        nutKeywordLabel.setText("Keyword In:");

        nutSearchButton.setText("Search");
        nutSearchButton.addActionListener(e-> searchNutInfoAction((String) nutTextField.getText()));

        GroupLayout nutSearchPanelLayout = new GroupLayout(nutSearchPanel);
        nutSearchPanel.setLayout(nutSearchPanelLayout);
        nutSearchPanelLayout.setHorizontalGroup(
            nutSearchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutSearchPanelLayout.createSequentialGroup()
                .addComponent(nutKeywordLabel)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(nutSearchPanelLayout.createSequentialGroup()
                .addComponent(nutTextField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nutSearchButton)
                .addContainerGap())
        );
        nutSearchPanelLayout.setVerticalGroup(
            nutSearchPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutSearchPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(nutKeywordLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nutSearchPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nutTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(nutSearchButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout nutLeftPanelLayout = new GroupLayout(nutLeftPanel);
        nutLeftPanel.setLayout(nutLeftPanelLayout);
        nutLeftPanelLayout.setHorizontalGroup(
            nutLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nutLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(nutViewListButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nutSearchPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        nutLeftPanelLayout.setVerticalGroup(
            nutLeftPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nutViewListButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nutSearchPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(359, Short.MAX_VALUE))
        );

        nutritionTab.setLeftComponent(nutLeftPanel);

        nutSortingLabel.setText("Sorting:");

        nutSortingComboBox.setModel(new DefaultComboBoxModel<>(new String[] 
        { "Product Name", "Calories-Ascending", "Calories-Descending",
            "Protein-Ascending","Protein-Descending",  "Fat-Ascending","Fat-Descending"}));
        nutSortingComboBox.addActionListener(e-> populateNutritionTable());
        GroupLayout nutTopPanelLayout = new GroupLayout(nutTopPanel);
        nutTopPanel.setLayout(nutTopPanelLayout);
        nutTopPanelLayout.setHorizontalGroup(
            nutTopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nutSortingLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nutSortingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        nutTopPanelLayout.setVerticalGroup(
            nutTopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutTopPanelLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nutTopPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nutSortingLabel)
                    .addComponent(nutSortingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        populateNutritionTable();
        nutTableScrollPane.setViewportView(nutritionTable);

        javax.swing.GroupLayout nutRightPanelLayout = new GroupLayout(nutRightPanel);
        nutRightPanel.setLayout(nutRightPanelLayout);
        nutRightPanelLayout.setHorizontalGroup(
            nutRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(nutTopPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nutTableScrollPane, GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
        );
        nutRightPanelLayout.setVerticalGroup(
            nutRightPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nutRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nutTopPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nutTableScrollPane, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
        );

        nutritionTab.setRightComponent(nutRightPanel);
        homeTabPane.addTab("Nutrition Tab", nutritionTab);  
    }
    
    private String nutSortedOption() {
        String sort= (String)nutSortingComboBox.getSelectedItem();
        String nutSelectedOption = "";
        if (sort.equals("Product Name")){
            nutSelectedOption = "default";
        }
        else{
            nutSelectedOption = sort;
        }    	
        return nutSelectedOption;
    }
    private void populateNutritionTable(){
        nutData.viewSortNutritionInfo(nutSortedOption());
        nutModel(); 
    }
    
    private void nutModel(){       
         nutritionTable.setModel(
         nutData.setNutritionalModel()
       );
       
        nutritionTable.getColumnModel().getColumn(0).setMinWidth(400);
        nutritionTable.getColumnModel().getColumn(1).setPreferredWidth(15);
        nutritionTable.getColumnModel().getColumn(2).setPreferredWidth(15);
        nutritionTable.getColumnModel().getColumn(3).setPreferredWidth(15);
        nutritionTable.getColumnModel().getColumn(4).setPreferredWidth(15);
        nutritionTable.getColumnModel().getColumn(5).setPreferredWidth(15);
        nutritionTable.getColumnModel().getColumn(6).setPreferredWidth(15);
        nutritionTable.repaint();
        
    }
    private void searchNutInfoAction(String productName){
        nutData.viewSearchNutritionInfo(productName);
        nutModel();
    }
    
    //method to handle creating a new shopping list
    private void createShopListButtonAction() {                                                     
        String listName = JOptionPane.showInputDialog("Enter Shopping List Name");
        if (listName == null) {
            return;
        }
        //check if name already exists
        String[] lists = shopData.getLists();
        if (lists != null) {
            for (int i=0; i<lists.length; i++) {
                if (lists[i].equals(listName)) {
                    JOptionPane.showMessageDialog(this, listName + " already exists");
                    return;
                }
            }
        }
        if (shopData.createShoppingList(listName)) {
            JOptionPane.showMessageDialog(this, "Shopping list " + listName + " created");
            populateShoppingTable(listName);
            selectShopListComboBox.setModel(new DefaultComboBoxModel<>(shopData.getLists()));
            selectShopListComboBox.setSelectedItem(listName);
            shopListNameLabel.setText(listName);
        } else {
            JOptionPane.showMessageDialog(this, "Create shopping list failed");
        }
    }
    
    
    private void deleteShopListButtonAction() {                                                     
        String list = shopListNameLabel.getText();
        int n = JOptionPane.showConfirmDialog(this,
                "Delete Shopping List: " + list + "?",
                "Delete",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            //delete here
            if(shopData.deleteShoppingList(list)){
                if (shopData.getLists() == null) {
                    shopData.createShoppingList("Default");
                    populateShoppingTable("Default");
                    selectShopListComboBox.setModel(new DefaultComboBoxModel<>(shopData.getLists()));
                } else {
                    String[] lists = shopData.getLists();
                    populateShoppingTable(lists[0]);
                    selectShopListComboBox.setModel(new DefaultComboBoxModel<>(lists));
                }
                JOptionPane.showMessageDialog(this, "List deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Delete Failed");
            }
        } else {
            //do nothing
        }
    }
    
    //
    private void editShopListButtonAction() {
        String name = shopListNameLabel.getText();
        String newName = JOptionPane.showInputDialog(this,
                "Enter Shopping List Name", name);
        if (newName == null) {
            return;
        }
        
        String[] lists = shopData.getLists();
        if (lists != null) {
            for (int i=0; i<lists.length; i++) {
                if (lists[i].equals(newName)) {
                    JOptionPane.showMessageDialog(this, newName + " already exists");
                    return;
                }
            }
        }
        if (shopData.editShoppingList(name, newName)) {
            JOptionPane.showMessageDialog(this, "Shopping list " + newName + " edited");
            populateShoppingTable(newName);
            selectShopListComboBox.setModel(new DefaultComboBoxModel<>(shopData.getLists()));
        } else {
            JOptionPane.showMessageDialog(this, "Edit shopping list failed");
        }
    }
    
    private void exportCSVButtonAction() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // save to file
            String output = "";
            Vector v = ((SLTableModel)shopListTable.getModel()).getInventory();
            for (int i=0; i<v.size(); i++) {
                output += ((InventoryItem)v.get(i)).name + ", ";
                output += ((InventoryItem)v.get(i)).sizeDisplay + ", ";
                output += ((InventoryItem)v.get(i)).category + ",\r\n";
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(output);
            writer.close();
        }
    }

    //method to handle adding new items to the shopping list
    private void addItemSLButtonAction() {           
        if (shopData.getLists() == null) {
            return;
        }
        AddItemSLDialog dialog = new AddItemSLDialog(this, shopData);
        String[] data = dialog.run();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addInventoryButton;
    private JCheckBox bakingCB;
    private JCheckBox beveragesCB;
    private JCheckBox breadsBakeryCB;
    private JPanel categoriesPanel;
    private JCheckBox dairyRefCB;
  //  private JLabel filterLabel;
    private JCheckBox householdSCB;
    private JPanel inventoryLeftPanel;
    private JPanel inventoryRightPanel;
    private JSplitPane inventorySplitPane;
    private JTabbedPane homeTabPane;
    private JTable inventoryTable;
    private JScrollPane jScrollPane1;
    private JCheckBox meatsPoultryCB;
    private JCheckBox miscellaneousCB;
    private JCheckBox ProduceCB;
    private JCheckBox pantryCB;
    private JComboBox<String> sortingComboBox;
    private JLabel sortingLabel;
    private JPanel sortingPanel;
    private JPanel searchPanel;
    private JLabel searchInLabel;
    private JComboBox<String> searchByComboBox;
    private JLabel enterKeywordLabel;
    private JTextField keywordTextField;
    private JButton goButton;
    private JComboBox<String> selectShopListComboBox;
    private JButton addItemShopListButton;
    private JButton createShopListButton;
    private JButton deleteshopListButton;
    private JButton editShopListButton;
    private JLabel selectShopListLabel;
    private JPanel shopListLeftPanel;
    private JLabel shopListNameLabel;
    private JPanel shopListRightPanel;
    private JPanel shopListRightTopPanel;
    private JScrollPane shopListScrollPane;
    private JSplitPane shopListSplitPane;
    private JPanel shopListTab;
    private JTable shopListTable;
    private JPanel viewShopListPanel;
    private JLabel nutKeywordLabel;
    private JPanel nutLeftPanel;
    private JPanel nutRightPanel;
    private JButton nutSearchButton;
    private JPanel nutSearchPanel;
    private JComboBox<String> nutSortingComboBox;
    private JLabel nutSortingLabel;
    private JTable nutritionTable;
    private JScrollPane nutTableScrollPane;
    private JTextField nutTextField;
    private JPanel nutTopPanel;
    private JButton nutViewListButton;
    private JSplitPane nutritionTab;
    private JButton viewInventoryButton;
    private JButton exportCSVButton;
    
    // End of variables declaration//GEN-END:variables
}
