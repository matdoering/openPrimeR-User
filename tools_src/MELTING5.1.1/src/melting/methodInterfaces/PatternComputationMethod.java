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

package melting.methodInterfaces;

import java.util.HashMap;

import melting.DataCollect;
import melting.Environment;
import melting.ThermoResult;
import melting.sequences.NucleotidSequences;

/** This interface is implemented by classes which contain a method or model to compute the pattern enthalpy and entropy.
 * */
public interface PatternComputationMethod {
	
	/**
	 * 
	 * @param environment
	 * @param pos1. Start position of the pattern in the sequence.
	 * @param pos2. End position of the pattern in the sequence. 
	 * @return false if the model used to compute the enthalpy and entropy of the pattern at the positions pos1 and 
	 * pos2 is not applicable with the environment. If the environment can make the results less reliable, a warning
	 * message will appear.
	 */
	public boolean isApplicable(Environment environment, int pos1, int pos2);
	
	/**
	 * 
	 * @param sequences which contains the sequences entered by the user.
     * @param pos1. Start position of the pattern in the sequence.
	 * @param pos2. End position of the pattern in the sequence.
	 * @param result which contains the computed enthalpy, entropy and melting temperature.
	 * @return ThermoResult result which contains the incremented enthalpy and entropy values.
	 */
	public ThermoResult computeThermodynamics(NucleotidSequences sequences, int pos1, int pos2, ThermoResult result);
	
	/**
	 * Check if one thermodynamic parameter is missing to compute the enthalpy and entropy of the pattern 
	 * at the positions pos1 and pos2.
	 * @param sequences which contains the sequences entered by the user.
     * @param pos1. Start position of the pattern in the sequence.
	 * @param pos2. End position of the pattern in the sequence. 
	 * @return false if one necessary thermodynamic parameter is missing.
	 */
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1, int pos2);
	
	/**
	 * This method is called to get the DataCollect of the implemented class.
	 * @return DataCollect object of the implemented class.
	 */
	public DataCollect getCollector();
	
	/** 
	 * To load the necessary thermodynamic parameters of the implemented class.
	 * @param options which contains the current options. (default options and options entered by the user)
	 */
	public void loadData(HashMap<String, String> options);
		
	/**
	 * To load the thermodynamic parameters from the file "name" and stock them in the map of a DataCollect object.
	 * @param  name : the file name where to find the thermodynamic parameters
	 * @param collector : The object which stocks the different thermodynamic parameters in a map.
	 */
	public void loadFile(String name, DataCollect collector);
	
	/**
	 * 
	 * @param  methodName : method or model name entered by the user.
	 * @return the file name entered by the user if he wrote a name file preceded by ":" in the method or model name 
	 * otherwise the default file name of the model.
	 */
	public String getDataFileName(String methodName);
	
	/**
	 * If a file name is entered by the user, the file name of the model becomes the file name entered by the user,
	 * otherwise the default file name of the model is used.
	 * @param  methodName : method or model name entered by the user.
	 */
	public void initialiseFileName(String methodName);
	
}
