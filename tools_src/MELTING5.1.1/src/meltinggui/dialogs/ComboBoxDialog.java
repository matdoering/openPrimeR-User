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

import meltinggui.MeltingLayout;
import meltinggui.widgets.ComboBoxOption;
import meltinggui.widgets.MeltingComboBox;

/**
 * A dialog with a label and a combo box with various options on it.  This
 * implements {@link DialogInterface <code>DialogInterface</code>}, so it
 * returns command-line parameters that the user would have entered were they
 * using the command line.
 * @author John Gowers
 */
public class ComboBoxDialog extends JPanel
  implements DialogInterface
{
  /**
   * The command line tag corresponding to the dialog.
   */
  private String commandLineTag;

  /**
   * Label telling the user what the combo box is.
   */
  private JLabel comboBoxLabel;

  /**
   * The combo box where the user can select options.
   */
  private MeltingComboBox comboBox;

  /**
   * Sets up the dialog.
   * @param comboBoxLabelText The text for the label on the dialog.
   * @param comboBoxOptions The combo box options to put in the combo box.
   * @param commandLineTag The command line tag corresponding to this combo
   *                       box.
   */
  public ComboBoxDialog(String comboBoxLabelText, 
                        ComboBoxOption[] comboBoxOptions,
                        String commandLineTag)
  {
    comboBoxLabel = new JLabel(comboBoxLabelText);
    comboBox = new MeltingComboBox(comboBoxOptions);
    this.commandLineTag = commandLineTag;

    setLayout(new MeltingLayout(this));
    add(comboBoxLabel, MeltingLayout.LABEL_GROUP);
    add(comboBox,      MeltingLayout.INPUT_GROUP);
  }

  /**
   * Returns the command-line flags corresponding to the user's choice on the
   * combo box.
   */
  @Override
  public String getCommandLineFlags()
  {
    String comboBoxValue = comboBox.getValue();
    String commandLineFlags;

    if (!(comboBoxValue.equals(""))) {
      commandLineFlags = " " + commandLineTag + " " + comboBoxValue;
    }
    else {
      commandLineFlags = "";
    }

    return commandLineFlags;
  }
}

