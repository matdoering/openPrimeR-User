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

package melting.patternModels.InternalLoops;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the 1x2 internal loop model zno07. It extends PatternComputation.
 * 
 *  Brent M Znosko et al (2007). Biochemistry 46: 14715-14724.
 */
public class Znosko071x2Loop extends PatternComputation
  implements NamedMethod
{	
	// Instance variable
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for internal loop
	 */
	public static String defaultFileName = "Znosko20071x2loop.xml";
	
	/**
	 * String formulaEnthalpy : enthalpy formula
	 */
	private static String formulaEnthalpy = "delat H = H(first mismath) + H(initiation 1x2 loop) + number AU closing x H(closing AU) + number GU closing x H(closing GU)";

  /**
   * Full name of the method.
   */
  private static String methodName = "Znosko et al. (2007)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		int [] positions = correctPositions(pos1, pos2, environment.getSequences().getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		String loopType = environment.getSequences().getInternalLoopType(pos1,pos2);

		if (environment.getHybridization().equals("rnarna") == false){
			OptionManagement.logWarning("\n The internal 1x2 loop parameters of " +
					"Znosko et al. (2007) are originally established " +
					"for RNA sequences.");
			
		}
		
		boolean isApplicable = super.isApplicable(environment, pos1, pos2);
		if (loopType.equals("1x2") == false && loopType.equals("2x1") == false){
			OptionManagement.logWarning("\n The thermodynamic parameters of Znosko et al. (2007) are" +
					"established only for 1x2 internal loop.");
			
			isApplicable = false;
		}
		
		return isApplicable;
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];

		NucleotidSequences internalLoop = sequences.getEquivalentSequences("rna");
		
		OptionManagement.logMessage("\n The 1 x 2 internal loop model is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(formulaEnthalpy + " (entropy formula is similar)");
    OptionManagement.logFileName(this.fileName);

		String [] mismatch = internalLoop.getLoopFistMismatch(pos1);
		double numberAU = internalLoop.calculateNumberOfTerminal("A", "U", pos1, pos2);
		double numberGU = internalLoop.calculateNumberOfTerminal("G", "U", pos1, pos2);
		
		Thermodynamics initiationLoop = this.collector.getInitiationLoopValue();
		
		OptionManagement.logMessage("\n 1x2 Internal loop :  enthalpy = " + initiationLoop.getEnthalpy() + "  entropy = " + initiationLoop.getEntropy());

		double enthalpy = result.getEnthalpy() + initiationLoop.getEnthalpy();
		double entropy = result.getEntropy() + initiationLoop.getEntropy();
		
		Thermodynamics firstMismatch; 
		if (sequences.getDuplex().get(pos1 + 1).isBasePairEqualTo("G", "A")){
			if (this.collector.getFirstMismatch("A", "G_not_RA/YG", "1x2") == null){
				firstMismatch = new Thermodynamics(0,0);
			}
			else {
				firstMismatch = this.collector.getFirstMismatch("A", "G_not_RA/YG", "1x2");
			}
			
			OptionManagement.logMessage("\n First mismatch A/G, not RA/YG : enthalpy = " + firstMismatch.getEnthalpy() + "  entropy = " + firstMismatch.getEntropy());
		}
		else {
			if (this.collector.getFirstMismatch(mismatch[0], mismatch[1], "1x2") == null){
				firstMismatch = new Thermodynamics(0,0);
			}
			else {
				firstMismatch = this.collector.getFirstMismatch(mismatch[0], mismatch[1], "1x2");
			}
			
			OptionManagement.logMessage("\n First mismatch " + mismatch[0] + "/" + mismatch[1] + " : enthalpy = " + firstMismatch.getEnthalpy() + "  entropy = " + firstMismatch.getEntropy());
		}
		enthalpy += firstMismatch.getEnthalpy();
		entropy += firstMismatch.getEntropy();
		
		if (numberAU > 0){
			Thermodynamics closureAU = this.collector.getClosureValue("A", "U");
			
			OptionManagement.logMessage("\n" + numberAU + " x AU closure : enthalpy = " + closureAU.getEnthalpy() + "  entropy = " + closureAU.getEntropy());

			enthalpy += numberAU * closureAU.getEnthalpy();
			entropy += numberAU * closureAU.getEntropy();
			
		}
		
		if (numberGU > 0){
			Thermodynamics closureGU = this.collector.getClosureValue("G", "U");
			
			OptionManagement.logMessage("\n" + numberGU + " x GU closure : enthalpy = " + closureGU.getEnthalpy() + "  entropy = " + closureGU.getEntropy());

			enthalpy += numberGU * closureGU.getEnthalpy();
			entropy += numberGU * closureGU.getEntropy();
			
		}
		
		result.setEnthalpy(enthalpy);
		result.setEntropy(entropy);
		
		return result;
	}
	
	@Override
	public void initialiseFileName(String methodName){
		super.initialiseFileName(methodName);
		
		if (this.fileName == null){
			this.fileName = defaultFileName;
		}
	}

	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];

		NucleotidSequences internalLoop = sequences.getEquivalentSequences("rna");

		boolean isMissingParameters = super.isMissingParameters(sequences, pos1, pos2);
		if (this.collector.getInitiationLoopValue() == null){
			OptionManagement.logWarning("\n The thermodynamic parameters for loop initiation are missing. Check the internal loop parameters.");

			return true;
		}
		
		if (internalLoop.calculateNumberOfTerminal("A", "U", pos1, pos2) > 0){
			if (this.collector.getClosureValue("A", "U") == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for AU closing are missing. Check the internal loop parameters.");

				return true;
			}
		}
		
		if (internalLoop.calculateNumberOfTerminal("G", "U", pos1, pos2) > 0){
			if (this.collector.getClosureValue("G", "U") == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for GU closing are missing. Check the internal loop parameters.");

				return true;
			}
		}
		return isMissingParameters;
	}
	
	// private method

	/**
	 * corrects the pattern positions in the duplex to have the adjacent
	 * base pair of the pattern included in the subsequence between the positions pos1 and pos2
	 * @param pos1 : starting position of the internal loop
	 * @param pos2 : ending position of the internal loop
	 * @param duplexLength : total length of the duplex
	 * @return int [] positions : new positions of the subsequence to have the pattern surrounded by the
	 * adjacent base pairs in the duplex.
	 */
	private int[] correctPositions(int pos1, int pos2, int duplexLength){
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
