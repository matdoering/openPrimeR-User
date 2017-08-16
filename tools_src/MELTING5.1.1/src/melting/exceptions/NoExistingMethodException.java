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

package melting.exceptions;

/**
 * This class represents an exception for a method or model which doesn't exist.
 * It extends RuntimeException.
 */
public class NoExistingMethodException extends RuntimeException {

	// Instance variables

	private static final long serialVersionUID = 4480352056557369137L;
	
	// Inherited constructors

	public NoExistingMethodException() {
		super();
	}

	public NoExistingMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoExistingMethodException(Throwable cause) {
		super(cause);
	}
	
	public NoExistingMethodException(String message){
		super(message);
	}

}
