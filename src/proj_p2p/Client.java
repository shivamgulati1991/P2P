package proj_p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Client implements Runnable {
	private static final String version = "P2P-CI/1.0";
	public static List<Rfc> rfcList=Collections.synchronizedList(new ArrayList<Rfc>());
	public ServerSocket serverSocket;
	
	public Client(int portNo) throws IOException{
		serverSocket=new ServerSocket(portNo);
		System.out.println("Starting client now..");
		System.out.println("Client is at Host: "+InetAddress.getLocalHost().getHostAddress());
		System.out.println("Port number: "+serverSocket.getLocalPort());
		new Thread(this).start();
	}
	
	public static int getRandomPort(int min, int max) {
		Random random = new Random();
		int randomPort = random.nextInt((max - min) + 1) + min;
	    return randomPort;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String IPaddr=null,hostName = null;
		if(args.length==2){
			IPaddr=args[0];
			int port=Integer.parseInt(args[1]);
			try {
				hostName=InetAddress.getByName(IPaddr).getHostName();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Socket clientSocket=null;
			ObjectOutputStream output=null;
			ObjectInputStream input=null;
			int randomPort=Client.getRandomPort(3000, 5000);
			try{
				clientSocket=new Socket(IPaddr,port);
				output=new ObjectOutputStream(clientSocket.getOutputStream());
				input=new ObjectInputStream(clientSocket.getInputStream());
				output.writeObject(hostName.toString());
				int clientPort=clientSocket.getLocalPort();
				
				System.out.println("Client is running now."+"Randomport: "+randomPort);
				System.out.println("Hostname: "+hostName+"  Port: "+clientPort);
			}
			catch(Exception e){
				System.err.print(e);
			}
		}
		else{
			System.out.println("Incorrect arguments entered.");
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
