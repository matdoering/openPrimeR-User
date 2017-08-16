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

/** This class represents a base pair of nucleic acids. The instance variables are :
 * int position : the position of the base pair in the sequence.
 * String topAcid : the nucleic acid from the sequence (5'3').
 * String bottomAcid : the nucleic acid from the complementary sequence (3'5').
 * private static ArrayList<String> existingNucleicAcids : the list registering all the possible nucleic acids
 * known by MELTING.
 * */
public class BasePair {
	
	// Instance variables
	
	/**
	 * String topAcid : the nucleic acid from the sequence (5'3').
	 */
	private String topAcid;
	
	/**
	 * String bottomAcid : the nucleic acid from the complementary sequence (3'5').
	 */
	private String bottomAcid;
	
	/**
	 * int position : the position of the base pair in the sequence.
	 */
	private int position;
	
	/**
	 *private static ArrayList existingNucleicAcids : the list registering all the possible nucleic acids
	 * known by MELTING.
	 */
	private static ArrayList<String> existingNucleicAcids = new ArrayList<String>();
	
	// BasePair constructor
	
	/**
	 * To create a BasePair, we need to give the position in the sequence where starts the String TopAcid or BottomAcid.
	 * @param pos : the position of the base pair in the sequence. It is the starting position of the String
	 * TopAcid or BottomAcid in the String sequence.
	 */
	public BasePair (int pos){
		this.position = pos;
	}
	
	// public methods
	
	/**
	 * This method is called to get the nucleic acid "TopAcid" of BasePair.
	 * @return String TopAcid : The nucleic acid "TopAcid" of BasePair.. It represents the
	 * nucleic acid from the sequence (5'3'). 
	 */
	public String getTopAcid() {
		return topAcid;
	}
	
	/**
	 * To change the TopAcid Value of BasePair..
	 *  
	 */
	public void setTopAcid(String topAcid) {
		this.topAcid = topAcid;
	}
	
	/**
	 * This method is called to get the nucleic acid "BottomAcid" of BasePair..
	 * @return String TopAcid : The nucleic acid "BottomAcid" of BasePair. It represents the
	 * nucleic acid from the complementary sequence (3'5'). 
	 */
	public String getBottomAcid() {
		return bottomAcid;
	}
	
	/**
	 * To change the BottomAcid Value of BasePair.
	 *  
	 */
	public void setBottomAcid(String bottomAcid) {
		this.bottomAcid = bottomAcid;
	}
	
	/**
	 * To check if the two nucleic acids in the base pair are complementary.
	 * @return true if the TopAcid and BottomAcid are complementary. There is complementarity if
	 * the two acids (TopAcid, BottomAcid) of the base pair are (A,T), (T,A), (A,U), (U,A), (C,G) or (G,C).
	 */
	public boolean isComplementaryBasePair(){
		if (topAcid.length() != 1 || bottomAcid.length() != 1){
			return false;
		}
		else {
			char acid1 = topAcid.charAt(0);
			char acid2 = bottomAcid.charAt(0);
			switch(acid1){
				case 'A': 
					if (acid2 == 'T' || acid2 == 'U'){
						return true;
					}
					return false;
			
				case 'T':
					if (acid2 == 'A'){
						return true;
					}
					return false;
			
				case 'G':
					if (acid2 == 'C'){
						return true;
					}
					return false;
		
				case 'C':
					if (acid2 == 'G'){
						return true;
					}
					return false;
			
				case 'U':
					if (acid2 == 'A'){
						return true;
					}
					return false;
			
				default:
					return false;
			}
		}
	}

	/**
	 * Check if the Nucleic acid TopAcid is a frequent nucleic acid. 
	 * @return true if the TopAcid of BasePair is a frequent nucleic acid. A frequent
	 * nucleic acid is A, T, U, G or C.
	 */
	public boolean isFrequentNucleicAcidTopBase(){
	if (topAcid.length() != 1){
		return false;
	}
	else{
		char base = topAcid.charAt(0);
		switch (base) {
		case 'A':
			return true;
		case 'T':
			return true;
		case 'C':
			return true;
		case 'G':
			return true;
		case 'U':
			return true;
		default:
			return false;
		}
	}
}
	
	/**
	 * Check if the Nucleic acid BottomAcid is a frequent nucleic acid. 
	 * @return true if the BottomAcid of BasePair is a frequent nucleic acid. A frequent
	 * nucleic acid is A, T, U, G or C.
	 */
	public boolean isWatsonCrickBottomBase(){
		if (bottomAcid.length() != 1){
			return false;
		}
		else{
			char base = bottomAcid.charAt(0);
			switch (base) {
			case 'A':
				return true;
			case 'T':
				return true;
			case 'C':
				return true;
			case 'G':
				return true;
			case 'U':
				return true;
			default:
				return false;
			}
		}
	}
	
