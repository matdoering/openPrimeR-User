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

import meltinggui.frames.MeltingFrame;
import meltinggui.frames.OuterFrame;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Main class for the Melting GUI. 
 */
public class MeltingGui
  extends JFrame implements Observer
{
  /**
   * Frame holding all the other components.
   */
  private OuterFrame outerFrame;
 
  /**
   * The text that would be entered into the command line were the user
   * using the command line.
   */
  private String commandLineText = new String("");

  /**
   * The mandatory part of the command line text.
   */
  private String mandatoryCommandLineText = new String("");

  /**
   * The optional part of the command line text.
   */
  private String generalCommandLineText = new String("");

  /**
   * Sets up the GUI and displays the main MELTING frame.
   */
  public MeltingGui()
  {
    outerFrame = new OuterFrame();
    outerFrame.addObserver(this);
  }

  /**
   * Receives command line options and sends out the MELTING results.
   */
  public void update(Observable observable, Object message)
  {
    String[] argsOption;
    melting.ThermoResult results;

    if (message instanceof ArgsMessage)
    {
      ArgsMessage argsMessage = (ArgsMessage) message;
      switch (argsMessage.getArgumentType()) {
        case MANDATORY :
          mandatoryCommandLineText = argsMessage.getCommandLineText();
          break;

        case GENERAL :
          generalCommandLineText = argsMessage.getCommandLineText();
          break;

        default:
          // Add appropriate message of exasperation here.
          break;
      }
    }

    commandLineText = mandatoryCommandLineText + generalCommandLineText; 
    argsOption = commandLineText.trim().split(" +");
    outerFrame.clearErrors();
    results = getMeltingResults(argsOption);
    outerFrame.displayMeltingResults(results);
  }

  /**
   * Gets results from the MELTING program.
   * @param argsOption Command line arguments for MELTING.
   * @return The results from MELTING.
   */
  private melting.ThermoResult getMeltingResults(String[] argsOption)
  {
    melting.ThermoResult results = new melting.ThermoResult(0.0, 0.0, 0.0);
    MeltingCalculator calculator = new MeltingCalculator(argsOption);
    try {
      results = calculator.fetchMeltingResults();
    }
    catch (RuntimeException exception) {
      outerFrame.logException(exception);
    }

    return results;
  }

  /**
   * Starts up the new frame, and creates an instance of MELTING on it.
   * @param args - Arguments from the command line.
   */
  public static void main(String args[])
  {
    // Get the Nimbus look and feel, if supported (Java SE 6 or later).  
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info :
        UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
    catch (ClassNotFoundException exception) {
      Logger.getLogger(MeltingFrame.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         exception);
    } 
    catch (InstantiationException exception) {
      Logger.getLogger(MeltingFrame.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         exception);
    } 
    catch (IllegalAccessException exception) {
      Logger.getLogger(MeltingFrame.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         exception);
    } 
    catch (UnsupportedLookAndFeelException exception) {
      Logger.getLogger(MeltingFrame.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         exception);
    } 

    java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run()
                    {
                      new MeltingGui();
                    }
                  });
  }
}
