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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.jboss.logging.Logger;

@javax.jws.WebService(serviceName = "HelloWS", portName = "hello", endpointInterface = "com.redhat.gss.jaxws.HelloWS")
public class HelloWSImpl implements HelloWS {
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Resource
	private WebServiceContext wsContext;

	public String hello(String name) {
		try {
			MessageContext mc = wsContext.getMessageContext();

			setSimpleSoapHeaders(mc);
			setComplexSoapHeaders(mc);
			
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

	private void setSimpleSoapHeaders(MessageContext mc) throws JAXBException {
		List<Header> headers = new ArrayList<Header>();
		Header dummyHeader = new Header(
				new QName("uri:org.apache.cxf", "example"), "example value",
				new JAXBDataBinding(String.class));
		headers.add(dummyHeader);

		// server side:
		mc.put(Header.HEADER_LIST, headers);
    }
	private void setComplexSoapHeaders(MessageContext mc) throws JAXBException {
		
		SampleHeader header = new SampleHeader();
		header.setName("sample");
		header.setValue("value");
		header.setMajorVersionAttribute("1");
		
		List<Header> headers = new ArrayList<Header>();
		Header dummyHeader = new Header(
				new QName("anynamespace", "SecurityHeader"), header,
				new JAXBDataBinding(SampleHeader.class));
		headers.add(dummyHeader);

		// server side:
		mc.put(Header.HEADER_LIST, headers);
	}

}
