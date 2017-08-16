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
import java.util.Iterator;
import java.util.Map.Entry;

import melting.Thermodynamics;
import melting.exceptions.ThermodynamicParameterError;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class represents a Handler to parse the subnodes to the data nodes in the xml files. It
 * extends the NodeHandler class
 */
public class ThermoHandler extends NodeHandler{
	
	// Instance variables
	
	/**
	 * Double enthalpy : enthalpy value. (delta H in cal/mol)
	 */
	private Double enthalpy;
	
	/**
	 * Double entropy : entropy value. (delta S in cal/mol)
	 */
	private Double entropy;
	
	/**
	 * Thermodynamics thermo : contains the enthalpy and antropy values of the node
	 */
	private Thermodynamics thermo;
	
	/**
	 * boolean hasEnthalpy : informs if the node contains a proper enthalpy value
	 */
	private boolean hasEnthalpy = false;
	
	/**
	 * boolean hasEntropy : informs if the node contains a proper entropy value
	 */
	private boolean hasEntropy = false;
	
	/**
	 * HashMap<String, String> attribut : contains the different possible attributes for the subnodes of Data nodes
	 */
	private HashMap<String, String> attribut = new HashMap<String, String>();
	
	/**
	 * String name : name of the current node.
	 */
	private String name;
	
	// ThermoHandler constructor

	/**
	 * creates a ThermoHandler object and initialises the instance variables of this object.
	 * @param  nodeName : node name
	 */
	public ThermoHandler(String nodeName){
		
		this.hasEnthalpy = false;
		this.hasEntropy = false; 
		this.name = nodeName;
		}
	
	// public methods
	
	/**
	 * This method is called to get the name String of the ThermoHandler object.
	 * @return the name String of the ThermoHandler object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method is called to get the enthalpy double value of the ThermoHandler object.
	 * @return the enthalpy double value of the ThermoHandler object.
	 */
	public Double getEnthalpy() {
		return enthalpy;
	}

	/**
	 * This method is called to get the entropy double value of the ThermoHandler object.
	 * @return the entropy double value of the ThermoHandler object.
	 */
	public Double getEntropy() {
		return entropy;
	}

	/**
	 * This method is called to get the Thermodynamics of the ThermoHandler object.
	 * @return the Thermodynamics of the ThermoHandler object.
	 */
	public Thermodynamics getThermo() {
		return thermo;
	}

	/**
	 * This method is called to get the attribut HashMap of the ThermoHandler object.
	 * @return the attribut HashMap of the ThermoHandler object.
	 */
	public HashMap<String, String> getAttribut() {
		return attribut;
	}
	
	/**
	 * initialises the attribut HashMap of the ThermoHandler object.
	 */
	public void initialiseAttributes(){
		for (Iterator<Entry<String, String>> i = attribut.entrySet().iterator(); i.hasNext();){
			i.next().setValue(null);
		}
		this.thermo = null;
		this.enthalpy = 0.0;
		this.entropy = 0.0;
		this.hasEnthalpy = false;
		this.hasEntropy = false;
		this.name = null;
	}
	
	// inherited method
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (subHandler == null) {
			if (name.equals("enthalpy")) {
				subHandler = new EnergyHandler();
				hasEnthalpy = true;
			}
			else if (name.equals("entropy")) {
				subHandler = new EnergyHandler();
				hasEntropy = true;
			}
			else if (name.equals(this.name)){
				for (int i = 0;i < attributes.getLength(); i++ ){
					attribut.put(attributes.getQName(i), attributes.getValue(i));
				}
			}
			else {
				throw new ThermodynamicParameterError("\n The node " + name + " in the xml file is not known.");
			}
		}
		else {
			subHandler.startElement(uri, localName, name, attributes);
			}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
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
				EnergyHandler handler = (EnergyHandler) subHandler;
				
				if (name.equals("enthalpy")) {
					this.enthalpy = handler.getEnergy();
				} 
				else if (name.equals("entropy")) {
					this.entropy = handler.getEnergy();
				}
				else {
					throw new ThermodynamicParameterError("\n The node " + name + " in the xml file is not known.");
				}
				subHandler = null;
			}
		} 
		else {

			if (name.equals(this.name)) {
				if (enthalpy == null){
					enthalpy = 0.0;
				}
				if (entropy == null){
					entropy = 0.0;
				}
				thermo = new Thermodynamics(enthalpy, entropy);
			}	
			if (hasEnthalpy == false && hasEntropy == false) {
				throw new ThermodynamicParameterError("\n No energy value is entered for the xml node " + name);
			}
			else if (hasEnthalpy == false && (this.name.equals("mismatch") == false  && this.name.equals("hairpin") == false && this.name.equals("bulge") == false)) {
				throw new ThermodynamicParameterError("\n No energy value is entered for the xml node " + name);
			}
			else if (hasEnthalpy == false && (this.name.equals("mismatch") == true || this.name.equals("bulge") == true || this.name.equals("hairpin") == true) && ((this.attribut.containsKey("size") && this.attribut.get("type") == "initiation") || this.attribut.containsKey("size") == false)) {
				throw new ThermodynamicParameterError("\n No energy value is entered for the xml node " + name);
			}
			else {
				if (thermo != null){
					completedNode();
				}
			}
		}
	}
}
