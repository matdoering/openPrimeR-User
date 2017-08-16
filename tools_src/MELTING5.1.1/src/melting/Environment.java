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

import melting.configuration.OptionManagement;
import melting.exceptions.OptionSyntaxError;
import melting.sequences.NucleotidSequences;

/**
 * This class represents the environment of the sequences for which we want to know the melting temperature.
 * It contains the sequences, the different ions and agents in the solution, the information about the sequences...
 * Instance variables :
 * HashMap<String, Double> concentrations : ion and agent concentrations
 * double oligomerConcentration : oligomer concentration (in excess)
 * int factor : correction factor of the oligomer concentration
 * boolean IsSelfComplementarity
 * String Hybridization : type of hybridization
 * NucleotidSequences sequences : contains the sequences
 * ThermoResult result : contains the results of Melting
 * HashMap<String, String> options : contains the options (default options and options entered by the user)
 */
public class Environment {

	// Instance variables
	
	/**
	 * HashMap<String, Double> concentrations : contains the different ion and agent concentrations
	 */
	private HashMap<String, Double> concentrations = new HashMap<String, Double>();
	
	/**
	 * double oligomerConcentration : oligomer concentration (concentration of the strand in excess if one
	 * strand is in excess).
	 */
	private double oligomerConcentration;
	
	/**
	 * int factor : correction factor for the oligomer concentration. It is 1 if the sequences are
	 * self complementary or if the one strand is in excess and 4 otherwise
	 */
	private int factor;
	
	/**
	 * boolean IsSelfComplementarity : informs if the sequences are self complementary.
	 */
	private boolean IsSelfComplementarity = false;
	
	/**
	 * String Hybridization : type of hybridization
	 */
	private String Hybridization;
	
	/**
	 * NucleotidSequences sequences : contains the sequence (5'3'), the complementary sequence (3'5') and the duplex
	 */
	private NucleotidSequences sequences;
	
	/**
	 * ThermoResult result : contains the results. (computed enthalpy, entropy and melting temperature)
	 */
	private ThermoResult result;
	
	/**
	 * HashMap<String, String> options : contains the options (default options and options entered by the user)
	 */
	private HashMap<String, String> options = new HashMap<String, String>();
	
	// Environment constructor
	
	/**
	 * creates an Environment object from the different options (default options and options entered by the user).
	 * initialises the different instance variables of the Environment object.
	 * If the options HashMap is null, an OptionSyntaxError is thrown.
	 * If all the required ion concentrations (Na, Mg, K, Tris) are 0, an OptionSyntaxError is thrown.
	 * @param options : contains the options (default options and options entered by the user)
	 */
	public Environment(HashMap<String, String> options){
		this.options = options;

		if (options == null){
			throw new OptionSyntaxError("\n Some required options are missing. You have to enter the type of hybridization, the oligomer concentration, the sequences (at less the sequence 5'3') and at less one of the following ion concentrations : Na+, Mg2+, Tris+, K+. Read the manual for further information or see the option " + OptionManagement.meltingHelp);
		}
				
		initialiseConcentrations();
		
		if (isRequiredConcentrations() == false){
			throw new OptionSyntaxError("\n You have to enter at lest one of these ion concentrations : Na+, Mg2+, K+ or Tris+.");
		}
		
		this.oligomerConcentration = Double.parseDouble(options.get(OptionManagement.nucleotides));
		this.Hybridization = options.get(OptionManagement.hybridization).toLowerCase();
		
		if (options.get(OptionManagement.selfComplementarity).equals("true")){
			this.IsSelfComplementarity = true;
			this.factor = 1;
		}
		else if (NucleotidSequences.isSelfComplementarySequence(options.get(OptionManagement.sequence).toUpperCase()) && NucleotidSequences.isSelfComplementarySequence(options.get(OptionManagement.complementarySequence).toUpperCase())){
			this.IsSelfComplementarity = true;
			this.factor = 1;
		}
		else {
			this.IsSelfComplementarity = false;
			this.factor = Integer.parseInt(options.get(OptionManagement.factor));
		}

		sortSquences(this.Hybridization, options.get(OptionManagement.sequence).toUpperCase(), options.get(OptionManagement.complementarySequence).toUpperCase());
		NucleotidSequences.initialiseModifiedAcidHashmap();
		
		this.result = new ThermoResult(0,0,0);

	}
	
