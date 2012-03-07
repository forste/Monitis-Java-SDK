package org.monitis.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtility {
	public static String getStackTraceAsString(Throwable exception) 
	{ 
		StringWriter sw = new StringWriter(); 
		PrintWriter pw = new PrintWriter(sw); 
		pw.print(" [ "); 
		pw.print(exception.getClass().getName()); 
		pw.print(" ] "); 
		pw.print(exception.getMessage()); 
		exception.printStackTrace(pw); 
		return sw.toString(); 
	}
}
