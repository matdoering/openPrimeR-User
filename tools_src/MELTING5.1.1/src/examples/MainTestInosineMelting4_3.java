package examples;

import java.util.ArrayList;
import java.util.Properties;

public class MainTestInosineMelting4_3 {

	public static void main(String[] args) {
		
		ArrayList<String> DNAInosineMethods = new ArrayList<String>();
		DNAInosineMethods.add("san05a.nn");
		
		Properties DNAInosineSequences = MainTest.loadSequencesTest("src/examples/test/DNAInosineSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t san05a.nn \n");

		MainTest.displayResultsInosineMelting4_3WithComplementarySequence(DNAInosineSequences, DNAInosineMethods, "dnadna", "1", "0.0001");
		
		ArrayList<String> RNAInosineMethods = new ArrayList<String>();
		RNAInosineMethods.add("bre07a.nn");
		
		Properties RNAInosineSequences = MainTest.loadSequencesTest("src/examples/test/RNAInosineSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t bre07a.nn \n");

		MainTest.displayResultsInosineMelting4_3WithComplementarySequence(RNAInosineSequences, RNAInosineMethods, "rnarna", "1", "0.0001");
		
	}
}
