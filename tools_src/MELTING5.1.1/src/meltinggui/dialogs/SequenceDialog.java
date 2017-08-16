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

import meltinggui.widgets.*;

/**
 * Dialog for specifying the sequence and complementary sequence.
 * @author John Gowers
 */
public class SequenceDialog extends InputFieldArray<MeltingTextArea>
                            implements DialogInterface
{
  /**
   * Creates the dialog.
   */
  public SequenceDialog()
  {
    super(new String[] {"Sequence (Option -S): ",
                        "Complementary sequence (Option -C): "},
          MeltingTextArea.class);
  }

  /**
   * Gets the sequence text.
   * @return The text specifying the sequence.
   */
  public String getSequenceText()
  {
    return getValue(0);
  }

  /**
   * Gets the complementary sequence text.
   * @return The text specifying the complementary sequence.
   */
  public String getComplementaryText()
  {
    return getValue(1);
  }

  /**
   * Selects all the text in the "Sequence" text box.
   */
  public void selectAllSequence()
  {
    selectAll(0);
  }

  /**
   * Selects all the text in the "Complementary sequence" text box.
   */
  public void complementarySelectAll()
  {
    selectAll(1);
  }

  /**
   * Gets the command-line flags corresponding to the chosen sequence and
   * complementary sequence.
   * @return The command-line flags the user would have to type in to specify
   *         the same sequences.
   */
  @Override
  public String getCommandLineFlags()
  {
    String commandLineFlags = " -S ";

    // Remove all white space from the sequences.
    String sequenceText = getSequenceText().replaceAll("[ \\t\\n]", "");
    String complementaryText =
                     getComplementaryText().replaceAll("[ \\t\\n]", "");

    commandLineFlags = commandLineFlags.concat(sequenceText);
    
    // Only give the flag for the complementary sequence if the user has
    // entered a complementary sequence.
    if (!(getComplementaryText().equals(""))) {
      commandLineFlags += " -C ";
      commandLineFlags += complementaryText;
    }

    return commandLineFlags;
  }
}

