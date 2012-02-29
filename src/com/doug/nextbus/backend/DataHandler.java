package com.doug.nextbus.backend;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class DataHandler extends DefaultHandler {
	
	HashMap<String, Object> xmlDataHash;
	ArrayList<HashMap<String, Object>> hashStack;
	boolean addToArray = false;
	boolean arrayOfHash = false;
	final String TEXT_NODE = "text";
	String chars;
	
	public HashMap<String, Object> getXMLData() {
		return xmlDataHash;
	}
	
	public void startDocument() throws SAXException{
		xmlDataHash = new HashMap<String, Object>();
		hashStack = new ArrayList<HashMap<String, Object>>();
		hashStack.add(xmlDataHash);
	
	}
	
	public void endDocument() throws SAXException {
		
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		
		
		HashMap<String,Object> parentHash = hashStack.get(hashStack.size()-1);
		HashMap<String,Object> childHash = new HashMap<String, Object>();
		
		for (int i = 0; i < atts.getLength(); i++) {
			childHash.put(atts.getLocalName(i),atts.getValue(i));
		}
		
		Object existingValue = parentHash.get(localName);
		
		if (existingValue != null) {
			ArrayList<Object> array = null;
			if (existingValue instanceof ArrayList) {
				array = (ArrayList<Object>) existingValue;
			} else {
				array = new ArrayList<Object>();
				array.add(existingValue);
				parentHash.put(localName, array);
			}
			
			array.add(childHash);
			
		} else {
			parentHash.put(localName, childHash);
		}
		hashStack.add(childHash);
	}
	
	public void endElement(String nameSpaceURI, String localName, String qName) throws SAXException {
		
		HashMap<String, Object> hashInProgress = hashStack.get(hashStack.size()-1);
		
		if (chars != null && chars.trim().length() > 0) {
			hashInProgress.put(TEXT_NODE, chars);
			
			chars = null;
		}
		
		hashStack.remove(hashStack.size() - 1);
		
	}
	
	public void characters(char ch[], int start, int length) {
		
		chars = new String(ch, start, length);
	}
	
}