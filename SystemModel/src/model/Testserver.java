package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Testserver {

	public static void main(String[] args) throws IOException {
		int port = 12345;
		ServerSocket sockSrv = new ServerSocket(port);
		
		Socket soc = sockSrv.accept();
		messages(soc);
		System.out.println("Closing socket..");
		soc.close();
		sockSrv.close();
	}
	
	private static void messages(Socket soc) throws IOException {
		System.out.println("Connected to: "+soc.getRemoteSocketAddress());
		BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		PrintWriter out = new PrintWriter(soc.getOutputStream(),true);
		String rc = "";
		while(!rc.equals("shutdown")) {
			try {
				rc = in.readLine();
			}catch(SocketException e){
				System.out.println("Socket connection lost. Exiting...");
				break;
			}
			System.out.println("Input: "+rc);
			out.println("Command: "+rc);
		}
		in.close();
		out.close();
	}

}
