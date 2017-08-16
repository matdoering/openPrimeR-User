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

package melting.correctionMethods;

import melting.Environment;
import melting.ThermoResult;
import melting.methodInterfaces.CorrectionMethod;
import melting.nearestNeighborModel.NearestNeighborMode;

/**
 * This class represents a specific type of ion correction which corrects the entropy value. It implements 
 * the CorrectionMethod interface.
 */
public abstract class EntropyCorrection implements CorrectionMethod{

	// CorrectionMethod interface implementation
	
	public ThermoResult correctMeltingResults(Environment environment) {
		double entropy = correctEntropy(environment);
		environment.addResult(0, entropy);
		double Tm = NearestNeighborMode.computesMeltingTemperature(environment);
		environment.setResult(Tm);
		
		return environment.getResult();
	}
	
	public boolean isApplicable(Environment environment){
		return true;
	}
	
	// protected method
	
	/**
	 * corrects the computed entropy (delta S (Na = 1M)).
	 * @param environment
	 * @return double : corrected entropy;
	 */
	protected double correctEntropy(Environment environment){
		return 0;
	}

}
