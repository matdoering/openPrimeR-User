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
 * This class represents the model wetdna91. It extends Wetmur91.
 */
public class WetmurDNA91 extends Wetmur91
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * Temperature formula
	 */
	private static String temperatureEquation = "Tm = 81.5 + 16.6 * log10(Na / (1.0 + 0.7 * Na)) + 0.41 * percentGC - 500 / duplexLength - percentMismatching";

  /**
   * Full name of the method.
   */
  private static String methodName = "Wetmur (1991) (DNA duplexes)";
	
	// public methods
	
	@Override
	public ThermoResult computesThermodynamics() {
		double percentGC = this.environment.getSequences().computesPercentGC();
		double percentMismatching = this.environment.getSequences().computesPercentMismatching();
		double duplexLength = (double)this.environment.getSequences().getDuplexLength();
		double Tm = super.computesThermodynamics().getTm(); 
		
		Tm = 81.5 + 16.6 * Math.log10(this.environment.getNa() / (1.0 + 0.7 * this.environment.getNa())) + 0.41 * percentGC - 500.0 / duplexLength - percentMismatching;

		this.environment.setResult(Tm);
		
    OptionManagement.logMethodName(methodName);
    OptionManagement.logTemperatureEquation(temperatureEquation);
		
		return this.environment.getResult();
	}
	
	@Override
	public boolean isApplicable() {
		
		if (this.environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The wetmur equation used here was originally established for DNA duplexes.");
		}
		
		return super.isApplicable();
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
