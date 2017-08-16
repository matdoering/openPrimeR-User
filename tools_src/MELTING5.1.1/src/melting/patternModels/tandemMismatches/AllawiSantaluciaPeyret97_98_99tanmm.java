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

import java.util.HashMap;

import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.methodInterfaces.PatternComputationMethod;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the tandem mismatches model allsanpey. It extends PatternComputation.
 * 
 * Allawi and SantaLucia (1997). Biochemistry 36: 10581-10594. 
 * 
 * Allawi and SantaLucia (1998). Biochemistry 37: 2170-2179.
 * 
 * Allawi and SantaLucia (1998). Nuc Acids Res 26: 2694-2701. 
 * 
 * Allawi and SantaLucia (1998). Biochemistry 37: 9435-9444.
 * 
 * Peyret et al. (1999). Biochemistry 38: 3468-3477
 */
public class AllawiSantaluciaPeyret97_98_99tanmm extends PatternComputation
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for tandem mismatches
	 */
	public static String defaultFileName = "AllawiSantaluciaPeyret1997_1998_1999tanmm.xml";
  
  /**
   * Full name of the method.
   */
  private static String methodName = "Allawi, Santalucia and Peyret" +
                                     "(1997, 1998, 1999)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		
		if (environment.getHybridization().equals("dnadna") == false){
			OptionManagement.logWarning("\n The tandem mismatch parameters of " +
					"Allawi, Santalucia and Peyret are originally established " +
					"for DNA duplexes.");
		}
		return super.isApplicable(environment, pos1, pos2);
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		OptionManagement.logMessage("\n The nearest neighbor model for tandem" +
                                " mismatches is");
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		NucleotidSequences newSequences = sequences.getEquivalentSequences("dna");
		
		double enthalpy = result.getEnthalpy();
		double entropy = result.getEntropy();
		Thermodynamics mismatchValue;
		for (int i = pos1; i < pos2; i++){
			mismatchValue = this.collector.getMismatchValue(newSequences.getSequenceNNPair(i), newSequences.getComplementaryNNPair(i));
			
			OptionManagement.logMessage(newSequences.getSequenceNNPair(i) + "/" + newSequences.getComplementaryNNPair(i) + " : enthalpy = " + mismatchValue.getEnthalpy() + "  entropy = " + mismatchValue);

			enthalpy += mismatchValue.getEnthalpy();
			entropy += mismatchValue.getEntropy();
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

		NucleotidSequences newSequences = sequences.getEquivalentSequences("dna");
		
		for (int i = pos1; i < pos2; i++){
			if (this.collector.getMismatchValue(newSequences.getSequenceNNPair(i), newSequences.getComplementaryNNPair(i)) == null){
				OptionManagement.logWarning("\n The thermodynamic parameter for " + newSequences.getSequenceNNPair(i) + "/" + newSequences.getComplementaryNNPair(i) + " are missing. Check the parameters for tandem mismatches.");
				return true;
			}
		}
		return super.isMissingParameters(newSequences, pos1, pos2);
	}
	
	@Override
	public void loadData(HashMap<String, String> options) {
		super.loadData(options);
		
		String singleMismatchName = options.get(OptionManagement.singleMismatchMethod);
		RegisterMethods register = new RegisterMethods();
		PatternComputationMethod singleMismatch = register.getPatternComputationMethod(OptionManagement.singleMismatchMethod, singleMismatchName);
		singleMismatch.initialiseFileName(singleMismatchName);
		String fileSingleMismatch = singleMismatch.getDataFileName(singleMismatchName);
		
		loadFile(fileSingleMismatch, this.collector);
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
