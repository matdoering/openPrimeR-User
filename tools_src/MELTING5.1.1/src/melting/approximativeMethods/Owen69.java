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
 * This class represents the model owe69. It extends Approximative.
 * 
 * Owen RJ, Hill LR, Lapage SP. Determination of DNA base 
 * compositions from melting profiles in dilute buffers. Biopolymers 1969;
 * 7:503–16.
 
 * Frank-Kamenetskii MD. Simplification of the empirical relationship 
 * between melting temperature of DNA, its GC content and concentration 
 * of sodium ions in solution. Biopolymers 1971;10:2623– 4.
 
 * Blake RD. Denaturation of DNA. In: Meyers RA, ed. Encyclopedia
 * of molecular biology and molecular medicine, Vol. 2. Weinheim,
 * Germany: VCH Verlagsgesellschaft, 1996:1–19.
 
 * Blake RD, Delcourt SG. Thermal stability of DNA. Nucleic Acids
 * Res 1998;26:3323–32.
*/
public class Owen69 extends ApproximativeMode
  implements NamedMethod
{
	
	// Instance variables
	
	/**
	 * temperature formula
	 */
	private static String temperatureEquation = "Tm = 87.16 + 0.345 * percentGC + log10(Na) * (20.17 - 0.066 * percentGC)";

  /**
   * The full name of the method.
   */
  private static String methodName = "Owen et al. (1969)";
	
	// public methods
	
	@Override
	public ThermoResult computesThermodynamics() {
		double percentGC = this.environment.getSequences().computesPercentGC();
		double Tm = super.computesThermodynamics().getTm(); 
		Tm = 87.16 + 0.345 * percentGC + Math.log10(this.environment.getNa()) * (20.17 - 0.066 * percentGC);
		
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
			OptionManagement.logWarning("\n The Owen et al. equation" +
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
