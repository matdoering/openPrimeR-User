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

package melting.ionCorrection.sodiumEquivalence;

import java.text.NumberFormat;
import java.util.HashMap;

import melting.configuration.OptionManagement;
import melting.methodInterfaces.SodiumEquivalentMethod;

/**
 * This class represents the methods to compute a sodium equivalence. It implements the SodiumEquivalentMethod interface.
 */
public abstract class SodiumEquivalent implements SodiumEquivalentMethod{

	// SodiumEquivalentMethod interface implementation

	public boolean isApplicable(HashMap<String, String> options) {
		String hybridization = options.get(OptionManagement.hybridization);
		
		if (hybridization.equals("dnadna") == false){
			OptionManagement.logWarning("\n The current equations to have the sodium equivalent" +
					"concentration are established for DNA duplexes.");
		}
		return true;
	}
	
	// public method
	
	/**
	 * This method is called to get a sodium equivalent concentration when other cations are present.
	 * @param Na : sodium concentration
	 * @param Mg : magnesium concentration
	 * @param K : potassium concentration
	 * @param Tris : Tris buffer concentration
	 * @param dNTP : dNTP concentration
	 * @param b : a formula parameter depending on the model.
	 * @return double NaEq : the sodium equivalent concentration which takes in account the other cation concentrations.
	 */
	public double getSodiumEquivalent(double Na, double Mg, double K, double Tris,
			double dNTP, double b) {
		OptionManagement.logMessage("\n Other cations than Na+ are present in the solution, we can use a sodium equivalence : "); 
		
		double NaEq = Na + K + Tris / 2 + b * Math.sqrt(Mg - dNTP);
		
		NumberFormat format = NumberFormat.getInstance(); 
		format.setMaximumFractionDigits(2);
		
		OptionManagement.logMessage("\n NaEq = " + format.format(NaEq) + " M."); 
		
		return NaEq;
	}
}
