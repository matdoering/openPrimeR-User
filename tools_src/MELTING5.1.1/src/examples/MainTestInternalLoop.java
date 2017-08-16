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

package examples;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This class exists to test the different internal loop methods.
 */
public class MainTestInternalLoop {

	public static void main(String[] args) {
		ArrayList<String> RNAInternalLoopMethods = new ArrayList<String>();
		RNAInternalLoopMethods.add("tur06");
		RNAInternalLoopMethods.add("zno07");

		Properties internalLoopSequences = MainTest.loadSequencesTest("src/examples/test/RNAInternalLoopSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t tur06 \t zno07 \n");

		MainTest.displayResultsWithComplementarySequence(internalLoopSequences, RNAInternalLoopMethods, "rnarna", "Na=1", "0.0001", "-intLP");
	}

}
