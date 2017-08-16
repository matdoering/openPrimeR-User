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

package melting.patternModels.tandemMismatches;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the tandem mismatches model tur06. It extends PatternComputation.
 * 
 * Douglas M Turner et al (2006). Nucleic Acids Research 34: 4912-4924.
 * 
 * Douglas M Turner et al (1999). J.Mol.Biol.  288: 911_940
 */
public class Turner99_06tanmm extends PatternComputation
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for tandem mismatches
	 */
	public static String defaultFileName = "Turner1999_2006tanmm.xml";
	
	/**
	 * String enthalpyFormula : enthalpy formula
	 */
	private static String enthalpyFormula = "delta H(5'PXYS/3'QWZT) = (H(5'PXWQ/3'QWXP) + H(5'TZYS/3'SYZT)) / 2 + penalty1( = H(GG pair adjacent to an AA or any non canonical pair with a pyrimidine) + penalty2 (= H(AG or GA pair adjacent to UC,CU or CC pairs or with a UU pair adjacent to a AA.))";

  /**
   * Full name of the method.
   */
  private static String methodName = "Turner et al. (1999, 2006)";

	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {

		if (environment.getHybridization().equals("rnarna") == false){
			
			OptionManagement.logWarning("\n The tandem mismatch parameters of " +
					"Turner (1999-2006) are originally established " +
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

		NucleotidSequences newSequences = sequences.getEquivalentSequences("rna");
		
		OptionManagement.logMessage("\n The nearest neighbor model for tandem" +
                                " mismatches is");
    OptionManagement.logMethodName(methodName);
		OptionManagement.logMessage(enthalpyFormula +
                                "(entropy formula is similar.)");
    OptionManagement.logFileName(this.fileName);

		double enthalpy = result.getEnthalpy();
		double entropy = result.getEntropy();
		StringBuffer closing = new StringBuffer();
	
		if (sequences.isSymmetric(pos1, pos2)){
			closing.append(newSequences.getSequence(pos1,pos2).charAt(0));
			closing.append("/");
			closing.append(newSequences.getComplementary(pos1,pos2).charAt(0));
			
			Thermodynamics mismatchValue = this.collector.getMismatchValue(newSequences.getSequence(pos1+1, pos2-1), newSequences.getComplementary(pos1+1,pos2-1), closing.toString());
			
			OptionManagement.logMessage("\n symmetric tandem mismatches " + sequences.getSequence(pos1, pos2) + "/" + sequences.getComplementary(pos1, pos2) + " : enthalpy = " + mismatchValue.getEnthalpy() + "  entropy = " + mismatchValue.getEntropy());
			
			enthalpy += mismatchValue.getEnthalpy();
			entropy += mismatchValue.getEntropy();
		}
		else {

			NucleotidSequences sequences1 = NucleotidSequences.buildSymetricSequences(newSequences.getSequence(pos1,pos2), newSequences.getComplementary(pos1,pos2));
			NucleotidSequences sequences2 = NucleotidSequences.buildSymetricSequences(NucleotidSequences.getInversedSequence(newSequences.getComplementary(pos1,pos2)),NucleotidSequences.getInversedSequence(newSequences.getSequence(pos1,pos2)));

			OptionManagement.logMessage("\n asymmetric tandem mismatches (other formula used): ");

			ThermoResult result1 = new ThermoResult(0,0,0);
			ThermoResult result2 = new ThermoResult(0,0,0);
			
			result1 = computeThermodynamics(sequences1, 0, sequences1.getDuplexLength() - 1, result1);
			result2 = computeThermodynamics(sequences2, 0, sequences2.getDuplexLength() - 1, result2);

			enthalpy += (result1.getEnthalpy() + result2.getEnthalpy()) / 2;
			entropy += (result1.getEntropy() + result2.getEntropy()) / 2;
			
			if (newSequences.isTandemMismatchGGPenaltyNecessary(pos1+1)){
				Thermodynamics penalty1 = this.collector.getPenalty("G/G_adjacent_AA_or_nonCanonicalPyrimidine");
				OptionManagement.logMessage("\n penalty1 : enthalpy = " + penalty1.getEnthalpy() + "  entropy = " + penalty1.getEntropy());
				enthalpy += penalty1.getEnthalpy();
				entropy += penalty1.getEntropy();
			}
			
			else if (newSequences.isTandemMismatchDeltaPPenaltyNecessary(pos1+1)){
				Thermodynamics penalty2 = this.collector.getPenalty("AG_GA_UU_adjacent_UU_CU_CC_AA");
				
				enthalpy += penalty2.getEnthalpy();
				entropy += penalty2.getEntropy();
				OptionManagement.logMessage("\n penalty2 : enthalpy = " + penalty2.getEnthalpy() + "  entropy = " + penalty2.getEntropy());
			}
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

		NucleotidSequences newSequences = sequences.getEquivalentSequences("rna");
		
		if (newSequences.isSymmetric(pos1,pos2) == false){
			
			NucleotidSequences sequences1 = NucleotidSequences.buildSymetricSequences(newSequences.getSequence(pos1,pos2), newSequences.getComplementary(pos1,pos2));
			NucleotidSequences sequences2 = NucleotidSequences.buildSymetricSequences(NucleotidSequences.getInversedSequence(newSequences.getComplementary(pos1,pos2)),NucleotidSequences.getInversedSequence(newSequences.getSequence(pos1,pos2)));
			
			if (isMissingParameters(sequences1, 0, sequences1.getDuplexLength() - 1)){
				return true;
			}
			if (isMissingParameters(sequences2, 0, sequences2.getDuplexLength() - 1)){
				return true;
			}
			if (newSequences.isTandemMismatchGGPenaltyNecessary(pos1+1)){
				if (this.collector.getPenalty("G/G_adjacent_AA_or_nonCanonicalPyrimidine") == null){
					OptionManagement.logWarning("\n The penalty for G/G adjacent to AA or a non canonical base pair with pyrimidine is missing. Check the tandem mismatch parameters.");
					return true;
				}
				
			}
			
			else if (newSequences.isTandemMismatchDeltaPPenaltyNecessary(pos1+1)){
				if (this.collector.getPenalty("AG_GA_UU_adjacent_UU_CU_CC_AA") == null){
					OptionManagement.logWarning("\n The penalty for AG, GA or UU adjacent to UU, CU, CC or AA is missing. Check the tandem mismatch parameters.");
					return true;
				}
				
			}
		}
		else{
			StringBuffer closing = new StringBuffer();
			
			closing.append(newSequences.getSequence(pos1,pos2).charAt(0));
			closing.append("/");
			closing.append(newSequences.getComplementary(pos1,pos2).charAt(0));
			if (this.collector.getMismatchValue(newSequences.getSequence(pos1+1, pos2-1), newSequences.getComplementary(pos1+1, pos2-1), closing.toString()) == null){
				OptionManagement.logWarning("The thermodynamic parameters for " + newSequences.getSequence(pos1+1, pos2-1) + "and" + newSequences.getComplementary(pos1+1, pos2-1) + " is missing. Check the tandem mismatch parameters.");
				return true;
			}
		}
		
		return super.isMissingParameters(newSequences, pos1, pos2);
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
