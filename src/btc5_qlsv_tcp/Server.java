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
    public static int soLanThi=2;//dk trong csdl
    public static Scanner sc = new Scanner(System.in);
    public static Connection cn = null;
    
    public int ktTonTaiTrongBD(String ma){
        cn = ConnectDB.SQLConnect();
        String sql = "SELECT * FROM BANGDIEM WHERE MASV='"+ma+"'";
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
            System.out.println(e.getMessage());
        }
        return tonTai;
    }
    
    public int ktsolanThi(String ma){
        cn = ConnectDB.SQLConnect();
        String sql = "SELECT MASV,LAN FROM BANGDIEM WHERE MASV='"+ma+"'AND LAN='"+soLanThi+"'";
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
            System.out.println(e.getMessage());
        }
        return tonTai;
    }
    
    public String dangnhap(String user,String pass){
        cn = ConnectDB.SQLConnect();
        String sql = "SELECT * FROM SINHVIEN sv WHERE sv.UserName='"+user+"' AND sv.PassWord='"+pass+"'";
        String mess = null;
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                mess ="Sinh viên đăng nhập thành công!";
            }else {
                mess="Sinh viên đăng nhập thất bại!";
            }
            rs.close();
            ps.close();
            cn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
        return masv;
    }
    
    public void insertBD(String masv,int lan,String ngaythi,float diem,String baithi){
        cn = ConnectDB.SQLConnect();
        String sql = "EXEC SP_INS_BANGDIEM '"+masv+"','"+lan+"','"+ngaythi+"','"+diem+"','"+baithi+"'";
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.executeUpdate();
            System.out.println("Them bang diem thanh cong!");
            ps.close();
            cn.close();
        } catch (SQLException e) {
            System.out.println("Them bang diem that bai!");
        }
    }
    
    public void updateBD(String masv,int lan,String ngaythi,float diem,String baithi){
        cn = ConnectDB.SQLConnect();
        String sql = "EXEC SP_UPD_BANGDIEM '"+masv+"','"+lan+"','"+ngaythi+"','"+diem+"','"+baithi+"'";
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.executeUpdate();
            System.out.println("Cap nhat bang diem thanh cong!");
            ps.close();
            cn.close();
        } catch (SQLException e) {
            System.out.println("Cap nhat bang diem that bai!");
        }
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
            
            String mess=s.dangnhap(user,pass);
            while(true){
                dos.writeUTF(mess);
                System.out.println(mess);
                if(mess.equals("Sinh viên đăng nhập thành công!")){
                        break;
                } else if(mess.equals("Sinh viên đăng nhập thất bại!")){
                    user = dis.readUTF();
                    pass = dis.readUTF();
                    mess=s.dangnhap(user,pass);
                } 
            }
            //hien cau hoi
            float diem=0;
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
                String masv=s.hienthiMSV(user);
                masv=s.hienthiMSV(user);
                dos.writeUTF(masv);
                float diemlamtron = Math.round(diem * 100)/100;
                dos.writeFloat(diemlamtron);
                int lan=1;
                String baiThi="Thu "+lan;
                String ngayThi=(String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date())));
                String mess2;
                if(s.ktTonTaiTrongBD(masv)==1){
                    if(s.ktsolanThi(masv)==1){
                        System.out.println("Khong the cap nhat diem, chi duoc thi toi da 2 lan, sinh vien "+masv+"da thi 2 lan!");
                        mess2="Sinh vien da thi 2 lan!";
                        dos.writeUTF(mess2);
                    }else{
                        lan++;
                        baiThi="Thu "+lan;
                        s.updateBD(masv, lan, ngayThi, diemlamtron, baiThi);
                    }
                } else {
                    s.insertBD(masv, lan, ngayThi, diemlamtron, baiThi);
                }
            } catch (SQLException e) {
                System.out.println("loi ket noi cau hoi");
            } 
        } catch (IOException e) {
            System.out.println("server ngung ket noi");
        }
    }
}