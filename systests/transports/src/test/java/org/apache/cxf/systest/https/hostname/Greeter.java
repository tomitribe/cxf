package org.apache.cxf.systest.https.hostname;

import org.apache.cxf.systest.https.hostname.types.ObjectFactory;

import javax.annotation.Generated;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://apache.org/hello_world", name = "Greeter")
@XmlSeeAlso({ObjectFactory.class})
@Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-07-05T11:48:08.411-03:00", comments = "Apache CXF 3.3.0-SNAPSHOT-da5b01b32a59867b48e2b35a9281b3625f58d514")
public interface Greeter {

    @WebMethod
    @RequestWrapper(localName = "sayHi", targetNamespace = "http://apache.org/hello_world/types", className = "org.apache.hello_world.types.SayHi")
    @ResponseWrapper(localName = "sayHiResponse", targetNamespace = "http://apache.org/hello_world/types", className = "org.apache.hello_world.types.SayHiResponse")
    @WebResult(name = "responseType", targetNamespace = "http://apache.org/hello_world/types")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-07-05T11:48:08.411-03:00")
    public java.lang.String sayHi();

    @WebMethod
    @RequestWrapper(localName = "pingMe", targetNamespace = "http://apache.org/hello_world/types", className = "org.apache.hello_world.types.PingMe")
    @ResponseWrapper(localName = "pingMeResponse", targetNamespace = "http://apache.org/hello_world/types", className = "org.apache.hello_world.types.PingMeResponse")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-07-05T11:48:08.411-03:00")
    public void pingMe() throws PingMeFault;

    @WebMethod
    @RequestWrapper(localName = "greetMe", targetNamespace = "http://apache.org/hello_world/types", className = "org.apache.hello_world.types.GreetMe")
    @ResponseWrapper(localName = "greetMeResponse", targetNamespace = "http://apache.org/hello_world/types", className = "org.apache.hello_world.types.GreetMeResponse")
    @WebResult(name = "responseType", targetNamespace = "http://apache.org/hello_world/types")
    @Generated(value = "org.apache.cxf.tools.wsdlto.WSDLToJava", date = "2018-07-05T11:48:08.411-03:00")
    public java.lang.String greetMe(
            @WebParam(name = "requestType", targetNamespace = "http://apache.org/hello_world/types")
                    java.lang.String requestType
    );
}