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

package melting.patternModels.secondDanglingEnds;

import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the second dangling end model ser06. It extends SecondDanglingEndMethod.
 * 
 * Martin J Serra et al. (2006). Nucleic Acids research 34: 3338-3344
 */
public class Serra06DoubleDanglingEnd extends SecondDanglingEndMethod
  implements NamedMethod
{	
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for second dangling end
	 */
	public static String defaultFileName = "Serra2006doublede.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Serra et al. (2006)";
	
	// PatternComputationMethod interface implementation
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		int [] positions = super.correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		NucleotidSequences newSequences = sequences.getEquivalentSequences("rna");
		
		OptionManagement.logMessage("\n The nearest neighbor model for double" +
                                " dangling end is");
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		result = super.computeThermodynamics(newSequences, pos1, pos2, result);
		
		double enthalpy = result.getEnthalpy();
		double entropy = result.getEntropy();
		
		String sequence = NucleotidSequences.convertToPyr_Pur(sequences.getSequenceContainig("-", pos1, pos2));
		String complementary = NucleotidSequences.convertToPyr_Pur(sequences.getComplementaryTo(sequences.getSequenceContainig("-", pos1, pos2), pos1, pos2));
		Thermodynamics doubleDanglingValue;

		if (sequence.charAt(0) == '-'){
			if (complementary.charAt(1) == 'Y' || (complementary.charAt(1) != 'Y' && complementary.charAt(2) == 'Y')){
				complementary = complementary.substring(1, 3);
			}
		}
		else {
			if (complementary.charAt(1) == 'Y' || (complementary.charAt(1) != 'Y' && complementary.charAt(0) == 'Y')){
				complementary = complementary.substring(0, 2);
			}
		} 
		
		if (sequences.getSequenceSens(sequences.getSequenceContainig("-", pos1, pos2), pos1, pos2).equals("5'3'")){
			doubleDanglingValue = this.collector.getDanglingValue(sequence,complementary);
		}
		else{
			doubleDanglingValue = this.collector.getDanglingValue(complementary,sequence);

		}
		
		OptionManagement.logMessage("\n" + sequences.getSequence(pos1, pos2) + "/" + sequences.getComplementary(pos1, pos2) + ": incremented enthalpy = " + doubleDanglingValue.getEnthalpy() + "  incremented entropy = " + doubleDanglingValue.getEntropy());
		
		enthalpy += doubleDanglingValue.getEnthalpy();
		entropy += doubleDanglingValue.getEntropy();
		
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
		
		String sequence = NucleotidSequences.convertToPyr_Pur(sequences.getSequenceContainig("-", pos1, pos2));
		String complementary = NucleotidSequences.convertToPyr_Pur(sequences.getComplementaryTo(sequences.getSequenceContainig("-", pos1, pos2), pos1, pos2));
		
		if (sequence.charAt(0) == '-'){

			if (complementary.charAt(1) == 'Y' || (complementary.charAt(1) != 'Y' && complementary.charAt(2) == 'Y')){
				complementary = complementary.substring(1, 3);
			}
		}
		else {
			if (complementary.charAt(1) == 'Y' || (complementary.charAt(1) != 'Y' && complementary.charAt(0) == 'Y')){
				complementary = complementary.substring(0, 2);
			}

		} 
		
		if (sequences.getSequenceSens(sequences.getSequenceContainig("-", pos1, pos2), pos1, pos2).equals("5'3'")){
			
			if (this.collector.getDanglingValue(sequence,complementary) == null){
				OptionManagement.logWarning("\n The thermodymamic parameters for " + sequence + "/" + complementary + " are missing. Check the second dangling ends parameters.");

				return true;			
			}
		}
		else{
			
			if (this.collector.getDanglingValue(complementary,sequence) == null){
				OptionManagement.logWarning("\n The thermodymamic parameters for " + complementary + "/" + sequence + " are missing. Check the second dangling ends parameters.");

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
