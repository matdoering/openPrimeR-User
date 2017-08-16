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

package melting.approximativeMethods;

import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.methodInterfaces.NamedMethod;

/** 
 * This class represents the approxiamtive model ahs01. It extends ApproximativeMode.
 * 
 * Nicolas Von Ahsen, Carl T Wittwer and Ekkehard Schutz, "Oligonucleotide
 * melting temperatures under PCR conditions : deoxynucleotide Triphosphate
 * and Dimethyl sulfoxide concentrations with comparison to alternative empirical 
 * formulas", 2001, Clinical Chemistry, 47, 1956-1961.
 */
public class Ahsen01 extends ApproximativeMode
  implements NamedMethod
{

	// instance variables 
	
	/**
	 * temperature formula.
	 */
	private static String temperatureEquation = "Tm = 80.4 + 0.345 * percentGC + log10(Na) * (17.0 - 0.135 * percentGC) - 550 / duplexLength.";

  /**
   * The full name of the method.
   */
  private static String methodName = "Ahsen et al. (2001)";
	
	// public methods 
	
	@Override
	public ThermoResult computesThermodynamics() {
		double Tm = super.computesThermodynamics().getTm();
		double percentGC = this.environment.getSequences().computesPercentGC();
		Tm = 80.4 + 0.345 * percentGC + Math.log10(this.environment.getNa()) * (17.0 - 0.135 * percentGC) - 550.0 / (double)this.environment.getSequences().getDuplexLength();

		environment.setResult(Tm);
		
    OptionManagement.logMethodName(methodName);
    OptionManagement.logTemperatureEquation(temperatureEquation);
		
		return environment.getResult();
	}

	@Override
	public boolean isApplicable() {
		boolean isApplicable = super.isApplicable();
		
		if (environment.getSequences().computesPercentMismatching() != 0){
			isApplicable = false;
		}
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The Ahsen et al. equation" +
					"was originally established for DNA duplexes.");
		}
		return isApplicable;
	}

  /**
   * Gets the name of the method.
   * @return The name of the method.
   */
  @Override
  public String getName()
  {
    return methodName;
  }
}
