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

package melting.patternModels.singleMismatch;

import java.text.MessageFormat;

import melting.Environment;
import melting.ThermoResult;
import melting.Thermodynamics;
import melting.configuration.OptionManagement;
import melting.patternModels.PatternComputation;
import melting.sequences.NucleotidSequences;
import melting.methodInterfaces.NamedMethod;

/**
 * This class represents the DNA/RNA single mismatch model wat11. It extends
 * PatternComputation.
 *
 * Watkins et al. (2011). Nucleic Acids Research 39: 1894 - 1902.
 *
 * @author John Gowers
 */
public class Watkins11mm extends PatternComputation
  implements NamedMethod
{
  // Instance variables

  /**
   * Default name for the xml file containing the thermodynamic parameters for
   * a single mismatch in a DNA/RNA duplex.
   */
  public static final String defaultFileName = "Watkins2011mm.xml";

  /**
   * Full name of the method.
   */
  private static String methodName = "Watkins et al. (2011)";

  // PatternComputationMethod interface implementation

  @Override
  public boolean isApplicable(Environment environment, int pos1, int pos2)
  {
    if ((environment.getHybridization().equals("dnarna") == false) &&
        (environment.getHybridization().equals("rnadna") == false)) {
      OptionManagement.logWarning("The single mismatch parameters of" +
                                  "Watkins et al. are originally" +
                                  "established for DNA/RNA duplexes.");
    }

    return super.isApplicable(environment, pos1, pos2);
  }

  @Override
  public ThermoResult computeThermodynamics(NucleotidSequences sequences,
                                            int pos1,
                                            int pos2,
                                            ThermoResult result)
  {
    // Correct the positions if we need to.
    int [] positions = correctPositions(pos1,
                                        pos2,
                                        sequences.getDuplexLength());
    pos1 = positions[0];
    pos2 = positions[1];

    OptionManagement.logMethodName(methodName);
    OptionManagement.logFileName(this.fileName);

    double enthalpy = result.getEnthalpy();
    double entropy = result.getEntropy();
    Thermodynamics mismatchValue;
    for (int i = pos1 ; i < pos2 ; i++) {
      mismatchValue =
            collector.getMismatchValue("d" + sequences.getSequenceNNPair(i),
                                    "r" + sequences.getComplementaryNNPair(i));

      OptionManagement.logMessage(
             MessageFormat.format("\nd{0}/r{1} : enthalpy = {2} entropy = {3}",
                                  new Object[] {
                                    sequences.getSequenceNNPair(i),
                                    sequences.getComplementaryNNPair(i),
                                    mismatchValue.getEnthalpy(),
                                    mismatchValue.getEntropy()
                                  }));
      enthalpy += mismatchValue.getEnthalpy();
      entropy += mismatchValue.getEntropy();
    }

    result.setEnthalpy(enthalpy);
    result.setEntropy(entropy);
    return result;
  }

  @Override
  public boolean isMissingParameters(NucleotidSequences sequences, 
                                     int pos1,
                                     int pos2)
  {
    // Correct the positions, if we have to.
    int[] positions = correctPositions(pos1,
                                       pos2,
                                       sequences.getDuplexLength());
    pos1 = positions[0];
    pos2 = positions[1];

    for (int i = pos1 ; i < pos2 ; i++) {
      if ((this.collector.getMismatchValue("d" + 
                                           sequences.getSequenceNNPair(i),
                         "r" + sequences.getComplementaryNNPair(i))) == null) {
        OptionManagement.logWarning(
                   MessageFormat.format("\n The thermodynamic parameters for" +
                                        " d{0}/r{1} are missing.  Check the" +
                                        " single mismatch parameters.",
                                        new Object[] {
                                          sequences.getSequenceNNPair(i),
                                          sequences.getComplementaryNNPair(i)
                                        }));
        return true;
      }
    }
    return super.isMissingParameters(sequences, pos1, pos2);
  }

  @Override
  public void initialiseFileName(String methodName)
  {
    if (this.fileName == null) {
      this.fileName = defaultFileName;
    }
  }

  // Private methods
  
  /**
   * Corrects the pattern positions in the duplex to have the adjacent base
   * pair of the pattern included in the subsequence between the positions pos1
   * and pos2.  
   * @param pos1 - starting position of the internal loop.
   * @param pos2 - ending position of the internal loop.
   * @param duplexLength - total lenfth of the duplex.
   * @return int[] positions - new positions of the subsequence to have the 
   *                           pattern surrounded by the adjacent base pairs
   *                           in the duplex.
   */
  private int[] correctPositions(int pos1, int pos2, int duplexLength)
  {
    if (pos1 > 0) {
      pos1--;
    }
    if (pos2 < duplexLength - 1) {
      pos2++;
    }
    int[] positions = {pos1, pos2};
    return positions;
  }

  /**
   * Gets the full name of the method.
   * @return The full name of the method.
   */
  @Override
  public String getName()
  {
    return methodName;
  }
}
