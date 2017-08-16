/* This program is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version
                                
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA                                                                  

 *       Marine Dumousseau and Nicolas Lenovere                                                   
 *       EMBL-EBI, neurobiology computational group,                          
 *       Cambridge, UK. e-mail: lenov@ebi.ac.uk, marine@ebi.ac.uk        */

package melting.handlers;

import java.util.HashMap;

import melting.Thermodynamics;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class represents a Handler to parse the Data nodes in the xml files. It
 * extends NodeHandler.
 */
public class DataHandler extends NodeHandler {

	// Instance variables

	/**
	 * attributes for the data node.
	 */
	private String type;

	/**
	 * HashMap<String, Thermodynamics> map : contains the thermodynamic
	 * parameters of the xml file.
	 */
	private HashMap<String, Thermodynamics> map = new HashMap<String, Thermodynamics>();

	// public methods

	/**
	 * This method is called to get the map of DataHandler.
	 * @return the map of DataHandler.
	 */
	public HashMap<String, Thermodynamics> getMap() {
		return map;
	}

	/**
	 * This method is called to get the type String of DataHandler.
	 * @return the  'type' of the DataHandler.
	 */
	public String getType() {
		return type;
	}

	/**
	 * This method is called to get the map of DataHandler.
	 * @return the map of DataHandler.
	 */
	private void setType(String s) {
		this.type = s;
	}
	
	// Inherited methods

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (subHandler == null) {
			if (name.equals("data")) {
				this.setType(attributes.getValue("type"));
			} else {
				subHandler = new ThermoHandler(name);
				subHandler.startElement(uri, localName, name, attributes);
			}
		} else {
			subHandler.startElement(uri, localName, name, attributes);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (subHandler != null) {
			subHandler.characters(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (subHandler != null) {
			subHandler.endElement(uri, localName, name);
			if (subHandler.hasCompletedNode()) {
				ThermoHandler handler = (ThermoHandler) subHandler;
				String key = name;
				if (handler.getAttribut().containsKey("type")) {
					key += handler.getAttribut().get("type");
				}
				if (handler.getAttribut().containsKey("loop")) {
					key += "loop" + handler.getAttribut().get("loop");
				}
				if (handler.getAttribut().containsKey("size")) {
					key += "size" + handler.getAttribut().get("size");
				}
				if (handler.getAttribut().containsKey("repeats")) {
					key += "repeats" + handler.getAttribut().get("repeats");
				}
				if (handler.getAttribut().containsKey("sequence")) {
					key += handler.getAttribut().get("sequence");
				}
				if (handler.getAttribut().containsKey("closing")) {
					key += "close" + handler.getAttribut().get("closing");
				}
				if (handler.getAttribut().containsKey("sens")) {
					key += "sens" + handler.getAttribut().get("sens");
				}
				if (handler.getAttribut().containsKey("parameter")) {
					key += "parameter" + handler.getAttribut().get("parameter");
				}
				map.put(key, handler.getThermo());
				handler.initialiseAttributes();
				subHandler = null;
			}
		} else {
			completedNode();
		}
	}
}
