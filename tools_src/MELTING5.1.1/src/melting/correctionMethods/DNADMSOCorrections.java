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
import melting.configuration.OptionManagement;
import melting.methodInterfaces.CorrectionMethod;

/**
 * This class represents the DMSO correction. It implements the CorrectionMethod interface.
 */
public abstract class DNADMSOCorrections implements CorrectionMethod{
	
	// CorrectionMethod interface implementation
	
	public boolean isApplicable(Environment environment) {
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The current DMSO corrections are established for DNA duplexes.");
		}
		return true;
	}

	public ThermoResult correctMeltingResult(Environment environment, double parameter) {
		double Tm = environment.getResult().getTm() - parameter * environment.getDMSO();
		
		OptionManagement.logWarning("\n The current DMSO corrections has not been tested with experimenta values.");

		environment.setResult(Tm);
		return environment.getResult();
	}

}
