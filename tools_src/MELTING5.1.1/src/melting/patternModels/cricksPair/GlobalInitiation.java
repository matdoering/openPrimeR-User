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

/**
 * This class represents one model to compute the initiation enthalpy and entropy.
 * The duplex initiation is computed by the addition of either an initiation penalty for duplexes 
 * containing at least one GC base pair or an initiation penalty for duplexes only composed of AT or AU base pairs.
 * It extends CricksNNMethod.
 */
public abstract class GlobalInitiation extends CricksNNMethod {
	
	// Inherited method
	
	@Override
	public ThermoResult computesHybridizationInitiation(Environment environment){
		
		super.computesHybridizationInitiation(environment);

		double enthalpy = 0.0;
		double entropy = 0.0;

		if (environment.getSequences().isOneGCBasePair()){
			Thermodynamics initiationOneGC = this.collector.getInitiation("one_GC_Pair");

			OptionManagement.logMessage("\n The initiation if there is at least one GC base pair : enthalpy = " + initiationOneGC.getEnthalpy() + "  entropy = " + initiationOneGC.getEntropy());

			enthalpy += initiationOneGC.getEnthalpy();
			entropy += initiationOneGC.getEntropy();
		}
		
		else {
			Thermodynamics initiationAllAT = this.collector.getInitiation("all_AT_pairs");
			
			OptionManagement.logMessage("\n The initiation if there is only AT base pairs : enthalpy = " + initiationAllAT.getEnthalpy() + "  entropy = " + initiationAllAT.getEntropy());
			enthalpy += initiationAllAT.getEnthalpy();
			entropy += initiationAllAT.getEntropy();
		}
		
		environment.addResult(enthalpy, entropy);
		
		return environment.getResult();
	}

}
