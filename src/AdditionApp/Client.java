package AdditionApp;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Client extends Thread {

	private int host;
	private String ID;
	private String bindobj;
	private Registry registry;
	private Addition stub;
	private Log userLog;
    private  ORB orb;
	
    private Client(String ID,ORB orb) throws SecurityException, IOException {
    	this.ID=ID;
    	userLog=new Log(ID+"-userLog.txt");
    	this.orb=orb;
    	//this.host=host;
    	//this.bindobj=bindobj;
    }
    

    
    public void run() {
    	
    	  //String host = (args.length < 1) ? null : args[0];
    	String location = ID.substring(0, 3);
    	
    	switch(location) {
    	
    	case"MTL":
    		host=2002;
    		//bindobj="MTLManagerOperation";
    		break;
      	case"OTW":
    		host=2003;
    		//bindobj="OTWManagerOperation";
    		break;
      	case"TOR":
    		host=2004;
    		//bindobj="TORManagerOperation";
    		break;
    	default:
    		System.out.println("Wrong ID");
  	
    	}
    	
    	
          try {
        	  
        	org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
   		    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
   		    stub = (Addition)  AdditionHelper.narrow(ncRef.resolve_str(location));
        	  
        	  
        	  
           //   registry = LocateRegistry.getRegistry(host);              
              
        //      stub = (Addition) registry.lookup(bindobj);
                      
          } catch (Exception e) {
              System.err.println("Client exception: " + e.toString());
              e.printStackTrace();
          }
          
          
      	String type = ID.substring(3, 4);
    	
      	if(type.equals("M")) {
      		managerOperation();
      	}
      	else if(type.equals("C")) {
      		//TODO
      		clientOperation();
      	}else {
      		System.out.println("Wrong ID");
      	}
      	
          
 	
    }
    

    
    public void callServerBookEvent(String customerID,String eventID,String eventType) throws RemoteException {
    	
    	int res=stub.bookEvent(customerID, eventID, eventType);
    	//System.out.println("booked "+res);
    	 if(res==1){
         	  System.out.println(customerID+" booked "+eventID+" successfully!");
         	  userLog.logger.info(customerID+" has booked "+eventID+ " successfully!");
         }else if(res==-2) {
        	  System.out.println(customerID+" has already booked"+eventID);
        	  userLog.logger.info(customerID+" has already booked"+eventID);
         }
         else if(res==-3) {
       	  System.out.println(ID+" :The capacity of "+eventID+" is full");
       	 userLog.logger.info(customerID+" cannot book "+eventID);
        }
    	 else {
           System.out.println("Failure Code:"+res);
         }
    }
    

    
    
    public void callAddEvent(String customerID,String eventID,String eventType,int Capacity) throws RemoteException {
      
      if(!customerID.substring(0, 3).equals(eventID.substring(0, 3))) {
        System.out.println(customerID+" cannot add "+eventID+" from other cities");
        userLog.logger.info(customerID+" cannot add "+eventID);
      }else {
        boolean res=stub.addEvent(customerID, eventID, eventType, Capacity);
        if(res) {
          System.out.println(customerID+" has added "+eventID+" successfully!");
          userLog.logger.info(customerID+" has added "+eventID+ " successfully!");
        }else {
          userLog.logger.info(customerID+" cannot add "+eventID);
        }
      }
      

    }
    
    public void callSwap(String customerID, String newEventID, String newEventType, String oldEventID,String oldEventType) {
    	
    	int res=stub.swapEvent(customerID, newEventID, newEventType, oldEventID, oldEventType);
    	if(res==1) {
    		 System.out.println(customerID+" has swapped "+oldEventID+" with "+newEventID+" successfully!");
    	}else if(res==-1) {
    		System.out.println(customerID+" doesn't book "+oldEventID);
    	}else {
    		System.out.println(customerID+" cannot swap "+oldEventID+" with "+newEventID);
    	}
    	
    }
    
    public void clientOperation() {
    	
    	try {
            String response2 = stub.sayHello2();
            System.out.println("response from server:" + response2);
            
            
           // System.out.println(stub.listEventAvailability(ID,"Conference"));  
         
            callServerBookEvent(ID, "MTLA100619", "Conference");
      
              
            stub.getBookingSchedule(ID);
            
            if(stub.getBookingSchedule(ID)!=null) {
            	
            	System.out.println(stub.getBookingSchedule(ID));
            	
            }
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        callSwap(ID,"MTLA100618","Seminars", "MTLA100619", "Conference");
    }
    
    
    
    public void managerOperation() {

    	try {
        String response2 = stub.sayHello2();
        System.out.println("response from server:" + response2);
        
        
        System.out.println(stub.listEventAvailability(ID,"Conference"));  
        
        
        callAddEvent(ID,"OTWA090619", "Conference", 50);
        callAddEvent(ID,"TORM100719","Conference",323);
        callAddEvent(ID,"OTWA101519","Seminars",43);
        callAddEvent(ID,"MTLA110519","Conference",2);
        
        

       
        callServerBookEvent(ID, "MTLA100619", "Conference");
        
        
          
        stub.getBookingSchedule(ID);
        
        if(stub.getBookingSchedule(ID)!=null) {
        	
        	System.out.println(stub.getBookingSchedule(ID));
        	
        	/*
        	String[] schedule=stub.getBookingSchedule(ID).split(" ");
        	
          for(String o : schedule) {
            System.out.println(ID+o);
          }

        }*/
        //System.out.println(stub.listEventAvailability(ID,"Conference"));
        }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
	
    }
    

  
    
    public static void main(String[] args) throws SecurityException, IOException {
  	       ORB orb = ORB.init(args, null); 
      //    demo1();
		
  	        Client client1 = new Client("MTLC2345",orb);
  	        client1.start();
  	        Client client2 = new Client("MTLC2344",orb);
  	        client2.start();
  	        Client client5 = new Client("MTLC2346",orb);
  	        client5.start();
  	        
    }
	
	
}
