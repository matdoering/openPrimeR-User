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

package meltinggui.widgets;

/**
 * Represents a generic input widget on the frame.
 * @author John Gowers
 */
public interface InputWidgetInterface
{
  /**
   * Sets the value in the widget.
   * @param newValue The new value for the widget.
   */
  void setValue(String newValue);

  /**
   * Gets the value given by the user.
   * @return That value.
   */
  String getValue();

  /**
   * Selects all the text in a widget.
   */
  void selectAll(); 
}

