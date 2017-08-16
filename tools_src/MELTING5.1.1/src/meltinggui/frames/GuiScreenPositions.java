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

import java.io.Serializable;

/**
 * The locations on the screen of the GUI and all the components it contains.  
 * @author John Gowers
 */
public class GuiScreenPositions
  implements Serializable
{
  /**
   * The screen position of the outer frame.
   */
  private FrameScreenPosition outerFrameScreenPosition;

  /**
   * The screen position of the MELTING frame.
   */
  private FrameScreenPosition meltingFrameScreenPosition;

  /**
   * The screen position of the error frame.
   */
  private FrameScreenPosition errorFrameScreenPosition;

  /**
   * Fills in the fields from the frames themselves.
   * @param outerFrame The outer frame to get the screen position from.
   * @param meltingFrame The MELTING frame.
   * @param errorFrame The error frame.
   */
  public GuiScreenPositions(OuterFrame outerFrame,
                            MeltingFrame meltingFrame,
                            ErrorFrame errorFrame)
  {
    outerFrameScreenPosition = new FrameScreenPosition(outerFrame);
    meltingFrameScreenPosition = new FrameScreenPosition(meltingFrame);
    errorFrameScreenPosition = new FrameScreenPosition(errorFrame);
  }

  /**
   * Sets the screen positions of the frames from the stored fields.
   * @param outerFrame The outer frame to set the screen position for.
   * @param meltingFrame The MELTING frame.
   * @param errorFrame The error frame.
   */
  public void setScreenPositions(OuterFrame outerFrame,
                                 MeltingFrame meltingFrame,
                                 ErrorFrame errorFrame)
  {
    outerFrameScreenPosition.setScreenPosition(outerFrame);
    meltingFrameScreenPosition.setScreenPosition(meltingFrame);
    errorFrameScreenPosition.setScreenPosition(errorFrame);
  }
}



