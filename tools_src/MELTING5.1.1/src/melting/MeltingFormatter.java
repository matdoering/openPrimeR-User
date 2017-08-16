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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class extends Formatter. It formats the MeltingLogger message. (information, warning and severe messages)
 */
public class MeltingFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuffer message = new StringBuffer();
		
		if (record.getLevel().equals(Level.WARNING) || record.getLevel().equals(Level.SEVERE)){
			if (record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				message.append(writer);
				message.append("\n");
			}
			message.append(record.getLevel() + " : " + record.getMessage() + "\n");
		}
		else {
			message.append(record.getMessage() + "\n");
		}
		return message.toString();
	}

}
