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
 * This class represents the model san98. It extends ApproximativeMode.
 * 
 * Santalucia J Jr, "A unified view of polymer, dumbbel, and 
 * oligonucleotide DNA nearest-neighbor thermodynamics", Proc
 * Nacl Acad Sci USA 1998, 95, 1460-1465.
 * */
public class Santalucia98 extends ApproximativeMode
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * Temperature formula
	 */
	private static String temperatureEquation = "Tm = 77.1 + 11.7 * log10(Na) + 0.41 * PercentGC - 528 / duplexLength.";

  /**
   * Full name of the method.
   */
  private static String methodName = "Santalucia et al. (1998)";

	// public methods
	
	@Override
	public ThermoResult computesThermodynamics() {
		double Tm = super.computesThermodynamics().getTm(); 
		Tm = 77.1 + 11.7 * Math.log10(this.environment.getNa()) + 0.41 * this.environment.getSequences().computesPercentGC() - 528.0 / (double)this.environment.getSequences().getDuplexLength();
		
		this.environment.setResult(Tm);
		
    OptionManagement.logMethodName(methodName);
    OptionManagement.logTemperatureEquation(temperatureEquation);
		
		return this.environment.getResult();
	}

	@Override
	public boolean isApplicable() {
		boolean isApplicable = super.isApplicable();
		
		if (environment.getSequences().computesPercentMismatching() != 0){
			isApplicable = false;
		}
		
		if (this.environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The Santalucia equation for polymers" +
					"was originally established for DNA duplexes.");
		}
		
		return isApplicable;
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
