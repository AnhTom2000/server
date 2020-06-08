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
	InputStream inputStream; // �Ӵ�������������
	OutputStream outputStream;// �򴮿��������
	SerialPort serialPort; // ���ڵ�����
	CommPortIdentifier portId;
	
	public CommUtil(GreetingServer greetingServer) {
		Enumeration portList = CommPortIdentifier.getPortIdentifiers(); //�õ���ǰ�����ϵĶ˿�
		String name="COM2";
		while (portList.hasMoreElements()) {
			CommPortIdentifier temp = (CommPortIdentifier) portList.nextElement();
			if (temp.getPortType() == CommPortIdentifier.PORT_SERIAL) {// �ж�����˿������Ǵ���
				if (temp.getName().equals(name)) { // �ж�����˿��Ѿ�����������
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
			serialPort.addEventListener(this); // ����ǰ�������һ��������
		} catch (TooManyListenersException e) {
		}
		serialPort.notifyOnDataAvailable(true); // ��������ʱ֪ͨ
		try {
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, // ���ô��ڶ�д����
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		}
		System.err.println("����CommUtil���ұ�ʵ������");
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
		
		case SerialPortEvent.DATA_AVAILABLE:// ���п�������ʱ��ȡ����,���Ҹ����ڷ�������
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
			System.out.println("���ͳɹ�");
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

