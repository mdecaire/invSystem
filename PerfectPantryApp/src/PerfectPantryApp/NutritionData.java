/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PerfectPantryApp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * This class deals only with nutritional data
 *
 * @author Michelle
 */
public class NutritionData {

    protected DefaultTableModel nTable = null;
    String query = "{CALL getNutrition()}";

    public DefaultTableModel setNutritionalModel() {
        nTable = new DefaultTableModel(new String[]{
            "Product name", "Calories", "unit", "Protien", "unit", "Fat", "unit"
        }, 0);

        try (Connection conn = JDBC.getConnection()) {

            CallableStatement st = conn.prepareCall(query);

            ResultSet rs = null;

            rs = st.executeQuery(); //performs query

            while (rs.next()) { //gets string from db
                String name = rs.getString("invName");
                double protein = rs.getDouble("protein");
                double fat = rs.getDouble("fat");
                double calories = rs.getDouble("calories");
                String nUOM = rs.getString("uom");
                //this line rounds to two decimal places
                nTable.addRow(new Object[]{name,
                    (Math.round(calories * 100.0) / 100.0),
                    nUOM, (Math.round(protein * 100.0) / 100.0), nUOM,
                    (Math.round(fat * 100.0) / 100.0), nUOM});
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(InventoryData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nTable;
    }

    //sets the query for searching NutritionInfo
    public void viewSearchNutritionInfo(String productName) {
        DataValidation data = new DataValidation();
        if (data.validateName(productName)) {
            buildQuery(" AND p.invName LIKE '%" + productName + "%')");
        }
        setNutritionalModel();
    }

    //sets the query for sorting info
    public void viewSortNutritionInfo(String order) {
        switch (order) {
            case "default":
                buildQuery(")Order By invName");
                break;
            case "Calories-Ascending":
                buildQuery(")Order By calories asc");
                break;
            case "Calories-Descending":
                buildQuery(")Order By calories desc");
                break;
            case "Protein-Ascending":
                buildQuery(")Order By protein asc");
                break;
            case "Protein-Descending":
                buildQuery(")Order By protein desc");
                break;
            case "Fat-Ascending":
                buildQuery(")Order By fat asc");
                break;
            case "Fat-Descending":
                buildQuery(")Order By fat desc");
                break;
            default:
                break;
        }

        setNutritionalModel();
    }

    //helper method to build the actual query
    private void buildQuery(String string) {
        query = "select p.invName, (n.nut_val/100*s.servingSize) as protein,\n"
                + "(n2.nut_val/100*s.servingSize) as fat,\n"
                + "(n3.nut_val/100*s.servingSize) as calories, s.uom\n"
                + "from Product p join inventory_List i on i.ProductID=p.ProductID\n"
                + "join Nutrition n on n. ProductID=p.ProductID\n"
                + "join Nutrition n2 on n2. ProductID=p.ProductID\n"
                + "join Nutrition n3 on n3. ProductID=p.ProductID\n"
                + "join serving_size s on s.ProductID=p.ProductID\n"
                + "WHERE (n.Nut_Code=203 AND n2.Nut_Code=204 AND  n3.Nut_Code=205" + string;
    }
}
