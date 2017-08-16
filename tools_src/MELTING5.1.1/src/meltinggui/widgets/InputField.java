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

import java.awt.Component;

/**
 * A widget for enter in a value, together with a label to tell you what to
 * enter.
 * @param W The type of widget to use.
 */
public class InputField<W extends Component & InputWidgetInterface>
                     extends InputFieldArray<W> implements InputWidgetInterface
{
  /**
   * Creates the widgets, and sets them up.
   * @param labelText The text to type in the label.
   * @param clazz The widget type.
   */
  public InputField(String labelText, Class<W> clazz)
  {
    super(new String[] {labelText}, clazz);
  }

  /**
   * Gets the value from the widget.
   * @return The value in the widget.
   */
  @Override
  public String getValue()
  {
    return getValue(0);
  }

  /**
   * Sets the value in the widget.
   * @param newText The new value in the widget.
   */
  @Override
  public void setValue(String newValue)
  {
    setValue(0, newValue);
  }

  /**
   * Selects all the text in the widget.
   */
  @Override
  public void selectAll()
  {
    selectAll(0);
  }

  /**
   * Disables the widget.
   */
  public void setDisabled()
  {
    disable(0);
  }

  /**
   * Enables the widget.
   */
  public void setEnabled()
  {
    enable(0);
  }
}
  

