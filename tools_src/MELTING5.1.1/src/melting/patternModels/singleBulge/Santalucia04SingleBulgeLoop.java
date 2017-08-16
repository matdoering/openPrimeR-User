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

package melting.patternModels.singleBulge;

import java.util.HashMap;

import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.methodInterfaces.PatternComputationMethod;
import melting.patternModels.longBulge.Santalucia04LongBulgeLoop;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the single bulge loop model san04. It extends Santalucia04LongBulgeLoop.
 * 
 * Santalucia et al (2004). Annu. Rev. Biophys. Biomol. Struct 33 : 415-440
 */
public class Santalucia04SingleBulgeLoop extends Santalucia04LongBulgeLoop
  implements NamedMethod
{
	// Instance variables
	
	/**
	 * StringBuffer formulaH : the enthalpy formula
	 */
	private static StringBuffer formulaH = new StringBuffer();
	
	/**
	 * StringBuffer formulaS : the entropy formula
	 */
	private static StringBuffer formulaS = new StringBuffer();

  /**
   * Full name of the method.
   */
  private static String methodName = "Santalucia et al. (2004)";
	
	// Santalucia04SingleBulgeLoop constructor
	
	public Santalucia04SingleBulgeLoop(){

		formulaH.append(formulaEnthalpy);
		formulaS.append(formulaEntropy);

		formulaH.append(" + H(intervening NN)");
		formulaS.append(" + S(intervening NN)");
	}
	
	// PatternComputationMethod interface implementation

	@Override
	public ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result) {
		int [] positions = super.correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		NucleotidSequences newSequences = sequences.getEquivalentSequences("dna");
		
		OptionManagement.logMessage("\n The nearest neighbor model for single" +
                                " bulge loop is");
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		result = super.computeThermodynamics(newSequences, pos1, pos2, result);
		String[] NNNeighbors = newSequences.getSingleBulgeNeighbors(pos1);
		
		Thermodynamics NNValue = this.collector.getNNvalue(NNNeighbors[0], NNNeighbors[1]);
		double enthalpy = result.getEnthalpy() + NNValue.getEnthalpy();
		double entropy = result.getEntropy() + NNValue.getEntropy();
		
		OptionManagement.logMessage("\n NN intervening"+ NNNeighbors[0] + "/" + NNNeighbors[1] +" :  enthalpy = " + NNValue.getEnthalpy() + "  entropy = " + NNValue.getEntropy());

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
		
		String[] NNNeighbors = newSequences.getSingleBulgeNeighbors(pos1);

		if (this.collector.getNNvalue(NNNeighbors[0], NNNeighbors[1]) == null){
			OptionManagement.logMessage("\n The thermodynamic parameters for " + NNNeighbors[0] + "/" + NNNeighbors[1] + " are missing. Check the single bulge loop parameters.");

			return true;
		}
		return super.isMissingParameters(newSequences, pos1, pos2);
	}
	
	@Override
	public void loadData(HashMap<String, String> options) {
		super.loadData(options);
		
		String crickName = options.get(OptionManagement.NNMethod);
		RegisterMethods register = new RegisterMethods();
		PatternComputationMethod NNMethod = register.getPatternComputationMethod(OptionManagement.NNMethod, crickName);
		NNMethod.initialiseFileName(crickName);

		String NNfile = NNMethod.getDataFileName(crickName);
		
		
		loadFile(NNfile, this.collector);
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
