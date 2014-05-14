/*
 * To the extent possible under law, Red Hat, Inc. has dedicated all copyright 
 * to this software to the public domain worldwide, pursuant to the CC0 Public 
 * Domain Dedication. This software is distributed without any warranty.  See 
 * <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.redhat.gss.jaxws;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.jboss.logging.Logger;

@javax.jws.WebService(serviceName = "hello_annotation", portName = "hello_annotation", endpointInterface = "com.redhat.gss.jaxws.AnnotationHelloWS")
public class AnnotationHelloWSImpl implements AnnotationHelloWS {
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Resource
	private WebServiceContext wsContext;
	
	public String hello(String name, @WebParam(header=true, mode=Mode.OUT) Holder<SampleHeader> outHeader) {
		try {
			MessageContext mc = wsContext.getMessageContext();
			
			SampleHeader header = new SampleHeader();
			header.setName("sample");
			header.setValue("value");
			header.setMajorVersionAttribute("1");
			
			outHeader.value = header;
			
			
			Integer counter = countCalls(name, mc);

			return "Hello, " + name + ". You've invoked this service " + counter + " times.";
		} catch (Exception e) {
			return "Fail";
		}
	}

	private Integer countCalls(String name, MessageContext mc) {
		HttpSession session = 
				((HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST)).getSession();

		// Get a session property "counter" from context
		if (session == null)
			throw new RuntimeException("No session in WebServiceContext");

		Integer counter = (Integer) session.getAttribute(name);
		if (counter == null) {
			counter = new Integer(0);
			log.info("Starting the Session");
		}

		counter = new Integer(counter.intValue() + 1);
		session.setAttribute(name, counter);
		return counter;
	}


}
