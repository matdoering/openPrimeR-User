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

import javax.swing.*;

/**
 * A combo box where the user can specify a particular option.  It implements
 * {@link InputWidgetInterface <code>InputWidgetInterface</code>} so it has a
 * method that returns a string value corresponding to particular command-line
 * parameters.
 * @author John Gowers
 */
public class MeltingComboBox extends JComboBox
  implements InputWidgetInterface
{
  /**
   * Array of combo box options for the combo box.
   */
  private ComboBoxOption[] comboBoxOptions;

  /**
   * Fills in the combo box options, and sets up the combo box model.
   * @param suppliedOptions The combo box options supplied parameters to the
   * constructor.
   */
  public MeltingComboBox(ComboBoxOption[] suppliedOptions)
  {
    // We add an empty option to the start so the user can select nothing.
    comboBoxOptions = addEmptyOption(suppliedOptions);
    
    setModel(getComboBoxModel());
  }

  /**
   * Adds an empty option to the start of an array of combo box options.
   * @param suppliedOptions The original combo box options supplied, without
   * the extra empty option.
   */
  private ComboBoxOption[] addEmptyOption(ComboBoxOption[] suppliedOptions)
  {
    int numberOfSuppliedOptions = suppliedOptions.length;
    ComboBoxOption[] newComboBoxOptions =
            new ComboBoxOption[numberOfSuppliedOptions + 1];
    newComboBoxOptions[0] = new ComboBoxOption(" Please select...", "");
    for (int i = 0 ; i < numberOfSuppliedOptions ; i++) {
      newComboBoxOptions[i + 1] = suppliedOptions[i].copy();
    }

    return newComboBoxOptions;
  }

  /**
   * Gets the combo box model.
   * @return The model used in the combo box.
   */
  private ComboBoxModel getComboBoxModel()
  {
    DefaultComboBoxModel newModel;
    int numberOfComboBoxOptions = comboBoxOptions.length;
    String[] comboBoxOptionText = new String[numberOfComboBoxOptions];

    for (int i = 0 ; i < numberOfComboBoxOptions ; i++) {
      comboBoxOptionText[i] = comboBoxOptions[i].getOptionText();
    }

    newModel = new DefaultComboBoxModel(comboBoxOptionText);
    return newModel;
  }

  /**
   * Gets the command-line text corrseponding to the selected option.
   * @return The command-line text.
   */
  @Override
  public String getValue()
  {
    return comboBoxOptions[getSelectedIndex()].getCommandLineText();
  }

  /**
   * Does nothing.
   * @param newValue
   */
  @Override
  public void setValue(String newValue) {}

  /**
   * Does nothing.
   */
  @Override
  public void selectAll() {}
}

