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

package melting.patternModels.cricksPair;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the nearest neighbor model xia98. It extends CricksNNMethod.
 * 
 * Xia et al (1998) Biochemistry 37: 14719-14735
 */
public class Xia98 extends CricksNNMethod
  implements NamedMethod
{
	
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for each Crick's pair
	 */
	public static String defaultFileName = "Xia1998nn.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Xia et al. (1998)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1, int pos2) {

		if (environment.getHybridization().equals("rnarna") == false){
			OptionManagement.logWarning("\n The model of Xia et al. (1998)" +
			"is established for RNA/RNA sequences.");
		}
		return super.isApplicable(environment, pos1, pos2);
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		NucleotidSequences newSequences = sequences.getEquivalentSequences("rna");
				
		return super.computeThermodynamics(newSequences, pos1, pos2, result);
	}
	
	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		NucleotidSequences newSequences = sequences.getEquivalentSequences("rna");
		
		boolean isMissing = super.isMissingParameters(newSequences, pos1, pos2);
		int [] truncatedPositions =  sequences.removeTerminalUnpairedNucleotides();

		double numberTerminalAU = sequences.calculateNumberOfTerminal("A", "U", truncatedPositions[0], truncatedPositions[1]);

		if (numberTerminalAU != 0) {
			if (this.collector.getTerminal("per_A/U") == null){
				isMissing = true;
				OptionManagement.logWarning("/n The thermodynamic parameters for terminal AU base pair are missing.");
			}	
		}
		
		return isMissing;
	}
	
	@Override
	public void initialiseFileName(String methodName){
		super.initialiseFileName(methodName);
		
		if (this.fileName == null){
			this.fileName = defaultFileName;
		}
	}
	
	// Inherited method
	
	@Override
	public ThermoResult computesHybridizationInitiation(Environment environment){

		NucleotidSequences newSequences = environment.getSequences().getEquivalentSequences("rna");
		
		super.computesHybridizationInitiation(environment);

		int [] truncatedPositions = newSequences.removeTerminalUnpairedNucleotides();
		
		double numberTerminalAU = newSequences.calculateNumberOfTerminal("A", "U", truncatedPositions[0], truncatedPositions[1]);
		double enthalpy = 0.0;
		double entropy = 0.0;
		
		if (numberTerminalAU != 0) {
			Thermodynamics terminalAU = this.collector.getTerminal("per_A/U");
			
			OptionManagement.logMessage("\n" + numberTerminalAU + " x penalty per terminal AU : enthalpy = " + terminalAU.getEnthalpy() + "  entropy = " + terminalAU.getEntropy());
			
			enthalpy += numberTerminalAU * terminalAU.getEnthalpy();
			entropy += numberTerminalAU * terminalAU.getEntropy();
		}
		
		environment.addResult(enthalpy, entropy);
		
		return environment.getResult();
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
