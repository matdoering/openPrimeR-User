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

package melting.patternModels.specificAcids;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the azobenzene (X_C or X_T) model asa05. It extends PatternComputation.
 * 
 * Asanuma et al. (2005). Nucleic acids Symposium Series 49 : 35-36
 */
public class Asanuma05Azobenzene extends PatternComputation
  implements NamedMethod
{	
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for azobenzene
	 */

  /**
   * Full name of the method.
   */
  private static String methodName = "Asanuma et al. (2005)";
	public static String defaultFileName = "Asanuma2005azobenmn.xml";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		boolean isApplicable = super.isApplicable(environment, pos1, pos2);

		int [] positions = correctPositions(pos1, pos2, environment.getSequences().getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		NucleotidSequences modified = environment.getSequences().getEquivalentSequences("dna");
		
		if (environment.getHybridization().equals("dnadna") == false) {
			OptionManagement.logWarning("\n The thermodynamic parameters for azobenzene of" +
					"Asanuma (2005) are established for DNA sequences.");
		}

		if (modified.calculateNumberOfTerminal("X", " ", pos1, pos2) > 0){
			OptionManagement.logWarning("\n The thermodynamics parameters for azobenzene of " +
					"Asanuma (2005) are not established for terminal benzenes.");
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
		
		Thermodynamics azobenzeneValue = this.collector.getAzobenzeneValue(sequences.getSequence(pos1, pos2,"dna"), sequences.getComplementary(pos1, pos2,"dna"));
		
		OptionManagement.logMessage("\n The azobenzene model is");
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		OptionManagement.logMessage(sequences.getSequence(pos1, pos2) + "/" + sequences.getComplementary(pos1, pos2) + " : enthalpy = " + azobenzeneValue.getEnthalpy() + "  entropy = " + azobenzeneValue.getEntropy());

		double enthalpy = result.getEnthalpy() + azobenzeneValue.getEnthalpy();
		double entropy = result.getEntropy() + azobenzeneValue.getEntropy();
		
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
		
		if (this.collector.getAzobenzeneValue(newSequences.getSequence(pos1,pos2),newSequences.getComplementary(pos1,pos2)) == null){
			OptionManagement.logWarning("\n The thermodynamic parameters for " + newSequences.getSequence(pos1,pos2) + "/" + newSequences.getComplementary(pos1,pos2)+ 
			" are missing. Check the azobenzene parameters.");
			return true;
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
