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

package melting.handlers;

import org.xml.sax.helpers.DefaultHandler;

/**
 * This class represents a Handler for any node of the data xml files. It extends DefaultHanlder.
 */
public abstract class NodeHandler extends DefaultHandler {

	// Instance variables
	
	/**
	 * boolean completed : informs if a node is completed
	 */
	private boolean completed = false;
	
	/**
	 * NodeHandler subHandler : the Handler for the subnode.
	 */
	protected NodeHandler subHandler;

	// public method
	
	/**
	 * To check if a node is completed.
	 * @return true if the node is completed.
	 */
	public boolean hasCompletedNode() {
		return completed;
	}

	// protected method
	
	/**
	 * if the node is completed, the boolean completed becomes true.
	 */
	protected void completedNode() {
		completed = true;
	}
}
