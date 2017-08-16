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

import java.awt.Frame;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.*;

/**
 * The location on the screen of a <code>Frame</code> or
 * <code>JInternalFrame</code>, together with whether it is maximized,
 * iconified or hidden.
 * @author John Gowers
 */
public class FrameScreenPosition
  implements Serializable
{
  /**
   * The bounds of the frame.
   */
  private Rectangle frameBounds;

  /**
   * Whether the frame is maximized.
   */
  private boolean maximized;

  /**
   * Whether the frame is iconified.
   */
  private boolean iconified;

  /**
   * Whether the frame is visible.
   */
  private boolean visible;

  /**
   * Constructor with no arguments does nothing.
   */
  public FrameScreenPosition() {}

  /**
   * Instantiates the class and fills in the fields from a <code>Frame</code>
   * object.
   */
  public FrameScreenPosition(Frame frame)
  {
    getScreenPosition(frame);
  }

  /**
   * Instantiates the class and fills in the fields from a
   * <code>JInternalFrame</code> object.
   */
  public FrameScreenPosition(JInternalFrame internalFrame)
  {
    getScreenPosition(internalFrame);
  }

  /**
   * Fills in the fields from the current screen position of a 
   * <code>Frame</code> object.
   * @param frame The frame to fill in the fields from.
   */
  public void getScreenPosition(Frame frame)
  {
    if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
      maximized = true;
    }
    else {
      maximized = false;
    }

    if ((frame.getExtendedState() & Frame.ICONIFIED) != 0) {
      iconified = true;
    }
    else {
      iconified = false;
    }

    // Temporarily restore the frame to get its restored bounds.
    frame.setExtendedState(Frame.NORMAL);

    // Wait a bit since the frame does not resize instantaneously.
    try {
      Thread.sleep(100);
    }
    catch (InterruptedException exception) {}

    frameBounds = frame.getBounds();

    visible = frame.isVisible();
  }

  /**
   * Fills in the fields from the current screen position of a 
   * <code>JInternalFrame</code> object.
   * @param internalFrame The frame to fill in the fields from.
   */
  public void getScreenPosition(JInternalFrame internalFrame)
  {
    frameBounds = internalFrame.getNormalBounds();

    if (internalFrame.isMaximum()) {
      maximized = true;
    }
    else {
      maximized = false;
    }

    if (internalFrame.isIcon()) {
      iconified = true;
    }
    else {
      iconified = false;
    }

    visible = internalFrame.isVisible();
  }

  /**
   * Sets up a <code>Frame</code> object using the stored fields.
   * @param frame The frame to set the screen position of.
   */
  public void setScreenPosition(Frame frame)
  {
    int state = Frame.NORMAL;

    frame.setBounds(frameBounds);
    
    if (maximized) {
      state |= Frame.MAXIMIZED_BOTH;
    }

    if (iconified) {
      state |= Frame.ICONIFIED;
    }

    frame.setExtendedState(state);

    frame.setVisible(visible);
  }

  /**
   * Sets up a <code>JInternalFrame</code> object using the stored fields.
   * @param internalFrame The internal frame to set the screen position of.
   */
  public void setScreenPosition(JInternalFrame internalFrame)
  {
    internalFrame.setBounds(frameBounds);
    
    try {
      internalFrame.setMaximum(maximized);
      
      internalFrame.setIcon(iconified);
    }
    catch (java.beans.PropertyVetoException exception) {}

    internalFrame.setVisible(visible);
  }
}

