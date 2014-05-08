/*
 * To the extent possible under law, Red Hat, Inc. has dedicated all copyright 
 * to this software to the public domain worldwide, pursuant to the CC0 Public 
 * Domain Dedication. This software is distributed without any warranty.  See 
 * <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.redhat.gss.jaxws;

import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;

@javax.jws.WebService
public interface AnnotationHelloWS
{
	public String hello(String name, @WebParam(header=true, mode=Mode.OUT) Holder<SampleHeader> outHeader);

}
