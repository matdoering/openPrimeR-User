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

package meltinggui.widgets;

/**
 * A dialog where the user can specify a particular hybridization type.  
 * Four types of hybridization are possible:
 *   . DNA / DNA
 *   . RNA / RNA
 *   . DNA / RNA
 *   . RNA / methyl-o-RNA
 * @author John Gowers
 */
public class HybridizationWidget extends MeltingComboBox
{
  /**
   * Sets up and creates the hybridization dialog.
   */
  public HybridizationWidget()
  {
    super(new ComboBoxOption[] {
            new ComboBoxOption("DNA / DNA", "dnadna"),
            new ComboBoxOption("RNA / RNA", "rnarna"),
            new ComboBoxOption("DNA / RNA", "dnarna"),
            new ComboBoxOption("RNA / DNA", "rnadna"),
            new ComboBoxOption("RNA / methyl-o-RNA", "rnamrna"),
            new ComboBoxOption("methyl-o-RNA / RNA", "mrnarna")
    });
  }
}