	// public methods
	
	/**
	 * sorts the sequences in function of the type of hybridization : In case of DNA/RNA type of hybridization, the sequence (5'3') must be a DNA sequence
	 * and the complementary sequence (3'5') a RNA sequence. In case of mRNA/RNA type of hybridization, the sequence (5'3') must be a mRNA sequence
	 * and the complementary sequence (3'5') a RNA sequence.
	 * Then creates the sequences NucleotidSequences of the Environment object.
	 * @param  hybridization : type of hybridization. Precise the nature of each sequence (DNA, RNA or mRNA)
	 * @param  firstSequence : sequence (5'3')
	 * @param  secondSequence : complementary sequence (3'5')
	 */
	public void sortSquences(String hybridization, String firstSequence, String secondSequence){
		if (hybridization.equals("rnadna") || hybridization.equals("rnamrna")){
			this.sequences = new NucleotidSequences(NucleotidSequences.getInversedSequence(secondSequence), NucleotidSequences.getInversedSequence(firstSequence));
		}
		else {
			this.sequences = new NucleotidSequences(firstSequence, secondSequence);
		}
	}

	/**
	 * This method is called to get the factor of Environment.
	 * @return int factor of Environment.
	 */
	public int getFactor() {
		return factor;
	}
	
	/**
	 * changes the factor value of Environment.
	 * @param factor : new factor value
	 */
	public void setFactor(int factor) {
		this.factor = factor;
	}

	/**
	 * This method is called to get the ThermoResult of Environment.
	 * @return ThermoResult result of Environment.
	 */
	public ThermoResult getResult() {
		return result;
	}

	/**
	 * This method is called to get the sequences NucleotidSequences of Environment.
	 * @return NucleotidSequences sequences of Environment.
	 */
	public NucleotidSequences getSequences() {
		return sequences;
	}

	/**
	 * This method is called to get the oligomerConcentration of Environment.
	 * @return double oligomerConcentration of Environment.
	 */
	public double getNucleotides() {
		return oligomerConcentration;
	}
	
	/**
	 * To check if the sequences are self complementary.
	 * @return true if the sequences are self complementary.
	 */
	public boolean isSelfComplementarity() {
		return IsSelfComplementarity;
	}
	
	/**
	 * changes the boolean value of the isSelfComplementarity boolean.
	 * @param b
	 */
	public void setSelfComplementarity(boolean b){
		this.IsSelfComplementarity = b;
	}
	
	/**
	 * This method is called to get the hybridization of Environment.
	 * @return String hybridization of Environment. (represents the type of hybridization)
	 */
	public String getHybridization() {
		return Hybridization;
	}
	
	/**
	 * This method is called to get the HashMap options of Environment.
	 * @return HashMap options of Environment.
	 */
	public HashMap<String, String> getOptions() {
		return options;
	}
	
	/**
	 * This method is called to get the sodium concentration.
	 * @return double Na corresponding to the sodium concentration.
	 */
	public double getNa() {
		if (concentrations.containsKey("Na")){
			return concentrations.get("Na");
		}
		return 0;
	}
	
	/**
	 * This method is called to get the magnesium concentration.
	 * @return double Mg corresponding to the magnesium concentration.
	 */
	public double getMg() {
		if (concentrations.containsKey("Mg")){
			return concentrations.get("Mg");
		}
		return 0;
	}
	
	/**
	 * changes the magnesium concentration in the environment
	 * @param Mg : new magnesium concentration
	 */
	public void setMg(double Mg){
		this.concentrations.put("Mg", Mg);
	}
	
