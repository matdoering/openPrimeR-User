package examples;

import java.util.ArrayList;
import java.util.Properties;

public class MainTestDNAMismatchesMeltingC {

	public static void main(String[] args) {
		ArrayList<String> DNASingleMismatchesMethods = new ArrayList<String>();
		DNASingleMismatchesMethods.add("dnadnamm.nn");
		
		Properties DNASingleMismatches = MainTest.loadSequencesTest("src/examples/test/DNASingleMismatchesSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t dnadnamm.nn \n");

		MainTest.displayResultsMismatchMeltingCWithComplementarySequence(DNASingleMismatches, DNASingleMismatchesMethods, "dnadna", "1", "0.0004");

	}
}
