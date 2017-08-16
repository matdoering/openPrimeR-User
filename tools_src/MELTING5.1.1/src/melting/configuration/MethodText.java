// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public Licence as published by the Free
// Software Foundation; either verison 2 of the Licence, or (at your option)
// any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public Licence for
// more details.  
//
// You should have received a copy of the GNU General Public Licence along with
// this program; if not, write to the Free Software Foundation, Inc., 
// 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
//
// Marine Dumousseau, Nicolas Lenovere
// EMBL-EBI, neurobiology computational group,             
// Cambridge, UK. e-mail: lenov@ebi.ac.uk, marine@ebi.ac.uk

package melting.configuration;

/**
 * The text representations corresponding to a method used in melting 
 * temperature computation.  They are: 
 *  - The full name of the method (e.g., 'Hicks and Santalucia (2004)')
 *  - The command-line flag corresponding to the method (e.g., 'san04').
 *
 * @author John Gowers
 */
public class MethodText
{
  /**
   * The full name of the method.
   */
  private String methodName;

  /**
   * The command-line flag corresponding to the method.
   */
  private String methodCommandLineFlag;

  /**
   * Sets up the method name and command line flag.
   * @param   methodName              The full name of the method.
   * @param   methodCommandLineFlag   The command-line flag corresponding to
   *                                  the method.
   */
  public MethodText(String methodName, String methodCommandLineFlag)
  {
    this.methodName = methodName;
    this.methodCommandLineFlag = methodCommandLineFlag;
  }

  /**
   * Gets the method name.
   * @return The full name of the method.
   */
  public String getMethodName()
  {
    return methodName;
  }

  /**
   * Gets the method command line flag.
   * @return The command line flag corresponding to this method.
   */
  public String getMethodCommandLineFlag()
  {
    return methodCommandLineFlag;
  }
}

