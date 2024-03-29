package AdditionApp;


/**
* AdditionApp/AdditionOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/z_yongxu/eclipse-workspace/CORBAtest/src/Addition.idl
* Saturday, July 6, 2019 2:30:38 o'clock PM EDT
*/

public interface AdditionOperations 
{
  void shutdown ();
  String sayHello ();
  String sayHello2 ();
  int swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType);
  boolean addEvent (String managerID, String eventID, String eventType, int bookingCapacity);
  boolean removeEvent (String ID, String eventID, String eventType);
  String listEventAvailability (String managerID, String eventType);
  int bookEvent (String customerID, String eventID, String eventType);
  boolean cancelEvent (String eventID, String eventType, String customerID);
  String getBookingSchedule (String customerID);
} // interface AdditionOperations
