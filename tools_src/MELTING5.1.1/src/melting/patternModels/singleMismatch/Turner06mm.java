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

package melting.patternModels.singleMismatch;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the single mismatch model tur06. It extends PatternComputation.
 * 
 * Douglas M Turner et al (2006). Nucleic Acids Research 34: 4912-4924.
 */
public class Turner06mm extends PatternComputation
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for single mismatch
	 */
	public static String defaultFileName = "Turner1999_2006longmm.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Turner et al. (2006)";
	
	/**
	 * String formulaEnthalpy : enthalpy formula
	 */
	private static String formulaEnthalpy = "delat H = H(loop initiation n=2) + number AU closing x H(closing AU) + number GU closing x H(closing GU) + H(bonus if GG mismatch) + H(bonus if 5'RU/3'YU)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {

		if (environment.getHybridization().equals("rnarna") == false){

			OptionManagement.logWarning("\n The single mismatches parameter of " +
					"Turner et al. (2006) are originally established " +
					"for RNA sequences.");
		}
		
		return super.isApplicable(environment, pos1, pos2);
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		NucleotidSequences mismatch = sequences.getEquivalentSequences("rna");
		
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(formulaEnthalpy +
                                " (entropy formula is similar)");
    OptionManagement.logFileName(this.fileName);

		Thermodynamics initiationValue = this.collector.getInitiationLoopValue("2");
		double enthalpy = result.getEnthalpy() + initiationValue.getEnthalpy();
		double entropy = result.getEntropy() + initiationValue.getEntropy();
		double numberAU = mismatch.calculateNumberOfTerminal("A", "U", pos1, pos2);
		double numberGU = mismatch.calculateNumberOfTerminal("G", "U", pos1, pos2);
		String [] mismatchAcid = mismatch.getLoopFistMismatch(pos1);
		
		OptionManagement.logMessage("\n initiation loop of 2 : enthalpy = " + initiationValue.getEnthalpy() + "  entropy = " + initiationValue.getEntropy());
		
		if (numberAU > 0){
			
			Thermodynamics closingAU = this.collector.getClosureValue("A", "U");
			
			OptionManagement.logMessage("\n" + numberAU + " x closing AU : enthalpy = " + closingAU.getEnthalpy() + "  entropy = " + closingAU.getEntropy());

			enthalpy += numberAU * closingAU.getEnthalpy();
			entropy += numberAU * closingAU.getEntropy();
		}
		
		if (numberGU > 0){
			
			Thermodynamics closingGU = this.collector.getClosureValue("G", "U");
			
			OptionManagement.logMessage("\n" + numberGU + " x closing GU : enthalpy = " + closingGU.getEnthalpy() + "  entropy = " + closingGU.getEntropy());

			enthalpy += numberGU * closingGU.getEnthalpy();
			entropy += numberGU * closingGU.getEntropy();
		}
		
		if (sequences.getDuplex().get(pos1 + 1).isBasePairEqualTo("G", "G")){
			Thermodynamics GGMismatch = this.collector.getFirstMismatch("G", "G", "1x1");
			
			OptionManagement.logMessage("\n GG mismatch bonus : enthalpy = " + GGMismatch.getEnthalpy() + "  entropy = " + GGMismatch.getEntropy());

			enthalpy += GGMismatch.getEnthalpy();
			entropy += GGMismatch.getEntropy();
		}
		
		else if (mismatchAcid[0].equals("RU") && mismatchAcid[1].equals("YU")){
			Thermodynamics RUMismatch = this.collector.getFirstMismatch("RU", "YU", "1x1");
			
			OptionManagement.logMessage("\n RU mismatch bonus : enthalpy = " + RUMismatch.getEnthalpy() + "  entropy = " + RUMismatch.getEntropy());
			
			enthalpy += RUMismatch.getEnthalpy();
			entropy += RUMismatch.getEntropy();
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
		
		NucleotidSequences mismatch = sequences.getEquivalentSequences("rna");
		
		double numberAU = mismatch.calculateNumberOfTerminal("A", "U", pos1, pos2);
		double numberGU = mismatch.calculateNumberOfTerminal("G", "U", pos1, pos2);
		String [] mismatchAcid = mismatch.getLoopFistMismatch(pos1);
		
		if (this.collector.getInitiationLoopValue("2") == null){
			OptionManagement.logWarning("\n The initiation parameters for a loop of 2 are missing. Check the single mismatch parameters.");
			return true;
		}
		if (numberAU > 0){
			if (this.collector.getClosureValue("A", "U") == null){
				OptionManagement.logWarning("\n The parameters for AU closing base pair are missing. Check the single mismatch parameters.");
				return true;
			}
		}
		
		if (numberGU > 0){
			if (this.collector.getClosureValue("G", "U") == null){
				OptionManagement.logWarning("\n The parameters for GU closing base pair are missing. Check the single mismatch parameters.");
				return true;
			}
		}
		
		if (sequences.getDuplex().get(pos1 + 1).isBasePairEqualTo("G", "G")){
			if (this.collector.getFirstMismatch("G", "G", "1x1") == null){
				OptionManagement.logWarning("\n The bonus parameters for GG mismatch are missing. Check the single mismatch parameters.");
				return true;
			}
		}
		
		else if (mismatchAcid[0].equals("RU") && mismatchAcid[1].equals("YU")){
			if (this.collector.getFirstMismatch("RU", "YU", "1x1") == null){
				OptionManagement.logWarning("\n The bonus parameters for RU/YU mismatch are missing. Check the single mismatch parameters.");
				return true;
			}
		}
		return super.isMissingParameters(mismatch, pos1, pos2);
	}
	
	@Override
	public void initialiseFileName(String methodName){
		super.initialiseFileName(methodName);
		
		if (this.fileName == null){
			this.fileName = defaultFileName;
		}
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