	/**
	 * Check if the nucleic acid represented by TopAcid is a pyrimidine.
	 * @return true if the TopAcid of BasePair is a pyrimidine. C, T and U are pyrimidines.
	 */
	public boolean isTopBasePyrimidine(){
		if (topAcid.length() != 1){
			return false;
		}
		else {
			char base = topAcid.charAt(0);
			switch (base) {
			case 'A':
				return false;
			case 'T':
				return true;
			case 'C':
				return true;
			case 'U':
				return true;
			case 'G':
				return false;
			default:
				return false;
			}
		}
		
	}
	
	/**
	 * Check if the nucleic acid represented by BottomAcid is a pyrimidine.
	 * @return true if the BottomAcid of BasePair is a pyrimidine. C, T and U are pyrimidines.
	 */
	public boolean isBottomBasePyrimidine(){
		if (bottomAcid.length() != 1){
			return false;
		}
		else {
			char base = bottomAcid.charAt(0);
			switch (base) {
			case 'A':
				return false;
			case 'T':
				return true;
			case 'C':
				return true;
			case 'U':
				return true;
			case 'G':
				return false;
			default:
				return false;
			}
		}
	}
	
	/**
	 * Check if the two nucleic acids of the base pair are (base1, base2) or (base2, base1).
	 * @param  base1 : first base (the nucleic acid is represented by a String)
	 * @param  base2 : second base (the nucleic acid is represented by a String) 
	 * @return true if (TopAcid, BottomAcid) is equal to (base1, base2) or (base2, base1).
	 */
	public boolean isBasePairEqualTo(String base1, String base2){
		
		if ((topAcid.equals(base1) && bottomAcid.equals(base2)) || (topAcid.equals(base2) && bottomAcid.equals(base1))){
			return true;
		}
		return false;
	}
	
	/**
	 * Check if the two nucleic acids of the base pair are (base1, base2).
	 * @param  base1 : first base (the nucleic acid is represented by a String)
	 * @param  base2 : second base (the nucleic acid is represented by a String) 
	 * @return true if (TopAcid, BottomAcid) is strictly equal to (base1, base2).
	 */
	public boolean isBasePairStrictlyEqualTo(String base1, String base2){
		
		if (topAcid.equals(base1) && bottomAcid.equals(base2)){
			return true;
		}
		return false;
	}
	
	/**
	 * This method is called to get the length of the nucleic acid.
	 * @return int length : The maximum length between the TopAcid length and the BottomAcid length. 
	 */
	public int getLengthAcid(){
		if (topAcid == null && bottomAcid == null){
			return 0;
		}
		else if (topAcid == null){
			return bottomAcid.length();
		}
		else if (bottomAcid == null){
			return topAcid.length();
		}
		else{
			return Math.max(topAcid.length(), bottomAcid.length());
		}
	}
	
	/**
	 * To check if the nucleic acids in the base pair are paired.
	 * @return true if the nucleic acids in the base pair are paired. When a nucleic acid is unpaired, there is a gap
	 * in the duplex represented by "-". Consequently, the nucleic acids are paired in BasePair if TopAcid
	 * and bottomAcid are not equal to "-".
	 */
	public boolean isUnpaired(){
		if (topAcid.equals("-") || bottomAcid.equals("-")){
			return true;
		}
		return false;
	}
	
	// Public static methods
	
	/**
	 * initialises the existingNucleicAcids of BasePair.
	 */
	public static void initialiseNucleicAcidList(){
		existingNucleicAcids.add("A");
		existingNucleicAcids.add("T");
		existingNucleicAcids.add("U");
		existingNucleicAcids.add("G");
		existingNucleicAcids.add("C");
		existingNucleicAcids.add("I");
		existingNucleicAcids.add("-");
		existingNucleicAcids.add("A*");
		existingNucleicAcids.add("AL");
		existingNucleicAcids.add("TL");
		existingNucleicAcids.add("GL");
		existingNucleicAcids.add("CL");
		existingNucleicAcids.add("UL");
		existingNucleicAcids.add("X_C");
		existingNucleicAcids.add("X_T");
	}
	
	/**
	 * This method is called to get the existingNucleicAcids of BasePair.
	 * @return ArrayList existingNucleicAcids of BasePair.
	 */
	public static ArrayList<String> getExistingNucleicAcids() {
		return existingNucleicAcids;
	}

	/**
	 * This method is called to get the position of BasePair in the duplex.
	 * @return int position of the BasePair object.
	 */
	public int getPosition() {
		return position;
	}
}
