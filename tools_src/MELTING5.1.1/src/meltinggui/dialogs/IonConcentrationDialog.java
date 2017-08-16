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
import java.awt.*;
import java.awt.event.*;

import meltinggui.widgets.*;

/**
 * A dialog allowing the user to specify the concentration of ions in the
 * solution.
 * @author John Gowers
 */
public class IonConcentrationDialog extends JComboBox
                                    implements DialogInterface
{
  /**
   * The different ions available.
   */
  private String[] ionNames = new String[] {"Na", "Mg", "K", "Tris"};

  /**
   * The options dispayed on the combo box.
   */
  private String[] comboBoxOptions = createComboBoxOptions(ionNames);

  /**
   * The index of the currently selected ion.
   */
  int currentIonIndex = 0;

  /**
   * The editor used in the combo box.
   */
  private IonDialogEditor editor;

  /**
   * Set up the dialog.
   */
  public IonConcentrationDialog()
  {
    setEditable(true);
    setModel(new DefaultComboBoxModel(comboBoxOptions));
    setSelectedIndex(-1);
    String startText = makeComboBoxOption(ionNames[currentIonIndex]);
    editor = new IonDialogEditor(startText);
    setEditor(editor);
    setRenderer(new IonDialogRenderer());

    editor.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event)
                    {
                      setNewComboBoxOptions();
                    }
    });

    editor.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent event)
                    {
                      setNewComboBoxOptions();
                    }
                    @Override
                    public void focusLost(FocusEvent event)
                    {
                      editor.commitEdit();
                      setNewComboBoxOptions();
                    }
    });
  }

  /**
   * Adds an event to the event dispatch queue asking it to set the new combo
   * box options in the dialog.
   */
  private void setNewComboBoxOptions()
  {
    SwingUtilities.invokeLater(new Runnable() {
                  /**
                   * Sets the new combo box options.
                   */
                  @Override
                  public void run()
                  {
                    ComboBoxModel newModel =
                                     new DefaultComboBoxModel(comboBoxOptions);
                    newModel.setSelectedItem(comboBoxOptions[currentIonIndex]);
                    setModel(newModel);
                  }
    });
  }

  /**
   * Adds an ':' sign to the end of the ion names to make the combo box
   * options.
   * @param ionNames The names of the ions.
   * @return The new combo box options.
   */
  private String[] createComboBoxOptions(String[] ionNames)
  {
    int numberOfIons = ionNames.length;
    String[] comboBoxOptions = new String[numberOfIons];

    for (int i = 0 ; i < numberOfIons ; i++) {
      comboBoxOptions[i] = makeComboBoxOption(ionNames[i]);
    }

    return comboBoxOptions;
  }

  /**
   * Makes an ion name into a combo box option by adding a ':' to the end.
   * @param ionName The name of the ion.
   * @return The new combo box option.
   */
  private String makeComboBoxOption(String ionName)
  {
    return ionName + ": ";
  }

  /**
   * Removes all the non-digit characters from a string.
   * @param oldString The string with non-digit characters.
   * @return The string without non-digit characters.
   */
  private String removeNonDigits(String oldString)
  {
    return oldString.replaceAll("[^\\d.]", "");
  }

  /**
   * Gets the command line flags the user would have typed in were they using
   * the command line.  The concentrations are found in the combo box options.
   * @return The new command-line flags.
   */
  @Override
  public String getCommandLineFlags()
  {
    int i;
    String concentration;
    String commandLineFlags = "";

    // We loop through the options on the combo box.  At each stage, check
    // whether a concentration has been entered; if it has, then add the
    // correct piece of command-line syntax to the string.
    for (i = 0 ; i < comboBoxOptions.length ; i++) {
      concentration = removeNonDigits(comboBoxOptions[i]);
      if (!concentration.equals("")) {
        commandLineFlags =
                    commandLineFlags.concat(ionNames[i]+"="+concentration+":");
      }
    }

    // Remove the dangling colon, if there is one.
    if (commandLineFlags.endsWith(":")) {
      commandLineFlags =
                commandLineFlags.substring(0, commandLineFlags.length() - 1);
    }

    return " -E " + commandLineFlags;
  }

  /**
   * Inner class: the editor for the combo box.
   */
  private class IonDialogEditor implements ComboBoxEditor
  {
    /**
     * The panel that makes up the editor component.
     */
    private EditorPanel editorPanel;

    /**
     * The text box where the user enters the concentration.
     */
    private MeltingTextField concentrationTextField;

    /**
     * A label showing the ion we are entering the concentration for.
     */
    private JLabel ionLabel;

    /**
     * A label displaying the units.
     */
    private JLabel unitsLabel;
    
    /**
     * Create the editor and set it up.
     * @param startText The text to display in the label.
     */
    public IonDialogEditor(String startText)
    {
      editorPanel = new EditorPanel();
      concentrationTextField = new MeltingTextField();
      ionLabel = new JLabel(startText);
      unitsLabel = new JLabel("mol/L");
      editorPanel.setLayout(new BorderLayout());
      editorPanel.add(ionLabel, BorderLayout.WEST);
      editorPanel.add(concentrationTextField, BorderLayout.CENTER);
      editorPanel.add(unitsLabel, BorderLayout.EAST);
    }

    /**
     * Change one of the options on the combo box.
     */
    public void setIonComboBoxOption(int comboBoxIndex, String newOption)
    {
      comboBoxOptions[comboBoxIndex] = newOption;
    }

    /**
     * Adds an action listener to the concentration text field.
     * @param listener The new listener.
     */
    @Override
    public void addActionListener(ActionListener listener)
    {
      concentrationTextField.addActionListener(listener);
    }

    /**
     * Removes an action listener from the concentration text field.
     * @param listener The action listener to remove.
     */
    @Override
    public void removeActionListener(ActionListener listener)
    {
      concentrationTextField.removeActionListener(listener);
    }

    /**
     * Gets the editor panel.
     * @return The editor panel.
     */
    @Override
    public EditorPanel getEditorComponent()
    {
      return editorPanel;
    }

    /**
     * Gets the concentration/ions entered.
     * @return The contents of the text field.
     */
    @Override
    public Object getItem()
    {
      return concentrationTextField.getText();
    }

    /**
     * Sets the contents of the text field.
     * @param object The new text.
     */
    @Override
    public void setItem(Object object)
    {
      String enteredText;
      if (object != null) {
        enteredText = object.toString();
      }
      else {
        enteredText = "";
      }

      // Set the current ion.
      for (int i = 0 ; i < ionNames.length ; i++) {
        if (enteredText.startsWith(ionNames[i])) {
          currentIonIndex = i;
          break; // We might as well.
        }
      }

      enteredText = removeNonDigits(enteredText);

      String newComboBoxOption = makeComboBoxOption(ionNames[currentIonIndex]);
      ionLabel.setText(newComboBoxOption);
      setIonComboBoxOption(currentIonIndex,
                           newComboBoxOption + enteredText);
      concentrationTextField.setText(enteredText);
      selectAll();
    }

    /**
     * Selects all the text in the concentration text box.
     */
    @Override
    public void selectAll()
    {
      concentrationTextField.selectAll();
    }

    /**
     * Removes a focus listener from the concentration text box.
     */
    public void removeFocusListener(FocusListener listener)
    {
      concentrationTextField.removeFocusListener(listener);
    }

    /**
     * Adds a focus listener to the concentration text box.
     */
    public void addFocusListener(FocusListener listener)
    {
      concentrationTextField.addFocusListener(listener);
    }
 
    /**
     * Commits currently entered text in the concentration text box.
     */
    public void commitEdit()
    {
      setItem(getItem());
    }
  }

  /**
   * Inner class - a modified JPanel used for the editor.
   */
  private class EditorPanel extends JPanel
  {
    /**
     * Overrides the 'isFocusable method of the JPanel to return false always.
     * This means that the editor itself never receives the focus, and that the
     * focus is transferred to the concentration text box.  The reasons for
     * this are complicated and to do with the way combo box editors are
     * implemented in Swing.
     */
     @Override
     public boolean isFocusable()
     {
       return false;
     }
  }

  /**
   * Inner class - the renderer for the ion dialog.
   */
  private class IonDialogRenderer extends JLabel implements ListCellRenderer
  {
    /**
     * Do nothing.
     *
     */
    public IonDialogRenderer()
    {
    
    }

    /**
     * Gets this renderer and adds the units on to the end of the text.
     * @return This renderer.
     */
    @Override
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
      setText(value.toString() + " mol/L");
      return this;
    }
  }
}

    



