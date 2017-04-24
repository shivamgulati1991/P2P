package proj_p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
				userMenu(output,input,hostName,InetAddress.getByName(IPaddr),clientPort,randomPort);
			}
			catch(Exception e){
				System.err.print(e);
			}
		}
		else{
			System.out.println("Incorrect arguments entered.");
		}
	}
	
	private static void userMenu(ObjectOutputStream output,ObjectInputStream input,String hostName,InetAddress IPaddr,int clientPort,int randomPort){
		System.out.println("Please select option number from the below choices:");
		System.out.println("\n1 - Add an RFC \n2 - List RFCs \n3 - Lookup RFC \n4 - Download(GET) RFC \n5 - Exit");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		try{
			while(true){
				int choice=Integer.parseInt(br.readLine());
				switch (choice){
				case 1:
					add(output,input,hostName,Integer.toString(randomPort),br);
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 2: 
					showRfcs(output,input,hostName,Integer.toString(randomPort));
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 3: 
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 4: 
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 5: System.exit(1);
				default: 
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				}
			}
		}
		catch(Exception e){
			System.out.println("Error occured!");
			System.err.println(e);
		}
	}
	
	private static void add(ObjectOutputStream output,ObjectInputStream input,String hostName,String randomPort,BufferedReader br){
		String rfcNumber=null,rfcTitle=null,fileName=null;
		try{
			System.out.println("Enter RFC number: ");
			rfcNumber=br.readLine();
			System.out.println("Enter RFC title: ");
			rfcTitle=br.readLine();
		}
		catch(Exception e){
			System.err.println(e);
		}
		
		fileName="RFC"+rfcNumber+".txt";
		File location=new File("Rfc");
		
		try{
			File file=new File(location.getCanonicalPath()+"\\"+fileName);
			if(file.exists()){
				System.out.println("yes");
				output.writeObject(" ADD RFC " + rfcNumber + " " + version + "\n HOST:"+ InetAddress.getByName(hostName) + "\n PORT:" + randomPort + "\n TITLE:" + rfcTitle + "\n");
				output.writeObject(rfcNumber);
				output.writeObject(hostName);
				output.writeObject(randomPort);
				output.writeObject(rfcTitle);
				System.out.println("RFC has been added\n");
				//System.out.println(input.readObject());
			}
			else if((!file.exists())){
				System.out.println("File doesn't exist for adding.");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	private static void showRfcs(ObjectOutputStream output, ObjectInputStream input, String hostName, String randomPort)
			throws IOException {
		output.writeObject(" LIST ALL " + version + "\n HOST: " + InetAddress.getByName(hostName)
				+ "\n PORT: " + randomPort + "\n");
		try {
			String resp = ((String) input.readObject()).trim();
			System.out.println(resp);
			if (! resp.startsWith(version)) {
				System.out.println("Error: Peer has different version");
				return;
			}
			System.out.println("200 ok");
			if ((resp.contains("200 OK"))) {
				System.out.println("200 ok");
				resp = (String) input.readObject();
				while (!resp.equalsIgnoreCase("end")) {
					System.out.print(resp);
					resp = (String) input.readObject();
				}
				return;
			} else{
				//handleErrorMessages(resp);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
