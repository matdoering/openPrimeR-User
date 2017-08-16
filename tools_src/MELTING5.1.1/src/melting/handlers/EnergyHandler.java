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

import melting.exceptions.ThermodynamicParameterError;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class represents a Handler to parse the enthalpy or entropy nodes in the xml files. It
 * extends NodeHandler.
 */
public class EnergyHandler extends NodeHandler{

	// Instance variables
	
	/**
	 * energy value for the enthalpy or entropy node. (cal/mol)
	 */
	private double energy;
	
	/**
	 * boolean hasEnergy : informs if an enthalpy or entropy node contains a proper energy value
	 */
	private boolean hasEnergy = false;
	
	/**
	 * StringBuilder stringEnergy : stocks the energy String value.
	 */
	private StringBuilder stringEnergy = new StringBuilder();
	
	// public methods
	
	/**
	 * This method is called to get the energy of EnergyHandler.
	 * @return double : the energy of EnergyHandler.
	 */
	public double getEnergy() {
		return energy;
	}
	
	/**
	 * This method is called to get the hasEnergy of EnergyHandler.
	 * @return double : the energy of EnergyHandler.
	 */
	public boolean getHasEnergy(){
		return hasEnergy;
	}
	
	// Inherited methods

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String e = new String(ch,start,length);
		stringEnergy.append(e);
	}
	
	@Override
	public void endElement(String uri, String localName, String name)
	throws SAXException {
		try {
			if (stringEnergy.length() != 0){
				energy = Double.parseDouble(stringEnergy.toString());
				hasEnergy = true;
			}
		} catch (NumberFormatException e2) {
			throw new ThermodynamicParameterError("\n There is one error in the files containing the thermodynamic parameters. The energy value" + stringEnergy + " must be a numeric value.", e2);
		}
		if (hasEnergy){
			completedNode();
		}
		else {
			throw new ThermodynamicParameterError("\n One enthalpy or entropy value is missing, the xml node is incomplete");
		}
	}
}
