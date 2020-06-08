package com.hciot.sample.client;

//文件名 GreetingClient.java

import java.net.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GreetingServer {

    private ServerSocket serverSocket;
    private Socket server;
    private OutputStream out;
    private DataInputStream in;
    private final int port = 9999;

    public GreetingServer() throws IOException {

        serverSocket = new ServerSocket(port);
        server = serverSocket.accept();

        try {
            OutputStream outToClient = server.getOutputStream();
            out = new DataOutputStream(outToClient);

            InputStream inToClient = server.getInputStream();
            in = new DataInputStream(inToClient);
            CommUtil commUtil = new CommUtil(this);
            new Thread(() -> {
                while (true) {
                    byte[] b = new byte[1024];
                    int i = 0;
                    try {
                        i = in.read(b);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String s = new String(b);
                    System.out.println("data:" + s);
                    //串口对象的发送方法发送到单片机当中

                    commUtil.send(s);
                }
            }
            ).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendToClient(String a) throws IOException {
       if(out == null)  out = new DataOutputStream(server.getOutputStream());
        System.out.println("1"+out);
            try {
                System.out.println("123456" + a);
                out.write(a.getBytes());
                out.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
}
	

