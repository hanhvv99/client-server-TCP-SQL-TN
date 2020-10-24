/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btc5_qlsv_tcp;
import java.sql.*;
/**
 *
 * @author Admin
 */
public class ConnectDB {
    public static Connection SQLConnect(){
        Connection cn = null;
        String user = "n17dcat022";
        String pass = "123";
        String url = "jdbc:sqlserver://localhost:1433;databaseName=KIEMTRALTM";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cn = DriverManager.getConnection(url, user, pass);
            if(cn!=null){
                System.out.println("Kết nối Datababe thành công");
            }else {
                System.out.println("Kết nối Database thất bại");
            }
        } catch (ClassNotFoundException|SQLException e) {
            System.out.println(e.getMessage());
        }
        return cn;
    } 
}
