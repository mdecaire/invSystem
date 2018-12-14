/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PerfectPantryApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Michelle
 */
public class ProductData {

    NutritionRecord record;
    List<NutritionRecord> recordList;
    private String productName = "", uom = "", upc = "";
    private int category = 0, productID = 0;
    private double servingSize = 0, protein = 0, fat = 0, calories = 0;

    public int getProductID() {
        return productID;
    }

    boolean AddProductToInventory(String upc, String[] prodData) {
        this.upc = upc;
        productID = 0;
        boolean productAddedCorrectly;
        if (!verifyProductDetails(prodData)) {
            return false;
        }
        productAddedCorrectly = insertIntoProduct();

        return productAddedCorrectly;
    }

    boolean addNutrition(String[] nutritionData) {
        if (!verifyNutritionDetails(nutritionData)) {
            return false;
        } else if (!insertIntoServingSize()) {
            JOptionPane.showMessageDialog(null, "Unable to insert into Serving Size");
            return false;
        } else if (!insertIntoNutrition()) {
            JOptionPane.showMessageDialog(null, "Unable to Insert Nutrition");
            return false;
        } else {
            JOptionPane.showMessageDialog(null, "Nutrition has been added successfully!");
            return true;
        }
    }

    private boolean insertIntoProduct() {
        boolean successfulCreate = false;
        String query = "INSERT into Product(UPC, invName,Category)"
                + "VALUES('" + upc + "'," + "'" + productName + "'," + category + ")";
        try (Connection conn = JDBC.getConnection()) {
            Statement stmt = conn.prepareStatement(query);
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                productID = rs.getInt(1);
                successfulCreate = true;
            } else {
                successfulCreate = false;
                // throw an exception from here
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Oops!" + ex);
            Logger.getLogger(InventoryData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return successfulCreate;
    }

    private boolean verifyProductDetails(String[] data) {
        boolean validData = false;
        DataValidation dv = new DataValidation();
        category = dv.getCategory(data[1]);
        if (dv.validateName(data[0])) {
            validData = true;
            productName = dv.getName();
        } else {
            return false;
        }

        return validData;
    }

    private boolean verifyNutritionDetails(String[] data) {
        boolean validData = false;

        uom = data[1];
        recordList = new ArrayList<NutritionRecord>();
        DataValidation dv = new DataValidation();

        if (dv.validateServingSize(data[0])) {
            validData = true;
            servingSize = dv.getServingSize();
        } else {
            return false;
        }

        if (dv.validateCalories(data[2])) {
            validData = true;
            double calories1 = dv.getCalories();
            calories = (calories1 / servingSize) * 100;
            record = new NutritionRecord(productID,
                    dv.getNutCode("calorie"), "calories", calories, uom);
            recordList.add(record);
        } else {
            return false;
        }

        if (dv.validateProtein(data[3])) {
            validData = true;
            double protein1 = dv.getProtein();
            protein = (protein1 / servingSize) * 100;

            record = new NutritionRecord(productID,
                    dv.getNutCode("protein"), "protein", protein, uom);
            recordList.add(record);
        } else {
            return false;
        }

        if (dv.validateFat(data[4])) {
            validData = true;
            double fat1 = dv.getFat();
            fat = (fat1 / servingSize) * 100;
            record = new NutritionRecord(productID,
                    dv.getNutCode("fat"), "fat", fat, uom);
            recordList.add(record);
        } else {
            return false;
        }
        return validData;
    }

    private boolean insertIntoServingSize() {
        boolean successfulCreate = false;
        String query = "INSERT into serving_size(ProductID,ServingSize,uom)"
                + "VALUES(" + productID + "," + servingSize + ",'" + uom + "')";
        try (Connection conn = JDBC.getConnection()) {
            Statement stmt = conn.createStatement();
            int rowAdded = stmt.executeUpdate(query);
            if (rowAdded > 0) {
                successfulCreate = true;
            }
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Oops!" + ex);
            Logger.getLogger(InventoryData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return successfulCreate;
    }

    private boolean insertIntoNutrition() {
        boolean successfulCreate = false;
        String query = "INSERT into Nutrition(ProductID,Nut_Code,Nutr_name, nut_val, uom)"
                + "VALUES(?,?,?,?,?)";
        try (Connection conn = JDBC.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            for (NutritionRecord nr : recordList) {
                ps.setInt(1, nr.productID);
                ps.setInt(2, nr.nut_code);
                ps.setString(3, nr.nut_name);
                ps.setDouble(4, nr.nut_val);
                ps.setString(5, uom);
                ps.addBatch();
            }

            ps.executeBatch();
            successfulCreate = true;
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Oops!" + ex);
            Logger.getLogger(InventoryData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return successfulCreate;
    }

}

class NutritionRecord {

    int productID;
    int nut_code;
    String nut_name;
    Double nut_val;
    String uom;

    NutritionRecord(int productID, int nutCode,
            String nut_name, double nut_val, String uom) {
        this.productID = productID;
        this.nut_code = nutCode;
        this.nut_name = nut_name;
        this.nut_val = nut_val;
        this.uom = uom;
    }

}
