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
 * This class represents the nearest neighbor model san96. It extends GlobalInitiation.
 * 
 * SantaLucia et al.(1996). Biochemistry 35 : 3555-3562
 */
public class Santalucia96 extends GlobalInitiation
  implements NamedMethod
{
	
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for each Crick's pair
	 */
	public static String defaultFileName = "Santalucia1996nn.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Santalucia (1996)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1, int pos2) {

		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The model of Santalucia (1996)" +
			"is established for DNA sequences.");
		}
		return super.isApplicable(environment, pos1, pos2);
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		NucleotidSequences newSequences = sequences.getEquivalentSequences("dna");

		return super.computeThermodynamics(newSequences, pos1, pos2, result);
	}
	
	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		NucleotidSequences newSequences = sequences.getEquivalentSequences("dna");
		
		boolean isMissing = super.isMissingParameters(newSequences, pos1, pos2);
		
		int [] truncatedPositions =  newSequences.removeTerminalUnpairedNucleotides();

		double number5AT = newSequences.getNumberTerminal5TA(truncatedPositions[0], truncatedPositions[1]);
		
		if (number5AT > 0) {
			if (this.collector.getTerminal("5_T/A") == null){
				isMissing = true;
				OptionManagement.logMessage("\n The thermodynamic parameters for terminal 5'TA base pair are missing.");
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

		NucleotidSequences newSequences = environment.getSequences().getEquivalentSequences("dna");
		super.computesHybridizationInitiation(environment);

		int [] truncatedPositions =  newSequences.removeTerminalUnpairedNucleotides();
		
		double enthalpy = 0.0;
		double entropy = 0.0;
		double number5AT = newSequences.getNumberTerminal5TA(truncatedPositions[0], truncatedPositions[1]);
		
		if (number5AT > 0) {
			Thermodynamics terminal5AT = this.collector.getTerminal("5_T/A");
			OptionManagement.logMessage("\n" + number5AT + " x  penalty for 5' terminal AT : enthalpy = " + terminal5AT.getEnthalpy() + "  entropy = " + terminal5AT.getEntropy());
			
			enthalpy += number5AT * terminal5AT.getEnthalpy();
			entropy += number5AT * terminal5AT.getEntropy();
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
