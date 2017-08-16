package examples;

import java.util.Properties;

/**
 * This class exists to test the formulas approximations of MELTING 4.2 with DNA sequences.
 */
public class MainTestApproximativeModeMeltingC {

public static void main(String[] args) {
		
		//Properties DNASequences = MainTest.loadSequencesTest("src/examples/test/DNAMagnesiumTestValues.txt");
		//System.out.print("\n\n melting.sequences \t TmExp \t wet91 \n");
		//MainTest.displayResultsNaEq(DNASequences,  "dnadna");
		
		Properties DNANoSelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNANoSelfComplementary.txt");
		//Properties DNASelfComplementarySequences = MainTest.loadSequencesTest("src/examples/test/DNASelfComplementary.txt");
		System.out.print("\n\n melting.sequences \t TmExp \t wet91 \n");
		MainTest.displayResultsNaEq(DNANoSelfComplementarySequences,  "dnadna");
	}
}
