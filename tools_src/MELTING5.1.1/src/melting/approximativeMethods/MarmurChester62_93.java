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

import java.util.HashMap;

import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the model che93. It extends ApproximativeMode.
 * 
 * Marmur J, Doty P, "Determination of the base composition of 
 * deoxyribonucleic acid from its thermal denaturation temperature", 
 * 1962, Journal of molecular biology, 5, 109-118.
 * 
 * Chester N, Marshak DR, "dimethyl sulfoxide-mediated primer Tm reduction : 
 * a method for analyzing the role of renaturation temperature in the polymerase 
 * chain reaction", 1993, Analytical Biochemistry, 209, 284-290.
 */
public class MarmurChester62_93 extends ApproximativeMode
  implements NamedMethod
{
	
	// Instance variables
	
	/**
	 * double parameter. This parameter changes if we use the model che93corr.
	 */
	private double parameter;
	
	/**
	 * temperature formula
	 */
	private String temperatureEquation = "Tm = 69.3 + 0.41 * PercentGC - parameter / duplexLength.";

  /**
   * The full name of the method
   */
  private String methodName;

  // public constructor

  /**
   * Sets up the private variables: <code>parameter</code> and
   * <code>methodName</code>.  
   * @param   parameter   535 for the corrected version, 
   *                      650 for the uncorrected version.
   * @param   methodName  The name of the method.
   */
  public MarmurChester62_93(double parameter, String methodName)
  {
    this.parameter = parameter;
    this.methodName = methodName;
  }

  /**
   * Sets up the private variables for the uncorrected version.
   * The parameter for the uncorrected version is 650.
   */
  public MarmurChester62_93()
  {
    this(650, "Marmur 1962, Chester et al. 1993");
  }
	
	// public methods
	
	@Override
	public ThermoResult computesThermodynamics() {
		double Tm = super.computesThermodynamics().getTm();
		
		Tm = 69.3 + 0.41 * this.environment.getSequences().computesPercentGC() - parameter / (double)this.environment.getSequences().getDuplexLength();
		
		this.environment.setResult(Tm);
		
    OptionManagement.logMethodName(methodName);
    OptionManagement.logTemperatureEquation(temperatureEquation);
		OptionManagement.logMessage("Where parameter = " + parameter);

		return this.environment.getResult();
	}

	@Override
	public boolean isApplicable() {
		boolean isApplicable = super.isApplicable();
		
		if (environment.getSequences().computesPercentMismatching() != 0){
			isApplicable = false;
		}
		
		if (this.environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The formula of Marmur, Doty, Chester " +
					"and Marshak is originally established for DNA duplexes.");
		}
		if (this.environment.getNa() != 0.0 || this.environment.getMg() != 0.0015 || this.environment.getTris() != 0.01 || this.environment.getK() != 0.05){
		   OptionManagement.logWarning("\n The formula of Marmur, Doty, Chester " +
			"and Marshak is originally established at a given ionic strength : " +
			"Na = 0 M, Mg = 0.0015 M, Tris = 0.01 M and k = 0.05 M");
		}
		
		return isApplicable;
	}
	
	@Override
	public void setUpVariables(HashMap<String, String> options) {
		String method = options.get(OptionManagement.approximativeMode);
		
		super.setUpVariables(options);
	}

  /**
   * Gets the full name of the method.
   * @return The full name of the method.
   */
  public String getName()
  {
    return methodName;
  }
	
	// protected method
	
	@Override
	protected boolean isNaEqPossible(){
		return false;
	}

}
