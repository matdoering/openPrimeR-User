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

package melting.sequences;

import java.util.ArrayList;
import java.util.HashMap;

import melting.configuration.OptionManagement;
import melting.exceptions.SequenceException;

/** This class represents a pair of nucleic acid sequences. It encodes and decodes them, recognizes patterns 
 * and provides general purpose sequence manipulation methods. The instance variables are :
 * ArrayList duplex : an ArrayList containing the sorted BasePair objects.
 * String sequence : the sequence (5'3') represented by a String.
 * String complementary : the complementary sequence (3'5') represented by a String.
 * NucleotidSequences rnaEquivalent : the NucleotidSequences object which represents this NucleotidSequences object with RNA sequences
 * NucleotidSequences dnaEquivalent : the NucleotidSequences object which represents this NucleotidSequences object with DNA sequences
 * HashMap modifiedAcidNames : a Map which makes a link between the encoded specific nucleic acid in the String and the matching SpecificAcidNames enum value.
 */
public class NucleotidSequences {
	
	// Instance variables

	/**
	 * ArrayList duplex : an ArrayList containing the sorted BasePair objects.
	 */
	private ArrayList<BasePair> duplex;
	
	/**
	 * String sequence : the sequence (5'3') represented by a String.
	 */
	private String sequence;
	
	/**
	 * String complementary : the complementary sequence (3'5') represented by a String.
	 */
	private String complementary;
	
	/**
	 * NucleotidSequences rnaEquivalent : the NucleotidSequences object which represents this NucleotidSequences object with RNA sequences
	 * This object is initialised when a model for RNA sequences is selected by the user and the hybridisation type is dnadna
	 */
	private NucleotidSequences rnaEquivalent;
	
	/**
	 * NucleotidSequences dnaEquivalent : the NucleotidSequences object which represents this NucleotidSequences object with DNA sequences
     * This object is initialised when a model for DNA sequences is selected by the user and the hybridisation type is rnarna
	 */
	private NucleotidSequences dnaEquivalent;
	
	/**
	 * HashMap modifiedAcidNames : a Map which makes a link between the encoded specific nucleic acid in the String and the matching SpecificAcidNames enum value.
	 */
	private static HashMap<String, SpecificAcidNames> modifiedAcidNames = new HashMap<String, SpecificAcidNames>();
	
	// NucleotidSequences constructors
	
	/**
	 * Creates a NucleotidSequences object from two String.
	 * @param  sequence : the sequence (5'3')
	 * @param  complementary : the complementary sequence (3'5')
	 */
	public NucleotidSequences(String sequence, String complementary){
		this(sequence, complementary, true);
	}
	
	/**
	 * Creates a NucleotidSequence object from two String. The sequences will be encoded if the boolean
	 * encode is true.
	 * @param  sequence : the sequence (5'3')
	 * @param  complementary : the complementary sequence (3'5')
	 * @param encode : If the sequences are already encoded, it is false otherwise
	 * the sequences must be encoded and the boolean encode is true;
	 * If the two encoded sequences are not the same length, a SequenceException is thrown.
	 */
	private NucleotidSequences(String sequence, String complementary, boolean encode) {
		String [] sequences;
		if (encode) {
			sequences = encodeSequences(sequence, complementary);
			sequences = correctSequences(sequences[0], sequences[1]);
		}
		else {
			sequences = new String[]{sequence, complementary};
		}
		
		if (sequences[0].length() != sequences[1].length()){
			throw new SequenceException("The sequences have two different lengths. Replace the gaps by the character '-'.");
		}
		this.sequence = sequences[0];
		this.complementary = sequences[1];
		this.duplex = getDuplexFrom(sequences[0], sequences[1]);
	}
	
	/**
	 * Creates a new NucleotidSequences object from a reference nucleotiDSequences object. The sequences
	 * are converted in the hybridization type "hybridization type". the rnaEquivalent or dnaEquivalent object
	 * of the reference NucleotidSequences object are initialised.
	 * @param  hybridizationType : hybridization type of the duplex.
	 * @param sequences : the reference nucleotidSequences object. 
	 */
	protected NucleotidSequences(String hybridizationType, NucleotidSequences sequences){
		this.sequence = convertSequence(sequences.sequence, hybridizationType);
		this.complementary = convertSequence(sequences.complementary, hybridizationType);
		this.duplex = sequences.duplex;
		
		if (hybridizationType.equals("dna")) {
			this.dnaEquivalent = this;
		}
		else if (hybridizationType.equals("rna")) {
			this.rnaEquivalent = this;
		}
	}
	
	// public methods
	
	/**
	 * This method is called when the same NucleotidSequences is required but with the sequences converted in the new
	 * hybridization type "hybridizationType"
	 * @param  hybridizationType : the new hybridization type of the sequences
	 * @return the initialised object rnaEquivalent if the hybridization type is "rna" or the initialised object dnaEquivalent if the hybridization type is "dna".
	 * If the String hybridization is neither "dna" nor "rna", a SequenceException is thrown.
	 */
	public NucleotidSequences getEquivalentSequences(String hybridizationType) {
		if (hybridizationType.equals("dna")) {
			if (dnaEquivalent == null){
				dnaEquivalent = new NucleotidSequences(hybridizationType, this);
			}
			return dnaEquivalent;
		}
		else if (hybridizationType.equals("rna")) {
			if (rnaEquivalent == null){
				rnaEquivalent = new NucleotidSequences(hybridizationType, this);
			}
			return rnaEquivalent;
		}
		throw new SequenceException("\n It is impossible to convert this sequences in sequences of type " + hybridizationType + ". MELTING can just convert a DNA sequence into a RNA sequence and a RNA sequence into a DNA sequence.");
	}
	
	/**
	 * encodes the String sequence and complementary of the NucleotidSequences object.
	 * @param  sequence : the sequence (5'3')
	 * @param  complementary : the complementary sequence (3'5')
	 * @return a String [] which contains the encoded String sequence and complementary of NucleotidSequences.
	 */
	public String [] encodeSequences(String sequence, String complementary){
		StringBuffer seq = new StringBuffer();
		StringBuffer comp = new StringBuffer();

		seq.append(sequence);
		comp.append(complementary);

		String [] sequences = {encodeSequence(complementary, seq), encodeComplementary(sequence, comp)};
		return sequences;
	}
	
	/**
	 * removes the unnecessary "-" in the String sequence and complementary of NucleotidSequences.
	 * @param  sequence : the sequence (5'3')
	 * @param  complementary : the complementary sequence (3'5')
	 * @return a String [] which contains the String sequence and complementary without unnecessary gaps ("-")
	 */
	public String [] correctSequences( String sequence, String complementary){
		StringBuffer correctedSequence = new StringBuffer(sequence.length());
		StringBuffer correctedComplementary = new StringBuffer(complementary.length());
		
		correctedSequence.append(sequence);
		correctedComplementary.append(complementary);
		
		for (int i = 0; i < correctedSequence.toString().length(); i++){
			if (correctedSequence.toString().charAt(i) == '-' && correctedComplementary.toString().charAt(i) == '-'){
				correctedSequence.deleteCharAt(i);
				correctedComplementary.deleteCharAt(i);
			}
		}
		String [] sequences = {correctedSequence.toString(), correctedComplementary.toString()};
		return sequences;
	}
	
