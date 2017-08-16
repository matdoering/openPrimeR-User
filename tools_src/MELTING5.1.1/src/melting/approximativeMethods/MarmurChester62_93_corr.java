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

package melting.approximativeMethods;

/**
 * This class represents the model che93corr.  It is a corrected version of the
 * che93 model.  For more details, see {@link MarmurChester62_93}.  
 *
 * von Ahsen et al. 2001, Marmur 1962, Chester et al. 1993.
 *
 * @author John Gowers
 */
public class MarmurChester62_93_corr extends MarmurChester62_93
{
  /**
   * Sets up the private variables for the corrected method.
   * The parameter for the corrected version is 535.
   */
  public MarmurChester62_93_corr()
  {
    super(535, "von Ahsen et al. 2001, Marmur 1962, Chester et al. 1993");
  }
}

