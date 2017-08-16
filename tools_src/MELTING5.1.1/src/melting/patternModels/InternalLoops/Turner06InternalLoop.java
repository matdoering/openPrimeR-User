
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

package melting.patternModels.InternalLoops;


import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the internal loop model tur06. It extends PatternComputation.
 * 
 * Douglas M Turner et al (2006). Nucleic Acids Research 34: 4912-4924.
 */
public class Turner06InternalLoop extends PatternComputation
  implements NamedMethod
{
	// Instance variable
	
	/**
	 * String defaultFileName : default name for the xml file containing the thermodynamic parameters for internal loop
	 */
	public static String defaultFileName = "Turner1999_2006longmm.xml";
	
	/**
	 * String formulaEnthalpy : enthalpy formula
	 */
	private static String formulaEnthalpy = "delat H = [H(first mismath) if loop length of 1 x n, n <= 2 or 2 x n, n != 2] + H(initiation loop of n) + (n1 - n2) x H(asymmetric loop) + number AU closing x H(closing AU) + number GU closing x H(closing GU)";

  /**
   * Full name of the method.
   */
  private static String methodName = "Turner et al. (2006)";
	
	// PatternComputationMethod interface implementation

	@Override
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		boolean isApplicable = super.isApplicable(environment, pos1, pos2);

		int [] positions = correctPositions(pos1, pos2, environment.getSequences().getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];
		
		String loopType = environment.getSequences().getInternalLoopType(pos1,pos2);

		if (environment.getHybridization().equals("rnarna") == false){
			OptionManagement.logWarning(" \n The internal loop parameters of " +
					"Turner et al. (2006) are originally established " +
					"for RNA sequences.");
		}
				
		if (loopType.charAt(0) == '3' && loopType.charAt(2) == '3' && environment.getSequences().getDuplex().get(pos1 + 2).isBasePairEqualTo("A", "G")){
			OptionManagement.logWarning(" \n The thermodynamic parameters of Turner (2006) excluded" +
					"3 x 3 internal loops with a middle GA pair. The middle GA pair is shown to enhance " +
					"stability and this extra stability cannot be predicted by this nearest neighbor" +
					"parameter set.");
			
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

		NucleotidSequences internalLoop = sequences.getEquivalentSequences("rna");
		
		OptionManagement.logMessage("\n The internal loop model is");
    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

		double saltIndependentEntropy = result.getSaltIndependentEntropy();
		double enthalpy = result.getEnthalpy();
		double entropy = result.getEntropy();
		boolean needFirstMismatchEnergy = true;

		String loopType = sequences.getInternalLoopType(pos1, pos2);

		String [] mismatch =  internalLoop.getLoopFistMismatch(pos1);
		
		double numberAU = internalLoop.calculateNumberOfTerminal("A", "U", pos1, pos2);
		double numberGU = internalLoop.calculateNumberOfTerminal("G", "U", pos1, pos2);
		
		if (loopType.charAt(0) == '1' && Integer.parseInt(loopType.substring(2, 3)) > 2){
			
			needFirstMismatchEnergy = false;
		}
		
		int loopLength = sequences.computesInternalLoopLength(pos1, pos2);
		Thermodynamics initiationLoop = this.collector.getInitiationLoopValue(Integer.toString(loopLength));
		if (initiationLoop != null){
			OptionManagement.logMessage("\n" + loopType + "Internal loop :  enthalpy = " + initiationLoop.getEnthalpy() + "  entropy = " + initiationLoop.getEntropy());

			enthalpy += initiationLoop.getEnthalpy();
			if (loopLength > 4){
				saltIndependentEntropy += initiationLoop.getEntropy();
			}
			else {
				entropy += initiationLoop.getEntropy();
			}
		}
		else {
			initiationLoop = this.collector.getInitiationLoopValue(">6");
			OptionManagement.logMessage("\n " + loopType + "Internal loop :  enthalpy = " + initiationLoop.getEnthalpy() + "  entropy = " + initiationLoop.getEntropy() + " - (1.08 x ln(loopLength / 6)) / 310.15");

			enthalpy += initiationLoop.getEnthalpy();

			if (loopLength > 4){
				saltIndependentEntropy += initiationLoop.getEntropy() - (1.08 * Math.log(loopLength / 6.0)) / 310.15;
			}
			else {
				entropy += initiationLoop.getEntropy() - (1.08 * Math.log(loopLength / 6.0)) / 310.15;
			}
		}
		
		
		if (numberAU > 0){

			Thermodynamics closureAU = this.collector.getClosureValue("A", "U");
			
			OptionManagement.logMessage("\n" + numberAU + " x AU closure : enthalpy = " + closureAU.getEnthalpy() + "  entropy = " + closureAU.getEntropy());

			enthalpy += numberAU * closureAU.getEnthalpy();
			entropy += numberAU * closureAU.getEntropy();
			
		}
		
		if (numberGU > 0){
			
			Thermodynamics closureGU = this.collector.getClosureValue("G", "U");
			
			OptionManagement.logMessage("\n" + numberGU + " x GU closure : enthalpy = " + closureGU.getEnthalpy() + "  entropy = " + closureGU.getEntropy());

			enthalpy += numberGU *  closureGU.getEnthalpy();
			entropy += numberGU * closureGU.getEntropy();
		}
		if (sequences.isAsymetricInternalLoop(pos1, pos2)){
			
			Thermodynamics asymmetry = this.collector.getAsymmetry();
			int asymetricValue = Math.abs(Integer.parseInt(loopType.substring(0, 1)) - Integer.parseInt(loopType.substring(2, 3)));
			OptionManagement.logMessage("\n" + asymetricValue + " x asymmetry : enthalpy = " + asymmetry.getEnthalpy() + "  entropy = " + asymmetry.getEntropy());
			
			enthalpy += asymetricValue * asymmetry.getEnthalpy();
			
			if (loopLength > 4){
				saltIndependentEntropy += asymetricValue * asymmetry.getEntropy();
			}
			else {
				entropy += asymetricValue * asymmetry.getEntropy();
			}
		}
		
		if (needFirstMismatchEnergy == true){

			Thermodynamics firstMismatch;
			if ((mismatch[0].charAt(1) == 'G' && mismatch[1].charAt(1) == 'G') || (mismatch[0].charAt(1) == 'U' && mismatch[1].charAt(1) == 'U')){	
				if (this.collector.getFirstMismatch(mismatch[0].substring(1, 2), mismatch[1].substring(1, 2), loopType) == null){
					firstMismatch = new Thermodynamics(0.0,0.0);
				}
				else{
					firstMismatch = this.collector.getFirstMismatch(mismatch[0].substring(1, 2), mismatch[1].substring(1, 2), loopType);
				}
				OptionManagement.logMessage("\n First mismatch : " + mismatch[0].substring(1, 2) + "/" + mismatch[1].substring(1, 2) + " : enthalpy = " + firstMismatch.getEnthalpy() + "  entropy = " + firstMismatch.getEntropy());
			}
			else {
				if (this.collector.getFirstMismatch(mismatch[0], mismatch[1], loopType) == null){
					firstMismatch = new Thermodynamics(0.0,0.0);
				}
				else{
					firstMismatch = this.collector.getFirstMismatch(mismatch[0], mismatch[1], loopType);
				}

				OptionManagement.logMessage("\n First mismatch : " + mismatch[0] + "/" + mismatch[1] + " : enthalpy = " + firstMismatch.getEnthalpy() + "  entropy = " + firstMismatch.getEntropy());
			}
			enthalpy += firstMismatch.getEnthalpy();
			entropy += firstMismatch.getEntropy();
			
		}
		result.setEnthalpy(enthalpy);
		result.setEntropy(entropy);
		result.setSaltIndependentEntropy(saltIndependentEntropy);
		
		return result;
	}

	

	@Override
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		int [] positions = correctPositions(pos1, pos2, sequences.getDuplexLength());
		pos1 = positions[0];
		pos2 = positions[1];

		NucleotidSequences newSequences = sequences.getEquivalentSequences("rna");
		
		boolean isMissingParameters = super.isMissingParameters(newSequences, pos1, pos2);
		if (this.collector.getInitiationLoopValue(Integer.toString(sequences.computesInternalLoopLength(pos1,pos2))) == null){
			if (this.collector.getInitiationLoopValue("6") == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for internal loop of 6 are missing. Check the internal loop parameters.");

				return true;
			}
		}
		
		if (newSequences.calculateNumberOfTerminal("A", "U", pos1, pos2) > 0){
			if (this.collector.getClosureValue("A", "U") == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for AU closing are missing. Check the internal loop parameters.");

				return true;
			}
		}
		
		if (newSequences.calculateNumberOfTerminal("G", "U", pos1, pos2) > 0){
			if (this.collector.getClosureValue("G", "U") == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for GU closing are missing. Check the internal loop parameters.");

				return true;
			}
		}
		
		if (sequences.isAsymetricInternalLoop(pos1, pos2)){
			if (this.collector.getAsymmetry() == null){
				OptionManagement.logWarning("\n The thermodynamic parameters for asymetric loop are missing. Check the internal loop parameters.");

				return true;
			}
		}

		return isMissingParameters;
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
