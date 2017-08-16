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
import java.util.List;
import java.util.ArrayList;
import java.awt.Component;
import java.awt.Color;

import meltinggui.MeltingLayout;

/**
 * A vertical array of widgets, each with a label describing what to type in
 * it.
 * @param W The type of widget to use.  
 */
public class InputFieldArray<W extends Component & InputWidgetInterface>
  extends JPanel
{
  /**
   * The labels in the array.
   */
  private List<JLabel> labels = new ArrayList<JLabel>();

  /**
   * The widgets on the array.
   */
  public List<W> widgets = new ArrayList<W>();

  /**
   * Number of widgets to create.
   */
  private int numberOfWidgets;

  /**
   * Create the widgets and set them up.  
   * @param labelText Array of strings holding the text for each label.
   */
  public InputFieldArray(String[] labelText, Class<W> clazz)
  {
    JLabel newLabel;
    W newWidget = null;

    setBackground(new Color(0, 0, 0, 0));

    setLayout(new MeltingLayout(this, 2, 0));

    numberOfWidgets = labelText.length;

    for (int i = 0 ; i < numberOfWidgets ; i++) {
      newLabel = new JLabel(labelText[i]);
      try {
        newWidget = clazz.newInstance();
      }
      catch (InstantiationException exception) {
        System.err.println("Instantiation exception: " +
                           exception.getMessage());
      }
      catch (IllegalAccessException exception) {
        System.err.println("Illegal access exception: " +
                           exception.getMessage());
      }
      labels.add(newLabel);
      widgets.add(newWidget);

      add(newLabel, MeltingLayout.LABEL_GROUP);
      add(newWidget, MeltingLayout.INPUT_GROUP);
    }
  }

  /**
   * Get the value from a particular widget.
   * @param index The index of that widget.
   * @return The value from that widget.
   */
  public String getValue(int index)
  {
    return widgets.get(index).getValue();
  }

  /**
   * Sets the value in a particular widget.
   * @param index The index of that widget.
   * @param newValue The new value for the widget.
   */
  public void setValue(int index, String newValue)
  {
    widgets.get(index).setValue(newValue);
  }

  /**
   * Selects all the text in a particular widget.
   * @param index The index of that widget.
   */
  public void selectAll(int index)
  {
    widgets.get(index).selectAll();
  }

  /**
   * Disables a widget.
   * @param index The index of the widget to disable.
   */
  public void disable(int index)
  {
    labels.get(index).setEnabled(false);
    widgets.get(index).setEnabled(false);
  }

  /**
   * Disables all widgets.
   */
  public void disableAll()
  {
    for (int i = 0 ; i < numberOfWidgets ; i++) {
      disable(i);
    }
  }

  /**
   * Enables a widget.
   * @param index The index of the widget to enable.
   */
  public void enable(int index)
  {
    labels.get(index).setEnabled(true);
    widgets.get(index).setEnabled(true);
  }

  /**
   * Enables all widgets.
   */
  public void enableAll()
  {
    for (int i = 0 ; i < numberOfWidgets ; i++) {
      enable(i);
    }
  }
}
    


