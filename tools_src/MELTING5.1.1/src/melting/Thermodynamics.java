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

package melting;

/**
 * This class represents the thermodynamic energy. It allows to stock the enthalpy and entropy values
 * of a thermodynamic parameter (a calorimetric value from one of the thermodynamic tables). 
 * It contains a enthalpy double and a entropy double as instance variables.
 */
public class Thermodynamics {
	
	// Instance variables
	
	/**
	 * protected double enthalpy : the enthalpy value (delta H). (initialised from a file containing the thermodynamic parameters)
	 */
	protected double enthalpy;
	
	/**
	 * protected double entropy : the entropy value (delta S). (initialised from a file containing the thermodynamic parameters)
	 */
	protected double entropy;
	
	// Thermodynamics constructor
	
	public Thermodynamics(double H, double S){
		this.enthalpy = H;
		this.entropy = S;
	}
	
	// public methods
	
	/**
	 * This method is called to get the enthalpy of Thermodynamics.
	 *  @return the enthalpy of Thermodynamics.
	 */
	public double getEnthalpy() {
		return enthalpy;
	}
	
	/**
	 * This method is called to change the enthalpy of Thermodynamics.
	 * @param enthalpy : new enthalpy value
	 */
	public void setEnthalpy(double enthalpy) {
		this.enthalpy = enthalpy;
	}
	
	/**
	 * This method is called to get the entropy of Thermodynamics.
	 *  @return the entropy of Thermodynamics.
	 */
	public double getEntropy() {
		return entropy;
	}
	
	/**
	 * This method is called to change the entropy of Thermodynamics.
	 * @param entropy : new entropy value
	 */
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}
}
