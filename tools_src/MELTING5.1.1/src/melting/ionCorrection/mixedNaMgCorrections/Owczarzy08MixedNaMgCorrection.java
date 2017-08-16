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
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.ionCorrection.magnesiumCorrections.Owczarzy08MagnesiumCorrection;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the mixed (Na,Mg) correction model owcmix08. It extends Owczarzy08MagnesiumCorrection.
 * 
 * Richard Owczarzy, Bernardo G Moreira, Yong You, Mark A 
 * Behlke, Joseph A walder, "Predicting stability of DNA duplexes in solutions
 * containing magnesium and monovalent cations", 2008, Biochemistry, 47, 5336-5353.
*/
public class Owczarzy08MixedNaMgCorrection
  extends Owczarzy08MagnesiumCorrection
  implements NamedMethod
{	
	// Instance variables
	
	private static String aFormula = "a = -0.6 / duplexLength + 0.025 x ln(Mg) + 0.0068 x ln(Mg)^2";
	
	private static String dFormula = "d = ln(Mg) + 0.38 x ln(Mg)^2";
	
	private static String gFormula = "g = a + b / (duplexLength^2)";

  /**
   * Full name of the method.
   */
  private static String methodName = "Owczarzy et al. (2008)";

	// CorrectionMethod interface implementation

	@Override
	public ThermoResult correctMeltingResults(Environment environment) {
		
		double monovalent = environment.getNa() + environment.getK() + environment.getTris() / 2;
		double square = Math.log(monovalent) * Math.log(monovalent);
		double cube = Math.log(monovalent) * Math.log(monovalent) * Math.log(monovalent);
		
		this.a = 3.92 / 100000 * (0.843 - 0.352 * Math.sqrt(monovalent) * Math.log(monovalent));
		this.d = 1.42 / 100000 * (1.279 - 4.03 / 1000 * Math.log(monovalent) - 8.03 / 1000 * square);
		this.g = 8.31 / 100000 * (0.486 - 0.258 * Math.log(monovalent) + 5.25 / 1000 * cube);
		
		double Tm = super.correctTemperature(environment);
		environment.setResult(Tm);
		return environment.getResult();
	}

	// Inherited method
	
	@Override
	protected void displayVariable(){
		OptionManagement.logMessage(aFormula);
		OptionManagement.logMessage(dFormula);
		OptionManagement.logMessage(gFormula);
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
