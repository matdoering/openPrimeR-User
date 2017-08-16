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

/**
 * A message that GUI components can send the main program, containing MELTING
 * arguments that it can use to get results.
 */
public class ArgsMessage
{
  /**
   * The different possible types of arguments that can be sent.  Currently,
   * the argument types supported are: 
   *  - MANDATORY: the mandatory arguments: -S, -C, -E, -H, -P.
   *  - GENERAL: all other arguments.
   */
  public enum ArgumentType
  {
    MANDATORY,
    GENERAL
  }

  /**
   * The type of arguments being sent.
   */
  private ArgumentType argumentType;

  /**
   * The command-line arguments being sent.
   */
  private String commandLineText;

  /**
   * Fills in the argument type and command-line arguments.
   * @param argumentType The type of arguments being sent.
   * @param commandLineText The command-line arguments being sent.
   */
  public ArgsMessage(ArgumentType argumentType, String commandLineText)
  {
    this.argumentType = argumentType;
    this.commandLineText = commandLineText;
  }

  /**
   * Gets the type of the message.
   * @return The type of arguments being sent.
   */
  public ArgumentType getArgumentType()
  {
    return argumentType;
  }

  /**
   * Gets the command-line arguments.
   * @return The command-line arguments being sent.
   */
  public String getCommandLineText()
  {
    return commandLineText;
  }
}

