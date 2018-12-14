/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PerfectPantryApp;

import static java.lang.Double.NaN;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Michelle
 * this  class does nothing but data validation
 */
public class DataValidation {

    private static String upc;
    //private static double usage;
    private static int quantity;
    private static double size, servSize, calories, protein, fat;
    private static String uom, productName;
    private java.sql.Date sqlExp = null;
    private HashMap<String, Integer> categoryMap = new HashMap();

    DataValidation() {
        upc = "";
        //usage = 0;
        size = 0;
        servSize = 0;
        calories = 0;
        protein = 0;
        fat = 0;
        uom = "";
        quantity = 0;
        productName = "";
        
    }
    
    public int getNutCode(String s){
        String mapCode=s.toLowerCase();
        switch(mapCode){
            case "fat":
                return 204;
            case "protein":
                return 203;
            case "calorie":
                return 205;
            default:
                return 0;
        }
    }
    public int getCategory(String s) {
        //reduces map initialization to the two places it is used
        intializeMap();
        return categoryMap.getOrDefault(s, 0);
    }
    
    public String getUPC() {
        return upc;
    }

    public double getSize() {
        return size;
    }

    public String getUOM() {
        return uom;
    }

    public java.sql.Date getExpiration() {
        return sqlExp;
    }

    public String getName() {
        return productName;
    }

    public double getServingSize() {
        return servSize;
    }

    public double getProtein() {
        return protein;
    }

    public double getCalories() {
        return calories;
    }

    public double getFat() {
        return fat;
    }
    public int getQuantity(){
        return quantity;
    }
    private void intializeMap(){
        categoryMap.put("Produce", 100);
        categoryMap.put("Meats, Poultry, and Seafood", 200);
        categoryMap.put("Dairy and Refrigerated", 300);
        categoryMap.put("Pantry", 400);
        categoryMap.put("Breads and Bakery", 500);
        categoryMap.put("Baking, Herbs, and Spices", 600);
        categoryMap.put("Beverages", 700);
        categoryMap.put("Household Supplies", 800);
        categoryMap.put("Miscellaneous", 900);
    }
    //helper to validate and set size
    boolean validateSize(String tempSize) {
       
        size = validateDouble("Size", tempSize);
        return !Double.isNaN(size);
    }
    //validates and sets serving size
    boolean validateServingSize(String tempSize) {
        servSize = 0;
        servSize = validateDouble("Serving Size", tempSize);
        return !Double.isNaN(servSize);
    }
    //validates and sets calories
    boolean validateCalories(String tempSize) {
        calories = 0;
        calories = validateDouble("Calories", tempSize);
       return !Double.isNaN(calories);
    }
    //validatates and sets protein
    boolean validateProtein(String tempSize) {
        protein = 0;
        protein = validateDouble("Protein", tempSize);
        return !Double.isNaN(protein);
    }
    //validates and sets fat
    boolean validateFat(String tempSize) {
        fat = 0;
        fat = validateDouble("Fat", tempSize);
       return !Double.isNaN(fat);
    }

    // method to validate and set units
    boolean validateUOM(String tempUOM) {
        uom = "";
        if (tempUOM.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid Input: Unit of measurement must not be empty.");
            return false;
        } else {
            uom = tempUOM;
        }
        return true;
    }

    //used to validate product names in shopping list add product and search 
    boolean validateName(String name) {
        if (name.equals("")) {
            JOptionPane.showMessageDialog(null, "Name must not be empty");
            return false;
        } else if (name.length() > 80) {
            JOptionPane.showMessageDialog(null, null,
                    "Name must be less than 80 Characters", JOptionPane.ERROR_MESSAGE);
            return false;
        } 
        else {
            productName = name;
            return true;
        }
    }

    //helper method to validate and set date
    boolean validateDate(String tempDate) {
        SimpleDateFormat dateFormat;
        String formatter = "";
        sqlExp = null;
        //parse data values
        if (!(tempDate.isEmpty())) {

            dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            formatter = String.format(tempDate);
            try {
                sqlExp = java.sql.Date.valueOf(formatter);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(null, "Date must be in yyyy-mm-dd format");
                return false;
            }
        }
        return true;
    }



    /*validates UPC and returns a string if and 
        why it failed or the word valid*/
    String validateUPC(String interfaceUpc) {
        upc = interfaceUpc;
        String regex = "[0-9]+";
        if (upc.isEmpty()) {
            return "empty";
        } else if (!upc.matches(regex)) {
            return "notANum";
        } else if (upc.length() != 12) {
            return "length";
        } else {

            return "valid";
        }

    }
    
    //validates the integer quantity
    public boolean validateQuantity(String quan) {
        quantity = 0;
        if (quan.equals("")) {
            JOptionPane.showMessageDialog(null, "Size cannot be empty!");
            return false;
        }
        try {
            quantity = Integer.parseInt(quan);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Size should be numeric with no decimal");
            return false;
        }
    }

    //validates and sets all doubles returns special case of NaN 
    //if unable to validate
    private double validateDouble(String type1, String tempDub) {
        double validNumber;
        String message = "Invalid Input:" + type1 + " must not be empty";
       
        if (tempDub.isEmpty()) {
            JOptionPane.showMessageDialog(null, message);
            return NaN;
        }
        try {
            validNumber = Double.parseDouble(tempDub);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, type1 + " should be a numeric value");
            return NaN;
        }
        return validNumber;
    }

}
