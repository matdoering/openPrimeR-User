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

package melting.patternModels.longDanglingEnds;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;

/**
 * This class represents the long dangling end model (for long poly A only)from Sugimoto et al. 2002. 
 * It extends PatternComputation.
 * 
 * Sugimoto et al. (2002). J. Am. Chem. Soc. 124: 10367-10372
 */
public abstract class SugimotoLongDanglingEndMethod extends PatternComputation {

	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		boolean isApplicable = super.isApplicable(environment, pos1, pos2);
		
		if (environment.isSelfComplementarity() == false){
			OptionManagement.logWarning("\n The Thermodynamic parameters for long dangling end of Sugimoto et al." +
					"(2002) is only established for self-complementary sequences.");
		}
		for (int i = pos1; i <= pos2; i++){
			if (environment.getSequences().getDuplex().get(i).isBasePairEqualTo("A", "-") == false){
				isApplicable = false;
				OptionManagement.logWarning("\n The thermodynamic parameters for long dangling end of Sugimoto et al." +
				"(2002) is only established for sequences with poly-A dangling ends.");
			}
		}
		return isApplicable;
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		
		OptionManagement.logMessage("\n The long dangling end model is from Sugimoto et al. (2002). (delta delta H and delta delta S)");
		OptionManagement.logMessage("\n File name : " + this.fileName);

		Thermodynamics longDangling = this.collector.getDanglingValue(sequences.getSequence(pos1, pos2), sequences.getComplementary(pos1, pos2));
		double enthalpy = result.getEnthalpy() + longDangling.getEnthalpy();
		double entropy = result.getEntropy() + longDangling.getEntropy();
		
		OptionManagement.logMessage(sequences.getSequence(pos1, pos2) + "/" + sequences.getComplementary(pos1, pos2) + " : enthalpy = " + longDangling.getEnthalpy() + "  entropy = " + longDangling.getEntropy());

		result.setEnthalpy(enthalpy);
		result.setEntropy(entropy);
		
		return result;
	}
	
	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		
		if (this.collector.getDanglingValue(sequences.getSequence(pos1, pos2), sequences.getComplementary(pos1, pos2)) == null){
			OptionManagement.logWarning("\n The thermodynamic parameters for " + sequences.getSequence(pos1, pos2) + "/" + sequences.getComplementary(pos1, pos2) + " are missing. Check the long dangling ends parameters.");
			return true;
		}
		return super.isMissingParameters(sequences, pos1, pos2);
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
}
