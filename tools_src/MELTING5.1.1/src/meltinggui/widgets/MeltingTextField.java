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
 * A text box for use with the MELTING program.  Implements
 * {@link InputWidgetInterface <code>InputWidgetInterface</code>}.  
 * @author John Gowers
 */
public class MeltingTextField extends JTextField
                              implements InputWidgetInterface
{
  /**
   * Creates a new MELTING text field with the default number of 16 columns.
   */
  public MeltingTextField()
  {
    this(16);
  }

  /**
   * Creates a new MELTING text field with a given number of columns.
   * @param   columns   The number of columns in the text field.
   */
  public MeltingTextField(int columns)
  {
    super(columns);
  }

  /**
   * Sets the text in the field.
   * @param newValue The new text for the field.
   */
  @Override
  public void setValue(String newValue)
  {
    setText(newValue);
  }

  /**
   * Returns the text from the field.
   * @return The text from the field.
   */
  @Override
  public String getValue()
  {
    return getText();
  }

  /**
   * Selects all the text in the field.
   */
  @Override
  public void selectAll()
  {
    super.selectAll();
  }
}
