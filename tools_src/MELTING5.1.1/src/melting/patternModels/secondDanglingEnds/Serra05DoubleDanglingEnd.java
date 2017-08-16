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
 * This class represents the second dangling end model ser05. It extends SecondDanglingEndMethod.
 * 
 * Martin J Serra et al. (2005). RNA 11: 512-516
 */
public class Serra05DoubleDanglingEnd extends SecondDanglingEndMethod
  implements NamedMethod
{	
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for second dangling end
	 */
	public static String defaultFileName = "Serra2005doublede.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Serra et al. (2005)";

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
		
		String sequence = sequences.getSequenceContainig("-", pos1, pos2);
		String complementary = NucleotidSequences.convertToPyr_Pur(sequences.getComplementaryTo(sequence, pos1, pos2));
		Thermodynamics doubleDanglingValue;
		String sens;
		
		if (complementary.charAt(1) == 'Y'){
			if (sequences.getSequenceSens(sequence, pos1, pos2).equals("5'3'")){
				sens = NucleotidSequences.getDanglingSens(sequence, complementary);

				doubleDanglingValue = this.collector.getSecondDanglingValue("","Y", sens);

			}
			else{
				sens = NucleotidSequences.getDanglingSens(complementary, sequence);
				doubleDanglingValue = this.collector.getSecondDanglingValue("Y","", sens);

			}
		}
		else {
			if (sequence.charAt(0) == '-'){
				complementary = complementary.substring(0, 2);
			}
			else{
				complementary = complementary.substring(1);
			}

			if (sequences.getSequenceSens(sequence, pos1, pos2).equals("5'3'")){
				sens = NucleotidSequences.getDanglingSens(sequence, complementary);

				doubleDanglingValue = this.collector.getSecondDanglingValue("",complementary, sens);

			}
			else{
				sens = NucleotidSequences.getDanglingSens(complementary, sequence);

				doubleDanglingValue = this.collector.getSecondDanglingValue(complementary,"", sens); 
			}
		}
		enthalpy += doubleDanglingValue.getEnthalpy();
		entropy += doubleDanglingValue.getEntropy();
		
		OptionManagement.logMessage("\n" + sequences.getSequence(pos1, pos2) + "/" + sequences.getComplementary(pos1, pos2) + ": incremented enthalpy = " + doubleDanglingValue.getEnthalpy() + "  incremented entropy = " + doubleDanglingValue.getEntropy());

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
		
		String sequence = sequences.getSequenceContainig("-", pos1, pos2);
		String complementary = NucleotidSequences.convertToPyr_Pur(sequences.getComplementaryTo(sequence, pos1, pos2));
		String sens;
		if (complementary.charAt(1) == 'Y'){
			if (sequences.getSequenceSens(sequence, pos1, pos2).equals("5'3'")){
				sens = NucleotidSequences.getDanglingSens(sequence, complementary);
				if (this.collector.getSecondDanglingValue("","Y", sens) == null){
					OptionManagement.logWarning("\n The thermodymamic parameters for x-Y-x/x are missing. Check the second dangling ends parameters.");
					return true;			
				}
			}
			else{
				sens = NucleotidSequences.getDanglingSens(complementary, sequence);

				if (this.collector.getSecondDanglingValue("Y","", sens) == null){
					OptionManagement.logWarning("\n The thermodymamic parameters for x-Y-x/x are missing. Check the second dangling ends parameters.");
					return true;			
				}
			}
		}
		else {
			if (sequence.charAt(0) == '-'){
				complementary = complementary.substring(0, 2);
			}
			else{
				
				complementary = complementary.substring(1);
			}
			if (sequences.getSequenceSens(sequence, pos1, pos2).equals("5'3'")){
				sens = NucleotidSequences.getDanglingSens(sequence, complementary);

				if (this.collector.getSecondDanglingValue("",complementary, sens) == null){
					OptionManagement.logWarning("\n The thermodymamic parameters for x/" + complementary + " are missing. Check the second dangling ends parameters.");

					return true;			
					}
			}
			else{
				sens = NucleotidSequences.getDanglingSens(complementary, sequence);

				if (this.collector.getSecondDanglingValue(complementary,"", sens) == null){
					OptionManagement.logWarning("\n The thermodymamic parameters for " + complementary.substring(1) + "/x are missing. Check the second dangling ends parameters.");

					return true;			
					}
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
