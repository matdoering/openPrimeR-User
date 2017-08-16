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

import melting.Environment;
import melting.ThermoResult;

/** This interface is implemented by classes which contain methods to correct the melting temperature. It can be a temperature correction for ions, formamide, DMSO,...
 * */
public interface CorrectionMethod {

	/**
	 * Check if the environment chosen by the user is a applicable with the correction model.
	 * @param environment
	 * @return false if the correction method can't be used with the Environment. If the environment can make the results less reliable, a warning message will appear.
	 */
	public boolean isApplicable(Environment environment);
	
	/**
	 * This method is called to correct the computed melting temperature.
	 * @param environment
	 * @return ThermoResult which contains the corrected results (enthalpy, entropy and melting temperature).
	 */
	public ThermoResult correctMeltingResults(Environment environment);
}
