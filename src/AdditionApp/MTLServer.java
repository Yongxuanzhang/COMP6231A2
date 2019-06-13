package AdditionApp;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

public class MTLServer  {


//MTL2002;OTA2003;TOR2004

public static void main(String args[]) throws SecurityException, IOException {


	try{
		
		//Data setup
		HashMap<String,HashMap<String,Integer>> MTLrecord=new HashMap<String,HashMap<String,Integer>>();
		
		HashMap<String,Integer>value=new HashMap<String,Integer>();
		HashMap<String,Integer>value1=new HashMap<String,Integer>();
		HashMap<String,Integer>value2=new HashMap<String,Integer>();
		HashMap<String,Integer>value3=new HashMap<String,Integer>();
		value.put("MTLA100619", 2);
		value2.put("MTLA100617", 3);
		value3.put("MTLA100618", 1);
	
		
		MTLrecord.put("Conference", value);
		MTLrecord.put("TradeShows", value2);
		MTLrecord.put("Seminars", value3);
		
		
		
		// create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
		ORB orb = ORB.init(args, null);      
		POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		rootpoa.the_POAManager().activate();

		// create servant and register it with the ORB
		//CenterServer mtl = new CenterServer("MTL", 30000);
		Server mtl = new Server("MTL",2002,MTLrecord);
		
		mtl.setORB(orb); 

		// get object reference from the servant
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(mtl);
		Addition href = AdditionHelper.narrow(ref);
		
		org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		NameComponent path[] = ncRef.to_name("MTL");
		ncRef.rebind(path, href);
		System.out.println("Montreal Server is running");


		// wait for invocations from clients
		while (true){
			orb.run();
		}
	} 

	catch (Exception e) {
		System.err.println("ERROR: " + e);
		e.printStackTrace(System.out);
	}

	System.out.println("Montreal Server Exiting ...");

}




}



