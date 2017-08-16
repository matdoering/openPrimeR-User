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

package melting.ionCorrection.magnesiumCorrections;

import melting.Environment;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.methodInterfaces.CorrectionMethod;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the magnesium correction model owc08. It implements the CorrectionMethod interface
 * 
 * Richard Owczarzy, Bernardo G Moreira, Yong You, Mark A Behlke, Joseph A walder, "Predicting stability of DNA duplexes in solutions
* containing magnesium and monovalent cations", 2008, Biochemistry, 47, 5336-5353.
 */
public class Owczarzy08MagnesiumCorrection
  implements CorrectionMethod, NamedMethod
{	
	// Instance variables
	
	protected double a = 3.92 / 100000;
	
	protected double b = -9.11 / 1000000;
	
	protected double c = 6.26 / 100000;
	
	protected double d = 1.42 / 100000;
	
	protected double e = -4.82 / 10000;
	
	protected double f = 5.25 / 10000;
	
	protected double g = 8.31 / 100000;
	
	/**
	 * temperature formula
	 */
	protected static String temperatureCorrection = "1 / Tm(Mg) = 1 / Tm(Na = 1M) + a +b x ln(Mg) + Fgc x (c + d x ln(Mg)) + 1 / (2 x (duplexLength - 1)) x (e + f x ln(Mg) + g x (ln(mg)))^2"; 

  /**
   * Full name of the method.
   */
  private static String methodName = "Owczarzy et al. (2008)";
	
	// CorrectionMethod interface implementation
	
	public boolean isApplicable(Environment environment) {
		boolean isApplicable = true;
		if (environment.getMg() == 0){
			OptionManagement.logWarning("\n The magnesium concentration must be a positive numeric value.");
			isApplicable = false;
		}
		
		else if (environment.getMg() < 0.0005 || environment.getMg() > 0.6){
			OptionManagement.logWarning("\n The magnesium correction of Owczarzy et al. " +
			"(2008) is accurate in the magnesium concentration range of 0.5mM to 600mM.");
		}
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The magnesium correction of Owczarzy et al. " +
					"(2008) is originally established for DNA duplexes.");
		}
		return isApplicable;
	}
	
	public ThermoResult correctMeltingResults(Environment environment) {

		double Tm = correctTemperature(environment);
		environment.setResult(Tm);
		
		return environment.getResult();
	}

	// protected methods
	
	/**
	 * corrects the computed melting temperature depending on the environment.
	 * @param environment
	 * @return double corrected melting temperature
	 */
	protected double correctTemperature(Environment environment) {
		OptionManagement.logMessage("\n The magnesium correction is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(temperatureCorrection);
		OptionManagement.logMessage("where : ");
		OptionManagement.logMessage("b = " + this.b);
		OptionManagement.logMessage("c = " + this.c);
		OptionManagement.logMessage("e = " + this.e);
		OptionManagement.logMessage("f = " + this.f);
		displayVariable();

		double Mg = environment.getMg() - environment.getDNTP();
		double square = Math.log(Mg) * Math.log(Mg);
		double Fgc = environment.getSequences().computesPercentGC() / 100.0;
		
		double TmInverse = 1.0 / (environment.getResult().getTm() + 273.15) + this.a +this.b * Math.log(Mg) + Fgc * (this.c + this.d * Math.log(Mg)) + 1.0 / (2.0 * ((double)environment.getSequences().getDuplexLength() - 1.0)) * (this.e + this.f * Math.log(Mg) + this.g * square);
		return (1.0 / TmInverse) - 273.15;
	}
	
	/**
	 * logs the a, d and g variables.
	 */
	protected void displayVariable(){
		OptionManagement.logMessage("a = " + this.a);
		OptionManagement.logMessage("d = " + this.d);
		OptionManagement.logMessage("g = " + this.g);
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
