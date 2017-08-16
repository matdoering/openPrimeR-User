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

package melting.nearestNeighborModel;

import melting.Environment;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.exceptions.MethodNotApplicableException;
import melting.exceptions.NoExistingMethodException;
import melting.exceptions.SequenceException;
import melting.methodInterfaces.CorrectionMethod;
import melting.methodInterfaces.MeltingComputationMethod;
import melting.methodInterfaces.PatternComputationMethod;
import melting.patternModels.cricksPair.CricksNNMethod;
import melting.sequences.SpecificAcidNames;

import java.util.HashMap;

/**
 * This class represents the nearest neighbor model. It implements the MelitngComputationMethod Interface
 */
public class NearestNeighborMode implements MeltingComputationMethod{

	// Instance variables
	
	/**
	 * environment containing the sequences, ion and agent concentrations and the options (default options and
	 * options entered by the user)
	 */
	private Environment environment;
	
	/**
	 * RegisterMethods register : registers all the pattern computation methods implemented by Melting
	 */
	private RegisterMethods register = new RegisterMethods();
	
	/**
	 * PatternComputationMethod azobenzeneMethod : represents the model for azobenzene
	 */
	private PatternComputationMethod azobenzeneMethod;
	
	/**
	 * PatternComputationMethod CNGRepeatsMethod : represents the model for CNG repeats
	 */
	private PatternComputationMethod CNGRepeatsMethod;
	
	/**
	 * PatternComputationMethod doubleDanglingEndMethod : represents the model for double dangling end
	 */
	private PatternComputationMethod doubleDanglingEndMethod;
	
	/**
	 * PatternComputationMethod hydroxyadenosineMethod : represents the model for hydroxyadenine
	 */
	private PatternComputationMethod hydroxyadenosineMethod;
	
	/**
	 * PatternComputationMethod inosineMethod : represents the model for inosine
	 */
	private PatternComputationMethod inosineMethod;
	
	/**
	 * PatternComputationMethod internalLoopMethod : represents the model for internal loop
	 */
	private PatternComputationMethod internalLoopMethod;
	
	/**
	 * PatternComputationMethod lockedAcidMethod : represents the model for locked acid nucleic
	 */
	private PatternComputationMethod lockedAcidMethod;
	
	/**
	 * PatternComputationMethod longBulgeLoopMethod : represents the model for long bulge loop 
	 */
	private PatternComputationMethod longBulgeLoopMethod;
	
	/**
	 * PatternComputationMethod longDanglingEndMethod : represents the model for long dangling end 
	 */
	private PatternComputationMethod longDanglingEndMethod;
	
	/**
	 * PatternComputationMethod cricksMethod : represents the model for crick's pair 
	 */
	private PatternComputationMethod cricksMethod;
	
	/**
	 * PatternComputationMethod singleBulgeLoopMethod : represents the model for single bulge loop 
	 */
	private PatternComputationMethod singleBulgeLoopMethod;
	
	/**
	 * PatternComputationMethod singleDanglingEndMethod : represents the model for single dangling end
	 */
	private PatternComputationMethod singleDanglingEndMethod;
	
	/**
	 * PatternComputationMethod singleMismatchMethod : represents the model for single mismatch
	 */
	private PatternComputationMethod singleMismatchMethod;
	
	/**
	 * PatternComputationMethod tandemMismatchMethod : represents the model for tandem mismatches
	 */
	private PatternComputationMethod tandemMismatchMethod;
	
	/**
	 * PatternComputationMethod wobbleMethod : represents the model for GU base pair
	 */
	private PatternComputationMethod wobbleMethod;
	
	// MeltingComputationMethod interface implementation
	
