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

package melting.approximativeMethods;

import melting.ThermoResult;
import melting.configuration.OptionManagement;

/**
 * This class represents the model from Wetmur 1991. It extends ApproximativeMode.
 * 
 * James G. Wetmur, "DNA Probes : applications of the principles of nucleic acid hybridization",
1991, Critical reviews in biochemistry and molecular biology, 26, 227-259*/
public abstract class Wetmur91 extends ApproximativeMode{
	
	// public methods
	
	@Override
	public ThermoResult computesThermodynamics() {
		ThermoResult result = super.computesThermodynamics();
		OptionManagement.logMessage(" from Wetmur (1991)");
		
		return result;
	}
}
