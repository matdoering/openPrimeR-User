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
 * This class exists to test the different nearest neighbor parameters with Melting 4.2.
 */
public class MainTestMeltingCPerfectMatching {

	public static void main(String[] args) {
		ArrayList<String> DNAmethods = new ArrayList<String>();
		DNAmethods.add("all97a.nn");
		DNAmethods.add("bre86a.nn");
		DNAmethods.add("san96a.nn");
		DNAmethods.add("sug96a.nn");
		
		ArrayList<String> RNAmethods = new ArrayList<String>();
		RNAmethods.add("fre86a.nn");
		RNAmethods.add("xia98a.nn");
		
		ArrayList<String> DNARNAmethods = new ArrayList<String>();
		DNARNAmethods.add("sug95a.nn");

		Properties DNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNANoSelfComplementary.txt");
		Properties DNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNASelfComplementary.txt");
	
		Properties RNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/RNANoSelfComplementary.txt");
		Properties RNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/RNASelfComplementary.txt"); 
		
		Properties DNARNASequences = MainTest.loadSequencesTest("src/examples/test/DNARNADuplexes.txt");
				
		System.out.print("\n\n melting.sequences \t TmExp \t all97a.nn \t bre86a.nn \t san96a.nn \t sug96a.nn \n");

		MainTest.displayResultsMeltingC(DNANoSelfComplementarySequences, DNAmethods, "dnadna", "1", "0.0004");
		
		System.out.print("\n\n melting.sequences \t TmExp \t all97a.nn \t bre86a.nn \t san96a.nn \t sug96a.nn \n");

		MainTest.displayResultsMeltingCSelfComplementary(DNASelfComplementarySequences, DNAmethods, "dnadna", "1", "0.0001");

		System.out.print("\n\n melting.sequences \t TmExp \t fre86a.nn \t xia98a.nn \n");
		
		MainTest.displayResultsMeltingC(RNANoSelfComplementarySequences, RNAmethods, "rnarna", "1", "0.0002");

		System.out.print("\n\n melting.sequences \t TmExp \t fre86a.nn \t xia98a.nn \n");
		
		MainTest.displayResultsMeltingCSelfComplementary(RNASelfComplementarySequences, RNAmethods, "rnarna", "1", "0.0001");

		System.out.print("\n\n melting.sequences \t TmExp \t sug95a.nn \n");

		MainTest.displayResultsDNA_RNAMeltingC(DNARNASequences, DNARNAmethods, "rnadna", "1", "0.0001");

	}

}
