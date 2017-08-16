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
 * This class exists to test the different sodium corrections with DNA sequences.
 */
public class MainTestDNASodiumCorrections {

	public static void main(String[] args) {

		ArrayList<String> sodiumMethods = new ArrayList<String>();
		sodiumMethods.add("ahs01");
		sodiumMethods.add("kam71");
		sodiumMethods.add("marschdot");
		sodiumMethods.add("owc1904");
		sodiumMethods.add("owc2004");
		sodiumMethods.add("owc2104");
		sodiumMethods.add("owc2204");
		sodiumMethods.add("san96");
		sodiumMethods.add("san04");
		sodiumMethods.add("schlif");
		sodiumMethods.add("tanna06");
		sodiumMethods.add("wet91");

		Properties DNASequences = MainTest.loadSequencesTest("src/examples/test/DNASodiumTestValues.txt");
		System.out.print("\n\n melting.sequences \t TmExp \t ash01 \t kam71 \t marschdot \t owc1904 \t owc2004 \t owc2104 \t owc2204 \t san96 \t san04 \t schlif \t tanna06 \t wet91 \n");
		MainTest.displayResultsSodium(DNASequences, sodiumMethods, "dnadna", "0.000002", "-ion");
	}

}
