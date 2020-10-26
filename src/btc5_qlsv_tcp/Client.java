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
/**
 *
 * @author Admin
 */
public class Client {
    public static Client c = new Client();
    public static Socket client = null;
    public static int port = 2010;
    public static Scanner sc = new Scanner(System.in);
    public static int slCH = 10;
    public static ArrayList<String> arrList = new ArrayList<String>(slCH);
    
    public static void main(String[] args) throws IOException {
        client = new Socket("localhost", port);
        try {
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            //DANG NHAP
            System.out.println("nhap username: ");
            String user = sc.nextLine();
            sc = new Scanner(System.in);
            System.out.println("nhap password: ");
            String pass = sc.nextLine();
            //gui thong tin dang nhap
            dos.writeUTF(user);
            dos.writeUTF(pass);
            //nhan thong tin dang nhap tu server
            String mess = dis.readUTF();
            System.out.println(mess);
            while(true){
                if(mess.equals("Sinh viên đăng nhập thành công!")){
                    break;
                } else if(mess.equals("Sinh viên đăng nhập thất bại!")){
                    sc = new Scanner(System.in);
                    System.out.println("nhap username: ");
                    user = sc.nextLine();
                    sc = new Scanner(System.in);
                    System.out.println("nhap password: ");
                    pass = sc.nextLine();
                    
                    //gui thong tin dang nhap
                    dos.writeUTF(user);
                    dos.writeUTF(pass);
                    //nhan thong tin dang nhap tu server
                    mess = dis.readUTF();
                    System.out.println(mess);
                }
            }
            //hien cau hoi
            try {
                for(int i=1;i<=slCH;i++){
                    String noidung= dis.readUTF();
                    String a= dis.readUTF();
                    String b= dis.readUTF();
                    String c= dis.readUTF();
                    String d= dis.readUTF();

                    System.out.println(i+") "+noidung);
                    System.out.println("a) "+a+"\tb) "+b+"\tc) "+c+"\td) "+d);
                    System.out.print("nhap cau tra loi(A,B,C,D): ");
                    String traloi=sc.nextLine();
                    String inTraloiHoa=traloi.toUpperCase();
                    while(true){
                        if(inTraloiHoa.equals("A")||inTraloiHoa.equals("B")||inTraloiHoa.equals("C")||inTraloiHoa.equals("D")){
                            dos.writeUTF(inTraloiHoa);
                            break;
                        }else{
                            System.out.print("nhap cau tra loi(A,B,C,D): ");
                            traloi=sc.nextLine();
                            inTraloiHoa=traloi.toUpperCase();
                        }
                    }
                    String dapan=dis.readUTF();//doc dap an tu server
                    arrList.add(dapan);
                }
                //in dap an
                System.out.println("Dap an cua bai thi la:");
                int i=1;
                for (String str : arrList) {
                    System.out.println(i+"-"+str);
                    i++;
                    if(i>slCH) break;
                }
                //in thong tin diem sv
                String masv = dis.readUTF();
                System.out.println("Ma sinh vien: "+masv);
                float diem = dis.readFloat();
                System.out.print("Tong diem bt cua ban la: "+diem+" diem\n");
                String mess2=dis.readUTF();
                System.out.println(mess2+", điểm không thể lưu!");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("client ngung ket noi");
        }
    }
}
