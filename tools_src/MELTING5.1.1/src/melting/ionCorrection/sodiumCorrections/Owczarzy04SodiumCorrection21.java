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
 * This class represents the sodium correction model owc2104. It implements the CorrectionMethod interface.
 * 
 * Richard Owczarzy, Yong You, Bernardo G. Moreira, Jeffrey A.Manthey, Lingyan Huang, Mark A. Behlke and Joseph 
 * A.Walder, "Effects of sodium ions on DNA duplex oligomers: Improved predictions of melting temperatures",
 * Biochemistry, 2004, 43, 3537-3554.
 */
public class Owczarzy04SodiumCorrection21
  implements CorrectionMethod, NamedMethod
{	
	// Instance variables
	
	/**
	 * String temperatureCorrection : formula for the temperature correction.
	 */
	private static String temperatureCorrection = "Tm(Na) = Tm(Na = 1M) + (-4.62 x Fgc + 4.52) x ln(NaEquivalent) - 0.985 x ln(Na)^2";

  /**
   * Full name of the method.
   */
  private static String methodName = "Owczarzy et al. (2004) (21)";
	
	// CorrectionMethod interface implementation
	
	public boolean isApplicable(Environment environment) {
		boolean isApplicable = true;
		double NaEq = Helper.computesNaEquivalent(environment);
		
		if (NaEq == 0){
			OptionManagement.logWarning("\n The sodium concentration must be strictly positive.");
			isApplicable = false;
		}
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The sodium correction of Owczarzy et al. (2004) 21 is originally established for " +
			"DNA duplexes.");

		}
		
		return isApplicable;
	}

	public ThermoResult correctMeltingResults(Environment environment) {
		
		OptionManagement.logMessage("\n The sodium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(temperatureCorrection);
		
		double NaEq = Helper.computesNaEquivalent(environment);
		double Fgc = environment.getSequences().computesPercentGC() / 100.0;
		double square = Math.log(NaEq) * Math.log(NaEq);
		double Tm = environment.getResult().getTm() + (-4.62 * Fgc + 4.52) * Math.log(NaEq) - 0.985 * square;

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