	public ThermoResult computesThermodynamics() {
		OptionManagement.logMessage("\n Nearest-Neighbor method :");

		analyzeSequence();
		int pos1 = 0; 
		int pos2 = 0;
		
		// computes the enthalpy and entropy when Na = 1M

		while (pos2 < this.environment.getSequences().getDuplexLength() - 1){
			int [] positions = getPositionsPattern(pos1);

			pos1 = positions[0];
			pos2 = positions[1];
			PatternComputationMethod currentCalculMethod = getAppropriatePatternModel(positions);
			if (currentCalculMethod == null){
				throw new NoExistingMethodException("\n There is no implemented method to compute the enthalpy and entropy of this segment " + environment.getSequences().getSequence(pos1, pos2) + "/" + environment.getSequences().getComplementary(pos1, pos2));
			}
			ThermoResult newResult = currentCalculMethod.computeThermodynamics(this.environment.getSequences(), pos1, pos2, this.environment.getResult());
			this.environment.setResult(newResult);
			
				pos1 = pos2 + 1;
		}
		double Tm = 0.0;
		boolean isASaltCorrectionNecessary = true;
		
		// computes the initiation enthalpy and entropy (there is no initiation enthalpy and entropy if the sequence is composed of CNG repeats)
		// computes the Tm(Na = 1M)
		
		if (this.CNGRepeatsMethod == null){
			if (this.cricksMethod == null){
				this.cricksMethod = initialiseMethod(OptionManagement.NNMethod, this.environment.getOptions().get(OptionManagement.NNMethod));
			}
			CricksNNMethod initiationMethod = (CricksNNMethod)this.cricksMethod;
			ThermoResult resultinitiation = initiationMethod.computesHybridizationInitiation(this.environment);

			this.environment.setResult(resultinitiation);
			Tm = computesMeltingTemperature(this.environment);
		}
		else {
			int CNGRepeats = (this.environment.getSequences().getDuplexLength() - 2) / 3;
			if (CNGRepeats > 4){
				Tm = computesHairpinTemperature(this.environment);
				
				isASaltCorrectionNecessary = false;
			}
			else{
				Tm = computesMeltingTemperature(this.environment);

			}
		}
		this.environment.setResult(Tm);
		
		// if a salt correction is necessary, a temperature correction is applied
		
		if (isASaltCorrectionNecessary){
			CorrectionMethod saltCorrection = register.getIonCorrectionMethod(this.environment);
			
			if (saltCorrection == null){
				throw new NoExistingMethodException("\n There is no implemented ion correction method.");
			}
			this.environment.setResult(saltCorrection.correctMeltingResults(this.environment));

			if (environment.getResult().getSaltIndependentEntropy() != 0){
				double TmInverse = 1 / (this.environment.getResult().getTm() + 273.15) + this.environment.getResult().getSaltIndependentEntropy() / this.environment.getResult().getEnthalpy();
				this.environment.setResult(1 / TmInverse - 273.15);
			}
		}
		return this.environment.getResult();
	}

	public boolean isApplicable() {
		boolean isApplicable = true;
		if (Integer.parseInt(this.environment.getOptions().get(OptionManagement.threshold)) <= this.environment.getSequences().getDuplexLength()){
			OptionManagement.logWarning("\n The Nearest Neighbor model is accurate for " +
			"shorter sequences. (length superior to 6 and inferior to" +
			 this.environment.getOptions().get(OptionManagement.threshold) +")");
			
			if (this.environment.getOptions().get(OptionManagement.globalMethod).equals("def")){
				isApplicable = false;
			}
		}
		if (this.environment.getOptions().get(OptionManagement.selfComplementarity).equals("true") && Integer.parseInt(this.environment.getOptions().get(OptionManagement.factor)) != 1){
			OptionManagement.logWarning("\n When the oligonucleotides are self-complementary, the correction factor F must be equal to 1.");
			isApplicable = false;
		}
		
		return isApplicable;
	}

	public void setUpVariables(HashMap<String, String> options) {
		this.environment = new Environment(options);
		
		}
	
	public RegisterMethods getRegister() {
		return register;
	}
	
	// private methods
	
