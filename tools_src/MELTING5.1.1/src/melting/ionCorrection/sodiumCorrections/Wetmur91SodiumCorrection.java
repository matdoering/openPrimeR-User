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

package melting.ionCorrection.sodiumCorrections;

import melting.Environment;
import melting.Helper;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.methodInterfaces.CorrectionMethod;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the sodium correction model wet91. It implements the CorrectionMethod interface.
 * 
 * James G. Wetmur, "DNA Probes : applications of the principles of nucleic acid hybridization",
 * 1991, Critical reviews in biochemistry and molecular biology, 26, 227-259
 */
public class Wetmur91SodiumCorrection
  implements CorrectionMethod, NamedMethod
{	
	// Instance variables
	
	/**
	 * String temperatureCorrection : formula for the temperature correction.
	 */
	private static String temperatureCorrection = "Tm(Na) = Tm(Na = 1M) + 16.6 x log10(Na / (1.0 + 0.7 * Na)) + 3.83";

  /**
   * Full name of the method.
   */
  private static String methodName = "Wetmur (1991)";
	
	// CorrectionMethod interface implementation

	public boolean isApplicable(Environment environment) {
		boolean isApplicable = true;
		double NaEq = Helper.computesNaEquivalent(environment);
		
		if (NaEq == 0){
			OptionManagement.logWarning("\n The sodium correction of Wetmur (1991) is applicable for " +
					"strictky positive sodium concentrations.");
			isApplicable = false;
		}
		return isApplicable;
	}
	
	public ThermoResult correctMeltingResults(Environment environment) {
		
		OptionManagement.logMessage("\n The sodium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(temperatureCorrection);
		
		double NaEq = Helper.computesNaEquivalent(environment);
		
		double Tm = environment.getResult().getTm() + 16.6 * Math.log10(NaEq / (1.0 + 0.7 * NaEq)) + 3.83;
		environment.setResult(Tm);
		
		return environment.getResult();
	}
	
  /**
   * Gets the full name of the method.
   * @return The full name of the method.
   */
  @Override
  public String getName()
  {
    return methodName;
  }
}
