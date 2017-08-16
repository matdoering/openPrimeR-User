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

package melting;

import java.util.HashMap;

import melting.sequences.NucleotidSequences;

/**
 * This class is useful to extract the different thermodynamic parameters from the HashMap which contains them.
 */
public class DataCollect {
	
	// Instance variables
	
	/**
	 * HashMap<String, Thermodynamics> data : contains the set of thermodynamic parameters values.
	 */
	private HashMap<String, Thermodynamics> data = new HashMap<String, Thermodynamics>();
	
	// public methods
	
	/**
	 * This method is called to get the data of DataCollect.
	 * @return the data HashMap<String, Thermodynamics> of DataCollect which contains the thermodynamic parameters.
	 */
	public HashMap<String, Thermodynamics> getData() {
		return data;
	}

	/**
	 * add all of the mappings from the specified HashMap to the data of DataCollect.
	 * @param<String, Thermodynamics> parameters : contains the new mappings to add to the data 
	 * of DataCollect.
	 */
	public void addData(HashMap<String, Thermodynamics> parameters) {
		this.data.putAll(parameters);
	}
	
	// => methods to extract the thermodynamic parameters for different crick's pair models
	
	/**
	 * to get the Thermodynamics containing the parameters for the specified type of terminal base pair.
	 * @param  type : type of the terminal base pair.
	 * @return Thermodynamics object containing the parameters for the specified type of terminal base pair.
	 */
	public Thermodynamics getTerminal(String type){
		Thermodynamics s = data.get("terminal" + type);
		return s;
	}
	
