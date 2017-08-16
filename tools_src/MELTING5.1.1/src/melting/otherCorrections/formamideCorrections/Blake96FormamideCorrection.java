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

package melting.otherCorrections.formamideCorrections;

import melting.Environment;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.methodInterfaces.CorrectionMethod;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the formamide correction model bla96. It implements the CorrectionMethod interface.
 * 
 * R. D. Blake* and Scott G. Delcourt, "Thermodynamic effects of formamide on DNA stability", Nucleic Acids Research, 1996, Vol. 24, No. 11 2095–2103
 */
public class Blake96FormamideCorrection
  implements CorrectionMethod, NamedMethod
{
	// Instance variables
	
	/**
	 * String temperatureCorrection : formula for the temperature correction
	 */
	private static String temperatureCorrection = "Tm (x mol formamide) = Tm(0 mole formamide) + (0.453 * Fgc - 2.88) * x mole formamide";

  /**
   * Full name of the method.
   */
  private static String methodName = "Blake and Delcourt (1996)";
	
	// CorrectionMethod interface implementation
	
	public boolean isApplicable(Environment environment) {
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The formamide correction from Blake et al.(1996) is established for DNA duplexes.");
		}
		return true;
	}
	
	public ThermoResult correctMeltingResults(Environment environment) {
		double Fgc = environment.getSequences().computesPercentGC() / 100.0;
		double Tm = environment.getResult().getTm() + (0.453 * Fgc - 2.88) * environment.getFormamide();
		
		OptionManagement.logMessage("\n The formamide correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(temperatureCorrection);
		
		OptionManagement.logWarning("\n The current formamide correction has not been tested with experimental values.");
		
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
