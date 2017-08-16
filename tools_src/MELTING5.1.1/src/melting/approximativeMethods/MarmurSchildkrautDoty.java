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
 * This class represents the model schdot. It extends ApproximativeMode.
 * 
 * James G. Wetmur, "DNA Probes : applications of the principles of nucleic acid hybridization",
*1991, Critical reviews in biochemistry and molecular biology, 26, 227-259

 * Marmur J, Doty P, "Determination of the base composition of 
 * deoxyribonucleic acid from its thermal denaturation temperature", 
 * 1962, Journal of molecular biology, 5, 109-118.
  
 * Chester N, Marshak DR, "dimethyl sulfoxide-mediated primer Tm reduction : 
 * a method for analyzing the role of renaturation temperature in the polymerase 
 * chain reaction", 1993, Analytical Biochemistry, 209, 284-290.
 
 * Schildkraut C, Lifson S, "Dependance of the melting temperature of DNA on salt 
 * concentration", 1965, Biopolymers, 3, 95-110.
 
 *  Wahl GM, Berger SL, Kimmel AR. Molecular hybridization of
 *  immobilized nucleic acids: theoretical concepts and practical
 *  considerations. Methods Enzymol 1987;152:399 – 407.
              
 *  Britten RJ, Graham DE, Neufeld BR. Analysis of repeating DNA
 *  sequences by reassociation. Methods Enzymol 1974;29:363–418.
         
 *  Hall TJ, Grula JW, Davidson EH, Britten RJ. Evolution of sea urchin
 *  non-repetitive DNA. J Mol Evol 1980;16:95–110.
 */
public class MarmurSchildkrautDoty extends ApproximativeMode
  implements NamedMethod
{
	
	// Instance variables
	
	/**
	 * temperature formula
	 */
	private static String temperatureEquation = "Tm = 81.5 + 16.6 * log10(Na) + 0.41 * percentGC - 675 / duplexLength.";

  /**
   * The name of the method.
   */
  private static String methodName =
                   "Marmur, Schildkraut and Doty (1965 - 1993)";
	
	// public methods
	
	@Override
	public ThermoResult computesThermodynamics() {
		double Tm = super.computesThermodynamics().getTm();
		Tm = 81.5 + 16.6 * Math.log10(this.environment.getNa()) + 0.41 * this.environment.getSequences().computesPercentGC() - 675.0 / (double)this.environment.getSequences().getDuplexLength();

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
			OptionManagement.logWarning("\n The Marmur-Schildkraut-Doty equation" +
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
