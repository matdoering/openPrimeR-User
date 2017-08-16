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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import melting.ThermoResult;

import meltinggui.MeltingLayout;
import meltinggui.widgets.*;

/**
 * A panel to display the melting temperature, enthalpy and entropy results.  
 * @author John Gowers
 */
public class ResultsPanel extends JPanel
{
  private OutputField enthalpyOutputField = new OutputField("Enthalpy: ");
  private JLabel enthalpyUnitsLabel = new JLabel("J/mol");
  private OutputField entropyOutputField = new OutputField("Entropy: ");
  private JLabel entropyUnitsLabel = new JLabel("J/molK");
  private OutputField tmOutputField = new OutputField("Melting temperature: ");
  private JLabel tmUnitsLabel = new JLabel("degrees C");

  /**
   * Create and set up the panel.
   */
  public ResultsPanel()
  {
    disableEnthalpyEntropy();

    setLayout(new GridBagLayout());
    GridBagConstraints constraints;

    enthalpyOutputField.add(enthalpyUnitsLabel, MeltingLayout.INPUT_GROUP);
    entropyOutputField.add(entropyUnitsLabel, MeltingLayout.INPUT_GROUP);
    tmOutputField.add(tmUnitsLabel, MeltingLayout.INPUT_GROUP);

    constraints = getGridBagRowCol(0, 0);
    add(enthalpyOutputField, constraints);
    constraints = getGridBagRowCol(0, 1);
    constraints.weightx = 1.0;
    add(Box.createHorizontalGlue(), constraints);
    add(entropyOutputField, getGridBagRowCol(0, 2));
    constraints = getGridBagRowCol(1, 0);
    constraints.gridwidth = 3;
    add(tmOutputField, constraints);
  }

  /**
   * Factory creating a standardized <code>GridBagConstraints</code> object for
   * the <code>GridBagLayout</code> for a given row and column number.
   * @param   row   The row to put the component in.
   * @param   col   The column to put the component in.
   * @return   The new <code>GridBagConstraints</code>.
   */
  private GridBagConstraints getGridBagRowCol(int row, int col)
  {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.weightx = 0.5;
    constraints.gridx = col;
    constraints.gridy = row;
    return constraints;
  }

  /**
   * Disables the enthalpy and entropy output fields.
   */
  public final void disableEnthalpyEntropy()
  {
    enthalpyOutputField.setDisabled();
    entropyOutputField.setDisabled();
  }

  /**
   * Enables the enthalpy and entropy output fields.
   */
  public final void enableEnthalpyEntropy()
  {
    enthalpyOutputField.setEnabled();
    entropyOutputField.setEnabled();
  }

  /**
   * Displays the results from the MELTING program.
   * @param results The {@link ThermoResult <code>ThermoResult</code>} 
   *                containing the results.
   */
  public void displayMeltingResults(melting.ThermoResult results)
  {
    double tm = results.getTm();
    tmOutputField.setValue(String.format("%.2f", tm));

    if (results.getCalculMethod() instanceof
                            melting.nearestNeighborModel.NearestNeighborMode) {
      double enthalpy = results.getEnthalpy();
      double entropy = results.getEntropy();

      enableEnthalpyEntropy();

      enthalpyOutputField.setValue(String.format("%.0f", enthalpy));
      entropyOutputField.setValue(String.format("%.2f", entropy));
    }
    else {
      disableEnthalpyEntropy();

      enthalpyOutputField.setValue("");
      entropyOutputField.setValue("");
    }
  }
}

