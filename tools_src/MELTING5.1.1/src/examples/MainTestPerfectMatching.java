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
 * This class exists to test the different nearest neighbor methods.
 */
public class MainTestPerfectMatching{

	public static void main(String[] args) {
		
		ArrayList<String> DNAmethods = new ArrayList<String>();
		DNAmethods.add("all97");
		DNAmethods.add("bre86");
		DNAmethods.add("san04");
		DNAmethods.add("san96");
		DNAmethods.add("sug96");
		DNAmethods.add("tan04");
		
		ArrayList<String> RNAmethods = new ArrayList<String>();
		RNAmethods.add("fre86");
		RNAmethods.add("xia98");
		
		ArrayList<String> DNARNAmethods = new ArrayList<String>();
		DNARNAmethods.add("sug95");
		
		ArrayList<String> mRNAmethods = new ArrayList<String>();
		mRNAmethods.add("tur06");

		Properties DNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNANoSelfComplementary.txt");
		Properties DNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNASelfComplementary.txt");
	
		Properties RNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/RNANoSelfComplementary.txt");
		Properties RNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/RNASelfComplementary.txt"); 
		
		Properties DNARNASequences = MainTest.loadSequencesTest("src/examples/test/DNARNADuplexes.txt");
		
		Properties mRNASequences = MainTest.loadSequencesTest("src/examples/test/2OMethylRNADuplexes.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t all97 \t bre86 \t san04 \t san96 \t sug96 \t tan04 \n");

		MainTest.displayResults(DNANoSelfComplementarySequences, DNAmethods, "dnadna", "Na=1", "0.0004", "-nn");
		
		System.out.print("\n\n melting.sequences \t TmExp \t all97 \t bre86 \t san04 \t san96 \t sug96 \t tan04 \n");

		MainTest.displayResults(DNASelfComplementarySequences, DNAmethods, "dnadna", "Na=1", "0.0001", "-nn");

		System.out.print("\n\n melting.sequences \t TmExp \t fre86 \t xia98 \n");
		
		MainTest.displayResults(RNANoSelfComplementarySequences, RNAmethods, "rnarna", "Na=1", "0.0002", "-nn");

		System.out.print("\n\n melting.sequences \t TmExp \t fre86 \t xia98 \n");
		
		MainTest.displayResults(RNASelfComplementarySequences, RNAmethods, "rnarna", "Na=1", "0.0001", "-nn");

		System.out.print("\n\n melting.sequences \t TmExp \t sug95 \n");

		MainTest.displayResults(DNARNASequences, DNARNAmethods, "rnadna", "Na=1", "0.0001", "-nn");

		System.out.print("\n\n melting.sequences \t TmExp \t tur06 \n");
		
		MainTest.displayResults(mRNASequences, mRNAmethods, "mrnarna", "Na=0.1", "0.0001", "-nn");
	}

}
