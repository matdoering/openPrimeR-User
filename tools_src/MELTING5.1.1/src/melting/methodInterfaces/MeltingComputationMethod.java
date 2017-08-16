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

package melting.methodInterfaces;

import java.util.HashMap;

import melting.ThermoResult;
import melting.configuration.RegisterMethods;

/** This interface is implemented by classes which contain methods to compute the melting temperature.
 * */

public interface MeltingComputationMethod {

	/**
	 * This method is called to compute the melting temperature.
	 * @return ThermoResult containing the computed enthalpy, entropy and melting temperature.
	 */
	public ThermoResult computesThermodynamics();
	
	/**
	 * This method is called to check that the options entered by the users are applicable with the used model (class).
	 * @return false if at least one chosen option makes the model completely false. If one chosen option may lead to less reliable results, a warning message will appear. (ex : wrong hybridization type) 
	 */
	public boolean isApplicable();
	
	/**
	 * Creates the Environment object and set up the variables of the chosen model. 
	 * @param options containing all the options (default options and options entered by the user)
	 */
	public void setUpVariables(HashMap<String, String> options);
	
	/**
	 * This method is called to get the RegisterMethod of the implemented class.
	 * @return the RegisterMethod of the implemented class.
	 */
	public RegisterMethods getRegister();
}
