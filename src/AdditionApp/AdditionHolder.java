package AdditionApp;

/**
* AdditionApp/AdditionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/z_yongxu/eclipse-workspace/CORBAtest/src/Addition.idl
* Saturday, July 6, 2019 2:30:38 o'clock PM EDT
*/

public final class AdditionHolder implements org.omg.CORBA.portable.Streamable
{
  public AdditionApp.Addition value = null;

  public AdditionHolder ()
  {
  }

  public AdditionHolder (AdditionApp.Addition initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = AdditionApp.AdditionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    AdditionApp.AdditionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return AdditionApp.AdditionHelper.type ();
  }

}
