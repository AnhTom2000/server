package com.hciot.sample.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
//@Component
public class CommUtil implements SerialPortEventListener {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


	GreetingServer greetingServer;
	InputStream inputStream; // 从串口来的输入流
	OutputStream outputStream;// 向串口输出的流
	SerialPort serialPort; // 串口的引用
	CommPortIdentifier portId;
	
	public CommUtil(GreetingServer greetingServer) {
		Enumeration portList = CommPortIdentifier.getPortIdentifiers(); //得到当前连接上的端口
		String name="COM2";
		while (portList.hasMoreElements()) {
			CommPortIdentifier temp = (CommPortIdentifier) portList.nextElement();
			if (temp.getPortType() == CommPortIdentifier.PORT_SERIAL) {// 判断如果端口类型是串口
				if (temp.getName().equals(name)) { // 判断如果端口已经启动就连接
					portId = temp;
				}
			}
		}
		try {
			assert portId != null;
			serialPort = (SerialPort) portId.open("My"+name, 2000);
		} catch (PortInUseException e) {

		}
		try {
			assert serialPort != null;
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
		}
		try {
			serialPort.addEventListener(this); // 给当前串口天加一个监听器
		} catch (TooManyListenersException e) {
		}
		serialPort.notifyOnDataAvailable(true); // 当有数据时通知
		try {
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, // 设置串口读写参数
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		}
		System.err.println("我是CommUtil，我被实例化了");
		this.greetingServer = greetingServer;
	}


	public void serialEvent(SerialPortEvent event) {
		int numBytes=0;
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		
		case SerialPortEvent.DATA_AVAILABLE:// 当有可用数据时读取数据,并且给串口返回数据
			byte[] readBuffer = new byte[200];
			
			try {
				while (inputStream.available() > 0) {
					numBytes = inputStream.read(readBuffer);
				}

				String data=new String(readBuffer).trim();
				greetingServer.sendToClient(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	public void send(String content){
		try {
			byte[] a = content.getBytes();
			outputStream.write(a);
			System.out.println("发送成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ClosePort() {
	    if (serialPort != null) {
	      serialPort.close();
	    }
	  }

	
}