	/**
	 * This method is called to get the complementary sequence to the sequence String between the positions pos1 and pos2. 
	 * @param  sequence : substring of one of the sequences (sequence 5'3' or the complementary 3'5')
	 * @param pos1 : starting position of the subsequence in the duplex
	 * @param pos2 : ending position of the subsequence in the duplex
	 * @return the substring between pos1 and pos2 which is the complementary to the sequence String.
	 * If the sequence String is neither a substring of the sequence of NucleotidSequences., nor a substring of 
	 * the complementary of NucleotidSequences., a SequenceException is thrown.
	 */
	public String getComplementaryTo(String sequence, int pos1, int pos2){
		
		if (sequence.equals(getSequence(pos1, pos2))){
			return getComplementary(pos1, pos2);
		}
		else if (sequence.equals(getComplementary(pos1, pos2))){
			return getSequence(pos1, pos2);
		}
		else{
			throw new SequenceException("\n There is no complementary sequence registered for the sequence " + sequence + ".");
		}
	}
	
	/**
	 * this method is called to get the String sequence or complementary of NucleotidSequences.
	 * which contains the pattern "pattern".
	 * @param  pattern
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return the complementary String or the sequence String, depending on which of them contains the pattern "pattern"
	 * between the positions pos1 and pos2. It returns null if neither the sequence String, nor the complementary String
	 * contains the pattern "pattern" between pos1 and pos2.
	 */
	public String getSequenceContainig(String pattern, int pos1, int pos2){
		if (getSequence(pos1, pos2).contains(pattern)){
			return getSequence(pos1, pos2);
		}
		else if (getComplementary(pos1, pos2).contains(pattern)){
			return getComplementary(pos1, pos2);
		}
		else{
			return null;
		}
	}
	
	/**
	 * to check if the pattern between the positions pos1 and pos2 in the duplex is a CNG repeat pattern.
	 * A CNG repeat pattern is when the total sequence String is G(CNG)xC where N is a mismatch (N,N) and
	 * x is the number of CNG repeats in the sequence String.
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return true if the pattern between the positions pos1 and pos2 in the duplex is a CNG repeat pattern.
	 */
	public boolean isCNGPattern(int pos1, int pos2){
		if (pos2 - pos1 + 1 != getDuplexLength() || pos2 - pos1 + 1 < 8 || (pos2 - pos1 - 1)/3 > 7){
			return false;
		}
		if (duplex.get(0).getTopAcid().equals("G") == false || duplex.get(duplex.size() - 1).getTopAcid().equals("C") == false){
			return false;
		}

		int index = 1;
		String CNG = "C" + duplex.get(2).getTopAcid() + "G";
		while (index <= getDuplexLength() - 4){

			if (getSequence(index, index + 2).equals(CNG) == false){
				return false;
			}
			else{
				index += 3;
			}
		}
		return true;
	}
	
