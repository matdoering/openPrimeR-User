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
 * This class exists to test the different approximative methods.
 */
public class MainTestApproximativeMode {

	public static void main(String[] args) {
		ArrayList<String> DNAmethods = new ArrayList<String>();
		DNAmethods.add("ahs01");
		DNAmethods.add("che93");
		DNAmethods.add("che93corr");
		DNAmethods.add("schdot");
		DNAmethods.add("owe69");
		DNAmethods.add("san98");
		DNAmethods.add("wetdna91");
		
		ArrayList<String> RNAmethods = new ArrayList<String>();
		RNAmethods.add("wetrna91");
		
		ArrayList<String> DNARNAmethods = new ArrayList<String>();
		DNARNAmethods.add("wetdnarna91");
		
		Properties DNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNANoSelfComplementary.txt");
		Properties DNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNASelfComplementary.txt");
	
		Properties RNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/RNANoSelfComplementary.txt");
		Properties RNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/RNASelfComplementary.txt"); 
		
		Properties DNARNASequences = MainTest.loadSequencesTest("src/examples/test/DNARNADuplexes.txt");
				
		System.out.print("\n\n melting.sequences \t TmExp \t ahs01 \t che93 \t che93corr \t shdot \t ow69 \t san98 \t wetdna91 \n");

		MainTest.displayResults(DNANoSelfComplementarySequences, DNAmethods, "dnadna", "Na=1", "0.0004", "-am");
		
		System.out.print("\n\n melting.sequences \t TmExp \t ahs01 \t che93 \t che93corr \t shdot \t ow69 \t san98 \t wetdna91 \n");

		MainTest.displayResults(DNASelfComplementarySequences, DNAmethods, "dnadna", "Na=1", "0.0001", "-am");

		System.out.print("\n\n melting.sequences \t TmExp \t wetrna91 \n");
		
		MainTest.displayResults(RNANoSelfComplementarySequences, RNAmethods, "rnarna", "Na=1", "0.0002", "-am");

		System.out.print("\n\n melting.sequences \t TmExp \t wetrna91 \n");
		
		MainTest.displayResults(RNASelfComplementarySequences, RNAmethods, "rnarna", "Na=1", "0.0001", "-am");

		System.out.print("\n\n melting.sequences \t TmExp \t wetrna91 \n");

		MainTest.displayResults(DNARNASequences, DNARNAmethods, "rnadna", "Na=1", "0.0001", "-am");
	}

}
