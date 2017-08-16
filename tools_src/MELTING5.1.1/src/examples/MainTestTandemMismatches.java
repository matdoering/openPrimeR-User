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
 * This class exists to test the different tandem mimsatches methods.
 */
public class MainTestTandemMismatches {

	public static void main(String[] args) {
		ArrayList<String> DNATandemMismatchesMethods = new ArrayList<String>();
		DNATandemMismatchesMethods.add("allsanpey");
		
		Properties DNATandemMismatches = MainTest.loadSequencesTest("src/examples/test/DNATandemMismatchesSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t allsanpey \n");

		MainTest.displayResultsWithComplementarySequence(DNATandemMismatches, DNATandemMismatchesMethods, "dnadna", "Na=1", "0.0004", "-tan");
		
		ArrayList<String> RNATandemMismatchesMethods = new ArrayList<String>();
		RNATandemMismatchesMethods.add("tur06");
		
		Properties RNATandemMismatches = MainTest.loadSequencesTest("src/examples/test/RNATandemMismatchesSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t tur06 \n");

		MainTest.displayResultsWithComplementarySequence(RNATandemMismatches, RNATandemMismatchesMethods, "rnarna", "Na=1", "0.0001", "-tan");
	}

}
