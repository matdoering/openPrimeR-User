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
 * This class represents the sodium correction model san96. It implements the CorrectionMethod interface.
 * 
 * SantaLucia et al.(1996). Biochemistry 35 : 3555-3562
 */
public class Santalucia96SodiumCorrection
  implements CorrectionMethod, NamedMethod
{	
	// Instance variables
	
	/**
	 * String temperatureCorrection : formula for the temperature correction.
	 */
	private static String temperatureCorrection = "Tm(Na) = Tm(Na = 1M) + 12.5 x log10(Na)";

  /**
   * Full name of the method.
   */
  private static String methodName = "Santalucia et al. (1996)";

	// CorrectionMethod interface implementation
	
	public boolean isApplicable(Environment environment) {
		boolean isApplicable = true;
		double NaEq = Helper.computesNaEquivalent(environment);
		if (NaEq == 0){
			OptionManagement.logWarning("\n The sodium concentration must be a positive numeric value.");
			isApplicable = false;
		}
		
		else if (NaEq < 0.1){
			OptionManagement.logWarning("\n The sodium correction of Santalucia et al. (1996) is not reliable for " +
					"sodium concentrations inferior to 0.1M.");
		}
		
		if (environment.getHybridization().equals("dnadna") == false){

			OptionManagement.logWarning("\n The sodium correction of Santalucia et al. (1996) is originally established for " +
			"DNA duplexes.");
		}
		return isApplicable;
	}
	
	public ThermoResult correctMeltingResults(Environment environment) {
		
		OptionManagement.logMessage("\n The sodium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(temperatureCorrection);

		double NaEq = Helper.computesNaEquivalent(environment);
		
		double Tm = environment.getResult().getTm() + 12.5 * Math.log10(NaEq);
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
