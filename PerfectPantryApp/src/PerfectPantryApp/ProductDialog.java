/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PerfectPantryApp;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.font.TextAttribute.FONT;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Michelle
 */
public class ProductDialog extends JDialog implements ActionListener {

    String upc;
    String[] data;
    JTextField nameField;
    JComboBox categoryChoices;
    JButton addBtn;
    JButton cancelBtn;
    JRadioButton AddNutrition;
    JRadioButton noAddingNutrition;
    ProductData newProd;
    JFrame frame;

    ProductDialog(JFrame parent, String upc) {
        super(parent, "Adding a new product", true);
        this.frame = parent;
        data = new String[2];
        data[0] = upc;
        for (int i = 1; i < data.length; i++) {
            data[i] = null;
        }
        this.upc = upc;
        //defaults to misc
        String[] produceOptions = {"Miscellaneous", "Produce",
            "Meats, Poultry, and Seafood",
            "Dairy and Refrigerated",
            "Pantry",
            "Breads and Bakery",
            "Baking, Herbs, and Spices",
            "Beverages",
            "Household Supplies"
        };

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        String message = "<html> <p style='font-style:italic;color:red;'>"
                + "Items indicated by * are required fields</p></html>";
        JLabel reqLabel = new JLabel(message);
        reqLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(reqLabel, gbc);

        JLabel label = new JLabel("Enter the Name of the Product*");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label, gbc);

        nameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel categoryLabel = new JLabel("Select a Category*");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(categoryLabel, gbc);

        categoryChoices = new JComboBox(produceOptions);
        categoryChoices.setSelectedIndex(0);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(categoryChoices, gbc);

        ButtonGroup nutritionGroup = new ButtonGroup();
        AddNutrition = new JRadioButton("Add Nutrition");
        noAddingNutrition = new JRadioButton("Do Not Add Nutrition");
        nutritionGroup.add(AddNutrition);
        nutritionGroup.add(noAddingNutrition);
        noAddingNutrition.setSelected(true);
        AddNutrition.setSelected(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(noAddingNutrition, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(AddNutrition, gbc);

        addBtn = new JButton("    Add     ");
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
    boolean productAdded = false;
    int productID = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        newProd = new ProductData();
        if (e.getSource() == addBtn) {
            data[0] = nameField.getText();
            data[1] = (String) categoryChoices.getSelectedItem();
            productAdded = newProd.AddProductToInventory(upc, data);
            productID = newProd.getProductID();
            
            if (AddNutrition.isSelected() || createDialog()) {
                NutritionDialog nd = new NutritionDialog(frame, newProd);
                nd.addSuccessful();
            }
            JOptionPane.showMessageDialog(null, "Item added to master list successfully");
            dispose();
        } else if (e.getSource() == cancelBtn) {
            dispose();
        }
    }

    public void run() {
        this.setVisible(true);
    }

    boolean addSuccessful() {
        run();
        return productAdded;

    }

    int getProductID() {
        return productID;
    }

    private boolean createDialog() {
        boolean addNutrition;
        int n = JOptionPane.showOptionDialog(this,
                "<html> <p style='font-style:italic;'>By not adding Nutrition "
                + " you acknowledge that you will not be able to"
                        + " see it in the nutritional tab"
                + " <br>Would you like to add it now?</p></html>",
                "Nutrition data will not be available!",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            addNutrition = true;
        } else {
            addNutrition = false;
        }
        return addNutrition;
    }

    class NutritionDialog extends JDialog implements ActionListener {

        String[] nutritionData;
        JComboBox uomChoices;
        JTextField serv_sizeField;
        JTextField proteinField;
        JTextField caloriesField;
        JTextField fatField;
        JButton addButton;
        JButton cancelButton;
        ProductData prodData;

        public NutritionDialog(JFrame frame, ProductData pd) {
             super(frame, "Adding nutrition", true);
            this.prodData = pd;
            nutritionData = new String[5];
            for (int i = 1; i < nutritionData.length; i++) {
                nutritionData[i] = null;
            }
            String[] uomOption = {"g", "ml"};
            String nutMessage = "<html><p style='font-style:italic;font-weight: bold;'>"
                    + "Please Complete all Fields</p></html>";
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel nutritionLabel = new JLabel(nutMessage);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(nutritionLabel, gbc);

            JLabel servSizeLabel = new JLabel("Amount of Serving Size in g or ml");
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(servSizeLabel, gbc);

            serv_sizeField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(serv_sizeField, gbc);

            JLabel uomLabel = new JLabel("Select a Unit of Measurement");
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(uomLabel, gbc);

            uomChoices = new JComboBox(uomOption);
            uomChoices.setSelectedIndex(0);
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(uomChoices, gbc);

            JLabel caloriesLabel = new JLabel("Enter Calories per serving");
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(caloriesLabel, gbc);

            caloriesField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(caloriesField, gbc);

            JLabel proteinLabel = new JLabel("Enter Protein per serving");
            gbc.gridx = 0;
            gbc.gridy = 7;
            panel.add(proteinLabel, gbc);

            proteinField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 7;
            panel.add(proteinField, gbc);

            JLabel fatLabel = new JLabel("Enter Fat per serving");
            gbc.gridx = 0;
            gbc.gridy = 8;
            panel.add(fatLabel, gbc);

            fatField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 8;
            panel.add(fatField, gbc);

            addButton = new JButton("    Add     ");
            addButton.addActionListener(this);
            gbc.gridx = 0;
            gbc.gridy = 9;
            panel.add(addButton, gbc);

            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            gbc.gridx = 1;
            gbc.gridy = 9;
            panel.add(cancelButton, gbc);

            getContentPane().add(panel);
            pack();
            setLocationRelativeTo(null);
        }
        boolean addedNutrition;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addButton) {
                nutritionData[0] = serv_sizeField.getText();
                nutritionData[1] = (String) uomChoices.getSelectedItem();
                nutritionData[2] = caloriesField.getText();
                nutritionData[3] = proteinField.getText();
                nutritionData[4] = fatField.getText();
                if (prodData.addNutrition(nutritionData)) {
                    addedNutrition = true;
                    dispose();
                }
            } else if (e.getSource() == cancelButton) {
                dispose();
            }
        }

        public void run() {
            this.setVisible(true);
        }

        boolean addSuccessful() {
            run();
            return productAdded;

        }
    }
}