	/**
	 * to get the Thermodynamics containing the parameters for the crick's pair composed of the String seq1 and the String seq2.
	 * @param  seq1 : nucleic acids from the sequence (5'3') in the Crick's pair.
	 * @param  seq2 : nucleic acids from the complementary sequence (3'5') in the Crick's pair. 
	 * @return Thermodynamics object containing the parameters for the crick's pair composed of the String seq1 and the String seq2.
	 */
	public Thermodynamics getNNvalue(String seq1, String seq2){
		Thermodynamics s = data.get("neighbor"+seq1+"/"+seq2);
		if (s == null){
			s = data.get("neighbor"+getSymetricSequencePairs(seq1, seq2));
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the hybridization initiation.
	 * @return Thermodynamics object containing the parameters for the hybridization initiation.
	 */
	public Thermodynamics getInitiation(){
		Thermodynamics s = data.get("initiation");
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the initiation with the specified type of initiation.
	 * @param  type : type of initiation
	 * @return Thermodynamics object containing the parameters for the initiation with the specified type of initiation.
	 */
	public Thermodynamics getInitiation(String type){
		Thermodynamics s = data.get("initiation" + type);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the symmetry correction.
	 * @return Thermodynamics object containing the parameters for the symmetry correction.
	 */
	public Thermodynamics getsymmetry(){
		Thermodynamics s = data.get("symmetry");
		return s;
	}
	
	// => methods to extract the thermodynamic parameters for different specific acid models. (modified nucleic acids)
	
	/**
	 * to get the Thermodynamics object containing the parameters for the crick's pair composed of the String seq1 and the String seq2 with
	 * one of the sequences containing a specific nucleic acid.
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @return Thermodynamics object containing the parameters for the crick's pair composed of the String seq1 and the String seq2 with
	 * one of the sequences containing a specific nucleic acid.
	 */
	public Thermodynamics getModifiedvalue(String seq1, String seq2){

		Thermodynamics s = data.get("modified"+seq1+"/"+seq2);
		if (s == null){
			s = data.get("modified"+getSymetricSequencePairs(seq1, seq2));
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing a specific
	 * nucleic acid and a dangling end.
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @param  sens : sens of the dangling end
	 * @return Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing a specific
	 * nucleic acid and a dangling end.
	 */
	public Thermodynamics getModifiedvalue(String seq1, String seq2, String sens){
		Thermodynamics s = data.get("modified"+seq1+"/"+seq2+"sens"+sens);
		if (s == null){
			s = data.get("modified"+getSymetricSequencePairs(seq1, seq2)+"sens"+sens);
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing an
	 * azobenzene ("X_C" or "X_T"). 
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @return Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing an
	 * azobenzene ("X_C" or "X_T").
	 */
	public Thermodynamics getAzobenzeneValue(String seq1, String seq2){
		String typeBase = "";
		if (seq1.contains("X_T") || seq2.contains("X_T")){
			typeBase = "trans";
		}
		else if (seq1.contains("X_C") || seq2.contains("X_C")){
			typeBase = "cys";
		}
		String [] sequences = NucleotidSequences.decodeSequences(seq1, seq2); 
		
		Thermodynamics s = data.get("modified"+ typeBase + sequences[0]+"/"+sequences[1]);
		
		if (s == null){
			s = data.get("modified"+ typeBase + getSymetricAzobenzeneSequencePairs(sequences[0], sequences[1]));
		}
		
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing a
	 * locked nucleic acid ("AL", "TL", "GL" or "CL"). 
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @return Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing a
	 * locked nucleic acid ("AL", "TL", "GL" or "CL"). 
	 */
	public Thermodynamics getLockedAcidValue(String seq1, String seq2){
		String [] sequences = NucleotidSequences.decodeSequences(seq1, seq2);
		
		Thermodynamics s = data.get("modified"+sequences[0]+"/"+sequences[1]);
		if (s == null){
			s = data.get("modified"+getSymetricLockedSequencePairs(sequences[0], sequences[1]));
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing a
	 * hydroxyadenine ("A*"). 
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @return Thermodynamics object containing the parameters for the String seq1 and the String seq2 with one of the sequences containing a
	 * hydroxyadenine ("A*"). 
	 */
	public Thermodynamics getHydroxyadenosineValue(String seq1, String seq2){
		String sens = NucleotidSequences.getDanglingSens(seq1, seq2);
		String [] sequences = NucleotidSequences.decodeSequences(seq1, seq2);
		
		if (sens == null){
			Thermodynamics s = data.get("modified"+sequences[0]+"/"+sequences[1]);
			if (s == null){
				s = data.get("modified"+getSymetricHydroxyadenineSequencePairs(sequences[0], sequences[1]));
			}
			return s;
		}
		else {
			Thermodynamics s = getModifiedvalue(sequences[0], sequences[1], sens);
			return s;
		}
	}
	
	// => methods to extract the thermodynamic parameters for the dangling end models. (single, second and long dangling ends)

	/**
	 * to get the Thermodynamics object containing the parameters for the dangling end present in the duplex
	 * composed of seq1 and seq2 with the specified sens.
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @param  sens : sens of the dangling end 
	 * @return Thermodynamics object containing the parameters for the dangling end present in the duplex
	 * composed of seq1 and seq2 with the specified sens. 
	 */
	public Thermodynamics getSecondDanglingValue(String seq1, String seq2, String sens){
		seq1 = seq1.replaceAll("-", "");
		seq2 = seq2.replaceAll("-", "");
		Thermodynamics s = data.get("dangling"+seq1+"/"+seq2+"sens"+sens);

		if (s == null){
			s = data.get("dangling"+getSymetricSequencePairs(seq1, seq2)+"sens"+sens);
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the dangling end present in the duplex
	 * composed of seq1 and seq2.
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @return Thermodynamics object containing the parameters for the dangling end present in the duplex
	 * composed of seq1 and seq2. 
	 */
	public Thermodynamics getDanglingValue(String seq1, String seq2){
		String sens = NucleotidSequences.getDanglingSens(seq1, seq2);
		seq1 = seq1.replaceAll("-", "");
		seq2 = seq2.replaceAll("-", "");
		Thermodynamics s = data.get("dangling"+seq1+"/"+seq2+"sens"+sens);

		if (s == null){
			s = data.get("dangling"+getSymetricSequencePairs(seq1, seq2)+"sens"+sens);
		}
		return s;
	}
	
	// => methods to extract the thermodynamic parameters for the mismatch models. (single mismatch, tandem mismatches, internal loops)
	
	/**
	 * to get the Thermodynamics object containing the parameters for the mismatch(es) present in the duplex
	 * composed of seq1 and seq2.
	 * @param  seq1 : sequence (5'3').
	 * @param  seq2 : complementary sequence (3'5'). 
	 * @return Thermodynamics object containing the parameters for the mismatch(es) present in the duplex
	 * composed of seq1 and seq2. 
	 */
	public Thermodynamics getMismatchValue(String seq1, String seq2){
		Thermodynamics s = data.get("mismatch"+seq1+"/"+seq2);
		if (s == null){
			s = data.get("mismatch"+getSymetricSequencePairs(seq1, seq2));
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for an internal loop with a specified size.
	 * @param  size : size of the internal loop.
	 * @return Thermodynamics object containing the parameters for an internal loop with a specified size
	 */
	public Thermodynamics getInternalLoopValue(String size){
		Thermodynamics s = data.get("mismatch"+"size"+size);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the initiation of an internal loop with a specified size.
	 * @param  size : size of the internal loop.
	 * @return Thermodynamics object containing the parameters for the initiation of an internal loop with a specified size
	 */
	public Thermodynamics getInitiationLoopValue(String size){
		Thermodynamics s = data.get("mismatch"+"initiation"+"size"+size);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the initiation of an internal loop.
	 * @return Thermodynamics object containing the parameters for the initiation of an internal loop
	 */
	public Thermodynamics getInitiationLoopValue(){
		Thermodynamics s = data.get("mismatch"+"initiation");
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the mismatching base pair (base1, base2).
	 * @param  base1 : from the sequence (5'3')
	 * @param  base2 : from the complementary sequence (3'5')
	 * @return Thermodynamics object containing the parameters for the mismatching base pair (base1, base2).
	 */
	public Thermodynamics getMismatchParameterValue(String base1, String base2){
		Thermodynamics s = data.get("parameters"+base1+"/"+base2);
		if (s == null){
			s = data.get("parameters"+getSymetricSequencePairs(base1, base2));
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the base pair (base1, base2) next to the mismatching base pair.
	 * @param  base1 : from the sequence (5'3')
	 * @param  base2 : from the complementary sequence (3'5')
	 * @return Thermodynamics object containing the parameters for the base pair (base1, base2) next to the mismatching base pair.
	 */
	public Thermodynamics getClosureValue(String base1, String base2){
		Thermodynamics s = data.get("closure"+"per_"+base1 + "/" + base2);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the the duplex composed of seq1 and seq2 with the
	 * specified type of closing base pairs.
	 * @param  seq1 : from the sequence (5'3')
	 * @param  seq2 : from the complementary sequence (3'5')
	 * @param closing : type of the base pair next to the mismatching base pair..
	 * @return Thermodynamics object containing the parameters for the the duplex composed of seq1 and seq2 with the
	 * specified type of closing base pairs.
	 */
	public Thermodynamics getMismatchValue(String seq1, String seq2, String closing){
		Thermodynamics s = data.get("mismatch" + seq1+"/"+seq2 + "close" + closing);
		if (s == null){
			s = data.get("mismatch"+getSymetricSequencePairs(seq1, seq2) + "close" + closing);
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the penalty of type 'type'.
	 * @param  type : type of the penalty
	 * @return Thermodynamics object containing the penalty of type 'type'.
	 */
	public Thermodynamics getPenalty(String type){
		Thermodynamics s = data.get("penalty" + type);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the internal loop asymmetry.
	 * @return Thermodynamics object containing the parameters for the internal loop asymmetry.
	 */
	public Thermodynamics getAsymmetry(){
		Thermodynamics s = data.get("asymetry");
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the first non canonical pair in the internal loop.
	 * @param  seq1 : from the sequence (5'3')
	 * @param  seq2 : from the complementary sequence (3'5')
	 * @param  loop : type of internal loop.
	 * @return Thermodynamics object containing the parameters for the first non canonical pair in the internal loop.
	 */
	public Thermodynamics getFirstMismatch(String seq1, String seq2, String loop){
		Thermodynamics s = data.get("mismatch"+"first_non_canonical_pair"+"loop"+loop+seq1+"/"+seq2);
		if (s == null){
			s = data.get("mismatch"+"first_non_canonical_pair"+"loop"+loop+getSymetricSequencePairs(seq1, seq2));
		}
		return s;
	}
	
	// => methods to extract the thermodynamic parameters for the hairpin loop models. (not implemented yet)
	
	/**
	 * to get the Thermodynamics object containing the parameters for a Hairpin loop of size 'size'.
	 * @param  size : size of the hairpin loop.
	 * @return Thermodynamics object containing the parameters for a Hairpin loop of size 'size'.
	 */
	public Thermodynamics getHairpinLoopvalue(String size){
		Thermodynamics s = data.get("hairpin"+size);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for a Hairpin loop 'seqLoop' preceded
	 * by the base 'base1' and followed by the base 'lastBase'.
	 * @param  base1 : base preceding the hairpin loop. (from the sequence (5'3'))
	 * @param  lastBase : base following the hairpin loop. (from the sequence (5'3')) 
	 * @param  seqLoop : hairpin loop sequence.
	 * @param  type : type of hairpin loop
	 * @return Thermodynamics object containing the parameters for a Hairpin loop 'seqLoop' preceded
	 * by the base 'base1' and followed by the base 'lastBase'.
	 */
	public Thermodynamics getBonus(String base1, String lastBase, String seqLoop, String type){
		Thermodynamics s = data.get("bonus"+type+base1+seqLoop+"/"+lastBase);
		if (s == null){
			s = data.get("bonus"+type+lastBase+NucleotidSequences.getInversedSequence(seqLoop)+"/"+base1);
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for a penalty of type 'type'
	 * for the specified parameter.
	 * @param  type : type of penalty
	 * @param  parameter
	 * @return Thermodynamics object containing the parameters for a penalty of type 'type'
	 * for the specified parameter.
	 */
	public Thermodynamics getPenalty(String type, String parameter){
		Thermodynamics s = data.get("penalty"+type+"parameter"+parameter);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the terminal mismatching base pair (seq1, seq2)
	 * of a hairpin loop.
	 * @param  seq1 : (5'3') orientation
	 * @param  seq2 : (3'5') orientation
	 * @return Thermodynamics object containing the parameters for the terminal mismatching base pair (seq1, seq2)
	 * of a hairpin loop.
	 */
	public Thermodynamics getTerminalMismatchvalue(String seq1, String seq2){
		Thermodynamics s = data.get("hairpin"+"terminal_mismatch"+seq1+"/"+seq2);
		if (s == null){
			s = data.get("hairpin"+"terminal_mismatch"+getSymetricSequencePairs(seq1, seq2));
		}
		return s;
	}
	
	// => methods to extract the thermodynamic parameters for the CNG models.

	/**
	 * to get the Thermodynamics object containing the parameters for the CNG pattern 'seq1'. 'repeats'
	 * is the number of CNG repeats in the sequence.
	 * @param  repeats : number of CNG repeats in the sequence.
	 * @param  seq1 : CNG pattern (5'3')
	 * @return Thermodynamics object containing the parameters for the CNG pattern 'seq1'.
	 */
	public Thermodynamics getCNGvalue(String repeats, String seq1){
		Thermodynamics s = data.get("CNG"+"repeats"+repeats+seq1);
		return s;
	}
	
	// => methods to extract the thermodynamic parameters for the bulge loop models. (single and long bulge loops)

	/**
	 * to get the Thermodynamics object containing the parameters for the single bulge (seq1, seq2).
	 * @param  seq1 : from the sequence (5'3')
	 * @param  seq2 : from the complementary sequence (3'5')
	 * @return Thermodynamics object containing the parameters for the single bulge (seq1, seq2).
	 */
	public Thermodynamics getSingleBulgeLoopvalue(String seq1, String seq2){
		String sequence1 =seq1.replace("-", "");
		String sequence2 =seq2.replace("-", "");

		Thermodynamics s = data.get("bulge"+sequence1+"/"+sequence2);
		if (s == null){
			s = data.get("bulge"+getSymetricSequencePairs(sequence1, sequence2));
		}
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for a bulge loop of size 'size'.
	 * @param  size : size of the bulge loop
	 * @return Thermodynamics object containing the parameters for a bulge loop of size 'size'.
	 */
	public Thermodynamics getBulgeLoopvalue(String size){
		Thermodynamics s = data.get("bulge"+"size"+size);
		return s;
	}
	
	/**
	 * to get the Thermodynamics object containing the parameters for the initiation of a bulge loop of size 'size'.
	 * @param  size : size of the bulge loop
	 * @return Thermodynamics object containing the parameters for the initiation of a bulge loop of size 'size'.
	 */
	public Thermodynamics getInitiationBulgevalue(String size){
		Thermodynamics s = data.get("bulge"+"initiation"+"size"+size);
		return s;
	}
	
	// private methods
	
	/**
	 * builds the symmetric duplex (5'3' orientation) from the String seq1 and seq2
	 * @param  seq1 : a sequence (5'3')
	 * @param  seq2 : a complementary sequence (3'5')
	 * @return String symmetric duplex (5'3' orientation).
	 * Ex :
	 * seq1 = "AC" (5'3')
	 * seq2 = "TG" (3'5')
	 * symmetric duplex => "GT/CA" (5'3' orientation) with 5'"GT"3' and 3'"CA"5'
	 */
	private String getSymetricSequencePairs(String seq1, String seq2){
		String newSeq1 = NucleotidSequences.getInversedSequence(seq1);
		String newSeq2 = NucleotidSequences.getInversedSequence(seq2);
		return newSeq2+"/"+ newSeq1;
	}
	
	/**
	 * builds the symmetric duplex (5'3' orientation) from the String seq1 and seq2 with one of
	 * the sequences containing an azobenzene ("X_C" or "X_T")
	 * @param  seq1 : a sequence (5'3')
	 * @param  seq2 : a complementary sequence (3'5')
	 * @return String symmetric duplex (5'3' orientation) containing the azobenzene.
	 */
	private String getSymetricAzobenzeneSequencePairs(String seq1, String seq2){
				
		String newSeq1 = NucleotidSequences.getInversedSequence(seq1);
		String newSeq2 = NucleotidSequences.getInversedSequence(seq2);
		
		String sequence1 = newSeq1.replace("C_X", "X_C");
		sequence1 = newSeq1.replace("T_X", "X_T");
		String sequence2 = newSeq2.replace("C_X", "X_C");
		sequence2 = newSeq2.replace("T_X", "X_T");
		return sequence2+"/"+ sequence1;
	}
	
	/**
	 * builds the symmetric duplex (5'3' orientation) from the String seq1 and seq2 with one of
	 * the sequences containing a locked nucleic acid ("AL", "TL", "GL", "CL" or "UL")
	 * @param  seq1 : a sequence (5'3')
	 * @param  seq2 : a complementary sequence (3'5')
	 * @return String symmetric duplex (5'3' orientation) containing the locked nucleic acid.
	 */
	private String getSymetricLockedSequencePairs(String seq1, String seq2){
		
		String newSeq1 = NucleotidSequences.getInversedSequence(seq1);
		String newSeq2 = NucleotidSequences.getInversedSequence(seq2);
		String base;
		String sequence1 = newSeq1;
		String sequence2 = newSeq2;

		if (newSeq1.contains("L")){
			base = newSeq1.substring(1, 2);
			sequence1 = newSeq1.replace("L" + base, base + "L");
		}
		else if(newSeq2.contains("L")){
			base = newSeq2.substring(1, 2);
			sequence2 = newSeq2.replace("L" + base, base + "L");
		}
		return sequence2+"/"+ sequence1;
	}
	
	/**
	 * builds the symmetric duplex (5'3' orientation) from the String seq1 and seq2 with one of
	 * the sequences containing a hydroxyadenine ("A*")
	 * @param  seq1 : a sequence (5'3')
	 * @param  seq2 : a complementary sequence (3'5')
	 * @return String symmetric duplex (5'3' orientation) containing a hydroxyadenine.
	 */
	private String getSymetricHydroxyadenineSequencePairs(String seq1, String seq2){
		
		String newSeq1 = NucleotidSequences.getInversedSequence(seq1);
		String newSeq2 = NucleotidSequences.getInversedSequence(seq2);
		String sequence1 = newSeq1.replace("*A", "A*");
		String sequence2 = newSeq2.replace("*A", "A*");

		return sequence2+"/"+ sequence1;
	}
}
