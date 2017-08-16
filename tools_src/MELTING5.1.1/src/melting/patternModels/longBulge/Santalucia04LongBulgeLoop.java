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

package melting.patternModels.longBulge;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the long bulge loop model san04. It extends PatternComputation.
 * 
 * Santalucia et al (2004). Annu. Rev. Biophys. Biomol. Struct 33 : 415-440
 */
public class Santalucia04LongBulgeLoop extends PatternComputation
  implements NamedMethod
{	
	// Instance variable
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for long bulge loop
	 */
	public static String defaultFileName = "Santalucia2004longbulge.xml";
	
	/**
	 * String formulaEnthalpy : enthalpy formula
	 */
	protected static String formulaEnthalpy = "delta H = number AT closing x H(closing AT penalty)";
	
	/**
	 * String formulaEntropy : entropy formula
	 */
	protected static String formulaEntropy = "delta S = number AT closing x S(closing AT penalty) + S(bulge loop of n)";

  /**
   * Full name of the method.
   */
  private static String methodName = "Santalucia (2004)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {

		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The single bulge loop parameters of " +
					"Santalucia (2004) are originally established " +
					"for DNA sequences.");
		}
		
		return super.isApplicable(environment, pos1, pos2);
	}

	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		OptionManagement.logWarning("\n The long bulge loop model san04 has not been tested with experimental values.");

		NucleotidSequences bulgeLoop = sequences.getEquivalentSequences("dna");
		
		OptionManagement.logMessage("\n The long bulge loop model is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(formulaEnthalpy + " and " + formulaEntropy);
    OptionManagement.logFileName(this.fileName);

		double enthalpy = result.getEnthalpy();
		double entropy = result.getEntropy();
		double numberAT = bulgeLoop.calculateNumberOfTerminal("A", "T", pos1, pos2);
		String bulgeSize = Integer.toString(Math.abs(pos2 - pos1) - 1);
		
		Thermodynamics bulgeLoopValue = this.collector.getBulgeLoopvalue(bulgeSize);
		if (bulgeLoopValue == null){
			bulgeLoopValue = this.collector.getBulgeLoopvalue("30");
			
			OptionManagement.logMessage("\n bulge loop of " + bulgeSize + " :  enthalpy = " + bulgeLoopValue.getEnthalpy() + "  entropy = " + bulgeLoopValue.getEntropy() + " - 2.44 x 1.99 x ln(bulgeSize / 30)");

			entropy += bulgeLoopValue.getEntropy() - 2.44 * 1.99 * Math.log(Double.parseDouble(bulgeSize) / 30.0);
		}
		else {
			OptionManagement.logMessage("\n bulge loop of " + bulgeSize + " :  enthalpy = " + bulgeLoopValue.getEnthalpy() + "  entropy = " + bulgeLoopValue.getEntropy());

			entropy += bulgeLoopValue.getEntropy();
		}
		
		if (numberAT> 0 && this.collector.getClosureValue("A", "T") != null){
			Thermodynamics closingAT = this.collector.getClosureValue("A", "T");

			OptionManagement.logMessage("\n" + numberAT + " x AT closing : enthalpy = " + closingAT.getEnthalpy() + "  entropy = " + closingAT.getEntropy());

			enthalpy += numberAT * closingAT.getEnthalpy();
			enthalpy += numberAT * closingAT.getEntropy();
		
		}
		
		result.setEnthalpy(enthalpy);
		result.setEntropy(entropy);
		
		return result;
	}

	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		boolean isMissingParameters = super.isMissingParameters(sequences, pos1, pos2);
		NucleotidSequences bulgeLoop = sequences.getEquivalentSequences("dna");
		
		double numberAT = bulgeLoop.calculateNumberOfTerminal("A", "T", pos1, pos2);
		String bulgeSize = Integer.toString(Math.abs(pos2 - pos1) - 1);
		
		if (numberAT > 0){
			if (this.collector.getClosureValue("A", "T") == null){
				OptionManagement.logWarning("\n The parameters for AT closing base pair are missing. The results can lose accuracy.");
			}
		}
		if (this.collector.getBulgeLoopvalue(bulgeSize) == null){
			if (this.collector.getBulgeLoopvalue("30") == null){
				OptionManagement.logWarning("\n The parameters for a bulge loop of " + bulgeSize + " are missing. Check the long bulge loop parameters.");

				return true;
			}
		}
		return isMissingParameters;
	}
	
	@Override
	public void initialiseFileName(String methodName){
		super.initialiseFileName(methodName);
		
		if (this.fileName == null){
			this.fileName = defaultFileName;
		}
	}
	
	// protected method
	
	/**
	 * corrects the pattern positions in the duplex to have the adjacent
	 * base pair of the pattern included in the subsequence between the positions pos1 and pos2
	 * @param pos1 : starting position of the internal loop
	 * @param pos2 : ending position of the internal loop
	 * @param duplexLength : total length of the duplex
	 * @return int [] positions : new positions of the subsequence to have the pattern surrounded by the
	 * adjacent base pairs in the duplex.
	 */
	protected int[] correctPositions(int pos1, int pos2, int duplexLength){
		if (pos1 > 0){
			pos1 --;
		}
		if (pos2 < duplexLength - 1){
			pos2 ++;
		}
		int [] positions = {pos1, pos2};
		return positions;
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
