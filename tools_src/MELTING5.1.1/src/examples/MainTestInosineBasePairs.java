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
 * This class exists to test the different inosine methods.
 */
public class MainTestInosineBasePairs {

	public static void main(String[] args) {
		ArrayList<String> DNAInosineMethods = new ArrayList<String>();
		DNAInosineMethods.add("san05");
		
		Properties DNAInosineSequences = MainTest.loadSequencesTest("src/examples/test/DNAInosineSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t san05 \n");

		MainTest.displayResultsWithComplementarySequence(DNAInosineSequences, DNAInosineMethods, "dnadna", "Na=1", "0.0001", "-ino");
		
		ArrayList<String> RNAInosineMethods = new ArrayList<String>();
		RNAInosineMethods.add("zno07");
		
		Properties RNAInosineSequences = MainTest.loadSequencesTest("src/examples/test/RNAInosineSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t zno07 \n");

		MainTest.displayResultsWithComplementarySequence(RNAInosineSequences, RNAInosineMethods, "rnarna", "Na=1", "0.0001", "-ino");
		
	}

}
