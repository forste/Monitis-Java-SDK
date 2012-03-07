package org.monitis.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.monitis.beans.Response;
import org.monitis.exception.ErrorCodes;
import org.monitis.exception.MonitisException;

/**
 * HttpConnector for sending GET, POST, PUT and DELETE methods
 *
 *
 */

public class HttpConnector {
	
	public static byte GET = 1;
	public static byte POST = 2;
	
	public static Logger log = Logger.getLogger(HttpConnector.class);
	
	public static Response send(byte type, String url, List<NameValuePair> data, String fileName)
			throws MonitisException {
		log.debug("send: "
				+"[type="+type
				+",url="+url
				+",data="+data.toString()
				+",fileName="+fileName);
		
		Response response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = null;
		if(type == GET) {
			method = new GetMethod(url);
		}
		else if(type == POST) {
			method = new PostMethod(url);
		}
		if (data != null) {
			NameValuePair[] array = new NameValuePair[data.size()];
			for (int i=0; i<data.size(); i++){
				array[i] = data.get(i);
				System.out.print(data.get(i).getName()+"="+data.get(i).getValue()+"&");
			}
			method.setQueryString(array);
			System.out.println();
		}
		try {
			client.executeMethod(method);
		} catch (HttpException e) {
			String m = "Failed executing [method="+method.toString()+"]!";
			log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
			throw new MonitisException(e, m);
		} catch (IOException e) {
			String m = "Failed executing [method="+method.toString()+"]!";
			log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
			throw new MonitisException(e, m);
		}

		if(fileName != null){
			InputStream in;
			try {
				in = method.getResponseBodyAsStream();
			} catch (IOException e) {
				String m = "Failed getting response body as stream [method="+method.toString()+"]!";
				log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
				throw new MonitisException(e, m);
			}
			FileOutputStream out;
			try {
				out = new FileOutputStream(fileName);
			} catch (FileNotFoundException e) {
				String m = "Failed opening output stream!";
				log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
				throw new MonitisException(e, m);
			}
			byte[] buffer = new byte[1024];
			int count = -1;
			try {
				while ((count = in.read(buffer)) != -1) {
					out.write(buffer, 0, count);
				}
			} catch (IOException e) {
				String m = "Failed writing from in to out!";
				log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
				throw new MonitisException(e, m);
			}
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				String m = "Failed closing out!";
				log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
				throw new MonitisException(e, m);
			}

		}
		else{
			String resptext;
			try {
				resptext = method.getResponseBodyAsString();
			} catch (IOException e) {
				String m = "Failed getting response body as string [method="+method.toString()+"]!";
				log.error(m+", Stacktrace: "+ExceptionUtility.getStackTraceAsString(e));
				throw new MonitisException(e, m);
			}
			response = new Response(new String(resptext), method.getStatusCode());
		}
		method.releaseConnection();

		return response;
	}
	
	
	public static Response sendPost(String url, List<NameValuePair> data)
			throws MonitisException {
		return sendPost(url, data, null);
	}
	
	public static Response sendPost(String url, List<NameValuePair> data, String fileName)
			throws MonitisException {
		return send(POST, url, data, fileName);
	}
	
	public static Response sendGet(String url, List<NameValuePair> data, String fileName)
			throws MonitisException {
		return send(GET, url, data, fileName);
	}
	
	public static void main(String[] args) {
		//apikey=9RR6Q707FJ8CFR4QJSBE0ERM9&timestamp=2011-04-18%2017:16:45&version=2&action=addMonitor&
		//monitorParams=test_monitor_parameter:TestParameter1:altitude:2:false;&
		//resultParams=test_monitor_parameter:TestParameter1:feet:2;&name=test_monitor&tags=test&validation=token&authToken=6I8VJKN4SP0VVVOD86O3761Q8Q
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new NameValuePair("apikey", "9RR6Q707FJ8CFR4QJSBE0ERM9"));
		data.add(new NameValuePair("timestamp", "2011-04-19 03:40:45"));
		data.add(new NameValuePair("version", "2"));
		data.add(new NameValuePair("action", "addMonitor"));
		data.add(new NameValuePair("monitorParams", "test_monitor_parameter:TestParameter1:altitude:2:false;"));
		data.add(new NameValuePair("resultParams", "test_monitor_parameter:TestParameter1:feet:2;"));
		data.add(new NameValuePair("name", "test_monitor"));
		data.add(new NameValuePair("tag", "test"));
		data.add(new NameValuePair("validation", "token"));
		data.add(new NameValuePair("authToken", "6I8VJKN4SP0VVVOD86O3761Q8Q"));// "38L89PP40CMMB5HUV79MNGR0E9"
		try {
			sendPost("http://monitis.com/customMonitorApi", data);
		} catch (MonitisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