	/**
	 * This method is called to get the Tris buffer concentration.
	 * @return double Tris corresponding to the Tris buffer concentration.
	 */
	public double getTris() {
		if (concentrations.containsKey("Tris")){
			return concentrations.get("Tris");
		}
		return 0;
	}
	
	/**
	 * This method is called to get the potassium concentration.
	 * @return double K corresponding to the potassium concentration.
	 */
	public double getK() {
		if (concentrations.containsKey("K")){
			return concentrations.get("K");
		}
		return 0;
	}
	
	/**
	 * This method is called to get the concentrations of Environment.
	 * @return HashMap concentrations of Environment. (contains the different ion and agent concentrations)
	 */
	public HashMap<String, Double> getConcentrations(){
		return this.concentrations;
	}
	
	/**
	 * This method is called to get the DMSO concentration.
	 * @return double DMSO corresponding to the DMSO concentration.
	 */
	public double getDMSO() {
		if (concentrations.containsKey("DMSO")){
			return concentrations.get("DMSO");
		}
		return 0;
	}
	
	/**
	 * This method is called to get the formamide concentration.
	 * @return double formamide corresponding to the formamide concentration.
	 */
	public double getFormamide() {
		if (concentrations.containsKey("formamide")){
			return concentrations.get("formamide");
		}
		return 0;
	}
	
	/**
	 * changes the sodium concentration in the environment
	 * @param Na : new sodium concentration
	 */
	public void setNa(double na) {
		concentrations.put("Na", na);
	}
	
	/**
	 * increments the ThermoResult enthalpy and ThermoResult entropy of Environment.
	 * with the specified double enthalpy and double entropy.  
	 * @param enthalpy : enthalpy incrementation value
	 * @param entropy : entropy incrementation value
	 */
	public void addResult(double enthalpy, double entropy){
		this.result.setEnthalpy(this.result.getEnthalpy() + enthalpy);
		this.result.setEntropy(this.result.getEntropy() + entropy);
	}
	
	/**
	 * changes the melting temperature of ThermoResult.
	 * @param temperature : new melting temperature
	 */
	public void setResult(double temperature){
		this.result.setTm(temperature);
	}
	
	/**
	 * changes the result ThermoResult of Environment.
	 * @param result : new ThermoResult object.
	 */
	public void setResult(ThermoResult result){
		this.result=result;
	}

	/**
	 * This method is called to get the dNTP concentration.
	 * @return double dNTP corresponding to the dNTP concentration.
	 */
	public double getDNTP() {
		if (concentrations.containsKey("dNTP")){
			return concentrations.get("dNTP");
		}
		return 0;
	}
	
	// private methods
	
	/**
	 * initialises the concentrations of Environment.
	 */
	private void initialiseConcentrations(){
		String [] solution = this.options.get(OptionManagement.solutioncomposition).split(":");
		
		for (int i = 0; i < solution.length; i++){
			String [] couple = solution[i].split("=");
			this.concentrations.put(couple[0], Double.parseDouble(couple[1]));
		}
	}
	
	/**
	 * to check if there is at least one of the required ion concentrations (Na, Mg, Tris or K)
	 * @return true if at least one of the following ion concentrations : Na, Mg, K or Tris concentration 
	 * is strictly superior to 0.
	 */
	private boolean isRequiredConcentrations(){
		double Na = 0;
		double Mg = 0;
		double K = 0;
		double Tris = 0;
		
		if (concentrations.containsKey("Na")){
			Na = concentrations.get("Na");
		}
		if (concentrations.containsKey("Mg")){
			Mg = concentrations.get("Mg");
		}
		if (concentrations.containsKey("K")){
			K = concentrations.get("K");
		}
		if (concentrations.containsKey("Tris")){
			Tris = concentrations.get("Tris");
		}
		if (Na > 0 || K > 0 || Mg > 0 || Tris > 0){
			return true;
		}
		return false;
	}
	
}
