package com.atm;
import com.atm.controllers.iso.*;
import com.atm.controllers.HttpController;
import com.atm.controllers.messaging.MessageSender;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static MessageSender messageSender = new MessageSender();
    private static Scanner sc = new Scanner(System.in);
    private static ISOController isoController = new ISOController();
//    private static HttpController httpController = new HttpController();
    private static String acc_number,pin;
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static ServerSocket serverSocket;
    private static Socket socket;
    public static void main(String[] args) {
        String server = "localhost5000";
//        System.out.println(server.substring(0,9));
        int pilihan;
        String konfirmasi,message,result,url;
        login();
        do {
            mainMenu();
            pilihan = Integer.parseInt(sc.nextLine());
            switch (pilihan){
                case 1:
                    int jumlah = pilihanTarikTunai();
                    try {
                        ISOWithdrawController isoWithdrawController = new ISOWithdrawController();
                        message = isoWithdrawController.buildISO(acc_number,pin,jumlah,server);
//                        url="withdraw";
//                        result = httpController.sendHttpRequest(message,url);
                        messageSender.createTask(message);
                        serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                        socket = serverSocket.accept();
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        result = dataInputStream.readUTF();
                        ISOMsg isoMsg = isoController.parseISOMessage(result);
                        if (isoMsg.getString(39).equalsIgnoreCase("00")){
                            System.out.println("Uang yang ditarik : Rp."+jumlah);
                            System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
                        }else if (isoMsg.getString(39).equalsIgnoreCase("51")){
                            System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
                            System.out.println("Saldo tidak mencukupi");
                        } else {
                            System.out.println("Pin / No Rekening salah");
                        }
                        serverSocket.close();
                        socket.close();
                        dataInputStream.close();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }


                    break;
                case 2:
                    int opsi;
                    String norekTujuan;
                    System.out.println("1.Ke Sesama Bank");
                    System.out.println("2.Ke Bank lain");
                    System.out.print("Masukkan Pilihan : ");
                    opsi=Integer.parseInt(sc.nextLine());
                    switch (opsi){
                        case 1:
                            try{
                                ISOTransInquiryController isoTransInquiryController = new ISOTransInquiryController();
                                ISOTransferController isoTransferController = new ISOTransferController();
                                System.out.print("Masukkan No Rekening Tujuan : ");
                                norekTujuan = sc.nextLine();
                                System.out.print("Masukkan Nominal : Rp.");
                                jumlah = Integer.parseInt(sc.nextLine());
                                message = isoTransInquiryController.buildISO(acc_number,pin,jumlah,"sama",norekTujuan,server);
//                                url="transinquiry";
//                                result = httpController.sendHttpRequest(message,url);

                                messageSender.createTask(message);
                                serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                                socket = serverSocket.accept();
                                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                result = dataInputStream.readUTF();

                                serverSocket.close();
                                socket.close();
                                dataInputStream.close();
                                ISOMsg isoMsg = isoController.parseISOMessage(result);
                                if (isoMsg.getString(39).equalsIgnoreCase("00")){
                                    System.out.println("No Rekening : "+norekTujuan);
                                    System.out.println("Atas Nama : "+isoMsg.getString(103).toUpperCase());
                                    System.out.println("Transfer sejumlah : Rp."+jumlah);
                                    System.out.print("Apakah anda yakin untuk transfer (ya/tidak)? ");
                                    konfirmasi = sc.nextLine();
                                    if (konfirmasi.equalsIgnoreCase("ya")){
                                        message = isoTransferController.buildISO(acc_number,pin,jumlah,"sama",norekTujuan,server);
//                                        url="transfer";
//                                        result = httpController.sendHttpRequest(message,url);
                                        messageSender.createTask(message);
                                        serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                                        socket = serverSocket.accept();
                                        dataInputStream = new DataInputStream(socket.getInputStream());
                                        result = dataInputStream.readUTF();

                                        isoMsg = isoController.parseISOMessage(result);
                                        if (isoMsg.getString(39).equalsIgnoreCase("00")){
                                            System.out.println("Transfer ke Nomor Rekening : "+isoMsg.getString(103).toUpperCase()+" Sukses");
                                            System.out.println("Sisa saldo Anda : Rp."+isoMsg.getString(62));
                                        }else if (isoMsg.getString(39).equalsIgnoreCase("12")){
                                            System.out.println("Transfer Gagal");
                                        }
                                        serverSocket.close();
                                        socket.close();
                                        dataInputStream.close();
                                        break;
                                    }else {
                                        break;
                                    }
                                }else if (isoMsg.getString(39).equalsIgnoreCase("05")){
                                    System.out.println("Pin / No Rekening salah");
                                }else if (isoMsg.getString(39).equalsIgnoreCase("76")){
                                    System.out.println("Rekening tujuan tidak terdaftar");
                                }else if (isoMsg.getString(39).equalsIgnoreCase("51")){
                                    System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
                                    System.out.println("Saldo tidak mencukupi");
                                }
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 2:
                            try{
                                ISOSwitchTransInquiryController isoSwitchTransInquiryController =
                                        new ISOSwitchTransInquiryController();
                                ISOSwitchTransferController isoSwitchTransferController = new ISOSwitchTransferController();
                                System.out.print("Masukkan No Rekening Tujuan (beserta kode bank 3333) : ");
                                norekTujuan = sc.nextLine();
                                String kodeBank = norekTujuan.substring(0,4);
                                String norekTujuanSub = norekTujuan.substring(4);
                                System.out.print("Masukkan Nominal : Rp.");
                                jumlah = Integer.parseInt(sc.nextLine());
                                message = isoSwitchTransInquiryController.buildISO(acc_number,pin,jumlah,kodeBank,norekTujuanSub,server);
//                                url="switchingtransinquiry";
//                                result = httpController.sendHttpRequest(message,url);
                                messageSender.createTask(message);
                                serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                                socket = serverSocket.accept();
                                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                result = dataInputStream.readUTF();

                                serverSocket.close();
                                socket.close();
                                dataInputStream.close();
                                ISOMsg isoMsg = isoController.parseISOMessage(result);
                                if (isoMsg.getString(39).equalsIgnoreCase("00")){
                                    System.out.println("No Rekening : "+norekTujuanSub);
                                    System.out.println("Atas Nama : "+isoMsg.getString(103).toUpperCase());
                                    System.out.println("Transfer sejumlah : Rp."+jumlah);
                                    System.out.println("Dikenakan Potongan sebesar Rp.6500");
                                    System.out.println("Total Biaya : Rp."+Integer.parseInt(isoMsg.getString(4)));
                                    System.out.print("Apakah anda yakin untuk transfer (ya/tidak)? ");
                                    konfirmasi = sc.nextLine();
                                    if (konfirmasi.equalsIgnoreCase("ya")){
                                        message = isoSwitchTransferController.buildISO(acc_number,pin,jumlah,kodeBank,norekTujuanSub,server);
//                                        url="switchingtransfer";
//                                        result = httpController.sendHttpRequest(message,url);
                                        messageSender.createTask(message);
                                        serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                                        socket = serverSocket.accept();
                                        dataInputStream = new DataInputStream(socket.getInputStream());
                                        result = dataInputStream.readUTF();

                                        isoMsg = isoController.parseISOMessage(result);
                                        if (isoMsg.getString(39).equalsIgnoreCase("00")){
                                            System.out.println("Transfer ke Nomor Rekening : "+isoMsg.getString(103)+" Sukses");
                                            System.out.println("Sisa saldo Anda : Rp."+isoMsg.getString(62));
                                        }else if (isoMsg.getString(39).equalsIgnoreCase("12")){
                                            System.out.println("Transfer Gagal");
                                        }
                                        serverSocket.close();
                                        socket.close();
                                        dataInputStream.close();
                                        break;
                                    }else {
                                        break;
                                    }
                                }else if (isoMsg.getString(39).equalsIgnoreCase("05")){
                                    System.out.println("Pin / No Rekening salah");
                                }else if (isoMsg.getString(39).equalsIgnoreCase("76")){
                                    System.out.println("Rekening tujuan tidak terdaftar");
                                }else if (isoMsg.getString(39).equalsIgnoreCase("51")){
                                    System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
                                    System.out.println("Saldo tidak mencukupi");
                                }
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                            break;
                        default:
                            System.out.println("Invalid input");
                            break;
                    }
                    break;
                case 3:
                    try {
                        ISOPurchaseController isoPurchaseController = new ISOPurchaseController();
                        ISOPurchaseInquiryController isoPurchaseInquiryController = new ISOPurchaseInquiryController();
                        String phoneNumber;
                        System.out.print("Masukkan Nomor Telepon : ");
                        phoneNumber = sc.nextLine();
                        jumlah = pilihanPurchase();
                        message = isoPurchaseInquiryController.buildISO(acc_number,pin,jumlah,phoneNumber,server);

                        messageSender.createTask(message);
                        serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                        socket = serverSocket.accept();
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        result = dataInputStream.readUTF();

//                        url="purchaseinquiry";
//                        result = httpController.sendHttpRequest(message,url);
                        ISOMsg isoMsg = isoController.parseISOMessage(result);
                        serverSocket.close();
                        socket.close();
                        dataInputStream.close();
                        if (isoMsg.getString(39).equalsIgnoreCase("00")){
                            System.out.println("Nomor Hp Tujuan : "+phoneNumber);
                            System.out.println("Pembelian Pulsa Sebesar : Rp."+jumlah);
                            System.out.println("Dikenakan Potongan sebesar Rp.1500");
                            System.out.print("Apakah anda yakin untuk membayar (ya/tidak)? ");
                            konfirmasi = sc.nextLine();
                            if (konfirmasi.equalsIgnoreCase("ya")){
                                message = isoPurchaseController.buildISO(acc_number,pin,jumlah,phoneNumber,server);
//                                url="purchase";
//                                result = httpController.sendHttpRequest(message,url);
                                messageSender.createTask(message);
                                serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                                socket = serverSocket.accept();
                                dataInputStream = new DataInputStream(socket.getInputStream());
                                result = dataInputStream.readUTF();
                                isoMsg = isoController.parseISOMessage(result);
                                if (isoMsg.getString(39).equalsIgnoreCase("00")){
                                    System.out.println("Pengisian Pulsa sebesar Rp."+jumlah+" ke Nomor Telepon " + phoneNumber+
                                            " telah berhasil");
                                    System.out.println("Dikenakan Potongan sebesar Rp.1500");
                                    System.out.println("Sisa saldo Anda : Rp."+isoMsg.getString(62));
//                                    break;
                                }else if (isoMsg.getString(39).equalsIgnoreCase("12")){
                                    System.out.println("Pembelian Gagal");
//                                    break;
                                }
                                serverSocket.close();
                                socket.close();
                                dataInputStream.close();
                                break;
                            }else {
                                break;
                            }
                        }else if (isoMsg.getString(39).equalsIgnoreCase("05")){
                            System.out.println("Pin / No Rekening salah");
                        }else if (isoMsg.getString(39).equalsIgnoreCase("51")){
                            System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
                            System.out.println("Saldo tidak mencukupi");
                        }
                    }catch (Exception e){
                        System.out.println("Error : "+e.getMessage());
                    }
                case 4:
                    try {
                        ISOBalanceController isoBalanceController = new ISOBalanceController();
                        message = isoBalanceController.buildISO(acc_number,pin,server);
//                        url="check";
//                        System.out.println(message);
                        messageSender.createTask(message);
                        serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                        socket = serverSocket.accept();
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        result = dataInputStream.readUTF();
//                        System.out.println(respon);

//                        result = httpController.sendHttpRequest(message,url);
                        ISOMsg isoMsg = isoController.parseISOMessage(result);
                        if (isoMsg.getString(39).equalsIgnoreCase("00")){
                            System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
//                            logger.info("Sisa Saldo {} dengan Nomor Rekening {}", isoMsg.getString(62),acc_number);
                        }else {
                            System.out.println("Pin / No Rekening salah");
                        }
                        serverSocket.close();
                        socket.close();
                        dataInputStream.close();
                    }catch (Exception e){
                        logger.error(e.getMessage());
                        System.out.println(e.getMessage());
                    }
                    break;
                case 5:
                    try{
                        ISOPaymentInquiryController isoPaymentInquiryController = new ISOPaymentInquiryController();
                        ISOPaymentController isoPaymentController = new ISOPaymentController();
                        String virtualAccount;
                        System.out.print("Masukkan Nomor Virtual Account : ");
                        virtualAccount = sc.nextLine();
                        System.out.print("Masukkan Nominal : Rp.");
                        jumlah = Integer.parseInt(sc.nextLine());
                        message = isoPaymentInquiryController.buildISO(acc_number,pin,jumlah,
                                "89508",virtualAccount,server);
//                        url="paymentinquiry";
//                        result = httpController.sendHttpRequest(message,url);
                        messageSender.createTask(message);
                        serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                        socket = serverSocket.accept();
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        result = dataInputStream.readUTF();

                        serverSocket.close();
                        socket.close();
                        dataInputStream.close();

                        ISOMsg isoMsg = isoController.parseISOMessage(result);

                        if (isoMsg.getString(39).equalsIgnoreCase("00")){
                            System.out.println("DANA Top Up Code : "+isoMsg.getString(33));
                            System.out.println("No Rekening : "+virtualAccount);
                            System.out.println("Atas Nama : "+isoMsg.getString(102).toUpperCase());
                            System.out.println("Top Up Sebesar : Rp."+jumlah);
                            System.out.print("Apakah anda yakin untuk membayar (ya/tidak)? ");
                            konfirmasi = sc.nextLine();
                            if (konfirmasi.equalsIgnoreCase("ya")){
                                message = isoPaymentController.buildISO(acc_number,pin,jumlah,isoMsg.getString(33),virtualAccount,server);

//                                url="payment";
//                                result = httpController.sendHttpRequest(message,url);

                                messageSender.createTask(message);
                                serverSocket = new ServerSocket(Integer.parseInt(server.substring(9)));
                                socket = serverSocket.accept();
                                dataInputStream = new DataInputStream(socket.getInputStream());
                                result = dataInputStream.readUTF();
                                isoMsg = isoController.parseISOMessage(result);
                                if (isoMsg.getString(39).equalsIgnoreCase("00")){
                                    System.out.println("Top Up DANA sebesar Rp."+jumlah+" ke Nomor Rekening " + virtualAccount+
                                            " telah berhasil");
                                    System.out.println("Sisa saldo Anda : Rp."+isoMsg.getString(62));
                                }else if (isoMsg.getString(39).equalsIgnoreCase("12")){
                                    System.out.println("Transfer Gagal");
                                }
                                serverSocket.close();
                                socket.close();
                                dataInputStream.close();
                                break;
                            }else {
                                break;
                            }
                        }else if (isoMsg.getString(39).equalsIgnoreCase("05")){
                            System.out.println("Pin / No Rekening salah");
                        }else if (isoMsg.getString(39).equalsIgnoreCase("76")){
                            System.out.println("Rekening tujuan tidak terdaftar");
                        }else if (isoMsg.getString(39).equalsIgnoreCase("51")){
                            System.out.println("Sisa saldo : Rp."+isoMsg.getString(62));
                            System.out.println("Saldo tidak mencukupi");
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 6:
                    System.out.println("Exitting Program...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
            System.out.print("Transaksi lagi (ya/tidak)? ");
            konfirmasi=sc.nextLine();
            if (konfirmasi.equalsIgnoreCase("ya")){
                login();
            }else {
                break;
            }
        }while (pilihan!=6);
    }

    private static void login(){
        System.out.print("Account Number : ");
        acc_number = sc.nextLine();
        System.out.print("PIN : ");
        pin = sc.nextLine();
    }

    private static void mainMenu(){
        System.out.println("\t\t-ATM UNTUK KITA SEMUA-");
        System.out.println("1.Tarik Tunai\t\t\t 4.Info Saldo");
        System.out.println("2.Transfer\t\t\t\t 5.Pembayaran (DANA)");
        System.out.println("3.Pembelian (Pulsa)\t\t 6.Exit");
        System.out.print("Masukkan Pilihan : ");
    }

    private static Integer pilihanTarikTunai(){
        int jumlah=0,opsi;
        System.out.println("1.50000");
        System.out.println("2.100000");
        System.out.println("3.200000");
        System.out.println("4.Jumlah lainnya");
        System.out.print("Masukkan Pilihan : ");
        opsi = Integer.parseInt(sc.nextLine());
        switch (opsi){
            case 1:
                jumlah=50000;
                break;
            case 2:
                jumlah=100000;
                break;
            case 3:
                jumlah=200000;
                break;
            case 4:
                do {
                    System.out.print("Masukkan nominal (kelipatan 50000) : ");
                    jumlah = Integer.parseInt(sc.nextLine());
                }while (jumlah%50000!=0);
                break;
            default:
                System.out.println("Invalid input");
                break;
        }
        return jumlah;
    }

    private static Integer pilihanPurchase(){
        int jumlah=0,opsi;
        System.out.println("1.25000\t\t4.200000");
        System.out.println("2.50000\t\t5.300000");
        System.out.println("3.100000\t6.500000");
        System.out.print("Masukkan Pilihan : ");
        opsi = Integer.parseInt(sc.nextLine());
        if (opsi==1) jumlah=25000;
        else if (opsi==2) jumlah=50000;
        else if (opsi==3) jumlah=100000;
        else if (opsi==4) jumlah=200000;
        else if (opsi==5) jumlah=300000;
        else if (opsi==6) jumlah=500000;
        else System.out.println("Invalid input");
        return jumlah;
    }
}