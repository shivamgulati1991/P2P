package proj_p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Server implements Runnable {
	private static final String version = "P2P-CI/1.0";
	public static List<Rfc> rfcList=Collections.synchronizedList(new ArrayList<Rfc>());
	public static List<Peers> peerList=Collections.synchronizedList(new ArrayList<Peers>());
	public ServerSocket sock;
	
	public Server(int portNo) throws IOException{
		sock=new ServerSocket(portNo);
		System.out.println("Starting server now..");
      	System.out.println("Host: "+ InetAddress.getLocalHost().getHostAddress()+ "  Port: " + sock.getLocalPort());
		System.out.println("OS: " + System.getProperty("os.name"));
		System.out.println("Version: " + version);
		new Thread(this).start();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int portNo=Integer.parseInt(args[0]);
		if(args.length == 1){	
			new Server(portNo);
		}
		else{
			System.out.println("Error. Check arguments. Add port number as an argument if missing.");
		}
	}

	public void addRfc(ObjectOutputStream output, ObjectInputStream input){
		int clientPort = 0;
		try {
				String rfcNumber = (String) input.readObject();
				String hostName = (String) input.readObject();
				String port = (String) input.readObject();
				String title = (String) input.readObject();
				clientPort = Integer.parseInt((String) input.readObject());
				Rfc newRfc=new Rfc(Integer.parseInt(rfcNumber),title,hostName);
				rfcList.add(newRfc);
				for(Rfc peer:rfcList)
					System.out.println(peer.rfcnumber+" "+peer.hostname+" "+peer.title);
				
			} catch (Exception e) {
				System.out.println(e);
			}
	}
	
	public void deleteRfc(){
		
	}
	
	public void showRfcs(){
		
	}
	
	public void lookupRfc(){
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Socket socket=null;
		ObjectInputStream  input = null;
	    ObjectOutputStream output = null;
	    String hostName = null;
	    int cPort = 0;
	    try{
		    socket = sock.accept();
	        new Thread(this).start();
	        cPort = socket.getPort();
	        String clientIP=socket.getInetAddress().toString();
			System.out.println("Client connected at port: "+ cPort);
			input = new ObjectInputStream (socket.getInputStream());
		    output = new ObjectOutputStream(socket.getOutputStream());
		    hostName = (input.readObject()).toString();
			Peers newPeer=new Peers(hostName+" "+clientIP,cPort);
			peerList.add(newPeer);
			System.out.println("New peer has been added.");
			
			//Display peerlist
			for(Peers peer:peerList)
				System.out.println(peer.hostname+" "+peer.port);
	    }
	    catch(Exception e){
	    	System.err.println(e);
	    }
	    
		   try { 
			   while (true)
		       {  
				   	String request = (String) input.readObject();
				 	System.out.println(request);
				 	//String[] result = request.trim().split("\\s");
				 	//executeRequest(result[0], input, output);
					switch(request.trim().split("\\s")[0].trim()){
					case "ADD":{
						addRfc(output, input);	
						break;
					}
					/*case "LIST":{
						listAllRFCs(input, output);
						break;
					}
					case "LOOKUP":{
						lookUpAnRFC(input, output);
						break;
					}*/
					case "default":{
						break;
					}
				}
		        }       
		  	} catch (Exception e) {
		  		//removePeer(clientPort, true);
		  		System.err.println(e);
		  		//System.out.println("Connection with client " + hostName +" @ " + clientPort + " closed");
		  	}
		
	}

}
