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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import melting.exceptions.FileException;
import melting.handlers.DataHandler;

import org.xml.sax.SAXException;

/**
 * This class is useful to parse the xml files containing the thermodynamic parameters and load the data (stock them in a map).
 */
public class FileReader {

	// Instance variables
	
	/**
	 * HashMap<String, HashMap<String, Thermodynamics>> loadedData : contains the parsed parameter file names.
	 * Each parsed file name is associate with a map containing the different parameters collected from the file.
	 */
	private static HashMap<String, HashMap<String, Thermodynamics>> loadedData = new HashMap<String, HashMap<String,Thermodynamics>>();
	
	// public static methods
	
	/**
	 * This method is called to get the loadedData of FileReader.
	 * @return the loadedData of FileReader.
	 */
	public static HashMap<String, HashMap<String, Thermodynamics>> getLoadedData() {
		return loadedData;
	}
	
	/**
	 * stocks the thermodynamic parameters from the file in a HashMap.
	 * @param File file : file containing the thermodynamic parameters to load.
	 * @return HashMap<String, Thermodynamics> map containing the file parameters.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * If an IOException is catch, a FileException is thrown.
	 */
	public static HashMap<String, Thermodynamics> readFile(File file) throws ParserConfigurationException,
			SAXException {
		if (loadedData.containsKey(file.getName())){
			return loadedData.get(file.getName());
		}
		else {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			try {
				DataHandler dataHandler = new DataHandler();
				saxParser.parse(file, dataHandler);
				loadedData.put(file.getName(), dataHandler.getMap());
				
				return dataHandler.getMap();
			} catch (IOException e) {
				throw new FileException("The file " + file.getName() + " can't be found.", e);
			} catch (SAXException e) {
				throw new FileException("The file " + file.getName() + " can't be parsed.", e);
			}
		}
	}
	
}
