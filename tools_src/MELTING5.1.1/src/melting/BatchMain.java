package melting;

import melting.configuration.OptionManagement;
import melting.configuration.RegisterMethods;
import melting.methodInterfaces.MeltingComputationMethod;
import melting.nearestNeighborModel.NearestNeighborMode;

import java.io.*;
import java.text.NumberFormat;
import java.util.Arrays;

public class BatchMain {

	public static void main(String[] args) {
		/**
		 * normal options
		 * 
		 * unnamed optional arg 1 is the filename to read sequences from
		 * 
		 * output is written in csv format sequences DeltaH DeltaS Tm (deg C)
		 */

		if (args.length < 1) {
			System.err.print("Usage: melting-batch [OPTIONS] sequencefile");
			System.exit(1);
		} else {
			// remove file from list of options, pass options to
			// optionManager.isMeltingInformationOption(args)
			String[] commonArgs = Arrays.copyOfRange(args, 0, args.length - 1);
			String filename = args[args.length - 1];

            InputStream is = null;
            OutputStream os = null;
			try {
                is = new FileInputStream(filename);
                os = new FileOutputStream(filename+".results.csv");
                System.out.println("Results will be written to "+filename+".results.csv");
				readFile(is, os, commonArgs);
			} catch (FileNotFoundException e) {
				System.err.print("File " + filename + " could not be opened");
			} catch (IOException e) {
				System.err.print("Error reading file " + filename);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                    }
                }
			}
		}
	}

	public static void readFile(InputStream is, OutputStream os, String[] defaultArgs)
			throws IOException {
		BufferedReader bis = new BufferedReader(new InputStreamReader(is));

		String line = null;
		do {
			line = bis.readLine();
			String[] newArgs = (line != null)?line.split("[ \t]+"):new String[0];
			if (newArgs.length == 1) {
				// sequence is alone, prefix with -S
				String[] newNewArgs = new String[2];
				newNewArgs[0] = "-S";
				newNewArgs[1] = newArgs[0];
				newArgs = newNewArgs;
			} else if (newArgs.length == 2) {
				// 2nd arg must be the complementary sequence
				String[] newNewArgs = new String[4];
				newNewArgs[0] = "-S";
				newNewArgs[1] = newArgs[0];
				newNewArgs[2] = "-C";
				newNewArgs[3] = newArgs[1];
				newArgs = newNewArgs;
			} else if (newArgs.length == 0) {
				// skip empty lines
				newArgs = null;
			} else {
				System.err.print("Invalid line [" + line + "]");
                newArgs = null;
			}

			if (newArgs != null) {
				String[] completeArgs = new String[defaultArgs.length
						+ newArgs.length];
				int i = 0;
				for (String arg : defaultArgs) {
					completeArgs[i++] = arg;
				}
				for (String arg : newArgs) {
					completeArgs[i++] = arg;
				}

				runMelting(os, completeArgs);
			}
		} while (line != null);
	}

	public static void runMelting(OutputStream os, String[] args) {
		try {
			OptionManagement manager = new OptionManagement();
			Environment environment = manager.createEnvironment(args);
			RegisterMethods register = new RegisterMethods();
			MeltingComputationMethod calculMethod = register
					.getMeltingComputationMethod(environment.getOptions());
			ThermoResult results = calculMethod.computesThermodynamics();
			environment.setResult(results);

			results = calculMethod.getRegister()
					.computeOtherMeltingCorrections(environment);

			environment.setResult(results);
			displaysMeltingResults(os, environment, environment.getResult(),
					calculMethod);

		} catch (Exception e) {
			OptionManagement.logError(e.getMessage());
		}
	}

	private static void displaysMeltingResults(OutputStream os, Environment environment,
			ThermoResult results, MeltingComputationMethod calculMethod) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os);
		displayColumnHeaders(osw, calculMethod);

		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(2);

		double enthalpy = results.getEnthalpy();
		double entropy = results.getEntropy();

		if (calculMethod instanceof NearestNeighborMode) {
            osw.write(environment
                    .getSequences().getSequence()
                    + "\t"
                    + environment.getSequences().getComplementary()
                    + "\t"
                    + format.format(results.getEnergyValueInJ(enthalpy))
                    + "\t"
                    + format.format(results.getEnergyValueInJ(entropy))
                    + "\t"
                    + format.format(results.getTm())
                    + System.getProperty("line.separator"));
		}
        else {
            osw.write(
				environment.getSequences().getSequence() + "\t"
						+ environment.getSequences().getComplementary() + "\t"
						+ format.format(results.getTm())+System.getProperty("line.separator"));
        }
        osw.flush();
	}
	
	private static boolean areHeadersDisplayed = false;

	private static void displayColumnHeaders(OutputStreamWriter osw,
			MeltingComputationMethod calculMethod) throws IOException {
		if (!areHeadersDisplayed) {
			if (calculMethod instanceof NearestNeighborMode) {
				osw.write(
                        "Sequence\tComplementary\tDeltaH\tDeltaS\tTm (deg C)"+System.getProperty("line.separator"));
			} else {
                osw.write(
                        "Sequence\tComplementary\tTm (deg C)"+System.getProperty("line.separator"));
			}
			areHeadersDisplayed = true;
            osw.flush();
		}
	}
}
