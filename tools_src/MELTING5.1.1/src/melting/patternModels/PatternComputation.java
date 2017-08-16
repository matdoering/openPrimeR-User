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

package melting.patternModels;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import melting.DataCollect;
import melting.Environment;
import melting.FileReader;
import melting.Helper;
import melting.ThermoResult;
import melting.configuration.OptionManagement;
import melting.exceptions.FileException;
import melting.exceptions.ThermodynamicParameterError;
import melting.methodInterfaces.PatternComputationMethod;
import melting.sequences.NucleotidSequences;

/**
 * This class represents the different pattern models. It implements the PatternComputationMethod interface.
 */
public abstract class PatternComputation implements PatternComputationMethod{

	// Instance variables
	
	/**
	 * DataCollect collector : contains the data HasMap with the thermodynamic parameters for the pattern computation.
	 */
	protected DataCollect collector = new DataCollect();
	
	/**
	 * String fileName : name of the xml file containing the thermodynamic parameters.
	 */
	protected String fileName;

	// PatternComputationMethod interface implementation
	
	public boolean isApplicable(Environment environment, int pos1,
			int pos2) {
		NucleotidSequences sequences = environment.getSequences();

		if (isMissingParameters(sequences, pos1, pos2)) {
			throw new ThermodynamicParameterError("\n Some thermodynamic parameters are missing to compute " +
					"melting temperature.");
		}
		return true;
	}
	
	public abstract ThermoResult computeThermodynamics(NucleotidSequences sequences,
			int pos1, int pos2, ThermoResult result);
	
	public boolean isMissingParameters(NucleotidSequences sequences, int pos1,
			int pos2) {
		return false;
	}
	
	public DataCollect getCollector() {
		return this.collector;
	}
	
	public void loadData(HashMap<String, String> options){

		loadFile(this.fileName, this.collector);
	}
	
	public void loadFile(String name, DataCollect collector){

		File dataFile = new File(OptionManagement.dataPathwayValue + File.separatorChar + name);
		try {
			collector.addData(FileReader.readFile(dataFile));

		} catch (ParserConfigurationException e) {
			throw new FileException("\n One of the files containing the thermodynamic parameters can't be parsed.", e);
		} catch (SAXException e) {
			throw new FileException("\n One of the files containing the thermodynamic parameters can't be parsed.", e);
		}
	}
	
	public String getDataFileName(String methodName){
		return this.fileName; 
	}
	
	public void initialiseFileName(String methodName){
		if (Helper.useOtherDataFile(methodName)){
			this.fileName = Helper.extractsOptionFileName(methodName);
		}
		else{
			this.fileName = null;
		}
	}
		
	// public static method
	
	/**
	 * This method is called to get the HasMap of DataCollect containing the thermodynamic parameters
	 * @return HasMap of DataCollect containing the thermodynamic parameters for this pattern.
	 */
	public static String getData(){
		return null;
	}
	
}
