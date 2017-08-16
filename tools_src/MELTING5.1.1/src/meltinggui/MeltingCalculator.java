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

package meltinggui;

import melting.Main;
import melting.ThermoResult;
import melting.configuration.OptionManagement;

/**
 * Class intended to provide an interface between the GUI and the MELTING
 * program itself.
 * @author John Gowers
 */
public class MeltingCalculator
{
  /**
   * Command-line arguments supplied to MELTING.
   */
  private String[] argsOption;

  /**
   * Results from running MELTING with those command-line arguments.
   */
  private ThermoResult results = new ThermoResult(0.0, 0.0, 0.0);

  /**
   * Creates the calculator.
   * @param argsOption Command line options for the calculator.
   */
  public MeltingCalculator(String[] argsOption)
  {
    setArgsOption(argsOption);
  }

  /**
   * Sets the command-line arguments.
   * @param argsOption The command-line arguments.
   */
  public void setArgsOption(String[] argsOption)
  {
    this.argsOption = argsOption;
  }

  /**
   * Computes and returns the results from MELTING.
   * @return The results from MELTING.
   */
  public ThermoResult fetchMeltingResults() throws RuntimeException
  {
    computeMeltingResults();
    return results;
  }

  /**
   * Returns the results from MELTING.
   * @return The results from MELTING.
   */
  public ThermoResult getResults()
  {
    return results;
  }

  /**
   * Computes the results from MELTING.
   */
  private void computeMeltingResults() throws RuntimeException
  {
    OptionManagement optionManager = new OptionManagement();
    results = Main.getMeltingResults(argsOption, optionManager);
  }
}
