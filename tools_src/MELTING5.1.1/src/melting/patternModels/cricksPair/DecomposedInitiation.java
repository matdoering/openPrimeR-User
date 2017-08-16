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
import melting.sequences.NucleotidSequences;

/**
 * This class represents one model to compute the initiation enthalpy and entropy.
 * The duplex initiation is computed by the addition of an initiation penalty for the first
 * base pair of the duplex and an initiation penalty for the last base pair of the duplex.
 * It extends CricksNNMethod.
 */
public abstract class DecomposedInitiation extends CricksNNMethod {
	
	// inherited method

	@Override
	public ThermoResult computesHybridizationInitiation(Environment environment){
		
		super.computesHybridizationInitiation(environment);
		
		NucleotidSequences sequences = environment.getSequences();
		int [] truncatedPositions = sequences.removeTerminalUnpairedNucleotides();
				
		double numberTerminalGC = environment.getSequences().calculateNumberOfTerminal("G", "C", truncatedPositions[0], truncatedPositions[1]);
		double numberTerminalAT = environment.getSequences().calculateNumberOfTerminal("A", "T", truncatedPositions[0], truncatedPositions[1]);
		double numberTerminalAU = environment.getSequences().calculateNumberOfTerminal("A", "U", truncatedPositions[0], truncatedPositions[1]);
		
		double enthalpy = 0.0;
		double entropy = 0.0;
		
		if (numberTerminalAT != 0){
			Thermodynamics initiationAT = this.collector.getInitiation("per_A/T");
			
			OptionManagement.logMessage("\n " + numberTerminalAT + " x Initiation per A/T : enthalpy = " + initiationAT.getEnthalpy() + "  entropy = " + initiationAT.getEntropy());
			
			enthalpy += numberTerminalAT * initiationAT.getEnthalpy();
			entropy += numberTerminalAT * initiationAT.getEntropy();
		}
		
		else if (numberTerminalAU != 0){
			Thermodynamics initiationAU = this.collector.getInitiation("per_A/U");
			
			OptionManagement.logMessage("\n " + numberTerminalAU + " x Initiation per A/U : enthalpy = " + initiationAU.getEnthalpy() + "  entropy = " + initiationAU.getEntropy());

			enthalpy += numberTerminalAU * this.collector.getInitiation("per_A/U").getEnthalpy();
			entropy += numberTerminalAU * this.collector.getInitiation("per_A/U").getEntropy();
		}
		
		if (numberTerminalGC != 0){
			Thermodynamics initiationGC = this.collector.getInitiation("per_G/C");

			OptionManagement.logMessage("\n " + numberTerminalGC + " x Initiation per G/C : enthalpy = " + initiationGC.getEnthalpy() + "  entropy = " + initiationGC.getEntropy());
			
			enthalpy += numberTerminalGC * initiationGC.getEnthalpy();
			entropy += numberTerminalGC * initiationGC.getEntropy();
		}
		
		environment.addResult(enthalpy, entropy);
		
		return environment.getResult();
	}

}
