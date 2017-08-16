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

package melting.patternModels.cricksPair;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.exceptions.MethodNotApplicableException;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the nearest neighbor model sug95. It extends CricksNNMethod.
 * 
 * Sugimoto et al. (1995). Biochemistry 34 : 11211-11216
 */
public class Sugimoto95 extends CricksNNMethod
  implements NamedMethod
{
		
	// Instance variables
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for each Crick's pair
	 */
	public static String defaultFileName = "Sugimoto1995nn.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Sugimoto et al. (1995)";

	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1, int pos2) {
		boolean isApplicable = true;
		if (environment.getHybridization().equals("dnarna") == false && environment.getHybridization().equals("rnadna") == false){
			isApplicable = false;
			OptionManagement.logWarning("\n The model of Sugimoto et al. (1995)" +
					"is established for hybrid DNA/RNA sequences.");
		}
		
		isApplicable = super.isApplicable(environment, pos1, pos2);
		
		if (environment.isSelfComplementarity()){
			throw new MethodNotApplicableException ( "\n The thermodynamic parameters of Sugimoto et al. (1995)" +
					"are established for hybrid DNA/RNA sequences and they can't be self complementary sequence.");
		}
		return isApplicable;
	}
	
	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);
		
		double enthalpy = result.getEnthalpy();
		double entropy = result.getEntropy();
		
		Thermodynamics NNValue;
		for (int i = pos1; i <= pos2 - 1; i++){
			NNValue = this.collector.getNNvalue("d" + sequences.getSequenceNNPair(i), "r" + sequences.getComplementaryNNPair(i));
			
			OptionManagement.logMessage("d"+ sequences.getSequenceNNPair(i) + "/" + "r" + sequences.getComplementaryNNPair(i) + " : enthalpy = " + NNValue.getEnthalpy() + "  entropy = " + NNValue.getEntropy());

			enthalpy += NNValue.getEnthalpy();
			entropy += NNValue.getEntropy();
		}
		
		result.setEnthalpy(enthalpy);
		result.setEntropy(entropy);
		
		return result;
	}

	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		boolean isMissing = false;
		for (int i = pos1; i <= pos2 - 1; i++){

			if (this.collector.getNNvalue("d" + sequences.getSequenceNNPair(i), "r" + sequences.getComplementaryNNPair(i)) == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for d" + sequences.getSequenceNNPair(i) + "/r" + sequences.getComplementaryNNPair(i) + "are missing.");
				isMissing = true;
			}
		}
		return isMissing;
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
