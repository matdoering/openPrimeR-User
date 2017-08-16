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

package meltinggui.dialogs;

import javax.swing.*;
import java.awt.Color;

import meltinggui.MeltingLayout;
import meltinggui.widgets.*;

/**
 * A dialog for specifying the oligomer concentration.
 * @author John Gowers
 */
public class OligomerConcentrationDialog extends InputField<MeltingTextField>
  implements DialogInterface
{
  /**
   * A label showing the units (mol/L).
   */
  private JLabel unitsLabel = new JLabel("mol/L");

  /**
   * Sets up the dialog.
   */
  public OligomerConcentrationDialog()
  {
    super("Oligomer concentration (option -P): ", MeltingTextField.class);
    setBackground(new Color(0, 0, 0, 0));
    add(unitsLabel, MeltingLayout.INPUT_GROUP);
  }

  /**
   * Gets the command-line flags.
   * @return The command-lin flags the user would have used to specify the
   *         given concentration if they had been using the command-line.
   */
  @Override
  public String getCommandLineFlags()
  {
    String commandLineFlags = " -P " + getValue();
    return commandLineFlags;
  }
}

