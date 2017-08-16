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

import javax.swing.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

/**
 * A frame with a text area for displaying error messages.
 * @author John Gowers
 */
public class ErrorFrame extends JInternalFrame
{
  /**
   * Panel for holding all the widgets.
   */
  private Box centerBox = new Box(BoxLayout.Y_AXIS);

  /**
   * Label with the title - 'An error has occurred.' on it.
   */
  private JLabel titleLabel = new JLabel("An error has occurred.");

  /**
   * Text area for displaying error messages.
   */
  private JTextArea exceptionLogTextArea = new JTextArea();

  /**
   * Scroll pane for holding the text area.
   */
  private JScrollPane exceptionLogScrollPane = new JScrollPane();

  /**
   * Set up the widgets on the frame.
   */
  public ErrorFrame()
  {
    super("An error has occurred...", true, true, true, true);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

    exceptionLogTextArea.setEditable(false);
    exceptionLogTextArea.setLineWrap(true);
    exceptionLogTextArea.setWrapStyleWord(true);
    exceptionLogTextArea.setBackground(new Color(0, 0, 0, 0));

    exceptionLogScrollPane.
            setVerticalScrollBarPolicy(ScrollPaneConstants.
                                                 VERTICAL_SCROLLBAR_AS_NEEDED);
    exceptionLogScrollPane.setViewportView(exceptionLogTextArea);

    centerBox.setOpaque(true);
    centerBox.add(titleLabel);
    centerBox.add(Box.createRigidArea(new Dimension(0, 18)));
    centerBox.add(exceptionLogScrollPane);

    Container contentPane = getContentPane();
    contentPane.add(Box.createRigidArea(new Dimension(360, 12)),
                                        BorderLayout.NORTH);
    contentPane.add(Box.createRigidArea(new Dimension(12, 386)),
                                        BorderLayout.WEST);
    contentPane.add(Box.createRigidArea(new Dimension(12,0)),
                                        BorderLayout.EAST);
    contentPane.add(Box.createRigidArea(new Dimension(0, 12)),
                                        BorderLayout.SOUTH);

    contentPane.add(centerBox, BorderLayout.CENTER);

    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    pack();
  }

  /**
   * Logs an exception into the exception log text area.
   * @param exception - the exception to log.
   */
  public void logException(Exception exception)
  {
    exceptionLogTextArea.append(exception.getMessage() + "\n");
  }

    public void clearErrors() {
        exceptionLogTextArea.setText("");
    }
}

