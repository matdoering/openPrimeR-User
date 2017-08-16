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

/** This interface is implemented by classes which contain a method to compute an equivalent sodium concentration when 
 * other ions are present.
 * */
public interface SodiumEquivalentMethod {

	/**
	 * This method is called to convert an initial sodium concentration in a new equivalent sodium concentration
	 * which takes in account the other cation concentrations.
	 * @param Na : sodium concentration entered by the user
	 * @param Mg : magnesium concentration entered by the user
	 * @param K : potassium concentration entered by the user
	 * @param Tris : Tris buffer concentration entered by the user.
	 * @param dNTP : dNTP concentration entered by the user
	 * @return double NaEq. It is the equivalent sodium computed concentration
	 */
	public double computeSodiumEquivalent(double Na, double Mg, double K, double Tris, double dNTP);
	
	/**
	 * Check if the model to compute an equivalent sodium cencentration is applicable with the options entered by the user.
	 * @param options which contains the options. (default options and options entered by the user)
	 * @return false if one of the options entered by the user make the model inapplicable. If one of the options can make the results
	 * less reliable, a warning message will appear.
	 */
	public boolean isApplicable(HashMap<String, String> options);
	
}
