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

/**
 * This class represents the single mismatch model from Znosco et al, 2007, 2008. It extends PatternComputation.
 */
public abstract class ZnoskoMethod extends PatternComputation{
	
	// Instance variables
	
	/**
	 * String formulaEnthalpy : enthalpy formula
	 */
	protected static String formulaEnthalpy = "delat H = H(single mismatch N/N) + number AU closing x H(closing AU) + number GU closing x H(closing GU) + H(NNN intervening)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		if (environment.getHybridization().equals("rnarna") == false){

			OptionManagement.logWarning("\n The single mismatches parameter of " +
					"Znosco et al. are originally established " +
					"for RNA duplexes.");
		}
		
		return super.isApplicable(environment, pos1, pos2);
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		Thermodynamics mismatchValue = this.collector.getMismatchParameterValue(sequences.getSequence(pos1 + 1, pos1 + 1), sequences.getComplementary(pos1 + 1, pos1 + 1));
		if (mismatchValue == null){
			mismatchValue = new Thermodynamics(0,0);
		}
		Thermodynamics NNNeighboringValue = this.collector.getMismatchValue(NucleotidSequences.convertToPyr_Pur(sequences.getSequence(pos1, pos2)), NucleotidSequences.convertToPyr_Pur( sequences.getComplementary(pos1, pos2)));
		if (NNNeighboringValue == null){
			NNNeighboringValue = new Thermodynamics(0,0);
		}
		OptionManagement.logMessage("\n N/N mismatch " + sequences.getSequence(pos1 + 1, pos1 + 1) + "/" + sequences.getComplementary(pos1 + 1, pos1 + 1) + " : enthalpy = " + mismatchValue.getEnthalpy() + "  entropy = " + mismatchValue.getEntropy());
		OptionManagement.logMessage("\n NNN intervening  " + NucleotidSequences.convertToPyr_Pur(sequences.getSequence(pos1, pos2)) + "/" + NucleotidSequences.convertToPyr_Pur(sequences.getComplementary(pos1, pos2)) + " : enthalpy = " + NNNeighboringValue.getEnthalpy() + "  entropy = " + NNNeighboringValue.getEntropy());
		
		double enthalpy = result.getEnthalpy() + mismatchValue.getEnthalpy() + NNNeighboringValue.getEnthalpy();
		double entropy = result.getEntropy() + mismatchValue.getEntropy() + NNNeighboringValue.getEntropy();
		
		NucleotidSequences mismatch = sequences.getEquivalentSequences("rna");
		
		double numberAU = mismatch.calculateNumberOfTerminal("A", "U", pos1, pos2);
		double numberGU = mismatch.calculateNumberOfTerminal("G", "U", pos1, pos2);
		
		if (numberAU > 0){
			Thermodynamics closingAU = this.collector.getClosureValue("A", "U");
			
			OptionManagement.logMessage("\n" + numberAU + " x AU closing : enthalpy = " + closingAU.getEnthalpy() + "  entropy = " + closingAU.getEntropy());

			enthalpy += numberAU * closingAU.getEnthalpy();
			entropy += numberAU * closingAU.getEntropy();
		}
		if (numberGU > 0){
			Thermodynamics closingGU = this.collector.getClosureValue("G", "U");

			OptionManagement.logMessage("\n" + numberGU + " x GU closing : enthalpy = " + closingGU.getEnthalpy() + "  entropy = " + closingGU.getEntropy());

			enthalpy += numberAU * closingGU.getEnthalpy();
			entropy += numberAU * closingGU.getEntropy();
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
		
			if (mismatch.calculateNumberOfTerminal("A", "U", pos1, pos2) > 0){
				if (this.collector.getClosureValue("A", "U") == null){
				OptionManagement.logWarning("\n The parameters for AU closing base pair are missing. Check the single mismatch parameters.");

					return true;
				}
			}
			
			if (mismatch.calculateNumberOfTerminal("G", "U", pos1, pos2) > 0){
				if (this.collector.getClosureValue("G", "U") == null){
					OptionManagement.logWarning("\n The parameters for GU closing base pair are missing. Check the single mismatch parameters.");

					return true;
				}
			}
		return super.isMissingParameters(mismatch, pos1, pos2);
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
