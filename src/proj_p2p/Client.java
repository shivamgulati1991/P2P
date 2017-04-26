/*
 * References from stack overflow community 
 * wrt to Socket programming, threads, Stream output and inputs in Java
 */
package proj_p2p;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Date;

public class Client implements Runnable {
	private static final String version = "P2P-CI/1.0";
	//public static List<Rfc> rfcList=Collections.synchronizedList(new ArrayList<Rfc>());
	public ServerSocket serverSocket;
	
	public Client(int portNo) throws IOException{
		serverSocket=new ServerSocket(portNo);
		System.out.println("Starting client now..\nClient is at Host: "+InetAddress.getLocalHost().getHostAddress()+" Port number: "+serverSocket.getLocalPort());
		new Thread(this).start();
	}
	
	public static int getRandomPort(int min, int max) {
		Random random = new Random();
		int randomPort = random.nextInt((max - min) + 1) + min;
	    return randomPort;
	}
	
	public static void main(String[] args) throws IOException {
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
			
			//start a new client for peer
			new Client(randomPort);
			try{
				clientSocket=new Socket(IPaddr,port);
				output=new ObjectOutputStream(clientSocket.getOutputStream());
				input=new ObjectInputStream(clientSocket.getInputStream());
				output.writeObject(hostName.toString());
				output.writeObject(randomPort);
				int clientPort=clientSocket.getLocalPort();				
				System.out.println("Client is running now.\nHostname: "+hostName+"  Port: "+randomPort);
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
	
	//Display user menu with options
	private static void userMenu(ObjectOutputStream output,ObjectInputStream input,String hostName,InetAddress IPaddr,int clientPort,int randomPort){
		System.out.println("\n\nPlease select option number from the below choices:");
		System.out.println("\n1 - Add an RFC \n2 - List RFCs \n3 - Lookup RFC \n4 - Download(GET) RFC \n5 - Exit");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		try{
			while(true){
				int choice=Integer.parseInt(br.readLine().trim());
				switch (choice){
				case 1:
					add(output,input,hostName,Integer.toString(randomPort),br);
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 2: 
					showRfcs(output,input,hostName,Integer.toString(randomPort));
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 3: 
					lookupRfc(output,input,br,hostName,Integer.toString(randomPort));
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 4: 
					getRfc(output,input,hostName,br);
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				case 5:
					Server.deleteFromLists(randomPort);
					System.exit(1);
				default: 
					userMenu(output,input,hostName,IPaddr,clientPort,randomPort);
				}
			}
		}
		catch(Exception e){
			System.out.println("Peer closed.");
		}
	}
	
	private static void add(ObjectOutputStream output,ObjectInputStream input,String hostName,String randomPort,BufferedReader br){
		System.out.println("You can add a new RFC here..");
		String rfcNumber=null,rfcTitle=null,fileName=null;
		try{
			System.out.println("Enter RFC number: ");
			rfcNumber=br.readLine().trim();
			System.out.println("Enter RFC title: ");
			rfcTitle=br.readLine().trim();
		}
		catch(Exception e){
			System.err.println(e);
		}
		
		fileName="RFC"+rfcNumber+".txt";
		File location=new File("Rfc");
		//check if file exists
		try{
			File file=new File(location.getCanonicalPath()+"\\"+fileName);
			if(file.exists()){
				output.writeObject(" ADD RFC " + rfcNumber + " " + version + "\n HOST:"+ hostName + "\n PORT:" + randomPort + "\n TITLE:" + rfcTitle + "\n");
				output.writeObject(rfcNumber);
				output.writeObject(hostName);
				output.writeObject(randomPort);
				output.writeObject(rfcTitle);
				System.out.println(input.readObject());
			}
			else if((!file.exists())){
				System.out.println("File doesn't exist for adding.");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	//show all RFCs when LIST called
	private static void showRfcs(ObjectOutputStream output, ObjectInputStream input, String hostName, String randomPort)
			throws IOException {
		System.out.println("Show RFCs called..");
		output.writeObject(" LIST ALL " + version + "\n HOST: " + hostName+ "\n PORT: " + randomPort + "\n");
		try {
			String reply = ((String) input.readObject()).trim();
			System.out.println(reply);
			if ((reply.contains("200 OK"))) {
				reply = (String) input.readObject();
				while (!reply.equalsIgnoreCase("end")) {
					System.out.print(reply);
					reply = (String) input.readObject();
				}
				return;
			} else{
				System.out.println("400 Bad Request. An error occured.");
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	//lookup the entered RFC and return the matching list
	private static void lookupRfc(ObjectOutputStream output, ObjectInputStream input, BufferedReader br, String hostName, String port) throws IOException {
			System.out.println("You can lookup an existing RFC here..");
			System.out.println("Enter RFC number to search:");
			String rfcNumber = br.readLine().trim();
			System.out.println("Enter title:");
			String rfcTitle = br.readLine().trim();
			output.writeObject(" LOOKUP RFC " + rfcNumber + " " + version + "\n HOST: "+ hostName + "\n PORT: " + port + "\n TITLE: " + rfcTitle+ "\n");
			output.writeObject(rfcNumber);
			output.writeObject(rfcTitle);
			
			try {
				String reply = ((String) input.readObject()).trim();
				System.out.println(reply);
				if ((reply.contains("200 OK"))) {
					reply = (String) input.readObject();
					while (!reply.equalsIgnoreCase("\n")) {
						System.out.print(reply);
						reply = (String) input.readObject();
					}
					return;
				} else{
					System.out.println("An error occured");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("An error occured");
			}
		}
	
	//download an RFC from another peer
	private static void getRfc(ObjectOutputStream output, ObjectInputStream input,String hostName,BufferedReader br) throws IOException {
		Socket newSocket = null;
		String host = "";
		int rfcNumber=0,port = 0;
		//take user input for the RFC and host and port and setup connection
		try {
			System.out.println("Enter RFC number: ");
			rfcNumber = Integer.parseInt(br.readLine().trim());
			System.out.println("Enter host: ");
			host = br.readLine().trim();
			System.out.println("Enter port: ");
			port = Integer.parseInt(br.readLine().trim());
			newSocket = new Socket(host, port);
		}
		 catch(Exception e){
			System.out.println("400 Bad Request. An error occured.");
		}
		
		//call the GET operation and download the requested RFC from the peer chosen
		try {
			ObjectOutputStream outputNew = new ObjectOutputStream(newSocket.getOutputStream());
			ObjectInputStream inputNew = new ObjectInputStream(newSocket.getInputStream());
			outputNew.writeObject(" GET RFC " + rfcNumber + " " + version + "\n HOST: "+ hostName + "\n OS: " + System.getProperty("os.name") + "\n");
			outputNew.writeObject(Integer.toString(rfcNumber));
			String reply = ((String) inputNew.readObject()).trim();
			System.out.println(reply);
			if (!reply.startsWith(version)) {
				System.out.println("Error: Peer has different version");
				return;
			}
			//check status response
			if ((reply.contains("200 OK"))) {
				File location = new File("Rfc");
				//append _1 in the end to depict a new file
				File newFile = new File(location.getCanonicalPath() + "\\RFC" + rfcNumber + "_1.txt");
				newFile.createNewFile();
				try {
					byte[] content = (byte[]) inputNew.readObject();
					Files.write(newFile.toPath(), content);
				} 
				catch (EOFException eof) {
					System.out.println("End of file has been reached.");
				}
			} else{
				System.out.println("An error occured while processing the request.");
			}
		} catch (Exception e) {
			System.out.println("An error occured.");
		}
	}
	
	//create a new RFC file and transfer the content when GET called
	private void createNewFileAtPeer(ObjectOutputStream output, ObjectInputStream input) throws IOException, ClassNotFoundException {
		String rfcNumber = (String) input.readObject();
		String fileName = "RFC" + rfcNumber + ".txt";          
		File location = new File("RFC");
		File file = new File(location.getCanonicalPath() + "\\" + fileName);
		
		if (file.exists()) {
			//get the current and date modified for file in GMT
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
			Date currentDate=new Date();
			Date modifiedDate=new Date(file.lastModified());
			System.out.println("Sending RFC to another peer..");
			byte[] content = Files.readAllBytes(file.toPath());
			String data=new String(content);
			output.writeObject(version + " 200 OK \n"+
				"Date: "+ dateFormat.format(currentDate) + " GMT\n"+
				"OS: " + System.getProperty("os.name")+ "\n"+
				"Last-Modified: " + dateFormat.format(modifiedDate) +" GMT \n"+
				"Content-Length: " + file.length() + 
				"\nContent-Type: text/text \n\n"+data);
				output.writeObject(content);
		} 
		else {
			output.writeObject("P2P-CI/1.0 404 Not Found \n");
			output.flush();
		}
		output.close();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket sock1 = null;
		ObjectInputStream inputGet= null; ObjectOutputStream outputGet = null;
		try {
			sock1 = serverSocket.accept();
			new Thread(this).start(); 
			inputGet = new ObjectInputStream(sock1.getInputStream());
			outputGet = new ObjectOutputStream(sock1.getOutputStream());
		} 
		catch (Exception e) {
			System.err.println(e);
			if (sock1.isConnected()) {
				try {
					sock1.close();
				} catch (IOException e1) {
					System.err.println(e1);
				}
			}
			return; 
		}
		try {
			String reply = (String) inputGet.readObject();
			System.out.println(reply);
			if (reply.contains("GET")) {   
				createNewFileAtPeer(outputGet, inputGet);
			}
		} 
		catch (Exception e) {
			System.err.println(e);
		} 
		finally {
			try {
				outputGet.close(); inputGet.close(); sock1.close();
			} 
			catch (Exception e) {
				System.err.println(e);
			}
		}
	}
}
