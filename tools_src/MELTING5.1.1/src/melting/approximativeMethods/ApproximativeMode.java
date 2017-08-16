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

import melting.Environment;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.exceptions.NoExistingMethodException;
import melting.methodInterfaces.MeltingComputationMethod;
import melting.methodInterfaces.SodiumEquivalentMethod;

/**
 * This class represents the approximative model. It implements the MelitngComputationMethod Interface.
 */
public class ApproximativeMode implements MeltingComputationMethod{
	
	// Instance variables
	
	/**
	 * environment containing the sequences, ion and agent concentrations and the options (default options and
	 * options entered by the user)
	 */
	protected Environment environment;
	
	/**
	 * RegisterMethods register : registers all the pattern computation methods implemented by Melting
	 */
	protected RegisterMethods register = new RegisterMethods();
	
	// MeltingComputationMethod interface implementation
	
	public ThermoResult computesThermodynamics() {
		OptionManagement.logMessage("\n Approximative method : ");
		
		return environment.getResult();
	}

	public boolean isApplicable() {
		boolean isApplicable = true;
		
		if (environment.getSequences().computesPercentMismatching() != 0){
			OptionManagement.logWarning("\n The approximative mode formulas" +
					"cannot properly account for the presence of mismatches" +
					" and unpaired nucleotides.");
		}
		
		if (Integer.parseInt(environment.getOptions().get(OptionManagement.threshold)) >= environment.getSequences().getDuplexLength()){
			if (environment.getOptions().get(OptionManagement.globalMethod).equals("def")){
				isApplicable = false;
			}
			OptionManagement.logWarning("\n The approximative equations " +
			"were originally established for long DNA duplexes. (length superior to " +
			 environment.getOptions().get(OptionManagement.threshold) +").");
		}
		return isApplicable;
	}
	
	public void setUpVariables(HashMap<String, String> options) {
		this.environment = new Environment(options);

		if (isNaEqPossible()){
			if (environment.getMg() > 0 || environment.getK() > 0 || environment.getTris() > 0){
				
				SodiumEquivalentMethod method = this.register.getNaEqMethod(options);
				if (method != null){
					environment.setNa(method.computeSodiumEquivalent(environment.getNa(), environment.getMg(), environment.getK(), environment.getTris(), environment.getDNTP()));
				}
				else{
					throw new NoExistingMethodException("\n There are other ions than Na+ in the solution and no ion correction method is avalaible for this type of hybridization.");
				}
			}
		}
	}
	
	public RegisterMethods getRegister() {
		return register;
	}
	
	// protected methods
	
	/**
	 * to check if a sodium equivalence is necessary. (when other ions are present)
	 * @return true if sodium equivalence is necessary.
	 */
	protected boolean isNaEqPossible(){
		return true;
	}
}