	/**
	 * This method is called to determine the terminal position of a pattern (perfectly matching sequences or not perfectly matching sequences)
	 * from the position pos1 in the duplex
	 * @param pos1 : starting position of a pattern in the duplex.
	 * @return int [] which contains the positions of the pattern in the duplex. ('pos1' is the starting position of the pattern in the duplex)
	 */
	private int [] getPositionsPattern(int pos1){
		int position = pos1;
		if (pos1 == 0){
			if(environment.getSequences().isCNGPattern(0, this.environment.getSequences().getSequence().length() - 1) && this.environment.isSelfComplementarity()){			
				int [] positions = {0, this.environment.getSequences().getDuplexLength() - 1};
				return positions;
			}
		}
			
		if (environment.getSequences().getDuplex().get(pos1).isComplementaryBasePair()){
			while (position < environment.getSequences().getDuplexLength() - 1){
				int testPosition = position + 1;
				if (environment.getSequences().getDuplex().get(testPosition).isComplementaryBasePair()){
					position ++;
				}
				else{
					break;
					}
				}
			int [] positions = {pos1, position};
			return positions;
		}
			
		else {
			if (environment.getSequences().getDuplex().get(pos1).isBasePairEqualTo("G", "U")){
				while (position < environment.getSequences().getDuplexLength() - 1){
					int testPosition = position + 1;
					if (environment.getSequences().getDuplex().get(testPosition).isBasePairEqualTo("G", "U")){
						position ++;
					}
					else{
						break;
					}
				}
				
				int [] positions = {pos1, position};
				return positions;
			}
			else {
				while (position < environment.getSequences().getDuplexLength() - 1){
					int testPosition = position + 1;
					if (environment.getSequences().getDuplex().get(testPosition).isBasePairEqualTo("G", "U") == false && environment.getSequences().getDuplex().get(testPosition).isComplementaryBasePair() == false){
						position ++;
						
					}
					else{
						break;
					}
				}
					int [] positions = {pos1, position};
					return positions;
				}
			}
	}
	
	/**
	 * defines which method is appropriate to compute the energy of the pattern at the positions 'positions' in the duplex. 
	 * @param positions : contains the positions of a pattern in the duplex
	 * @return the appropriate PatternComputationMethod to compute the energy of the pattern at the positions 'positions' in the duplex.
	 * If no appropriate PatternComputationMethod exists to compute the energy of the pattern at the positions 'positions' in the duplex,
	 * it returns null.
	 * If there is a terminal mismatch in the duplex, a SequenceException is thrown.
	 * If a nucleic acid is unknown, a SequenceException is thrown.
	 */
	private PatternComputationMethod getAppropriatePatternModel(int [] positions){
		if (positions[0] == 0 || positions[1] == environment.getSequences().getDuplexLength() - 1){
			if (environment.getSequences().isCNGPattern(positions[0], positions[1]) && this.environment.isSelfComplementarity()){
				if (this.CNGRepeatsMethod == null){
					initialiseCNGRepeatsMethod();
				}
				return this.CNGRepeatsMethod;
			}
			else if (environment.getSequences().isDanglingEnd(positions[0], positions[1])){
				if (positions[1] - positions[0] + 1 == 1){
					if (this.singleDanglingEndMethod == null){
						initialiseSingleDanglingEndMethod();
					}
					return this.singleDanglingEndMethod;
				}
				else if (positions[1] - positions[0] + 1 == 2){
					if (this.doubleDanglingEndMethod == null){
						initialiseDoubleDanglingEndMethod();
					}
					return this.doubleDanglingEndMethod;
				}
				else if (positions[1] - positions[0] + 1 > 2){
					if (this.longDanglingEndMethod == null){
						initialiseLongDanglingEndMethod();
					}
					return this.longDanglingEndMethod;
				}
				else {
					throw new SequenceException("\n We don't recognize the structure " + environment.getSequences().getSequence(positions[0], positions[1]) + "/" + environment.getSequences().getComplementary(positions[0], positions[1]));
				}
			}
			else if (environment.getSequences().isGUSequences(positions[0], positions[1])){
				if (this.wobbleMethod == null){
					initialiseWobbleMethod();
				}
				return this.wobbleMethod;
			}
		else if (environment.getSequences().isMismatchPair(positions[0]) || environment.getSequences().isMismatchPair(positions[1])){
			throw new NoExistingMethodException("\n No method for terminal mismatches has been implemented yet.");
		}
	}
	if (environment.getSequences().isPerfectMatchSequence(positions[0], positions[1])){
			if (this.cricksMethod == null){
				initialiseCrickMethod();
			}
			return this.cricksMethod;
		}
		
		else if (environment.getSequences().isGUSequences(positions[0], positions[1])){
			if (this.wobbleMethod == null){
				initialiseWobbleMethod();
			}
			return this.wobbleMethod;
		}
		else if (environment.getSequences().isMismatch(positions[0], positions[1])){
			if (positions[1] - positions[0] + 1 == 1){
				if (this.singleMismatchMethod == null){
					initialiseSingleMismatchMethod();
				}
				return this.singleMismatchMethod;
			}
			else if (positions[1] - positions[0] + 1 == 2 && this.environment.getSequences().isNoGapInSequence(positions[0], positions[1])){
				if (this.tandemMismatchMethod == null){
					initialiseTandemMismatchMethod();
				}
				return this.tandemMismatchMethod;
			}
			else if (positions[1] - positions[0] + 1 >= 2){
				if (this.internalLoopMethod == null){
					initialiseInternalLoopMethod();
				}
				return this.internalLoopMethod;
			}
		}
		else if (environment.getSequences().isBulgeLoop(positions[0], positions[1])){
			if (positions[1] - positions[0] + 1 == 1){
				if (this.singleBulgeLoopMethod == null){
					initialiseSingleBulgeLoopMethod();
				}
				return this.singleBulgeLoopMethod;
			}
			else if (positions[1] - positions[0] + 1 >= 2){
				if (this.longBulgeLoopMethod == null){
					initialiseLongBulgeLoopMethod();
				}
				return this.longBulgeLoopMethod;
			}
		}
		else if (environment.getSequences().isRegisteredNucleicAcid(positions[0])){
			SpecificAcidNames acidName = environment.getSequences().getModifiedAcidName(environment.getSequences().getDuplex().get(positions[0]));
			if (acidName != null){

				switch (acidName) {
				case inosine:
					if (this.inosineMethod == null){
						initialiseInosineMethod();
					}
					return this.inosineMethod;
				case azobenzene:
					if (this.azobenzeneMethod == null){
						initialiseAzobenzeneMethod();
					}
					return this.azobenzeneMethod;
				case hydroxyadenine:
					if (this.hydroxyadenosineMethod == null){
						initialiseHydroxyadenosineMethod();
					}
					return this.hydroxyadenosineMethod;
				case lockedNucleicAcid:
					if (this.lockedAcidMethod == null){
						initialiseLockedAcidMethod();
					}
					return this.lockedAcidMethod;
				default:
					throw new SequenceException("\n" + acidName + " is unknown.");
				}
			}
		}
		return null;
	}
	
