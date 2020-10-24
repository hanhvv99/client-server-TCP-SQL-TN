/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btc5_qlsv_tcp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
/**
 *
 * @author Admin
 */
public class Server {
    public static Server s = new Server();
    public static ServerSocket server = null;
    public static Socket client = null;
    public static int port = 2010;
    public static int slCH = 10;
    public static Scanner sc = new Scanner(System.in);
    public static Connection cn = null;
    
    public int kt(String ma,String ngaythi){
        cn = ConnectDB.SQLConnect();
        String sql = "SELECT * FROM BANGDIEM WHERE MASV='"+ma+"' AND NGAYTHI=CONVERT(DATETIME,'"+ngaythi+"',103)";
        int tonTai=0;
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                tonTai = 1;
            }
            rs.close();
            ps.close();
            cn.close();
        } catch (SQLException e) {
            System.out.println("loi kiem tra!");
        }
        return tonTai;
    }
    
    public String dangnhap(String user,String pass,String masv,String ngayThi){
        cn = ConnectDB.SQLConnect();
        String sql = "SELECT * FROM SINHVIEN sv WHERE sv.UserName='"+user+"' AND sv.PassWord='"+pass+"'";
        String mess = null;
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                mess ="Sinh viên đăng nhập thành công!";
            }else if (s.kt(masv, ngayThi)==1){
                mess="Sinh viên đã làm bài ngày hôm nay!";
            }else {
                mess="Sinh viên đăng nhập thất bại!";
            }
            rs.close();
            ps.close();
            cn.close();
        } catch (SQLException e) {
            System.out.println("loi dang nhap!");
        }
        return mess;
    }
    
    public String hienthiMSV(String user){
        cn = ConnectDB.SQLConnect();
        String sql = "SELECT MASV FROM SINHVIEN sv WHERE sv.UserName='"+user+"'";
        String masv=null;
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                masv = rs.getString("MASV");
            }
            rs.close();
            ps.close();
            cn.close();
        } catch (SQLException e) {
            System.out.println("Loi khong co ma sv nay");
        }
        return masv;
    }
    
    public boolean insertBD(String masv,int lan,String ngaythi,float diem,String baithi){
        cn = ConnectDB.SQLConnect();
        String sql = "EXEC SP_INS_BANGDIEM '"+masv+"','"+lan+"','"+ngaythi+"','"+diem+"','"+baithi+"'";
        try {
            PreparedStatement ps = cn.prepareCall(sql);
            ps.executeUpdate();
            
            ps.close();
            cn.close();
            return true;
        } catch (SQLException e) {
            if(s.kt(masv, ngaythi)==1){
                System.out.println("Sinh vien "+masv+"da thi ngay hom nay, khong the luu bang diem!");
            }
        }
        return false;
    }
    
    public static void main(String[] args) throws IOException{
        server = new ServerSocket(port);
        try {
            System.out.println("server da san sang!");
            client = server.accept();
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            System.out.println("client da ket noi toi server");
            
            //nhap thong tin dang nhap
            String user = dis.readUTF();
            String pass = dis.readUTF();
            
            String masv=s.hienthiMSV(user);
            int lan=1;
            String ngayThi=(String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date())));
            float diem=0;
            String baiThi="Thu "+lan;
            
            String mess=s.dangnhap(user,pass,masv,ngayThi);
            while(true){
                dos.writeUTF(mess);
                System.out.println(mess);
                if(mess.equals("Sinh viên đăng nhập thành công!")){
                    break;
                } else if(mess.equals("Sinh viên đăng nhập thất bại!") || mess.equals("Sinh viên đã làm bài ngày hôm nay!")){
                    user = dis.readUTF();
                    pass = dis.readUTF();
                    mess=s.dangnhap(user,pass,masv,ngayThi);
                } 
            }
            
            //hien cau hoi
            cn = ConnectDB.SQLConnect();
            String sql = "SELECT TOP "+slCH+" BODE.CAUHOI, BODE.NOIDUNG, BODE.A, BODE.B, BODE.C, BODE.D, BODE.DAP_AN FROM BODE ORDER BY NEWID()";
            try {
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    String noidung= rs.getString("NOIDUNG");
                    String a= rs.getString("A");
                    String b= rs.getString("B");
                    String c= rs.getString("C");
                    String d= rs.getString("D");
                    String dapan = rs.getString("DAP_AN");
                    
                    dos.writeUTF(noidung);
                    dos.writeUTF(a);
                    dos.writeUTF(b);
                    dos.writeUTF(c);
                    dos.writeUTF(d);
                    
                    String traloi=dis.readUTF();
                    if(traloi.equals(dapan)){
                        diem = diem+1;
                    }else{
                        diem = diem+0;
                    }
                    dos.writeUTF(dapan);//GUI DAP AN qua client
                }
                dos.writeUTF(masv);
                float diemlamtron = Math.round(diem * 100)/100;
                dos.writeFloat(diemlamtron);
                if(s.insertBD(masv, lan, ngayThi, diemlamtron, baiThi)==true){
                    System.out.println("them bang diem thanh cong!");
                    lan++;
                } else {
                    System.out.println("them bang diem that bai!");
                }
            } catch (SQLException e) {
                System.out.println("loi ket noi cau hoi");
            }         
        } catch (IOException e) {
            System.out.println("server ngung ket noi");
        }
    }
}
