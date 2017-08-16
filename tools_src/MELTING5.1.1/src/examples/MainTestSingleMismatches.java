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
 * This class exists to test the different single mismatch methods.
 */
public class MainTestSingleMismatches {

	public static void main(String[] args) {
		ArrayList<String> DNASingleMismatchesMethods = new ArrayList<String>();
		DNASingleMismatchesMethods.add("allsanpey");
		
		Properties DNASingleMismatches = MainTest.loadSequencesTest("src/examples/test/DNASingleMismatchesSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t allsanpey \n");

		MainTest.displayResultsWithComplementarySequence(DNASingleMismatches, DNASingleMismatchesMethods, "dnadna", "Na=1", "0.0004", "-sinMM");
		
		ArrayList<String> RNASingleMismatchesMethods1 = new ArrayList<String>();
		RNASingleMismatchesMethods1.add("zno07");
		RNASingleMismatchesMethods1.add("tur06");
		
		ArrayList<String> RNASingleMismatchesMethods2 = new ArrayList<String>();
		RNASingleMismatchesMethods2.add("zno07");
    RNASingleMismatchesMethods2.add("zno08");
    RNASingleMismatchesMethods2.add("tur06");
    
		Properties RNASingleMismatches1 = MainTest.loadSequencesTest("src/examples/test/RNASingleMismatchesSequences1.txt");
		Properties RNASingleMismatches2 = MainTest.loadSequencesTest("src/examples/test/RNASingleMismatchesSequences2.txt");

		System.out.print("\n\n melting.sequences \t TmExp \t zno07 \t tur06 \n");

		MainTest.displayResultsWithComplementarySequence(RNASingleMismatches1, RNASingleMismatchesMethods1, "rnarna", "Na=1", "0.0001", "-sinMM");
		
		System.out.print("\n\n melting.sequences \t TmExp \t zno07 \t zno08 \t tur06 \n");

		MainTest.displayResultsWithComplementarySequence(RNASingleMismatches2, RNASingleMismatchesMethods2, "rnarna", "Na=1", "0.0001", "-sinMM");
    
    ArrayList<String> DNARNASingleMismatchesMethods = new ArrayList<String>();
    DNARNASingleMismatchesMethods.add("wat10");
    
    Properties DNARNASingleMismatches = MainTest.loadSequencesTest("src/examples/test/DNARNASingleMismatchesSequences.txt");
    
    System.out.print("\n\n melting.sequences \t TmExp \t wat10 \n");
    
    MainTest.displayResultsWithComplementarySequence(DNARNASingleMismatches, DNARNASingleMismatchesMethods, "rnadna", "Na=1", "0.0001", "-sinMM");

	}

}
