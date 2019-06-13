package AdditionApp;
import java.io.IOException;
import java.util.HashMap;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class OTWServer {

public static void main(String args[]) throws SecurityException, IOException {
	try{
	HashMap<String,HashMap<String,Integer>> OTWrecord=new HashMap<String,HashMap<String,Integer>>();
	
	HashMap<String,Integer>value1=new HashMap<String,Integer>();
	HashMap<String,Integer>value2=new HashMap<String,Integer>();
	HashMap<String,Integer>value3=new HashMap<String,Integer>();
	
	value1.put("OTWA100619", 22);
	value1.put("OTWA100618", 23);
	value1.put("OTWA100617", 25);
	value2.put("OTWM110419", 23);
	value3.put("OTWE090519", 26);
	OTWrecord.put("Conference", value1);
	OTWrecord.put("TradeShows", value2);
	OTWrecord.put("Seminars", value3);
	
	Server otw = new Server("OTW",2003,OTWrecord);


	// create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
	ORB orb = ORB.init(args, null);      
	POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	rootpoa.the_POAManager().activate();

	// create servant and register it with the ORB

	
	otw.setORB(orb); 

	// get object reference from the servant
	org.omg.CORBA.Object ref = rootpoa.servant_to_reference(otw);
	Addition href = AdditionHelper.narrow(ref);
	
	org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
	NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	NameComponent path[] = ncRef.to_name("OTW");
	ncRef.rebind(path, href);
	System.out.println("Ottwa Server is running");


	// wait for invocations from clients
	while (true){
		orb.run();
	}
} 

catch (Exception e) {
	System.err.println("ERROR: " + e);
	e.printStackTrace(System.out);
}

System.out.println("Ottwa Server Exiting ...");

}

	
	
	
	
	}