	/**
	 * to check to check if the sequences between the positions pos1 and pos2 in the duplex are perfectly 
	 * matching sequences. Two sequences are perfectly matching sequences when all the nucleic acids of each strand
	 * are paired with their complementary base pair.
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return true if the sequences between the positions pos1 and pos2 are perfectly matching sequences.
	 */
	public boolean isPerfectMatchSequence(int pos1, int pos2){
		if (arePositionsOutOfRange(pos1, pos2)){
			return false;
		}
		
		for (int i = pos1; i <= pos2; i++){
			if (duplex.get(i).isComplementaryBasePair() == false){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * to check if there is at least one dangling end between the positions pos1 and pos2 in the duplex.
	 * A dangling end is when a terminal nucleic acid is un paired.
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return true if there is at least one dangling end between the positions pos1 and pos2 in the duplex
	 */
	public boolean isDanglingEnd(int pos1, int pos2){
		
		if ((getSequence(pos1, pos2).contains("-") && getComplementary(pos1, pos2).contains("-")) || (getSequence(pos1, pos2).contains("-") == false && getComplementary(pos1, pos2).contains("-") == false)){
			return false;
		}
		
		if (pos1 != 0 && pos2 != getDuplexLength() - 1){
			return false;
		}

		int numberGapSequence = 0;
		int numberGapComplementary = 0;
		for (int i = pos1; i <= pos2; i++){
			BasePair pair = duplex.get(i);
			if (pair.getTopAcid().equals("-")){
				numberGapSequence ++;
				if (pair.isWatsonCrickBottomBase() == false){
					return false;
				}
			}
			else if (pair.getBottomAcid().equals("-")){
				numberGapComplementary ++;
				if (pair.isFrequentNucleicAcidTopBase() == false){
					return false;
				}
			}
		}
		
		if (numberGapSequence < pos2 - pos1 + 1 && numberGapComplementary < pos2 - pos1 + 1){
			return false;
		}
		return true;
	}
	
	/**
	 * to check if the pattern between the positions pos1 and pos2 in the duplex is only composed of GU base pairs.
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return true if the pattern between the positions pos1 and pos2 in the duplex is only composed of GU base pairs.
	 */
	public boolean isGUSequences(int pos1, int pos2){
		if (pos2 > getDuplexLength() - 1 || pos1 < 0){
			return false;
		}
		
		for (int i = pos1; i <= pos2; i++){
			BasePair pair = duplex.get(i);
			if (pair.isBasePairEqualTo("G", "U") == false){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * to check if the base pair at the position pos in the duplex is a mismatch.
	 * A mismatch is when the bases int the base pair (A, T, G, C or U) are not complementary base pairs. 
	 * @param pos : the position of the base pair to test in the duplex ArrayList
	 * @return true if the base pair at the positions pos in the duplex is a mismatch.
	 */
	public boolean isMismatchPair(int pos){
		BasePair pair = duplex.get(pos);
		if (pair.isComplementaryBasePair() == false && pair.isWatsonCrickBottomBase() && pair.isFrequentNucleicAcidTopBase()){
			return true;
		}
		return false;
	}
	
	/**
	 * to check if the pattern between the positions pos1 and pos2 in the duplex is a single mismatch, tandem mismatch or internal loop.
	 * There is an internal loop when all the base pairs in the duplex are not complementary. if there are gaps 
	 * in the sequences, if no one of the sequences is only composed of gaps (-) between the positions pos1 and pos2 in the duplex,
	 * there is also an internal loop (but asymmetric).
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return true if the pattern between the positions pos1 and pos2 in the duplex is a single mismatch, a tandem mismatch or an internal loop.
	 * Ex : 
	 * (A, G) is a single mismatch
	 * (AC, CC) is a tandem mismatch
	 * (ACGTGC, CCTCAT) is a symmetric internal loop
	 * (ACGTGC, CC-CAT) is an asymmetric internal loop
	 */
	public boolean isMismatch(int pos1, int pos2){
		
		if (pos2 > getDuplexLength() - 1 || pos1 < 0){
			return false;
		}
		int numbergapSequence = 0;
		int numbergapComplementary = 0;
		for (int i = pos1; i <= pos2; i++){
			BasePair pair = duplex.get(i);
			if (isMismatchPair(i) == false){
				if (pair.isUnpaired() == false || (pair.isUnpaired() && (pair.isWatsonCrickBottomBase() == false && pair.isFrequentNucleicAcidTopBase() == false))){
					return false;
				}
				else if (pair.isUnpaired() && pair.getTopAcid().equals("-")){
					numbergapSequence++;
				}
				else if (pair.isUnpaired() && pair.getBottomAcid().equals("-")){
					numbergapComplementary++;
				}
			}
		}
		if (numbergapSequence == pos2 - pos1 + 1 || numbergapComplementary == pos2 - pos1 + 1){
			return false;
		}
		return true;
	}
	
	/**
	 * to check if the pattern between the positions pos1 and pos2 in the duplex is a single or long bulge loop.
	 * There is an bulge loop when one of the strands is only composed of gaps (-) between the positions pos1 and pos2
	 * int the duplex.
	 * @param pos1 : starting position of the pattern in the duplex
	 * @param pos2 : ending position of the pattern in the duplex
	 * @return true if the pattern between the positions pos1 and pos2 in the duplex is a single or long bulge loop.
	 * Ex : 
	 * (T, -) is a single bulge loop (this base pair is not a terminal base pair, otherwise it is a dangling end)
	 * (ACT, ---) is a long bulge loop
	 */
	public boolean isBulgeLoop(int pos1, int pos2){
		if (arePositionsOutOfRange(pos1, pos2)){
			return false;
		}

		if ((getSequence(pos1, pos2).contains("-") && getComplementary(pos1, pos2).contains("-")) || (getSequence(pos1, pos2).contains("-") == false && getComplementary(pos1, pos2).contains("-") == false)){
			return false;
		}
		
		int numberGapSequence = 0;
		int numberGapComplementary = 0;
		for (int i = pos1; i <= pos2; i++){
			BasePair pair = duplex.get(i);
			if (pair.getTopAcid().equals("-")){
				numberGapSequence ++;
				if (pair.isWatsonCrickBottomBase() == false){
					return false;
				}
			}
			else if (pair.getBottomAcid().equals("-")){
				numberGapComplementary ++;
				if (pair.isFrequentNucleicAcidTopBase() == false){
					return false;
				}
			}
		}
		
		if (numberGapSequence < pos2 - pos1 + 1 && numberGapComplementary < pos2 - pos1 + 1){
			return false;
		}
		
		return true;
	}
	
	/**
	 * to check if the nucleic acids in the base pair at the position pos in the duplex are existing nucleic acids. (exist in the
	 * existingNucleicAcids of BasePair)
	 * @param pos : position of the nucleic acid in the duplex
	 * @return true if the two nucleic acids are known by MELTING. 
	 */
	public boolean isRegisteredNucleicAcid(int pos){
		if (pos > getDuplexLength() - 1 || pos < 0){
			return false;
		}
		BasePair pair = duplex.get(pos);
		if (BasePair.getExistingNucleicAcids().contains(pair.getTopAcid()) == false && BasePair.getExistingNucleicAcids().contains(pair.getBottomAcid()) == false){
			return false;
		}
		return true;
	}
	
	/**
	 * This method is called to get the name of a specific nucleic acid in the base pair (BasePair object).
	 * @param pair
	 * @return the SpecificAcidNames enum which represents the name of the specific nucleic acid in the base pair.
	 */
	public SpecificAcidNames getModifiedAcidName(BasePair pair){
		SpecificAcidNames name;	

		if (pair != null && modifiedAcidNames.containsKey(pair.getTopAcid())){
			name = modifiedAcidNames.get(pair.getTopAcid());
			return name;
		}
		
		else if (pair != null && modifiedAcidNames.containsKey(pair.getBottomAcid())){
			name = modifiedAcidNames.get(pair.getBottomAcid());
			return name;
		}
		return null;
	}
	
	/**
	 * to check if there is at least one pyrimidine in the base pair at the position pos of the duplex ArrayList.
	 * @param pos : position of the base pair in the duplex
	 * @return true if the TopAcid String is a pyrimidine ("C", "T" or "U") or if the BottomAcid String is a pyrimidine.
	 */
	public boolean isAPyrimidineInThePosition(int pos){
		BasePair pair = duplex.get(pos);
		if (pair.isTopBasePyrimidine() || pair.isBottomBasePyrimidine()){
			return true;
		}
		return false;
	}
	
	/**
	 * To check if there is a GG base pair adjacent to a AA base pair or a base pair containing a pyrimidine ("C", "T" or "U"). 
	 * @param pos : position of the Crick' pair in the duplex
	 * @return true if there is a GG base pair adjacent to a AA base pair or a base pair containing a pyrimidine ("C", "T" or "U").
	 */
	public boolean isTandemMismatchGGPenaltyNecessary(int pos){
		BasePair pair = duplex.get(pos);
		BasePair secondpair = null;
		if (pos + 1 > getDuplexLength() - 1){
			return false;
		}
		else{
			secondpair = duplex.get(pos + 1);
		}
		if ((pair.isBasePairStrictlyEqualTo("G", "G") && secondpair.isBasePairStrictlyEqualTo("A", "A")) || (secondpair.isBasePairStrictlyEqualTo("G", "G") && pair.isBasePairStrictlyEqualTo("A", "A"))){
			return true;	
		}
		else if ((pair.isBasePairStrictlyEqualTo("G", "G") && isAPyrimidineInThePosition(pos + 1)) || (secondpair.isBasePairStrictlyEqualTo("G", "G") && isAPyrimidineInThePosition(pos))){
			return true;
		}
		return false;
	}
	
	/**
	 * to check if there is an AG or GA base pair adjacent to an UC or CU base pair or a CC base pair
	 * adjacent to an AA base pair.
	 * @param pos : positions of the Crick's pair in the duplex
	 * @return true if there is an AG or GA base pair adjacent to an UC or CU base pair or a CC base pair
	 * adjacent to an AA base pair. 
	 */
	public boolean isTandemMismatchDeltaPPenaltyNecessary(int pos){
		BasePair pair = duplex.get(pos);
		BasePair secondpair = null;
		if (pos + 1 > getDuplexLength() - 1){
			return false;
		}
		else{
			secondpair = duplex.get(pos + 1);
		}

		if (pair.isBasePairEqualTo("A", "G") || secondpair.isBasePairEqualTo("A", "G")){
			if (pair.isBasePairEqualTo("C", "U") || secondpair.isBasePairEqualTo("C", "U")){
				return true;
			}
			else if (pair.isBasePairEqualTo("C", "C") || secondpair.isBasePairEqualTo("C", "C")){
				return true;
			}
		}
		else if ((pair.isBasePairEqualTo("U", "U") && secondpair.isBasePairEqualTo("A", "A")) || (secondpair.isBasePairEqualTo("U", "U") && pair.isBasePairEqualTo("A", "A"))){
			return true;
		}
		return false;
	}

	/**
	 * to chack if there is no gap (an unpaired nucleic acid) in the duplex between theb positions pos1 and pos2
	 * @param pos1 : starting position of the subsequence in the duplex
	 * @param pos2 : ending position of the subsequence in the duplex
	 * @return false if one of the sequences composing the duplex contains at least one gap ("-") between the positions pos1 and pos2 
	 */
	public boolean isNoGapInSequence(int pos1, int pos2){
		if (getSequence(pos1, pos2).contains("-") || getComplementary(pos1, pos2).contains("-")){
			return false;
		}
		return true;
	}
	
	/**
	 * To check if the String sequence and String complementary are symmetric between the positions pos1 and pos2
	 * in the duplex ArrayList of the NucleotidSequences object. The two sequences are symmetric if the inverse of one of them
	 * is equal to the other.
	 * @param pos1 : starting position of the subduplex in the duplex ArrayList of NucleotidSequences object.
	 * @param pos2 : ending position of the subduplex in the duplex ArrayList of NucleotidSequences object.
	 * @return true if the String sequence and the String complementary are symmetric between the positions pos1 and pos2
	 * in the duplex.
	 */
	public boolean isSymmetric(int pos1, int pos2){
		for (int i = pos1; i < pos2; i++){
			if (duplex.get(i).getTopAcid() != duplex.get(pos2 - pos1 - i).getBottomAcid()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * gives the type of the internal loop at the positions pos1 + 1 and pos2 - 1 in the duplex ArrayList of the NucleotidSequences object.
	 * @param pos1 : position preceding the starting position of the internal loop
	 * @param pos2 : position following the ending position of the internal loop
	 * @return String type of the internal loop at the positions pos1 + 1 and pos2 - 1 in the duplex ArrayList.
	 * The type of the internal loop is determined by the number of nucleic acids from the sequence String which are included in the internaql loop
	 * and the number of nucleic acids from the complementary String which are included in the internaql loop
	 * Ex of type of internal loop : "1x2", "2x3", "4x4", ....
	 */
	public String getInternalLoopType(int pos1, int pos2){
		int internalLoopSize1 = 0;
		int internalLoopSize2 = 0;
		for (int i = pos1 + 1; i <= pos2 - 1; i++ ){
			BasePair pair = duplex.get(i);
			if (pair.getTopAcid().equals("-") == false){
				internalLoopSize1 ++;
			}
			
			if (pair.getBottomAcid().equals("-") == false){
				internalLoopSize2 ++;
			}
		}
		if (isAsymetricInternalLoop(pos1, pos2)){
			if (Math.min(internalLoopSize1, internalLoopSize2) == 1 && Math.max(internalLoopSize2, internalLoopSize1) > 2){
				return Integer.toString(internalLoopSize1) + "x" + "n_n>2";
			}
			else if ((internalLoopSize1 == 1 && internalLoopSize2 == 2) || (internalLoopSize1 == 2 && internalLoopSize2 == 1) || (internalLoopSize1 == 2 && internalLoopSize2 == 3) || (internalLoopSize1 == 3 && internalLoopSize2 == 2)){
				return Integer.toString(Math.min(internalLoopSize1, internalLoopSize2)) + "x" + Integer.toString(Math.max(internalLoopSize1, internalLoopSize2));
			}
			else {
				return "others_non_2x2";
			}
		}
		else{
			if ((internalLoopSize1 == 2 && internalLoopSize2 == 2) || (internalLoopSize1 == 1 && internalLoopSize2 == 1)){
				return Integer.toString(Math.min(internalLoopSize1, internalLoopSize2)) + "x" + Integer.toString(Math.max(internalLoopSize1, internalLoopSize2));
			}
			else {
				return "others_non_2x2";
			}
		}
	}

	/**
	 * This method is called to get the first mismatch of the internal loop at the positions pos1 + 1 and pos2 - 1 
	 * in the duplex ArrayList of the NucleotidSequences object. 
	 * @param pos1 : the position preceding the internal loop. 
	 * @return String [] containing the first Crick's pair of the internal loop at the positions pos1 + 1 and pos2 - 1.
	 * The nucleic acids in the first base pair of the crick's pair are converted into purine or pyrimidine.
	 * this returned String [] contains the nucleic acids from the sequence String (5'3') and the nucleic acids from
	 * the complementary String (3'5').
	 * Ex : 
	 * internal loop : (ATTGCC, TGCGCG)
	 * The first Crick's pair in the loop : (AT, TG)
	 * The first msimatch in the loop : (T, G) 
	 * String [] first mismatch => {RT, YG}
	 */
	public String [] getLoopFistMismatch(int pos1){
		String mismatch1 = convertToPyr_Pur(duplex.get(pos1).getTopAcid()) + duplex.get(pos1 + 1).getTopAcid();
		String mismatch2 = convertToPyr_Pur(duplex.get(pos1).getBottomAcid()) + duplex.get(pos1 + 1).getBottomAcid();
		String [] firstMismatch = {mismatch1, mismatch2};
		return firstMismatch;
	}
	
	/**
	 * This method is called to get the base pairs adjacent to the single bulge loop (unpaired nucleic acid) at the position pos1 in the duplex ArrayList
	 * @param pos1 : position preceding the unpaired nucleic acid in the single bulge loop. 
	 * @return String [] containing the nucleic acids adjacent to the bulge loop from the sequence String (5'3')
	 * and the nucleic acids adjacent to the bulge loop from the complementary String (5'3')
	 */
	public String [] getSingleBulgeNeighbors(int pos1){
		String NNPair1 = duplex.get(pos1).getTopAcid() + duplex.get(pos1 + 2).getTopAcid();
		String NNPair2 = duplex.get(pos1).getBottomAcid() + duplex.get(pos1 + 2).getBottomAcid();
		String [] NNPair = {NNPair1, NNPair2};
		return NNPair;
	}
	
	/**
	 * to check if the internal loop between the positions pos1 + 1 and pos2 - 1 in the duplex ArrayList
	 * is asymetric. An internal loop is asymetric if one of the strands contains gap(s). 
	 * @param pos1 : position preceding the starting position of the internal loop
	 * @param pos2 : position following the ending position of the internal loop
	 * @return true if the internal loop between the positions pos1 + 1 and pos2 - 1 in the duplex ArrayList is asymetric.
	 */
	public boolean isAsymetricInternalLoop(int pos1, int pos2){
		
		for (int i= pos1 + 1; i <= pos2 - 1; i++){
			BasePair pair = duplex.get(i);
			if (pair.isUnpaired()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method is called to get a substring of the sequence String between the positions pos1 and pos2 in the duplex.
	 * This substring is converted in the type precised by the hybridizationType String.
	 * @param pos1 : starting position of the subsequence
	 * @param pos2 : ending position of the subsequence
	 * @param hybridizationType : type of sequence ("dna" or "rna")
	 * @return substring of the sequence String between pos1 and pos2 in the new type precised by the hybridizationType String
	 */
	public String getSequence(int pos1, int pos2, String hybridizationType){
		
		return convertSequence(getSequence(pos1, pos2), hybridizationType);
	}
	
	/**
	 * This method is called to get a substring of the complementary String between the positions pos1 and pos2 in the duplex.
	 * This substring is converted in the type precised by the hybridizationType String.
	 * @param pos1 : starting position of the subsequence
	 * @param pos2 : ending position of the subsequence
	 * @param hybridizationType : type of sequence ("dna" or "rna")
	 * @return substring of the complementary String between pos1 and pos2 in the new type precised by the hybridizationType String
	 */
	public String getComplementary(int pos1, int pos2, String hybridizationType){
		return convertSequence(getComplementary(pos1, pos2), hybridizationType);
	}

	/**
	 * computes the number of nucleic acids in the internal loop between the positions pos1 + 1 and
	 * pos2 - 1 in the duplex ArrayList of the NucleotidSequences object.
	 * @param pos1 : position preceding the starting position of the internal loop
	 * @param pos2 : position following the ending position of the internal loop
	 * @return int number nucleic acids in the internal loop at the positions pos1 + 1 : pos2 - 1 in the duplex ArrayList.
	 */
	public int computesInternalLoopLength (int pos1, int pos2){
		int loop = 2 * (pos2 - pos1 - 1);
		
		for (int i = pos1 + 1; i <= pos2 - 1; i++){
			BasePair pair = duplex.get(i);
			if (pair.isUnpaired()){
				loop--;
			}
			if (pair.isUnpaired()){
				loop--;
			}
		}
		return loop;
	}

	/**
	 * This method is called to get the duplex of NucleotidSequences.
	 * @return the duplex ArrayList containing BasePair objects.
	 */
	public ArrayList<BasePair> getDuplex() {
		return duplex;
	}
	
	/**
	 * To get the orientation of the subsequence sequence.
	 * @param  sequence : a substring of sequence String or complementary String.
	 * @param pos1 : the starting position of the substring in the sequence String or complementary String
	 * @param pos2 : the ending position of the substring in the sequence String or complementary String
	 * @return String "5'3'" if the substring sequence is a substring of the sequence (5'3') or String "3'5'"
	 * if the substring sequence is a substring of the complementary sequence (3'5').
	 * If the substring is neither a substring of the sequence, nor a substring of the complementary sequence
	 * a SequenceException is thrown.
	 */
	public String getSequenceSens(String sequence, int pos1, int pos2){
		if (sequence.equals(getSequence(pos1, pos2))){
			return "5'3'";
		}
		else if (sequence.equals(getComplementary(pos1, pos2))){
			return "3'5'";
		}
		else {
			throw new SequenceException("\n We don't recognize the structure " + getSequence(pos1, pos2));
		}
	}
	
	/**
	 * computes the percentage of GC base pairs in the duplex of NucleotidSequences.
	 * @return double percentage of GC base pairs in the duplex.
	 */
	public double computesPercentGC(){
		double numberGC = 0.0;
		
		for (int i = 0; i < getDuplexLength();i++){
			BasePair pair = duplex.get(i);
			if (pair.isBasePairEqualTo("G", "C")){
				numberGC++;
			}
		}
		
		return numberGC / (double)getDuplexLength() * 100.0;
	}
	
	/**
	 * Computes the percentage of mismatching base pairs in the duplex of NucleotidSequences.
	 * @return double percentage of mismatching base pairs in the duplex
	 */
	public double computesPercentMismatching(){
		double numberMismatching = 0.0;
	
		for (int i = 0; i < getDuplexLength();i++){
			BasePair pair = duplex.get(i);
			if (pair.isComplementaryBasePair() == false){
				numberMismatching++;
			}
		}
		return numberMismatching / (double)getDuplexLength() * 100.0;
	}
	
	/**
	 * To check if there is at least one GC base pair in the duplex of NucleotidSequences.
	 * @return true if at least one GC base pair exists in the duplex.
	 */
	public boolean isOneGCBasePair(){
		for (int i = 0; i < getDuplexLength();i++){
			BasePair pair = duplex.get(i);
			if (pair.isBasePairEqualTo("G", "C")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method is called to get the length of the ArrayList duplex of NucleotidSequences.
	 * @return int length which represents the length of the nucleic acid duplex.
	 */
	public int getDuplexLength(){
		return duplex.size();
	}
	
	/**
	 * This method is called to get the encoded sequence String of NucleotidSequences.
	 * @return String sequence of NucleotidSequences.
	 */
	public String getSequence() {
		return getSequence(0, getDuplexLength() - 1);
	}

	/**
	 * This method is called to get the encoded complementary String of NucleotidSequences.
	 * @return String complementary of NucleotidSequences.
	 */
	public String getComplementary() {
		return getComplementary(0, getDuplexLength() - 1);
	}
	
	/**
	 * This method is called to get a substring between two positions of the sequence String.
	 * @param pos1 : starting position of the subsequence
	 * @param pos2 : ending position of the subsequence
	 * @return the sequence substring between pos1 and pos2.
	 * If pos1 and pos2 are out of the duplex length range, a SequenceException is thrown.
	 */
	public String getSequence(int pos1, int pos2){
		if (arePositionsOutOfRange(pos1, pos2)){
			throw new SequenceException("\n The length of the duplex has to be inferior to " + pos2 + 1 + "and superior to 0.");
		}
		
		int [] positions = convertAcidPositionsIntoStringPositions(pos1, pos2);
		pos1 = positions[0];
		pos2 = positions[1];
		return sequence.substring(pos1, pos2);
	}
	
	/**
	 * This method is called to get a substring between two positions of the complementary String.
	 * @param pos1 : starting position of the subsequence
	 * @param pos2 : ending position of the subsequence
	 * @return the complementary substring between pos1 and pos2.
	 * If pos1 and pos2 are out of the duplex length range, a SequenceException is thrown.
	 */
	public String getComplementary(int pos1, int pos2){
		if (pos1 < 0 || pos2 > getDuplexLength() - 1){
			throw new SequenceException("\n The length of the duplex has to be inferior to " + pos2 + 1 + "and superior to 0.");
		}
		
		int [] positions = convertAcidPositionsIntoStringPositions(pos1, pos2);
		pos1 = positions[0];
		pos2 = positions[1];
		return complementary.substring(pos1, pos2);
	}
	
	/**
	 * to get the two nucleic acids which are from the sequence (5'3') in the Crick's pair at the position "pos" in the duplex.
	 * @param pos : position of the crick's pair in the duplex.
	 * @return a substring of the sequence String which represents the nucleic acids from the sequence (5'3') in the crick'base pair at the position
	 * pos. 
	 */
	public String getSequenceNNPair(int pos){
		return getSequence(pos, pos+1);
	}
	
	/**
	 * to get the two nucleic acids which are from the complementary sequence (3'5') in the Crick's pair at the position "pos" in the duplex.
	 * @param pos : position of the crick's pair in the duplex.
	 * @return a substring of the complementary String which represents the nucleic acids from the complementary sequence (3'5') in the crick'base pair at the position
	 * pos. 
	 */
	public String getComplementaryNNPair(int pos){
		return getComplementary(pos, pos+1);
	}
	
	/**
	 * to get the two unlocked nucleic acids which are from the sequence (5'3') in the Crick's pair at the position "pos" in the duplex.
	 * @param pos : position of the crick's pair in the duplex.
	 * @return a substring of the sequence String which represents the unlocked nucleic acids from the sequence (5'3') in the crick'base pair at the position
	 * pos. 
	 */
	public String getSequenceNNPairUnlocked(int pos){
		return getSequence(pos, pos+1).replace("L", "").replace(" ", "");
	}
	
	/**
	 * to get the two unlocked nucleic acids which are from the complementary sequence (3'5') in the Crick's pair at the position "pos" in the duplex.
	 * @param pos : position of the crick's pair in the duplex.
	 * @return a substring of the complementary String which represents the unlocked nucleic acids from the complementary sequence (3'5') in the crick'base pair at the position
	 * pos. 
	 */
	public String getComplementaryNNPairUnlocked(int pos){
		return getComplementary(pos, pos + 1).replace("L", "").replace(" ", "");
	}
	
	/**
	 * to get the two nucleic acids from each sequence in the Crick's pair at the position "pos" in the duplex. we convert the hydroxyadenine (A*) into an adenine (A).
	 * @param pos : position of the crick's pair in the duplex.
	 * @return a String [] containing the substrings which represent the nucleic acids from each sequence in the crick'base pair at the position
	 * pos. The hydroxyadenine is replaced by an adenine.
	 */
	public String[] getNNPairWithoutHydroxyA(int pos) {
		String[] pair1 = removeHydroxyA(getDuplex().get(pos).getTopAcid(), getDuplex().get(pos).getBottomAcid());
		String[] pair2 = removeHydroxyA(getDuplex().get(pos+1).getTopAcid(), getDuplex().get(pos+1).getBottomAcid());
		return new String[] { pair1[0] + pair2[0], pair1[1] + pair2[1] };
	}
	
	/**
	 * calculates the number of base pairs (base1, base2) at the postions pos1 and pos2 in the duplex of NucleotidSequences.
	 * @param  base1
	 * @param  base2
	 * @param pos1 : starting position of the subsequence in the duplex of NucleotidSequences.
	 * @param pos2 : ending position of the subsequence in the duplex of NucleotidSequences.
	 * @return int number of base pairs (base1, base2) at the postions pos1 and pos2 in the duplex of NucleotidSequences.
	 */
	public double calculateNumberOfTerminal(String base1, String base2, int pos1, int pos2){
		return calculateNumberOfTerminal(base1, base2, this.duplex, pos1, pos2);
	}
	
	/**
	 * calculates the number of terminal 5'T3'A in the subduplex between pos1 and pos2
	 * @param pos1 : starting position of the subsequence in the duplex of NucleotidSequences.
	 * @param pos2 : ending position of the subsequence in the duplex of NucleotidSequences.
	 * @return int number of terminal 5'T3'A in the subduplex between pos1 and pos2. 5'A/3'T means that at the position
	 * pos1, the base pair is (T,A) and at the position pos2, the base pair is (A,T).
	 */
	public double getNumberTerminal5TA(int pos1, int pos2){
		double number5TA = 0;
		BasePair firstTerminalBasePair = duplex.get(pos1);
		BasePair lastTerminalBasePair = duplex.get(pos2);
		
		if (firstTerminalBasePair.isBasePairStrictlyEqualTo("T", "A")){
			number5TA ++;
		}
		if (lastTerminalBasePair.isBasePairStrictlyEqualTo("A", "T")){
			number5TA ++;
		}
		return number5TA;
	}
	
	/**
	 * find the real starting and ending positions of the duplex. The real starting and ending positions
	 * of the duplex are the first and last positions in the duplex where the nucleic acids are paired.
	 * @return int [] containing the first and last positions in the duplex where the nucleic acids are paired.
	 */
	public int [] removeTerminalUnpairedNucleotides(){
		int indexStart = 0;
		while (indexStart <= getDuplexLength() - 1){
			BasePair pair = duplex.get(indexStart);
			if (pair.isUnpaired()){
				indexStart ++;
			}
			else {
				break;
			}
		}

		if (indexStart >= getDuplexLength() - 1){
			throw new SequenceException("/n There is no possible hybridization with the sequences " + this.sequence + " and " + this.complementary + ".");
		}
		
		int indexEnd = getDuplexLength() - 1;
		while (indexEnd >= 0){
			BasePair pair = duplex.get(getDuplexLength() - indexEnd - 1);
			if (pair.isUnpaired()){
				indexEnd --;
			}
			else {
				break;
			}
		}
		if (indexEnd <= 0){
			throw new SequenceException("/n There is no possible hybridization with the sequences " + this.sequence + " and " + this.complementary + ".");
		}
		int [] positions = {indexStart, indexEnd};
		return positions;
	}
	
	/**
	 * To check if the base pair at the position pos in the duplex is (base1, base2) or (base2, base1).
	 * @param  base1
	 * @param  base2
	 * @param pos : position of the base pair in the duplex
	 * @return true if the tuple (TopAcid, BottomAcid) of BasePair at the position pos in the duplex ArrayList
	 * is (base1, base2) or (base2, base1).
	 */
	public boolean isBasePair(String base1, String base2, int pos){
		BasePair pair = duplex.get(pos);
		if (pair.isBasePairEqualTo(base1, base2)){
			return true;
		}
		return false;
	}
	
	// private methods
	
	/**
	 * encodes the complementary of NucleotidSequences.
	 * @param  sequence : the sequence (5'3')
	 * @param Buffer comp : the complementary sequence (3'5') to encode
	 * @return the encoded complementary String
	 */
	private String encodeComplementary(String sequence, StringBuffer comp){
		int pos = 0;
		while (pos < sequence.length()){
			String acid = getNucleicAcid(sequence, pos);
			if (acid != null){
				if (acid.equals("X_T") || acid.equals("X_C")){
					comp.insert(pos," ");
					comp.insert(pos + 1, " ");
					comp.insert(pos + 2, " ");
				}
				else if (acid.length() > 1){
					if (acid.charAt(1) == 'L'){
						comp.insert(pos + 1," ");
					}
					else if(acid.equals("A*")){
						comp.insert(pos + 1," ");
					}
				}
				pos += acid.length();
			}
			else {
				pos ++;
			}
		}	
		
		return comp.toString();
	}
	
	/**
	 * encodes the sequence of NucleotidSequences.
	 * @param  complementary : the complementary sequence (3'5')
	 * @param Buffer seq : the sequence (5'3') to encode
	 * @return the encoded sequence String
	 */
	private String encodeSequence(String complementary, StringBuffer seq){
		int pos = 0;
		
		while (pos < complementary.length()){
			String acid = getNucleicAcid(complementary, pos);
			if (acid != null){
				if (acid.equals("X_T") || acid.equals("X_C")){
					seq.insert(pos," ");
					seq.insert(pos + 1, " ");
					seq.insert(pos + 2, " ");
				}
				else if (acid.length() > 1){
					if (acid.charAt(1) == 'L'){
						seq.insert(pos + 1," ");
					}
					else if (acid.equals("A*")){
						seq.insert(pos + 1," ");
					}
				}
				pos += acid.length();
			}
			else{
				pos ++;
			}
		}	
		
		return seq.toString();
	}
	
	/**
	 * To check if two positions are in the duplex length range.
	 * @param pos1 : starting position in the duplex
	 * @param pos2 : ending position in the duplex
	 * @return true if at least one of the positions is out of the duplex length range.
	 */
	private boolean arePositionsOutOfRange(int pos1, int pos2){
		if (pos1 < 0 || pos2 > getDuplexLength() - 1){
			return true;
		}
		return false;
	}
	
	/**
	 * To get the positions of the nucleic acid or the subsequence in the String sequence (or String complementary).
	 * @param pos1 : starting position of the subsequence in the duplex
	 * @param pos2 : ending position of the subsequence in the duplex
	 * @return int [] positions which contains the starting and ending positions in the sequence String
	 * (or complementary String).
	 */
	private int [] convertAcidPositionsIntoStringPositions(int pos1, int pos2){
		pos1 = this.duplex.get(pos1).getPosition();
		pos2 = this.duplex.get(pos2).getPosition() + this.duplex.get(pos2).getLengthAcid();
		
		int [] positions = {pos1, pos2};
		return positions;
	}
	
	/**
	 * converts a sequence String into a new type (hybridizationType) of sequence.
	 * @param  sequence : sequence to convert in a new type. ("dna" or "rna")
	 * @param  hybridizationType : type of the sequence ("dna" or "rna")
	 * @return String converted sequence. if the hybridizationType String is "rna", the 
	 * sequence String is converted into a RNA sequence and if the hybridizationType String 
	 * is "dna", the sequence String is converted into a RNA sequence. 
	 * If the hybridizationType String is neither "rna", nor "dna", a SequenceException is thrown.
	 */
	private String convertSequence(String sequence, String hybridizationType){
		char acidToRemplace;
		char remplacingAcid;
		
		if (hybridizationType.equals("dna")){
			remplacingAcid = 'T';
			acidToRemplace = 'U';
		}
		else if (hybridizationType.equals("rna")) {
			remplacingAcid = 'U';
			acidToRemplace = 'T';
		}
		else {
			throw new SequenceException("\n It is impossible to convert this sequences in sequences of type " + hybridizationType + ". MELTING can just convert a DNA sequence into a RNA sequence and a RNA sequence into a DNA sequence.");
		}
		
		String newSequence = sequence.replace(acidToRemplace, remplacingAcid);

		return newSequence;
	}

	// public static methods
	
	/**
	 * This method is called to get the duplex of NucleotidSequences.
	 * @param  sequence : sequence (5'3')
	 * @param  complementary : complementary sequence (3'5')
	 * @return the duplex of NucleotidSequences.
	 */
	public static ArrayList<BasePair> getDuplexFrom(String sequence, String complementary){
		ArrayList<BasePair> duplex = new ArrayList<BasePair>();
		int pos = 0;
		while (pos < sequence.length()){
			BasePair pair = getBasePair(sequence, complementary, pos);
			duplex.add(pair);
			pos += pair.getLengthAcid();
		}
		return duplex;
	}
	
	/**
	 * decodes the String sequence and complementary of NucleotidSequences.
	 * @param  sequence : sequence (5'3')
	 * @param  complementary : complementary sequence (3'5')
	 * @return a String [] object containing the decoded String sequence and complementary of NucleotidSequences.
	 */
	public static String [] decodeSequences(String sequence, String complementary){
		StringBuffer seq = new StringBuffer();
		StringBuffer comp = new StringBuffer();

		seq.append(sequence);
		comp.append(complementary);

		String [] sequences = {decodeSequence(sequence), decodeSequence(complementary)};
		return sequences;
	}
	
	/**
	 * creates a NucleotidSequences with two symmetric sequences String created from the
	 * seq1 String an seq2 String.
	 * @param  seq1 : sequence (5'3')
	 * @param  seq2 : complementary sequence (3'5')
	 * @return NucleotidSequences whith the sequence String symmetric to the complementary String.
	 */
	public static NucleotidSequences buildSymetricSequences(String seq1, String seq2){
		StringBuffer symetricSequence = new StringBuffer(seq1.length());
		
		symetricSequence.append(seq1.substring(0, 2));
		symetricSequence.append(seq2.substring(1, 2));
		symetricSequence.append(seq2.substring(0,1));
	
		StringBuffer symetricComplementary = new StringBuffer(seq1.length());
		
		symetricComplementary.append(seq2.substring(0, 2));
		symetricComplementary.append(seq1.substring(1, 2));
		symetricComplementary.append(seq1.substring(0, 1));
		
		return new NucleotidSequences(symetricSequence.toString(), symetricComplementary.toString());
	}
	
	/**
	 * creates the inverse String of the sequence
	 * @param  sequence to inverse
	 * @return String which is the inverse of the sequence
	 */
	public static String getInversedSequence(String sequence){
		StringBuffer newSequence = new StringBuffer(sequence.length());

		for (int i = 0; i < sequence.length(); i++){

			newSequence.append(sequence.charAt(sequence.length() - i - 1)); 
		}
		return newSequence.toString();
	}
	
	/**
	 * To check if a sequence is self complementary. A self complementary sequence is a 
	 * sequence which can get bound or hybridized to itself.
	 * @param  sequence
	 * @return true if the sequence is self complementary.
	 */
	public static boolean isSelfComplementarySequence(String sequence){
		String seq = removeDanglingEnds(sequence);
		BasePair pair = new BasePair(0);

		for (int i = 0; i < seq.length() - 1; i++){
			pair.setTopAcid(seq.substring(i, i + 1));
			pair.setBottomAcid(seq.substring(seq.length() - i - 1, seq.length() - i));
			
			if (pair.isComplementaryBasePair() == false){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * converts a sequence into a String composed of purine and pyrimidine.
	 * A and G are purines (represented by "R" in the new sequence String), C, T and U are
	 * pyrimidines (represented by "Y" in the new sequence String)
	 * @param  sequence to convert in purine-pyrimidine sequence.
	 * @return the new sequence converted into a String composed of purines ("R") and pyrimidines ("Y").
	 * If the sequence String is not only composed of - (gap), A, T, U, G and C, a SequenceExceptions is thrown.
	 */
	public static String convertToPyr_Pur(String sequence){
		StringBuffer newSeq = new StringBuffer(sequence.length());
		for (int i = 0; i <= sequence.length() - 1; i++){
			switch (sequence.charAt(i)) {
			case 'A':
				newSeq.append('R');
				break;
			case 'G':
				newSeq.append('R');
				break;
			case 'U':
				newSeq.append('Y');
				break;
			case 'T':
				newSeq.append('Y');
				break;
			case 'C':
				newSeq.append('Y');
				break;
			case '-':
				newSeq.append('-');
				break;
			default:
				throw new SequenceException("\n There are non Watson-Crick base pairs in the sequence " + sequence + ". MELTING needs Watson-Crick base pairs to convert them into purine/pyrimidine base pairs (A, T, G, C, U).");
			}
		}
		return newSeq.toString();
	}
	
	/**
	 * This method is called to get the modifiedAcidNames of NucleotidSequences.
	 * @return the public static modifiedAcidNames of NucleotidSequences.
	 */
	public static HashMap<String, SpecificAcidNames> getModifiedAcidNames() {
		return modifiedAcidNames;
	}
	
	/**
	 * initialises the modifiedAcidNames of NucleotidSequences.
	 */
	public static void initialiseModifiedAcidHashmap(){
		modifiedAcidNames.put("I", SpecificAcidNames.inosine);
		modifiedAcidNames.put("AL", SpecificAcidNames.lockedNucleicAcid);
		modifiedAcidNames.put("TL", SpecificAcidNames.lockedNucleicAcid);
		modifiedAcidNames.put("CL", SpecificAcidNames.lockedNucleicAcid);
		modifiedAcidNames.put("GL", SpecificAcidNames.lockedNucleicAcid);
		modifiedAcidNames.put("A*", SpecificAcidNames.hydroxyadenine);
		modifiedAcidNames.put("X_C", SpecificAcidNames.azobenzene);
		modifiedAcidNames.put("X_T", SpecificAcidNames.azobenzene);
	}
	
	/**
	 * This method is called when we want to get the complementary of NucleotidSequences with the
	 * type of hybridization "hybridization".
	 * @param  sequence
	 * @param  hybridization : type of hybridization
	 * @return String which represents the complementary sequence (3'5') with the specified nature.(rna, dna)
	 */
	public static String getComplementarySequence(String sequence, String hybridization){
		StringBuffer complementary = new StringBuffer(sequence.length());
		for (int i = 0; i < sequence.length(); i++){
			switch(sequence.charAt(i)){
			case 'A':
				if (hybridization.equals("dnadna") || hybridization.equals("rnadna")){
					complementary.append('T');
				}
				else if (hybridization.equals("rnarna") || hybridization.equals("mrnarna") || hybridization.equals("rnamrna") || hybridization.equals("dnarna")){
					complementary.append('U');
				}
				break;
			case 'T':
				if (i != 0){
					if (sequence.charAt(i - 1) != '_'){
						complementary.append('A');
					}
				}
				else{
					complementary.append('A');
				}
				break;
			case 'C':
				if (i != 0){
					if (sequence.charAt(i - 1) != '_'){
						complementary.append('G');
					}
				}
				else{
					complementary.append('G');
				}
				break;
			case 'G':
				complementary.append('C');
				break;
			case 'U':
				complementary.append('A');
				break;
			case '-':
				complementary.append('-');
				break;
			}
		}
		return complementary.toString();
	}
	
	/**
	 * Checks if the String sequence is composed of nucleic acids known by MELTING.
	 * @param  sequence
	 * @return false if one of the nucleic acids is not known by MELTING. (not registered in the ArrayList existingNucleicAcids of the
	 * BasePair class).
	 * If the length of the sequence String is 0, a SequenceException is thrown.
	 */
	public static boolean checkSequence(String sequence){
		
		int position = 0;
		
		if (sequence.length() == 0){
			throw new SequenceException("\n There is no 5'3' sequence. It has to be entered with the option " + OptionManagement.sequence);
		}
		while (position < sequence.length()){
			String acid = getNucleicAcid(sequence, position);
			if (acid == null){
				return false;
			}
			
			position += acid.length();
		}
		return true;
	}
	
	/**
	 * this method is called to get the sens of the dangling end for the sequences seq1 and seq2
	 * @param  seq1 : String terminal pattern of the sequence (5'3')
	 * @param  seq2 : String terminal pattern of the complementary sequence (3'5')
	 * @return String "5" if the dangling end (the unpaired nucleic acid) is a 5' dangling end and String "3"
	 * if the dangling end is a 3' dangling end.
	 * If both seq1 and seq2 contain "-", or if neither the sequence seq1 nor the sequence seq2 contains dangling ends 
	 * (or gaps for the complementary sequence), a SequenceException is thrown.
	 */
	public static String getDanglingSens(String seq1, String seq2){
		if (seq1.length() == 0){
			return "3";
		}
		else if (seq2.length() == 0){
			return "5";
		}
		else if (seq2.charAt(0) == '-'){
			return "5";
		}
		else if (seq1.charAt(0) == '-'){
			return "3";
		}
		else if (seq2.charAt(seq2.length() - 1) == '-'){
			return "3";
		}
		else if (seq1.charAt(seq1.length() - 1) == '-'){
			return "5";
		}
		else if (seq1.contains("-") == false && seq2.contains("-") == false){
			return null;
		}
		throw new SequenceException("\n We cannot determine the sens of the dangling end " + seq1 + "/" + seq2 + ".");
	}
	
	// private static methods
	
	/**
	 * Creates an ArrayList which contains a list of nucleic acids String from the existingNucleicAcids ArrayList of the BasePair class
	 * Each nucleic acid String can match the first nucleic acid of the seq String. 
	 * @param  seq
	 * @return an ArrayList containing the existing nucleic acids which can match the first nucleic acid of the seq String.
	 * If the ArrayList size is null and there is no existing nucleic acid which can match the first nucleic acid of the seq String,
	 * a SequenceException is thrown.
	 */
	private static ArrayList<String> getListMatchingNucleicAcids(String seq) {
		ArrayList<String> possibleNucleicAcids = new ArrayList<String>();

		for (int i = 0; i < BasePair.getExistingNucleicAcids().size(); i++){
			if (seq.startsWith(BasePair.getExistingNucleicAcids().get(i))){
				possibleNucleicAcids.add(BasePair.getExistingNucleicAcids().get(i));
			}
		}
		
		if (possibleNucleicAcids.size() == 0 && seq.charAt(0) != ' '){
			throw new SequenceException("\n Some nucleic acids are unknown in the sequence " + seq + ". MELTING can recognize A, T, G, C, U, X_C, X_T, A*, Al, Tl, Gl, Cl. Check the manual for further information.");
		}
		
		return possibleNucleicAcids;
	}
	
	/**
	 * extracts the longer nucleic acid String from an ArrayList.
	 * @param possibleNucleicAcids : listing all the existing nucleic acids which can match the nucleic acid we want to extract 
	 * from a String 
	 * @return the longer nucleic acid String of the ArrayList possibleNucleicAcids.
	 */
	private static String extractNucleicAcidFrom(ArrayList<String> possibleNucleicAcids){
		int lengthAcid = 0;
		String acid = null;
		
		for (int i = 0; i < possibleNucleicAcids.size() ; i++){
			if (possibleNucleicAcids.get(i).length() > lengthAcid){
				lengthAcid = possibleNucleicAcids.get(i).length();
				acid = possibleNucleicAcids.get(i);
			}
		}
		
		return acid;
	}
	
	/**
	 * This method is called to get the nucleic acid at the position "pos" in the sequence "sequence"
	 * @param  sequence : represents one of the sequence or a substring of the sequence
	 * @param pos : The nucleic acid position in the sequence
	 * @return nucleic acid String at the position "pos" in the sequence String. If the nucleic acid
	 * is not known or registered, this method return null.
	 */
	private static String getNucleicAcid(String sequence, int pos){
		String seq = sequence.substring(pos);
		ArrayList<String> possibleNucleicAcids = getListMatchingNucleicAcids(seq);
		
		if (possibleNucleicAcids.size() == 0){
			return null;
		}
		else {
			String acid = extractNucleicAcidFrom(possibleNucleicAcids);
			return acid;
		}	
	}
	
	/**
	 * decodes the String "sequence".
	 * @param  sequence
	 * @return the decoded String "sequence".
	 */
	private static String decodeSequence(String sequence){
		
		String newSequence = sequence.replaceAll("X_[TC]", "X").replaceAll(" ", "");

		return newSequence;
	}
	
	/**
	 * removes the dangling ends and terminal gaps in the String sequence.
	 * @param  sequence : the sequence with dangling ends and gaps to remove
	 * @return String sequence without dangling ends and terminal gaps.
	 * If the new sequence String has a length inferior or equal to 0, a SequenceException is thrown.
	 */
	private static String removeDanglingEnds(String sequence){
		StringBuffer newSequence = new StringBuffer();
		newSequence.append(sequence);
		int startIndex = 0;
		
		while (startIndex <= sequence.length() - 1){
			if (sequence.charAt(startIndex) == '-'){
				newSequence.deleteCharAt(0);
				newSequence.deleteCharAt(newSequence.toString().length() - 1);
				startIndex ++;
			}
			else{
				break;
			}
		}
		
		int endIndex = sequence.length() - 1;
		while (endIndex >=0){
			if (sequence.charAt(endIndex) == '-'){
				newSequence.deleteCharAt(newSequence.toString().length() - 1);
				newSequence.deleteCharAt(0);
				endIndex --;
			}
			else{
				break;
			}
		}
		
		if (newSequence.toString().length() == 0){
			throw new SequenceException("\n There is no possible hybridization with this sequence "+ sequence +".");
		}
		return newSequence.toString();
	}
	
	/**
	 * This method is called to get the BasePair object from the String sequence and complementary at the position pos in the duplex 
	 * @param  sequence : sequence (5'3')
	 * @param  complementary : complementary sequence (3'5')
	 * @param pos : position in the sequence where is the base pair.
	 * @return BasePair which represents the nucleic acid base pair at the position pos in the duplex
	 * If neither the nucleic acid from the sequence (5'3') nor the nucleic acid from the complementary sequence (3'5')
	 * is known (acid1 == null and acid2 == null), a SequenceException is thrown.
	 */
	private static BasePair getBasePair(String sequence, String complementary, int pos){
		BasePair acid = new BasePair(pos);
		
		String acid1 = getNucleicAcid(sequence, pos);
		String acid2 = getNucleicAcid(complementary, pos);
		if (acid1 == null && acid2 == null){
			throw new SequenceException("\n The nucleic acids at the position " + pos + " are unknown in the duplex " + sequence + "/" + complementary + ". MELTING can recognize A, T, G, C, U, X_C, X_T, A*, Al, Tl, Gl, Cl. Check the manual for further information.");
		}
		else if (acid1 == null){
			acid1 = sequence.substring(pos, pos + acid2.length());
		}
		else if (acid2 == null){
			acid2 = complementary.substring(pos, pos + acid1.length());
		}
		acid.setTopAcid(acid1);
		acid.setBottomAcid(acid2);
		
		return acid;
	}
	
	/**
	 * To convert the hydroxyadenine (A*) into an adenine (A) in the acid1 String and acid2 String.
	 * @param  acid1 : nucleic acid String from the sequence (5'3')
	 * @param  acid2 : nucleic acid String from the complementary sequence (3'5')
	 * @return String [] containing the acid1 String and acid2 String with the hydroxyadenine replaced by adenine.
	 */
	private static String[] removeHydroxyA(String acid1, String acid2) {
		if (acid1.equals("A*")) {
			return new String[]{"A", "T"};
		}
		if (acid2.equals("A*")) {
			return new String[]{"T", "A"};
		}
		return new String[]{acid1, acid2};
	}
	
	/**
	 * calculates the number of base pair (base1, base2) and (base2, base1) at the positions pos1 and pos2 in the duplex ArrayList.
	 * @param  base1
	 * @param  base2
	 * @param duplex
	 * @param pos1 : starting position of the subsequence in the duplex ArrayList.
	 * @param pos2 : ending position of the subsequence in the duplex ArrayList.
	 * @return int number of base pairs (base1, base2) and (base2, base1) at the positions pos1 and pos2 of the duplex ArrayList.
	 */
	private static double calculateNumberOfTerminal(String base1, String base2, ArrayList<BasePair> duplex, int pos1, int pos2){
		double numberOfTerminal = 0.0;
		BasePair firstTerminalBasePair = duplex.get(pos1);
		BasePair lastTerminalBasePair = duplex.get(pos2);

		if (firstTerminalBasePair.isBasePairEqualTo(base1, base2)){
			numberOfTerminal++;
		}
		
		if (lastTerminalBasePair.isBasePairEqualTo(base1, base2)){
			numberOfTerminal++;
		}
		return numberOfTerminal;	
	}
}
