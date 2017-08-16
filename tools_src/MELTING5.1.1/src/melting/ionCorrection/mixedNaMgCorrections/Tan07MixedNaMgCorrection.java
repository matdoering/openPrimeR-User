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

package melting.ionCorrection.mixedNaMgCorrections;

import melting.Environment;
import melting.configuration.OptionManagement;
import melting.correctionMethods.EntropyCorrection;
import melting.ionCorrection.magnesiumCorrections.Tan06MagnesiumCorrection;
import melting.ionCorrection.magnesiumCorrections.Tan07MagnesiumCorrection;
import melting.ionCorrection.sodiumCorrections.Tan06SodiumCorrection;
import melting.ionCorrection.sodiumCorrections.Tan07SodiumCorrection;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the mixed (Na,Mg) correction model tanmix07. It extends EntropyCorrection.
 * 
 * Zhi-Jie Tan and Shi-Jie Chen, "RNA helix stability in Mixed Na+/Mg2+
 * solutions", 2007, Biophysical Journal, 92, 3615-3632.
 */

public class Tan07MixedNaMgCorrection extends EntropyCorrection
  implements NamedMethod
{	
	// Instance variables

	/**
	 * String entropyCorrection : formula for the entropy correction
	 */
	protected static String entropyCorrection = "delta S(Na, Mg) = delta S(Na = 1M) - 3.22 x ((duplexLength - 1) x (x1 x g1 + x2 x g2) + g12)"; 
	
	/**
	 * String x1Formula : formula representing the fractional contribution of Na+ ions
	 */
	protected static String x1Formula = "x1 = Na / (Na + (8.1 - 32.4 / duplexLength) x (5.2 - ln(Na)) x Mg)";
	
	/**
	 * String x2Formula : formula representing the fractional contribution of Mg2+ ions
	 */
	protected static String x2Formula = "x2 = 1 - x1";
	
	/**
	 * String gFormula : function associated with the electrostatic folding free energy per base stack.
	 */
	protected static String gFormula = "g12 = -0.6 x x1 x x2 x ln(Na) x ln((1 / x1 - 1) x Na) / duplexLength";

  /**
   * Full name of the method.
   */
  private static String methodName = "Zhi-Jie Tan and Shi-Jie Chen (2007)";
	
	// CorrectionMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment) {
		boolean isApplicable = super.isApplicable(environment);
		if (environment.getMg() == 0){
			OptionManagement.logWarning(" \n The magnesium concentration must be a positive numeric value.");
			isApplicable = false;
		}
		else if (environment.getMg() < 0.1 && environment.getMg() > 0.3){
			OptionManagement.logWarning("\n The mixed Na/Mg correction of Zhi-Jie Tan et al. (2007)" +
					"is reliable for magnesium concentrations between 0.1M and 0.3M.");
		}
		
		if (environment.getNa() < 0 && environment.getNa() > 1){
			OptionManagement.logWarning("\n The mixed Na/Mg correction of Zhi-Jie Tan et al. (2007)" +
					"is reliable for sodium concentrations between 0M and 1M.");
		}
		
		if (environment.getHybridization().equals("rnarna") == false || environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The magnesium correction of Zhi-Jie Tan et al. (2007)is originally established for " +
			"RNA or DNA duplexes.");
		}
		return isApplicable;
	}
	
	// Inherited method
	
	@Override
	protected double correctEntropy(Environment environment){
		
		OptionManagement.logMessage("\n The magnesium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(entropyCorrection);

		double Na = environment.getNa();
		double Mg = environment.getMg() - environment.getDNTP();
		double duplexLength = (double)environment.getSequences().getDuplexLength();
		
		double x1 = Na / (Na + (8.1 - 32.4 / duplexLength) * (5.2 - Math.log(Na)) * Mg);
		double x2 = 1 - x1;
		double g12 = -0.6 * x1 * x2 * Math.log(Na) * Math.log((1 / x1 - 1) * Na) / duplexLength;
		
		double g1 = 0;
		double g2 = 0;
		if (environment.getHybridization().equals("rnarna")){
			g1 = Tan07SodiumCorrection.calculateFreeEnergyPerBaseStack(environment);
			g2 = Tan07MagnesiumCorrection.computeFreeEnergyPerBaseStack(environment);
		}
		else if (environment.getHybridization().equals("dnadna")){
			g1 = Tan06SodiumCorrection.calculateFreeEnergyPerBaseStack(environment);
			g2 = Tan06MagnesiumCorrection.computeFreeEnergyPerBaseStack(environment);
		}
				
		double entropy = -3.22 * ((duplexLength - 1) * (x1 * g1 + x2 * g2) + g12);
		
		OptionManagement.logMessage("and where : ");
		OptionManagement.logMessage(gFormula);
		OptionManagement.logMessage(x1Formula);
		OptionManagement.logMessage(x2Formula);
		
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
