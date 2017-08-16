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

package meltinggui;

import java.util.Observable;

/**
 * Class created with the sole intention of extending the
 * {@link java.util.Observable <code>Observable</code> class and overriding
 * its <code>setChanged</code> method so it is no longer protected.
 */
public class MeltingObservable
  extends Observable
{
  /**
   * Notifies the program that the observable has changed.
   */
  @Override
  public void setChanged()
  {
    super.setChanged();
  }
}

