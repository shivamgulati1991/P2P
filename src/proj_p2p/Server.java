package proj_p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server implements Runnable {
	private static final String version = "P2P-CI/1.0";
	public static LinkedList<Rfc> rfcList=new LinkedList<Rfc>();
	public static LinkedList<Peers> peerList=new LinkedList<Peers>();
	public ServerSocket socket;
	
	public Server(int portNo) throws IOException{
		socket=new ServerSocket(portNo);
      	System.out.println("Host: "+ InetAddress.getLocalHost().getHostAddress()+ "  Port: " + socket.getLocalPort());
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
		
		Socket sock=null;
		ObjectInputStream  inputstream = null; 
	    ObjectOutputStream outputstream = null;
	    String host = null;
	    int cPort = 0;
		
	}

}
