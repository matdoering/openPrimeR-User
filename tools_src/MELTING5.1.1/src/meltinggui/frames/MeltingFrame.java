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

package meltinggui.frames;

import melting.configuration.OptionManagement;
import meltinggui.ArgsMessage;
import meltinggui.MeltingLayout;
import meltinggui.MeltingObservable;
import meltinggui.dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;

/**
 * The frame that opens when the user runs the GUI.
 * @author John Gowers
 */
public class MeltingFrame extends JInternalFrame
{
  /**
   * Creates the new frame.
   */
  public MeltingFrame()
  {
    super("Melting v"+ OptionManagement.versionNumber, true, false, true, true);
    initializeWidgets();
  }
  /**
   * Observable used to send messages back to all observers.
   */
  private MeltingObservable observable = new MeltingObservable();

  /**
   * Adds an observer to the melting frame observable.
   * @param observer The new observer.
   */
  public void addObserver(Observer observer)
  {
    observable.addObserver(observer);
  }

  /**
   * The text that would be entered into the command line were the user using
   * the command line.  Only mandatory options (-C -S -E -P -H) are included.
   */
  private String commandLineText;

  // Widgets to put on to the frame.  Declarations indented to show structure.
  private JPanel mainPanel = new JPanel();
    private JPanel sequencesPanel = new JPanel();
      private SequenceDialog sequenceDialog = new SequenceDialog();
    private JPanel hybridizationPanel = new JPanel();
      private HybridizationDialog hybridizationDialog =
                                                     new HybridizationDialog();
    private JPanel oligomerConcentrationPanel = new JPanel();
      private OligomerConcentrationDialog oligomerConcentrationDialog = 
                                             new OligomerConcentrationDialog();
    private JPanel ionConcentrationsPanel = new JPanel();
      private IonConcentrationDialog ionConcentrationDialog = 
                                                  new IonConcentrationDialog();
    private JPanel buttonsPanel = new JPanel();
      private JButton getThermodynamicsButton = 
                                             new JButton("Get Thermodynamics");
      private JToggleButton moreOptionsButton =
                                          new JToggleButton("More Options...");
    private JPanel commandLinePanel = new JPanel();
      private JTextArea commandLineTextArea = new JTextArea(" -S  -H  -P  -E");
    private JPanel resultsPanelPanel = new JPanel();
      private ResultsPanel resultsPanel = new ResultsPanel();

  /**
   * Sets up the widgets and puts them on the frame.
   */
  private void initializeWidgets()
  {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    GridBagConstraints constraints = new GridBagConstraints();

    getRootPane().setDefaultButton(getThermodynamicsButton);
    getThermodynamicsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
              sendMeltingArgs();
            }
    });
    commandLineTextArea.setBackground(new Color(0, 0, 0, 0));
    commandLineTextArea.setEditable(false);
    commandLineTextArea.setLineWrap(true);

    mainPanel.setLayout(new GridBagLayout());
    sequencesPanel.setLayout(new GridLayout(1, 1));
    hybridizationPanel.setLayout(new GridLayout(1, 1));
    oligomerConcentrationPanel.setLayout(new GridLayout(1, 1));
    ionConcentrationsPanel.setLayout(
                    new MeltingLayout(ionConcentrationsPanel, 10, 1));
    buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
    commandLinePanel.setLayout(new BorderLayout(30, 30));
    resultsPanelPanel.setLayout(new GridLayout(1, 2));

    sequencesPanel.add(sequenceDialog);
    hybridizationPanel.add(hybridizationDialog);
    oligomerConcentrationPanel.add(oligomerConcentrationDialog);
    ionConcentrationsPanel.add(new JLabel("Ion concentrations (option -E): "),
                               MeltingLayout.LABEL_GROUP);
    ionConcentrationsPanel.add(ionConcentrationDialog,
                               MeltingLayout.INPUT_GROUP);
    buttonsPanel.add(getThermodynamicsButton, BorderLayout.WEST);
    buttonsPanel.add(Box.createHorizontalGlue());
    //TODO buttonsPanel.add(moreOptionsButton, BorderLayout.EAST);
    commandLinePanel.add(commandLineTextArea, BorderLayout.CENTER);
    resultsPanelPanel.add(resultsPanel);

    constraints = getGridBagRow(0);
    constraints.weighty = 0.5;
    mainPanel.add(sequencesPanel, constraints);
    mainPanel.add(hybridizationPanel, getGridBagRow(1));
    mainPanel.add(oligomerConcentrationPanel, getGridBagRow(2));
    mainPanel.add(ionConcentrationsPanel, getGridBagRow(3));
    mainPanel.add(buttonsPanel, getGridBagRow(4));
    constraints = getGridBagRow(5);
    constraints.weighty = 1.0;
    mainPanel.add(commandLinePanel, constraints);
    mainPanel.add(resultsPanelPanel, getGridBagRow(6));
    JTextPane usageNotice = new JTextPane();
    usageNotice.setText("Notice: for more options (more computation methods, melting4 interface, batch mode, ...), see commandline-based scripts in this package.");
    mainPanel.add(usageNotice, getGridBagRow(7));

    Container contentPane = getContentPane();
    contentPane.add(Box.createRigidArea(new Dimension(532, 12)),
                    BorderLayout.NORTH);
    contentPane.add(Box.createRigidArea(new Dimension(0, 12)),
                    BorderLayout.SOUTH);
    contentPane.add(Box.createRigidArea(new Dimension(12, 0)),
                    BorderLayout.WEST);
    contentPane.add(Box.createRigidArea(new Dimension(12, 0)),
                    BorderLayout.EAST);
    contentPane.add(mainPanel, BorderLayout.CENTER);

    pack();
  }

  /**
   * Factory for making standardized <code>GridBagConstraints</code> for use
   * with <code>GridBagLayout</code>.  These constraints are designed so they:
   *  - always fill up all available space
   *  - are always in the first column of the layout.
   * @param row the row of the layout for the new constraints.
   * @return The new <code>GridBagConstraints</code>
   */
  private GridBagConstraints getGridBagRow(int row)
  {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = row;
    constraints.weightx = 0.5;
    constraints.weighty = 0.0;

    return constraints;
  }

  /**
   * Sets the command-line text in the command-line text area.
   */
  private void setCommandLineText()
  {
    commandLineText = sequenceDialog.getCommandLineFlags() +
                      hybridizationDialog.getCommandLineFlags() +
                      oligomerConcentrationDialog.getCommandLineFlags() +
                      ionConcentrationDialog.getCommandLineFlags();
    commandLineTextArea.setText(commandLineText);
  }

  /**
   * Calculates and displays the MELTING results.
   */
  public void sendMeltingArgs()
  {
    setCommandLineText();
    ArgsMessage message = new ArgsMessage(ArgsMessage.ArgumentType.MANDATORY,
                                          commandLineText);

    observable.setChanged();
    observable.notifyObservers(message);
  }

  /**
   * Displays the MELTING results.
   * @param results The results from the MELTING program.
   */
  public void displayMeltingResults(melting.ThermoResult results)
  {
    resultsPanel.displayMeltingResults(results);
  }
}
