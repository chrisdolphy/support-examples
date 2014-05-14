package com.redhat.gss.jaxws;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;


public class TestClient
{
  public static void main(String[] args) throws Exception
  {
    URL wsdl = new URL("http://localhost:8080/soaHeaderWS/hello?wsdl");
    QName qname = new QName("http://jaxws.gss.redhat.com/", "HelloWS");
    Service service = Service.create(wsdl, qname);
    HelloWS port = service.getPort(HelloWS.class);
    System.out.println(port.hello("Chris"));
  }

}
