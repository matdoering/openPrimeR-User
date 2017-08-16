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
import melting.correctionMethods.EntropyCorrection;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the sodium correction model san04. It extends EntropyCorrection.
 * 
 * John Santalucia, Jr., "A unified view of polymer, dumbbell, and oligonucleotide DNA nearest-neighbor
 * thermodynamics.", 1998, Proc. Natl. Acad. Sci. USA, 95, 1460-1465
 * 
 * Santalucia et al (2004). Annu. Rev. Biophys. Biomol. Struct 33 : 415-440
 */
public class Santalucia98_04SodiumCorrection extends EntropyCorrection
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * String entropyCorrection : formula for the entropy correction.
	 */
	private static String entropyCorrection = "delta S(Na) = delta S(Na = 1M) + 0.368 * (duplexLength - 1) x ln(Na)";

  /**
   * Full name of the method.
   */
  private static String methodName = "SantaLucia et al. (1998, 2004)";

	// CorrectionMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment) {
		boolean isApplicable = super.isApplicable(environment);
		double NaEq = Helper.computesNaEquivalent(environment);
		if (NaEq == 0){
			OptionManagement.logWarning("\n The sodium concentration must be a positive numeric value.");
			isApplicable = false;
		}
		
		else if (NaEq < 0.05 || NaEq > 1.1){
			OptionManagement.logWarning("\n The sodium correction of Santalucia et al. (1998 - 2004) is only reliable for " +
					"sodium concentrations between 0.015M and 1.1M.");
		}
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The sodium correction of Santalucia et al. (1998 - 2004) is originally established for " +
			"DNA duplexes.");
		}
		
		if (environment.getSequences().getDuplexLength() > 16){
			OptionManagement.logWarning("\n The sodium correction of Santalucia et al. (1998 - 2004) begins to break down for " +
			"DNA duplexes longer than 16 bp.");
		}
		return isApplicable;
	}
	
	@Override
	public ThermoResult correctMeltingResults(Environment environment) {
		double NaEq = Helper.computesNaEquivalent(environment);
		environment.setNa(NaEq);
		
		return super.correctMeltingResults(environment);
	}
	
	// Inherited method
	
	@Override
	protected double correctEntropy(Environment environment){
		
		OptionManagement.logMessage("\n The sodium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(entropyCorrection);

		double Na = environment.getNa();
		double entropy = 0.368 * ((double)environment.getSequences().getDuplexLength() - 1.0) * Math.log(Na);
		
		return entropy;
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
