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

package melting;

import java.text.NumberFormat;

import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.methodInterfaces.MeltingComputationMethod;
import melting.nearestNeighborModel.NearestNeighborMode;

/**
 * The Melting main class which contains the public static void main(String[] args) method.
 */
public class Main {

	// private static methods
	
	/**
	 * Compute the entropy, enthalpy and the melting temperature and display the results. 
	 * @param args : contains the options entered by the user.
	 * @param OptionManagement optionManager : the OptionManegement which allows to manage
	 * the different options entered by the user.
	 */
	private static ThermoResult runMelting(String [] args, OptionManagement optionManager){
		try {
                        ThermoResult results = 
                                        getMeltingResults(args, optionManager);
			displaysMeltingResults(results);
                        return results;
			
		} catch (Exception e) {
			OptionManagement.logError(e.getMessage());
                        return null;
		}
	}
        
        /**
         * Compute the entropy, enthalpy and melting temperature, and return 
         * these results.
         * @param args options (entered by the user) that determine the
         *             sequence, hybridization type and other features of the
         *             environment.
         * @param optionManager the {@link 
         *                           melting.configuration.OptionManagement 
         *                           <code>OptionManagement</code>} which
         *                      allows the program to manage the different
         *                      options entered by the user.  
         * @return The results of the Melting computation.
         */
        public static ThermoResult getMeltingResults(String[] args,
                                                OptionManagement optionManager)
        {
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            
            // Set up the environment from the supplied arguments and get the 
            // results.
            Environment environment = optionManager.createEnvironment(args);
            RegisterMethods register = new RegisterMethods();
            MeltingComputationMethod calculMethod = 
                register.getMeltingComputationMethod(environment.getOptions());
            ThermoResult results = calculMethod.computesThermodynamics();
            results.setCalculMethod(calculMethod);
            environment.setResult(results);
            
            // Apply corrections to the results.
            results = calculMethod.getRegister().
                                   computeOtherMeltingCorrections(environment);
            environment.setResult(results);
            return environment.getResult();
        }
	
	/**
	 * displays the results of Melting : the computed enthalpy and entropy (in cal/mol and J/mol), and the computed 
	 * melting temperature (in degrees).
	 * @param results : the ThermoResult containing the computed enthalpy, entropy and
	 * melting temperature
	 * @param MeltingComputationMethod calculMethod : the melting computation method (Approximative or nearest neighbor computation)
	 */
	private static void displaysMeltingResults(ThermoResult results)
        {
		NumberFormat format = NumberFormat.getInstance(); 
		format.setMaximumFractionDigits(2);
                MeltingComputationMethod calculMethod = 
                                                     results.getCalculMethod();
		
		double enthalpy = results.getEnthalpy();
		double entropy = results.getEntropy();

		OptionManagement.logInfo("\n The MELTING results are : ");
		if (calculMethod instanceof NearestNeighborMode){
			OptionManagement.logInfo("Enthalpy : " + format.format(enthalpy) + " cal/mol ( " + format.format(results.getEnergyValueInJ(enthalpy)) + " J /mol)");
			OptionManagement.logInfo("Entropy : " + format.format(entropy) + " cal/mol-K ( " + format.format(results.getEnergyValueInJ(entropy)) + " J /mol-K)");
		}
		OptionManagement.logInfo("Melting temperature : " + format.format(results.getTm()) + " degrees C.\n");
	}
	
	// public static main method
	
	/**
	 * @param args : contains the options entered by the user.
	 */
	public static void main(String[] args) {
	
		OptionManagement optionManager = new OptionManagement();
		
		if (args.length == 0){
			optionManager.initialiseLogger();
			optionManager.readMeltingHelp();
		}
		else if (optionManager.isMeltingInformationOption(args)){
			try {
				optionManager.readOptions(args);

			} catch (Exception e) {
				OptionManagement.logError(e.getMessage());
			}
		}
		else {
			runMelting(args, optionManager);
		}
	}
}