	/**
	 * initialises the crickMethod of NearestNeighborMode.
	 */
	private void initialiseCrickMethod(){
		String optionName = OptionManagement.NNMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.cricksMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the azobenzeneMethod of NearestNeighborMode.
	 */
	private void initialiseAzobenzeneMethod(){
		String optionName = OptionManagement.azobenzeneMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.azobenzeneMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the CNGRepeatsMethod of NearestNeighborMode.
	 */
	private void initialiseCNGRepeatsMethod(){
		String optionName = OptionManagement.CNGMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.CNGRepeatsMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the doubleDanglingEndMethod of NearestNeighborMode.
	 */
	private void initialiseDoubleDanglingEndMethod(){
		String optionName = OptionManagement.doubleDanglingEndMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.doubleDanglingEndMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the hydroxyadenosineMethod of NearestNeighborMode.
	 */
	private void initialiseHydroxyadenosineMethod(){
		String optionName = OptionManagement.hydroxyadenineMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.hydroxyadenosineMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the inosineMethod of NearestNeighborMode.
	 */
	private void initialiseInosineMethod(){
		String optionName = OptionManagement.inosineMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.inosineMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the internalLoopMethod of NearestNeighborMode.
	 */
	private void initialiseInternalLoopMethod(){
		String optionName = OptionManagement.internalLoopMethod;
		String methodName = this.environment.getOptions().get(optionName);

		this.internalLoopMethod = initialiseMethod(optionName, methodName);

	}
	
	/**
	 * initialises the lockedAcidMethod of NearestNeighborMode.
	 */
	private void initialiseLockedAcidMethod(){
		String optionName = OptionManagement.lockedAcidMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.lockedAcidMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the longBulgeLoopMethod of NearestNeighborMode.
	 */
	private void initialiseLongBulgeLoopMethod(){
		String optionName = OptionManagement.longBulgeLoopMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.longBulgeLoopMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the longDanglingEndMethod of NearestNeighborMode.
	 */
	private void initialiseLongDanglingEndMethod(){
		String optionName = OptionManagement.longDanglingEndMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.longDanglingEndMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the singleBulgeLoopMethod of NearestNeighborMode.
	 */
	private void initialiseSingleBulgeLoopMethod(){
		String optionName = OptionManagement.singleBulgeLoopMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.singleBulgeLoopMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the singleDanglingEndMethod of NearestNeighborMode.
	 */
	private void initialiseSingleDanglingEndMethod(){
		String optionName = OptionManagement.singleDanglingEndMethod;
		String methodName = this.environment.getOptions().get(optionName);

		this.singleDanglingEndMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the singleMismatchMethod of NearestNeighborMode.
	 */
	private void initialiseSingleMismatchMethod(){
		String optionName = OptionManagement.singleMismatchMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.singleMismatchMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the tandemMismatchMethod of NearestNeighborMode.
	 */
	private void initialiseTandemMismatchMethod(){
		String optionName = OptionManagement.tandemMismatchMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.tandemMismatchMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the wobbleMethod of NearestNeighborMode.
	 */
	private void initialiseWobbleMethod(){
		String optionName = OptionManagement.wobbleBaseMethod;
		String methodName = this.environment.getOptions().get(optionName);
		this.wobbleMethod = initialiseMethod(optionName, methodName);
	}
	
	/**
	 * initialises the PatternComputationMethod which represents the model 'methodName' entered
	 * with the option 'optionName'
	 * @param  optionName : option name
	 * @param  methodName : method or model name
	 * @return the initialised PatternComputationMethod.
	 */
	private PatternComputationMethod initialiseMethod(String optionName, String methodName){

		PatternComputationMethod necessaryMethod = register.getPatternComputationMethod(optionName, methodName);
		if (necessaryMethod != null){
			necessaryMethod.initialiseFileName(methodName);
			necessaryMethod.loadData(environment.getOptions());

		}
		else {
			throw new NoExistingMethodException("one or more method(s) is(are) missing to compute the melting" +
					"temperature.");
		}
		return necessaryMethod;
	}
	
	/**
	 * to check if all the necessary methods and models to compute the enthalpy, entropy and melting temperature of the duplex
	 * are applicable.
	 * @return true if all the necessary methods and models to compute the enthalpy, entropy and melting temperature of the duplex
	 * are applicable.
	 * If one necessary method or model doesn't exist, a NoExistingMethodException is thrown
	 */
	private boolean checkIfMethodsAreApplicable(){
		int pos1 = 0;
		int pos2 = 0;
		boolean isApplicableMethod = true;
		while (pos2 + 1 <= environment.getSequences().getDuplexLength() - 1){

			int [] positions = getPositionsPattern(pos1);
			pos1 = positions[0];
			pos2 = positions[1];
			PatternComputationMethod necessaryMethod = getAppropriatePatternModel(positions);
			if (necessaryMethod == null){
				throw new NoExistingMethodException("\n We don't have a method to compute the energy for the positions from " + pos1 + " to " + pos2 );
			}
			if (necessaryMethod.isApplicable(this.environment, pos1, pos2) == false){
				OptionManagement.logWarning("\n We cannot comput the melting temperature, the method to compute the structure from" + pos1 + " to " + pos2 + " is not applicable with the chosen options.");
				isApplicableMethod = false;
			}
				pos1 = pos2 + 1;
		}
		return isApplicableMethod;
	}
	
	/**
	 * analyses the sequences. Check all the necessary methods and models to compute the enthalpy, entropy and melting temperature of the duplex
	 * are applicable.
	 * If one of the methods or models is not applicable, a MethodNotApplicableException is thrown.
	 */
	private void analyzeSequence(){
		if (checkIfMethodsAreApplicable() == false){
			throw new MethodNotApplicableException("\n We cannot compute the melting because one method is not applicable. Check the sequences.");
		}
	}
	
	// public static methods

	/**
	 * computes the melting temperature of a hairpin loop (when there is more than 4 CNG repeats in a sequence)
	 * @param environment
	 * @return double melting temperature of a hairpin loop for the Environment 'environment'.
	 */
	public static double computesHairpinTemperature(Environment environment){
		double Tm = environment.getResult().getEnthalpy() / environment.getResult().getEntropy() - 273.15;
		OptionManagement.logMessage("\n Melting temperature : Tm = delta H / delta - 273.15");
		
		return Tm;
	}
	
	/**
	 * computes the melting temperature for for the Environment 'environment'.
	 * @param environment
	 * @return double melting temperature for for the Environment 'environment'.
	 */
	public static double computesMeltingTemperature(Environment environment){
		double Tm = environment.getResult().getEnthalpy() / (environment.getResult().getEntropy() + 1.99 * Math.log( environment.getNucleotides() / environment.getFactor() )) - 273.15;
		OptionManagement.logMessage("\n Melting temperature : Tm = delta H / (delta S + 1.99 x ln([nucleotides] / F)) - 273.15");
		return Tm;
	}

}
