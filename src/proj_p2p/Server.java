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

	public void addRfc(){
		
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
		ObjectInputStream  input;
	    ObjectOutputStream output;
	    String hostName = null;
	    int cPort = 0;
	    try{
		    socket = sock.accept();
	        new Thread(this).start();
	        cPort = socket.getPort();
			System.out.println("Client connected at port: "+ cPort);
			input = new ObjectInputStream (socket.getInputStream());
		    output = new ObjectOutputStream(socket.getOutputStream());
		    hostName = (input.readObject()).toString();
			
		    //List peerObj = new peerList(hostName, Integer.toString(cPort));
			//peerList.add(peerObj);
			//System.out.println("Peer Added successfully");
	    }
	    catch(Exception e){
	    	System.err.println(e);
	    }
		
	}

}
