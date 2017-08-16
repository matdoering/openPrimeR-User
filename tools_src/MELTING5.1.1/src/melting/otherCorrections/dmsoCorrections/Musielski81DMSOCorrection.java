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

package melting.otherCorrections.dmsoCorrections;

import melting.Environment;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.correctionMethods.DNADMSOCorrections;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the DMSO correction model mus81. It extends DNADMSOCorrections.
 * 
 * Musielski H., Mann W, Laue R, Michel S, "Influence of dimethylsulfoxide
 * on transcription by bacteriophage T3-induced RNA polymerase.", Z allg Microbiol 1981; 21, 447-456.
 */
public class Musielski81DMSOCorrection extends DNADMSOCorrections
  implements NamedMethod
{
	// Instance variables

	private static double parameter = 0.6;
	
	/**
	 * String temperatureCorrection : formula for the temperature correction
	 */
	private static String temperatureCorrection = "Tm (x % DMSO) = Tm(0 % DMSO) - 0.6 * x % DMSO";

  /**
   * Full name of the method.
   */
  private static String methodName = "Musielski et al. (1981)";
	
	// CorrectionMethod interface implementation

	public ThermoResult correctMeltingResults(Environment environment) {
		OptionManagement.logMessage("\n The DMSO correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(temperatureCorrection);
		
		return super.correctMeltingResult(environment, parameter);
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
