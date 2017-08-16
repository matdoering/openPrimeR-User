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
import melting.configuration.OptionManagement;
import melting.correctionMethods.EntropyCorrection;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the sodium correction model ahs01. It extends EntropyCorrection.
 * 
 * Nicolas Von Ahsen, Carl T Wittwer and Ekkehard Schutz, "Oligonucleotide
 * melting temperatures under PCR conditions : deoxynucleotide Triphosphate
 * and Dimethyl sulfoxide concentrations with comparison to alternative empirical 
 * formulas", 2001, Clinical Chemistry, 47, 1956-1961.
*/
public class Ahsen01SodiumCorrection extends EntropyCorrection
  implements NamedMethod
{	
	// Instance variables
	
	/**
	 * String entropyCorrection : formula for the entropy correction.
	 */
	private static String entropyCorrection = "delat S(Na) = delta S(Na = 1M) + 0.847 x (duplexLength - 1) x log10(Na)";

  /**
   * Full name of the method.
   */
  private static String methodName = "Ahsen et al. (2001)";
	
	// CorrectionMethod interface implementation
	
	@Override
	public boolean isApplicable(Environment environment) {
		boolean isApplicable = super.isApplicable(environment);
		double NaEq = Helper.computesNaEquivalent(environment);
		
		if (NaEq == 0){
			OptionManagement.logWarning("\n The sodium concentration must be strictly positive.");
			isApplicable = false;
		}
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The sodium correction of Ahsen et al. (2001) is originally established for " +
			"DNA duplexes.");
		}
		return isApplicable;
	}
	
	// Inherited method
	
	@Override
	protected double correctEntropy(Environment environment){
		
		OptionManagement.logMessage("\n The sodium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(entropyCorrection);
		
		double entropy = 0.847 * ((double)environment.getSequences().getDuplexLength() - 1.0) * Math.log10(environment.getNa());

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
