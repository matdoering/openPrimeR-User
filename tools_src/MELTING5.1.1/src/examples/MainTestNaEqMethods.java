package examples;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This class exists to test the sodium equivalent formulas with DNA sequences.
 */
public class MainTestNaEqMethods {

	public static void main(String[] args) {
		
		ArrayList<String> NaEqMethods = new ArrayList<String>();
		NaEqMethods.add("ahs01");
		NaEqMethods.add("mit96");
		NaEqMethods.add("pey00");
		
		Properties DNASequences = MainTest.loadSequencesTest("src/examples/test/DNAMagnesiumTestValues.txt");
		System.out.print("\n\n melting.sequences \t TmExp \t ahs01 \t mit96 \t pey00 \n");
		MainTest.displayResultsNaEq(DNASequences, NaEqMethods, "dnadna", "0.000002", "-naeq");
	}

}
