package examples;

import java.util.ArrayList;
import java.util.Properties;

public class MainTestDNADanglingEndMeltingC {

	public static void main(String[] args) {
		
		ArrayList<String> DNASingleDanglingEndMethods = new ArrayList<String>();
		DNASingleDanglingEndMethods.add("dnadnade.nn");
		
		Properties DNASingleDanglingEnd = MainTest.loadSequencesTest("src/examples/test/DNASingleDanglingEndSequences.txt");
		
		System.out.print("\n\n melting.sequences \t TmExp \t dnadnade.nn \n");

		MainTest.displayResultsDanglingEndMeltingC(DNASingleDanglingEnd, DNASingleDanglingEndMethods, "dnadna", "1", "0.0001");

	}
}
