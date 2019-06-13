package AdditionApp;
import java.io.IOException;
import java.util.HashMap;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class TORServer{


public static void main(String args[]) throws SecurityException, IOException {

	try{
	HashMap<String,HashMap<String,Integer>> TORrecord=new HashMap<String,HashMap<String,Integer>>();
	
	HashMap<String,Integer>value=new HashMap<String,Integer>();
	HashMap<String,Integer>value1=new HashMap<String,Integer>();
	HashMap<String,Integer>value2=new HashMap<String,Integer>();
	HashMap<String,Integer>value3=new HashMap<String,Integer>();

	value.put("TORA100519", 211);
	value1.put("TORA100519", 111);
	value2.put("TORA100519", 311);

	TORrecord.put("Conference", value);
	TORrecord.put("TradeShows", value1);
	TORrecord.put("Seminars", value2);
	
	Server tor = new Server("TOR",2004,TORrecord);
	// create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
	ORB orb = ORB.init(args, null);      
	POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	rootpoa.the_POAManager().activate();

	// create servant and register it with the ORB

	
	tor.setORB(orb); 

	// get object reference from the servant
	org.omg.CORBA.Object ref = rootpoa.servant_to_reference(tor);
	Addition href = AdditionHelper.narrow(ref);
	
	org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
	NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	NameComponent path[] = ncRef.to_name("TOR");
	ncRef.rebind(path, href);
	System.out.println("Toronto Server is running");


	// wait for invocations from clients
	while (true){
		orb.run();
	}
} 

catch (Exception e) {
	System.err.println("ERROR: " + e);
	e.printStackTrace(System.out);
}

System.out.println("Toronto Server Exiting ...");

}

	
	
}

