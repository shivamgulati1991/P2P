package proj_p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

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
		if(args.length == 1){	
			new Server(Integer.parseInt(args[0]));
		}
		else{
			System.out.println("Pass port number as argument");
		}
	}

	public void addRfc(ObjectOutputStream output, ObjectInputStream input){
		try {
				String rfcNumber = (String) input.readObject();
				String hostName = (String) input.readObject();
				int clientPort = Integer.parseInt(((String) input.readObject()).trim());
				String title = (String) input.readObject();
				
				Rfc newRfc=new Rfc(Integer.parseInt(rfcNumber),title,hostName+":"+clientPort);
				rfcList.add(newRfc);
				for(Rfc peer:rfcList)
					System.out.println(peer.rfcnumber+" "+peer.hostname+" "+peer.title);
				
				System.out.println("RFC has been added successfully");
				output.writeObject("RFC has been added successfully");
			} catch (Exception e) {
				System.out.println(e);
			}
	}
	

	public void showRfcs(ObjectOutputStream output,ObjectInputStream input) {
		try{
			output.writeObject(version + " 200 OK\n");
			ListIterator<Rfc> iterator = rfcList.listIterator();
			Rfc traverseRfc = null;
		    while((iterator.hasNext()))                                    
		    {
		    	traverseRfc = iterator.next();
		    	output.writeObject(traverseRfc.rfcnumber + " " + traverseRfc.title + " " + traverseRfc.hostname + "\n");
		    }
		    output.writeObject("end");
		} catch(Exception e){
			System.out.println("An error occured.");
		}
	}
	
	public void lookupRfc(ObjectOutputStream output,ObjectInputStream input){
		try {
			int rfcNumber = Integer.parseInt((String) input.readObject());
			String rfcTitle= (String) input.readObject();
			ListIterator<Rfc> iterator = rfcList.listIterator();
			Rfc traverseRfc = null;
			int checkExists = 0;
			String existingRfcs="";
			
			//get all the lookup values for RFCs
			while((iterator.hasNext()))                                    
		    {
				//Iterate to find RFCs
				traverseRfc = (Rfc) iterator.next();
		    	if(rfcNumber==traverseRfc.rfcnumber){
		    		checkExists = 1;
		    		existingRfcs=existingRfcs + rfcNumber + " " + traverseRfc.title + " " + traverseRfc.hostname + "\n";
		    	}
		    }
			if(checkExists==1){
			output.writeObject(version + " 200 OK\n");
			output.writeObject(existingRfcs);
			output.writeObject("\n");
			}
			else{
				output.writeObject("404 Not Found");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void deleteFromLists(int port){
			int i = 0;
			int rfcPort;
			try{
				Rfc traverseRfc=null;
				while(i<rfcList.size()){
					traverseRfc=(Rfc) rfcList.get(i);
					rfcPort=Integer.parseInt(traverseRfc.hostname.split(":")[1]);
					if(rfcPort==port){
						rfcList.remove(i);
						i=0;
						continue;
					}
					i++;
				}
			}
			catch(Exception e){
				System.out.println("An error occured. Try again.");
			}
		try {
			ListIterator<Peers> iter = peerList.listIterator();
			Peers traversePeer = null;
			while((iter.hasNext()))                                    
			{
				traversePeer = (Peers) iter.next();
				if(port==traversePeer.port){
					peerList.remove(traversePeer);				
				}
			}
		} catch (ConcurrentModificationException e) {
			//System.err.println(e);
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Socket socket=null;
		ObjectInputStream  input = null;
	    ObjectOutputStream output = null;
	    String hostName = null;
	    int cPort = 0,randomPort=0;
	    try{
		    socket = sock.accept();
	        new Thread(this).start();
	        cPort = socket.getPort();
	        String clientIP=socket.getInetAddress().toString();
			System.out.println("Client connected at port: "+ cPort);
			input = new ObjectInputStream (socket.getInputStream());
		    output = new ObjectOutputStream(socket.getOutputStream());
		    hostName = (input.readObject()).toString();
		    randomPort=(int) (input.readObject());
			Peers newPeer=new Peers(hostName+" "+clientIP,randomPort);
			peerList.add(newPeer);
			System.out.println("New peer has been added.");
			
			//Display All peers 
			System.out.println("\nPeers:");
			for(Peers peer:peerList)
				System.out.println(peer.hostname+" "+peer.port);
	    }
	    catch(Exception e){
	    	System.err.println(e);
	        {
	        	//close peer
	        	deleteFromLists(cPort);
	        	try {
	        		socket.close();
	        	}
	        	catch(IOException ie){
	        		System.err.println(ie);
	        	} 
            }
	    }	    
		   try { 
			   while (true)
		       {  
				   	String request = (String) input.readObject();
				 	System.out.println(request);
					switch(request.trim().split("\\s")[0].trim()){
					case "ADD":{
						addRfc(output, input);	
						break;
					}
					case "LIST":{
						showRfcs(output, input);
						break;
					}
					case "LOOKUP":{
						lookupRfc(output, input);
						break;
					}
					case "default":{
						break;
					}
				}
		        }       
		  	} catch (Exception e) {
		  		//Remove peer when closed
		  		deleteFromLists(randomPort);
		  		//System.err.println(e);
		  		System.out.println("Peer closed: " + hostName +"Port: "+randomPort);
		  	}
		
	}

}
