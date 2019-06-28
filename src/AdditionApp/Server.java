package AdditionApp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import AdditionApp.AdditionPOA;

public class Server extends AdditionPOA{
	private ORB orb;
	private HashMap<String,HashMap<String,Integer>> record=new HashMap<String,HashMap<String,Integer>>();
	private HashMap<String,LinkedList<String>> userSchedule=new HashMap<String,LinkedList<String>>();
	private int port;
	//used for transmit data
	private int receivePort1;
	private int receivePort2;
	private int requestPort1;
	private int requestPort2;
	
	private int listenPort1;
	private int listenPort2;
	private int targetPort1;
	private int targetPort2;
	private int sendbackport1;
	//private int sendbackport2;

	private DatagramSocket aSocket1;
	private DatagramSocket aSocket2;
	private String location;
	private Registry registry;
	private Log serverLog;

	private String[] receiveList1=null;
	private String[] receiveList2=null;
	//private boolean binded=false;
	private int feedback=1;
	private boolean fromThis=true;
	private boolean cancelOther=false;
	private DatagramSocket aSocket = null;
	private FileOutputStream fileOutputStream = null;
	private File serverFile;
	
    private DatagramSocket socket1 = null;
    private DatagramSocket socket2 = null;

    public Server(String location,int port,HashMap<String,HashMap<String,Integer>> newRecord) throws SecurityException, IOException {
    	//recordSetup(record);
    	//Location="MTL";
    	this.location=location;
    	this.port=port;
    	//this.serverLog= new Log(location+"serverOperation.txt");
    	serverFile= new File(location+"-serverOperationLog.txt");
    	 if(!serverFile.exists()){
    		 serverFile.createNewFile();
         }
    	this.record=newRecord;
    	receivePort1=port+3000;
    	receivePort2=port+3005;
    	
        listenPort1=port+4000;
        listenPort2=port+4005;
        
        Thread t = new Thread(new Runnable(){   //running thread which will request data using UDP/sockets 
            public void run(){
            	System.out.println("UDP Online");
                while(true) {
                try {
                  listenUDPt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                }
        });
        	t.start();
        
        
    	switch(port){
 
    	case 2002:

    		targetPort1=5003;
    		targetPort2=5004;
    		requestPort1=6003;
    		requestPort2=6004;
    		break;
    	case 2003:

    		targetPort1=5002;
    		targetPort2=5009;
            requestPort1=6002;
            requestPort2=6004;
    		break;	
    	case 2004:

    		targetPort1=5007;
    		targetPort2=5008;
            requestPort1=6002;
            requestPort2=6003;
    		break;
    		
    	
    		
    	}
    	

    }

	public void setORB(ORB orb_val) {
		orb = orb_val; 
	}
    
    public boolean listenUDPt() {
        //System.out.println("Ut run");

        boolean res=false;
            
            try {
              //System.out.println(socket1);
              //if(!socket1.isBound()) {
                socket1 = new DatagramSocket(listenPort1);
                
              //}
              //if(!socket2.isBound()) {
               // socket2 = new DatagramSocket(listenPort2);
              //}
            
               byte[] data1= new byte[1000];
               byte[] data2= new byte[1000];
               DatagramPacket recevPacket1 = new DatagramPacket(data1,data1.length);
               DatagramPacket recevPacket2 = new DatagramPacket(data2,data2.length);
              // System.out.println("before receive in udpt");
               socket1.receive(recevPacket1);
               socket1.receive(recevPacket2);
              // socket2.receive(recevPacket2);
              // System.out.println("after receive in udpt");
               
               if (socket1!= null)socket1.close();
   				
              // socket1.close();
               byte[] d=recevPacket1.getData();
               int dlen = recevPacket1.getLength();
               String info = new String(d,0,dlen,"UTF-8");
               System.out.println("UDPt1"+info);
               
               byte[] d2=recevPacket2.getData();
               int dlen2 = recevPacket2.getLength();
               String info2 = new String(d2,0,dlen2,"UTF-8");
               System.out.println("UDPt2"+info2);
               

               
               if(info.equals("Conference")||info.equals("Seminars")||info.equals("TradeShows")) {
    
              	 System.out.println("INsidetheIF"+info);
                 this.sendData(info, targetPort1);
                 this.sendData(info, targetPort2);
                 res=true;
               }
               if(info2.equals("Conference")||info2.equals("Seminars")||info2.equals("TradeShows")) {
                 
                 System.out.println("INsidetheIF2"+info2);
               this.sendData(info2, targetPort1);
               this.sendData(info2, targetPort2);
               res=true;
             }
               
               if(info.substring(0, 3).equals(location)) {
                 try {
                   Thread.sleep(500);
                 } catch (InterruptedException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
                 }
              	 System.out.println("UDPt"+info);
              	 //eventID-CustomerID-EventType
              	 
              	  String[] bookInfo=info.split("-");
              	  fromThis=false;
              	  sendbackport1=Integer.parseInt(bookInfo[3]);
                    if(bookInfo[4].equals("1")) {
                      this.bookEvent(bookInfo[1], bookInfo[0], bookInfo[2]); 
                    }    	 
               }
               if(info2.substring(0, 3).equals(location)) {
                 try {
                   Thread.sleep(500);
                 } catch (InterruptedException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
                 }
                 System.out.println("UDPt2"+info2);
                 //eventID-CustomerID-EventType
                 
                  String[] bookInfo=info2.split("-");
                  fromThis=false;
                  sendbackport1=Integer.parseInt(bookInfo[3]);
                  if(bookInfo[4].equals("1")) {
                    this.bookEvent(bookInfo[1], bookInfo[0], bookInfo[2]); 
                  }
                        
             }
               if(info.substring(0, 6).equals("cancel")) {
                  System.out.println("UDPcancel--"+info);        
                  String[] bookInfo=info.split("-");
                  //String cancelInfo="cancel-"+eventID+"-"+customerID+"-"+eventType+"-"+listenPort2;
                  sendbackport1=Integer.parseInt(bookInfo[4]);
                  //this.cancelEvent(eventID, eventType, customerID)
                  if(bookInfo[1].substring(0, 3).equals(location)) {
                   
                    cancelOther=true;
                    if(bookInfo[5].equals("1")) {
                      System.out.println("excute cancel--");   
                      this.cancelEvent(bookInfo[1], bookInfo[3], bookInfo[2]);
                    }
                  }
                  
               }
               if(info2.substring(0, 6).equals("cancel")) {
                 System.out.println("UDPcancel--"+info2);        
                 String[] bookInfo=info2.split("-");
                 //String cancelInfo="cancel-"+eventID+"-"+customerID+"-"+eventType+"-"+listenPort2;
                 sendbackport1=Integer.parseInt(bookInfo[4]);
                 //this.cancelEvent(eventID, eventType, customerID)
                 if(bookInfo[1].substring(0, 3).equals(location)) {
                   
                   cancelOther=true;
                   if(bookInfo[5].equals("1")) {
                     System.out.println("excute cancel--");   
                     this.cancelEvent(bookInfo[1], bookInfo[3], bookInfo[2]);
                   }
                  
                 }
                 
              }
             
                 //feedback=Integer.parseInt(info); 
               //Change to UDP.
               //TODO:1.book event from other server(Modity stub).2.
               
               
               
               
            } catch ( IOException e) {
                
                e.printStackTrace();
            }finally {
              //socket1.close();
             // listenUDPt();
            }
            
            return res;
        
      }
    
    
    public void writeFile(String info) throws IOException {
		 fileOutputStream = new FileOutputStream(serverFile,true);
		 Date date = new Date();	
		 info+="\r\n";  
	     fileOutputStream.write(info.getBytes());
	     fileOutputStream.write(date.toString().getBytes());
	     fileOutputStream.flush();
	     fileOutputStream.close();
    }
    
	@Override
	public void shutdown() {		
		orb.shutdown(false);
	}

	@Override
	public String sayHello() {
		   return "Welcome to "+location+" Server!";	
	}

	@Override
	public String sayHello2() {
		   return "Welcome to "+location+" Server!";
	}

	@Override
	public synchronized boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity) {
		HashMap<String,Integer> rec=new HashMap<String,Integer>();
		
		
		if(!record.containsKey(eventType))return false;
		else {
			
			rec.put(eventID, bookingCapacity);
	
			record.get(eventType).put(eventID, bookingCapacity);
			
			System.out.println(record.get(eventType).get(eventID));
	
			 try {
			   System.out.println("Write add in log");
				 String tempWrite=managerID+" has added "+eventType+eventID+" of "+location+"Server ";
				 writeFile(tempWrite);
			} catch (Exception e) {

				e.printStackTrace();
			}
			
			return true;
		}
		
		
	}

	@Override
	public synchronized boolean removeEvent(String ID, String eventID, String eventType) {
		if(!record.containsKey(eventType)||!record.get(eventType).containsKey(eventID))return false;

		HashMap<String,Integer> temp=new HashMap<String,Integer>();
		temp.put(eventID,record.get(eventType).get(eventID));	
		record.get(eventType).remove(eventID, record.get(eventType).get(eventID));

		if(userSchedule!=null) {
			for (Map.Entry<String,LinkedList<String>> entry : userSchedule.entrySet()) {
				
				if(userSchedule.get(entry.getKey())!=null) {
					 for(String o:userSchedule.get(entry.getKey())) {
					   System.out.println("userschedule"+o);
					   String[] comaperEvent=o.split(" ");
					   System.out.println("userschedule eventID"+comaperEvent[1]);
							if(eventID.equals(comaperEvent[1])) {
								userSchedule.get(entry.getKey()).remove(o);
							}
						 }

					}
				}
		}

		 String tempWrite=ID+" has removed "+eventType+eventID+" of "+location+"Server ";
         try {
          writeFile(tempWrite);
        } catch (IOException e) {
       
          e.printStackTrace();
        }

		return true;
	}

	@Override
	public String listEventAvailability(String managerID, String eventType) {

		
		if(!record.containsKey(eventType))return null;
		
		Thread t2 = new Thread(new Runnable(){	//running thread which will request data using UDP/sockets 
			public void run(){
				//while(true) {
				try {
					requestData(eventType);
				} catch (NotBoundException | RemoteException e) {
					e.printStackTrace();
				}
			}
				//}
		});
		t2.start();
		

		
		String res="";
		
		Map<String,Integer> temp=record.get(eventType);
		
		for (Map.Entry<String,Integer> entry : temp.entrySet()) {
			 
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    res+=entry.getKey()+" "+entry.getValue();
		}
	
		//this.sendData(res);
		System.out.println("Before receive data in "+location);
		this.receiveData();
		
		//String[] receiveList1;
		if(receiveList1.length>0) {
			for(String ts:receiveList1) {
				res+=" "+ts;
			}
		}

		if(receiveList2.length>0) {
			for(String ts:receiveList2) {
				res+=" "+ts;
			}
		}
		System.out.println(res);
        String tempWrite=managerID+" has listed "+eventType+" of "+location+"Server";
        try {
         writeFile(tempWrite);
       } catch (IOException e) {
      
         e.printStackTrace();
       }
		
		return res;
	}

	public LinkedList<String> thisEventList(String eventType) throws RemoteException {
		
		
		LinkedList<String> res= new LinkedList<String>();
		
		Map<String,Integer> temp=record.get(eventType);
		
		for (Map.Entry<String,Integer> entry : temp.entrySet()) {
			 
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    res.add(entry.getKey()+" "+entry.getValue());
		}
	

		return res;
	}
	
	public void requestData(String eventType) throws AccessException, RemoteException, NotBoundException {
		
	  
	    
		//System.out.println("targetStub1 "+targetStub1);
		//Registry registry1 = LocateRegistry.getRegistry(targetPort1);    
		//Registry registry2 = LocateRegistry.getRegistry(targetPort2);

		//ServerOperation stub1 = (ServerOperation) registry1.lookup(targetStub1);
		//ServerOperation stub2 = (ServerOperation) registry2.lookup(targetStub2);
	    this.sendRequest(eventType, requestPort1);
	    this.sendRequest(eventType, requestPort2);
	    this.sendRequest(eventType, requestPort1);
	    this.sendRequest(eventType, requestPort2);
		//stub1.sendData(eventType, receivePort1);
		//stub2.sendData(eventType, receivePort2);
		//stub2.sendData(eventType, listenPort1);
		
	}
	

	public void sendRequest2(String eventID,String CustomerID,String eventType,int targetPort,String flag) {
		   
		  System.out.println("sendrequest2:");

		        
		        try {
		            aSocket = new DatagramSocket();
		            
		            
		            String bookInfo=eventID+"-"+CustomerID+"-"+eventType+"-"+listenPort2+"-"+flag;
		            byte[] sData=bookInfo.getBytes();
		            
		            System.out.println("send:"+bookInfo);
		            InetAddress address = InetAddress.getByName("localhost");
		            //int port=8088;
		            DatagramPacket sendPacket=new DatagramPacket(sData,sData.length,address,targetPort);
		            //DatagramPacket sendPacket2=new DatagramPacket(sData,sData.length,address,targetPort2);
		            aSocket.send(sendPacket);
		            //aSocket.send(sendPacket2);
		          
		           // System.out.println(bf.toString());
		            aSocket.close();
		        } catch (Exception e) {         
		            e.printStackTrace();
		        }finally {if(aSocket != null) aSocket.close();}
		}
	
	
	public void sendRequest(String eventType,int targetPort) {
	   
	  System.out.println("sendrequest:"+eventType+targetPort);

	        
	        try {
	            aSocket = new DatagramSocket();
	            
	            byte[] sData=eventType.getBytes();
	            
	            
	            InetAddress address = InetAddress.getByName("localhost");
	            DatagramPacket sendPacket=new DatagramPacket(sData,sData.length,address,targetPort);

	            aSocket.send(sendPacket);

	            aSocket.close();
	        } catch (Exception e) {         
	            e.printStackTrace();
	        }finally {if(aSocket != null) aSocket.close();}
	}
	
	
    public void sendData(String eventType,int targetPort) throws RemoteException {
    	StringBuffer bf=new StringBuffer();
    	LinkedList<String> data=this.thisEventList(eventType);
    	
    	//bf.append("this is from"+location);
    	for(String s:data) {
    		bf.append(s+".");
    	}
    	
    	try {
			aSocket = new DatagramSocket();
			
			byte[] sData=bf.toString().getBytes();
			
			InetAddress address = InetAddress.getByName("localhost");
			//int port=8088;
			DatagramPacket sendPacket=new DatagramPacket(sData,sData.length,address,targetPort);
			//DatagramPacket sendPacket2=new DatagramPacket(sData,sData.length,address,targetPort2);
			aSocket.send(sendPacket);
			//aSocket.send(sendPacket2);
			System.out.println("message is from"+location);
			System.out.println(bf.toString());
			aSocket.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}finally {if(aSocket != null) aSocket.close();}
    	
    }
	

    public void receiveData() {
    	try {
    		  //int receivePort=port+5000;
    		  aSocket1 = new DatagramSocket(receivePort1);
    		  aSocket2 = new DatagramSocket(receivePort2);
			  byte[] data1= new byte[2000];
			  byte[] data2= new byte[2000];
		      DatagramPacket recevPacket1 = new DatagramPacket(data1,data1.length);
		      DatagramPacket recevPacket2 = new DatagramPacket(data2,data2.length);
			  aSocket1.receive(recevPacket1);
			  aSocket2.receive(recevPacket2);
			  
			  byte[] d1=recevPacket1.getData();
			  int dlen1 = recevPacket1.getLength();
			  String info1 = new String(d1,0,dlen1,"UTF-8");
			  receiveList1=info1.split("\\.");
		
			  
			  byte[] d2=recevPacket2.getData();
			  int dlen2 = recevPacket2.getLength();
			  String info2 = new String(d2,0,dlen2,"UTF-8");
			  receiveList2=info2.split("\\.");
			   System.out.println("message is "+info1);
			   System.out.println("message is "+info2);
			   aSocket1.close();
			   aSocket2.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}finally {
			if(aSocket1 != null) aSocket1.close();
			if(aSocket2 != null) aSocket2.close();
		}
    	
    	
    }
    
    public void sendBack(int back) {
    	
    	
		  System.out.println("sendback");

	        
	        try {
	            aSocket = new DatagramSocket();
	            
	            
	            String bookInfo=Integer.toString(back);
	            byte[] sData=bookInfo.getBytes();
	            
	            System.out.println("send:"+bookInfo+"-port"+sendbackport1);
	            InetAddress address = InetAddress.getByName("localhost");
	            //int port=8088;
	            DatagramPacket sendPacket=new DatagramPacket(sData,sData.length,address,sendbackport1);
	         
	            aSocket.send(sendPacket);

	            aSocket.close();
	        } catch (Exception e) {         
	            e.printStackTrace();
	        }finally {if(aSocket != null) aSocket.close();}
  	
  }
  
  
  public int listenFeedBack() {

     
          try {
     
              socket2 = new DatagramSocket(listenPort2);
              
     
          
             byte[] data1= new byte[1000];    
             DatagramPacket recevPacket1 = new DatagramPacket(data1,data1.length);   
             System.out.println("before receive in udpfb");
             socket2.receive(recevPacket1);
             System.out.println("after receive in udpfb");
             
             
             byte[] d=recevPacket1.getData();
             int dlen = recevPacket1.getLength();
             String info = new String(d,0,dlen,"UTF-8");
             System.out.println("UDPfeedback"+info);

             feedback=Integer.parseInt(info); 
             System.out.println("feedback is "+feedback);
           
          } catch ( IOException e) {
              
              e.printStackTrace();
          }finally {
            socket2.close();        
          }
          
          return feedback;
      
    }
  
  public int checkCounts(String customerID,String eventID) {
    
    LinkedList<String> userRecords = userSchedule.get(customerID);
    String month =  eventID.substring(6, 8);
    System.out.println("Month is"+month);
    int count = 0;
    if(userRecords!=null) {
    	  System.out.println("userRecords is"+userRecords);
      for(String o:userRecords) {
        String[] temp=o.split(" ");
        System.out.println("Month of temp is"+temp[1]);
        if(month.equals(temp[1].substring(6, 8))) {
        	//MTLE100519
          count++;
        }
      }
    }

    
    if(count>2)return -1;
    
    return 0;
  }
    
	@Override
	public synchronized int bookEvent(String customerID, String eventID, String eventType) {
	
		
		/**
		 * use error code for return:
		 * -1:Book more than 3 events in 1 month from other servers;
		 * -2:Same eventID and eventType;
		 * -3:The capacity is full;
		 * -4:Location Error;
		 * -5:Event doesn't exist;
		 * -6:EventType doesn't exist;
		 * 
		 */

		
		System.out.println(customerID);
		System.out.println(userSchedule.containsKey(customerID)+" -- ");
		
        if(userSchedule.containsKey(customerID)) {
          
          for(String s:userSchedule.get(customerID)) {
              
              if(s.equals(eventType+" "+eventID)) {
                  sendBack(-2);
                  String tempWrite=customerID+" cannot book "+eventID;
                  try {
                   writeFile(tempWrite);
                 } catch (IOException e) {
                
                   e.printStackTrace();
                 }
                  return -2;
              }
          }

      }
        
        if(checkCounts(customerID,eventID)==-1) {
            sendBack(-1);
             String tempWrite=customerID+" cannot book "+eventID+ "from other cities more than 3 times 1 month";
                try {
                 writeFile(tempWrite);
               } catch (IOException e) {
              
                 e.printStackTrace();
               }
            return -1;
        }
    

		if(!eventID.substring(0, 3).equals(location)) {
			

					sendRequest2(eventID,customerID,eventType,requestPort1,"1");
					sendRequest2(eventID,customerID,eventType,requestPort2,"1");
	                sendRequest2(eventID,customerID,eventType,requestPort1,"2");
	                sendRequest2(eventID,customerID,eventType,requestPort2,"2");

					feedback=listenFeedBack();
					System.out.println("feedback value in thread"+feedback);
				

			if(feedback==1) {
				this.insertEvent(customerID, eventID, eventType);
			}
			System.out.println("feedback value "+feedback);
			return feedback;
		}
		
		System.out.println("send back in book 1");
		if(!record.containsKey(eventType)) {
			sendBack(-6);
	         String tempWrite=customerID+" cannot book "+eventID;
	            try {
	             writeFile(tempWrite);
	           } catch (IOException e) {
	          
	             e.printStackTrace();
	           }
			return -6;
		}
	
		
		System.out.println("send back in book 2");
		String eventLoc= eventID.substring(0, 3);
		

		if(!record.get(eventType).containsKey(eventID)) {
			sendBack(-5);
			return -5;
		}
	
		
    	int checkTimes=0;
		if(userSchedule.get(customerID)!=null) {
		for(String o:userSchedule.get(customerID)) {
			checkTimes++;
			}
		}

			System.out.println("send back in book 3");
		
		//check capacity	
		int newCapacity=record.get(eventType).get(eventID);
		if(newCapacity==0) {
			
	        String tempWrite=customerID+" cannot book "+eventID;
	        try {
	         writeFile(tempWrite);
	       } catch (IOException e) {
	      
	         e.printStackTrace();
	       }
		  
			sendBack(-3);
			return -3;
		}
		else {
			newCapacity--;
			System.out.println("capacity"+newCapacity+" of "+eventID);
			record.get(eventType).put(eventID, newCapacity);
		}
		
	
		
		
		insertEvent(customerID,eventID,eventType);
		System.out.println("send back in book 4");
        String tempWrite=customerID+" has booked "+eventID;
        try {
         writeFile(tempWrite);
       } catch (IOException e) {
      
         e.printStackTrace();
       }
		sendBack(1);
		return 1;
	}

	public void insertEvent(String customerID,String eventID,String  eventType) {
		
		if(customerID.substring(0, 3).equals(location)) {
			if(userSchedule.containsKey(customerID)) {
				userSchedule.get(customerID).add(eventType+" "+eventID);
				
			}else {
				LinkedList<String> usTemp= new LinkedList<String>();
				usTemp.add(eventType+" "+eventID);		
				userSchedule.put(customerID, usTemp);
			}
			
		}

	}
	
	@Override
	public synchronized boolean cancelEvent(String eventID, String eventType, String customerID) {
		  boolean result=false;//else return false;
		     
	      if(!eventID.substring(0, 3).equals(location)) {
	        
	        if(userSchedule.containsKey(customerID)) {
	          if(userSchedule.get(customerID).contains(eventType+" "+eventID)){
	            
	            userSchedule.get(customerID).remove(eventType+" "+eventID);
	            
	            
	            sendcancelRequest(eventID,customerID,eventType,requestPort1,"1");
	            sendcancelRequest(eventID,customerID,eventType,requestPort2,"1");
	            sendcancelRequest(eventID,customerID,eventType,requestPort1,"2");
	            sendcancelRequest(eventID,customerID,eventType,requestPort2,"2");
	            feedback=listenFeedBack();
	            System.out.println("feedback value in thread"+feedback);
	        
	              if(feedback==1) {
	                  return true;
	              }//else return false;
	              
	            if(!result) {
	              this.sendBack(0);
	              //this.sendBack(0);
	              return false;
	            }
	            
	            return true;
	          }else {   
	            return false;
	          }

	        }else {        
	            return false;
	        }
	        
	        

	          //System.out.println("feedback value "+feedback);
	         
	      }
	      
		  if(cancelOther) {
		    //only update capacity
	        for (Map.Entry<String,HashMap<String,Integer>> entry : record.entrySet()) {
	          
	          for(Map.Entry<String,Integer> entry2 : entry.getValue().entrySet()) {
	              System.out.println("value of e2 "+(entry2.getKey()));
	              
	              if(entry2.getKey().equals(eventID)) {
	                            
	                System.out.println("tc ++ here");
	                  int tc=entry2.getValue()+1;
	                  record.get(entry.getKey()).put(eventID, tc);
	                  result=true;
	              }
	          }
	      }
	        this.sendBack(1);
	        //this.sendBack(1);
	        String tempWrite=customerID+" canceled "+eventID;
	        try {
	         writeFile(tempWrite);
	       } catch (IOException e) {
	      
	         e.printStackTrace();
	       }
	        return true;
		  }
		  
		  
		  
			//boolean result=false;//else return false;
			System.out.println("value of uc "+userSchedule.containsKey(customerID));
			//System.out.println("value of ucID "+userSchedule.get(customerID).contains(eventType+" "+eventID));
			
			if(userSchedule.containsKey(customerID)) {
	          if(userSchedule.get(customerID).contains(eventType+" "+eventID)){
	            
	            userSchedule.get(customerID).remove(eventType+" "+eventID);
	            
	            //userSchedule.get(customerID).re
	            for (Map.Entry<String,HashMap<String,Integer>> entry : record.entrySet()) {
	                
	                for(Map.Entry<String,Integer> entry2 : entry.getValue().entrySet()) {
	                    System.out.println("value of e2 "+(entry2.getKey()));
	                    
	                    if(entry2.getKey().equals(eventID)) {
	                                  
	                        int tc=entry2.getValue()+1;
	                        record.get(entry.getKey()).put(eventID, tc);
	                        result=true;
	                    }
	                }
	            }
	            
	            if(!result) {
	              this.sendBack(0);
	              String tempWrite=customerID+" cannot cancel "+eventID;
	              try {
	               writeFile(tempWrite);
	             } catch (IOException e) {
	            
	               e.printStackTrace();
	             }
	              return false;
	            }
	            
	            this.sendBack(1);
	            String tempWrite=customerID+" canceled"+eventID;
	            try {
	             writeFile(tempWrite);
	           } catch (IOException e) {
	          
	             e.printStackTrace();
	           }
	            return true;
	          }else {
	            this.sendBack(0);
	            String tempWrite=customerID+" cannot cancel "+eventID;
	            try {
	             writeFile(tempWrite);
	           } catch (IOException e) {
	          
	             e.printStackTrace();
	           }
	            return false;
	          }

			}else {
			  this.sendBack(0);
	          String tempWrite=customerID+" cannot cancel "+eventID;
	          try {
	           writeFile(tempWrite);
	         } catch (IOException e) {
	        
	           e.printStackTrace();
	         }
				return false;
			}
	   // return false;
	}


	private void sendcancelRequest(String eventID, String customerID, String eventType,
      int requestPort,String flag) {
      
     System.out.println("sendcancelrequest:");

           
           try {
               aSocket = new DatagramSocket();
               
               
               String cancelInfo="cancel-"+eventID+"-"+customerID+"-"+eventType+"-"+listenPort2+"-"+flag;
               byte[] sData=cancelInfo.getBytes();
               
               System.out.println("send:"+cancelInfo);
               InetAddress address = InetAddress.getByName("localhost");
               //int port=8088;
               DatagramPacket sendPacket=new DatagramPacket(sData,sData.length,address,requestPort);
               //DatagramPacket sendPacket2=new DatagramPacket(sData,sData.length,address,targetPort2);
               aSocket.send(sendPacket);
               //aSocket.send(sendPacket2);
             
              // System.out.println(bf.toString());
               aSocket.close();
           } catch (Exception e) {         
               e.printStackTrace();
           }finally {if(aSocket != null) aSocket.close();}
    
  }


	
	@Override
	public String getBookingSchedule(String customerID) {
	    String tempWrite=customerID+" get the schedule ";
	    try {
	     writeFile(tempWrite);
	   } catch (IOException e) {
	  
	     e.printStackTrace();
	   }
			if(!userSchedule.containsKey(customerID)) {
				return null;
			}
			
			else {
				String res=null;
				for(String o: userSchedule.get(customerID)) {
					res+=o;
				}
			
				 return res;
				
			}

	}
	
	
	
	
	
}

