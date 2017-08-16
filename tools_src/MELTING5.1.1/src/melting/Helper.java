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

import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.exceptions.NoExistingMethodException;
import melting.methodInterfaces.SodiumEquivalentMethod;

/**
 * This class contains useful public static methods.
 */
public class Helper {
	
	/**
	 * to check if the user wants to use another thermodynamic parameters file with the used model.
	 * The new file name is specified in the methodName, preceded by the model name and ":"
	 * @param  methodName : the method or model option entered by the user.
	 * @return true if the methodName String contains ":". (a new file name has been entered by the user)
	 * Ex : san04 => the model name (no specified file name, the default file name of the san04 model is used)
	 * Ex : san04:file.xml => the model name followed by a new file name. (the san04 model will be used with the 
	 * thermodynamic parameters of the new file file.xml)
	 */
	public static boolean useOtherDataFile(String methodName){
		if (methodName.contains(":")){
			return true;
		}
		return false;
	}
	
	/**
	 * This method is called to extract the new file name to the method or model option entered by the user.
	 * @param  methodName : the method or model option entered by the user.
	 * @return String new file name (or pathway + new file name) containing the thermodynamic parameters.
	 */
	public static String extractsOptionFileName(String methodName){
		return methodName.split(":")[1];
	}
	
	/**
	 * This method is called to extract the method or model name to the method or model option entered by the user.
	 * @param  methodName : the method or model option entered by the user.
	 * @return String method or model name.
	 */
	public static String extractsOptionMethodName(String methodName){
		return methodName.split(":")[0];
	}
	
	/**
	 * computes a sodium equivalent concentration from the different ion concentrations of the Environment object.
	 * @param environment
	 * @return double sodium equivalent concentration.
	 */
	public static double computesNaEquivalent(Environment environment){
		double NaEq = environment.getNa() + environment.getK() + environment.getTris() / 2;
		
		if (environment.getMg() > 0){
			RegisterMethods setNaEqMethod = new RegisterMethods();
			SodiumEquivalentMethod method = setNaEqMethod.getNaEqMethod(environment.getOptions());
			if (method == null){
				throw new NoExistingMethodException("\n There is no implemented method to compute the Na equivalent concentration. Check the option " + OptionManagement.NaEquivalentMethod);
			}
			
			NaEq = method.computeSodiumEquivalent(environment.getNa(), environment.getMg(), environment.getK(), environment.getTris(),environment.getDNTP());
		}
		
		return NaEq;
	}
}
