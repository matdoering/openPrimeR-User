package melting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.io.*;

import melting.configuration.OptionManagement;
import melting.exceptions.OptionSyntaxError;

public class CompatibleMain {
	
	private static HashMap<String, String> indexModelNames = new HashMap<String, String>();
	private static HashMap<String, String> modelhybridizationType = new HashMap<String, String>();
	private static ArrayList<String> unchangedOptions = new ArrayList<String>();
	private static ArrayList<String> precedentOptionsList = new ArrayList<String>();
	
	private static void initialiseIndexModelNames(){
		indexModelNames.put("all97a.nn", "all97");
		indexModelNames.put("bre86a.nn", "bre86");
		indexModelNames.put("san96a.nn", "san96");
		indexModelNames.put("sug96a.nn", "sug96");
		indexModelNames.put("san04a.nn", "san04");
		indexModelNames.put("fre86a.nn", "fre86");
		indexModelNames.put("xia98a.nn", "xia98");
		indexModelNames.put("sug95a.nn", "sug95");
		indexModelNames.put("san05a.nn", "san05");
		indexModelNames.put("bre07a.nn", "zno07");
		indexModelNames.put("dnadnade.nn", "bom00");
		indexModelNames.put("dnadnamm.nn", "allsanpey");
		indexModelNames.put("wet91a", "wet91");
		indexModelNames.put("san96a", "san96");
		indexModelNames.put("san98a", "san04");
	}
	
	private static void initialiseModelHybridizationType(){
		modelhybridizationType.put("all97", "dnadna");
		modelhybridizationType.put("bre86", "dnadna");
		modelhybridizationType.put("san96", "dnadna");
		modelhybridizationType.put("sug96", "dnadna");
		modelhybridizationType.put("san04", "dnadna");
		modelhybridizationType.put("fre86", "rnarna");
		modelhybridizationType.put("xia98", "rnarna");
		modelhybridizationType.put("sug95", "dnarna");
	}
	
	private static void initialiseUnchangedOptions(){
		unchangedOptions.add("-C");
		unchangedOptions.add("-F");
		unchangedOptions.add("-h");
		unchangedOptions.add("-H");
		unchangedOptions.add("-L");
		unchangedOptions.add("-P");
		unchangedOptions.add("-p");
		unchangedOptions.add("-S");
		unchangedOptions.add("-T");
		unchangedOptions.add("-V");
	}
	
	private static void initialisePrecedentOptionsList(){
		precedentOptionsList.add("-h");
		precedentOptionsList.add("-L");
		precedentOptionsList.add("-p");
		precedentOptionsList.add("-V");
		precedentOptionsList.add("-C");
		precedentOptionsList.add("-F");
		precedentOptionsList.add("-H");
		precedentOptionsList.add("-P");
		precedentOptionsList.add("-S");
		precedentOptionsList.add("-T");
		precedentOptionsList.add("-v");
		precedentOptionsList.add("-A");
		precedentOptionsList.add("-D");
		precedentOptionsList.add("-M");
		precedentOptionsList.add("-x");
		precedentOptionsList.add("-i");
		precedentOptionsList.add("-K");
		precedentOptionsList.add("-N");
		precedentOptionsList.add("-k");
		precedentOptionsList.add("-t");
		precedentOptionsList.add("-G");

	}
		
	private static boolean isNecessaryToConvert(String name){
		
		if (indexModelNames.containsKey(name)){
			return true;
		}
		
		return false;
	}
	
	private static String convertModelName(String name){
		String newName = name;
		
		if (isNecessaryToConvert(name)){
			newName = indexModelNames.get(name);
		}
		
		return newName;
	}
	
	private static boolean isMissingOptionValue(String option){
		if (option.length() <= 2){
			return true;
		}
		return false;
	}
	
	private static StringBuffer convertFileNameOptions(String [] args, StringBuffer convertedArgs){
		for (int i = 0; i < args.length; i++){
			char oldOption = args[i].charAt(1);
			String newName;
			
			switch (oldOption) {
			case 'A':
				
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -A (not encouraged to use) needs a file name (filea.nn) or a model name. See the documentation of MELTING 4.3 or use the option " + OptionManagement.NNMethod + " of MELTING 5.");
				}
				else{
					newName = convertModelName(args[i].substring(2));
					
					convertedArgs.append(" " + OptionManagement.NNMethod + " " + newName);
					
					if (modelhybridizationType.containsKey(newName)){
						convertedArgs.append(" " + OptionManagement.hybridization + " " + modelhybridizationType.get(newName));
					}
					else {
						convertedArgs.append(" " + OptionManagement.hybridization + " " + " dnadna");
					}
				}
				
				break;
				
			case 'D':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -D (not encouraged to use) needs a file name (dnadnade.nn) or a model name. See the documentation of MELTING 4.3 or use the option " + OptionManagement.singleDanglingEndMethod + " of MELTING 5.");
				}
				else{
					newName = convertModelName(args[i].substring(2));
					
					convertedArgs.append(" " + OptionManagement.singleDanglingEndMethod + " " + newName);
				}
				break;
			
			case 'i':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -i (not encouraged to use) needs a file name (filea.nn) or a model name. See the documentation of MELTING 4.3 or use the option " + OptionManagement.inosineMethod + " of MELTING 5.");
				}
				else{
					newName = convertModelName(args[i].substring(2));
					convertedArgs.append(" " + OptionManagement.inosineMethod + " " + newName);
				}	
				break;
				
			case 'M':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -M (not encouraged to use) needs a file name (dnadnamm.nn) or a model name. See the documentation of MELTING 4.3 or use the option " + OptionManagement.singleMismatchMethod + " of MELTING 5.");
				}
				else{
					newName = convertModelName(args[i].substring(2));
					convertedArgs.append(" " + OptionManagement.singleMismatchMethod + " " + newName);
				}
				break;
				
			case 'K':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -K (not encouraged to use) needs a correction name. See the documentation of MELTING 4.3 or use the option " + OptionManagement.ionCorrection + " of MELTING 5.");
				}
				else{
					newName = convertModelName(args[i].substring(2));
					
					convertedArgs.append(" " + OptionManagement.ionCorrection + " " + newName);
				}
				
				break;
			default:
				break;
			}
		}
		return convertedArgs;
	}
	
	private static String convertOptionSyntax (String option){
		if (option.length() > 2){
			String newOption = " " + option.substring(0, 2) + " " + option.substring(2);
			return newOption;
		}
		else{
			String newOption = " " + option;
			return newOption;
		}
	}
	
	private static StringBuffer collectIonConcentrations(String [] args, StringBuffer convertedArgs){
		
		boolean isFirstConcentration = true;
		StringBuffer concentrations = new StringBuffer();
		concentrations.append(" " + OptionManagement.solutioncomposition + " ");

		for (int i = 0; i < args.length; i++){
			char oldOption = args[i].charAt(1);
			if (isFirstConcentration == true && concentrations.toString().equals(" " + OptionManagement.solutioncomposition + " ") == false){
				isFirstConcentration = false;
			}
			
			switch (oldOption) {
			case 'N':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -N (not encouraged to use) needs a positive numeric value. See the documentation of MELTING 4.3 or use the option " + OptionManagement.solutioncomposition + " of MELTING 5.");
				}
				else{
					if (isFirstConcentration == false){
						concentrations.append(":");
					}
					concentrations.append("Na=" + args[i].substring(2));
				}
				break;
				
			case 'G':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -G (not encouraged to use) needs a positive numeric value. See the documentation of MELTING 4.3 or use the option " + OptionManagement.solutioncomposition + " of MELTING 5.");
				}
				else{
					if (isFirstConcentration == false){
						concentrations.append(":");
					}
					concentrations.append("Mg=" + args[i].substring(2));
				}
				
				break;
			
			case 'k':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -k (not encouraged to use) needs a positive numeric value. See the documentation of MELTING 4.3 or use the option " + OptionManagement.solutioncomposition + " of MELTING 5.");
				}
				else{
					if (isFirstConcentration == false){
						concentrations.append(":");
					}
					concentrations.append("K=" + args[i].substring(2));
				}
				
				break;
				
			case 't':
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -t (not encouraged to use) needs a positive numeric value. See the documentation of MELTING 4.3 or use the option " + OptionManagement.solutioncomposition + " of MELTING 5.");
				}
				else{
					if (isFirstConcentration == false){
						concentrations.append(":");
					}
					concentrations.append("Tris=" + args[i].substring(2));
				}
				
				break;				
			default:
				break;
			}
		}
		if (concentrations.toString().equals(" " + OptionManagement.solutioncomposition + " ") == false){
			convertedArgs.append(concentrations);
		}
		return convertedArgs;
	}
	
	private static boolean isAnInputFile(String [] args){
		for (int i = 0; i < args.length; i++){
			if (args[i].substring(0, 2).equals("-I")){
				return true;
			}
		}
		return false;
	}
	
	private static String getInputFileName(String [] args){

		for (int i = 0; i < args.length; i++){
			if (args[i].substring(0, 2).equals("-I")){
				if (isMissingOptionValue(args[i])){
					throw new OptionSyntaxError("The option -I (not encouraged to use) needs an input file name. See the documentation of MELTING 4.3. This option doesn't exist in MELTING 5.");
				}
				return args[i].substring(2);
			}
		}
		return null;
	}
	
	private static String [] readInputFile(String fileName){
		StringBuffer newArgs = new StringBuffer();
		
		try {
			java.io.FileReader fileReader = new java.io.FileReader(fileName);
			
			BufferedReader bufferReader = new BufferedReader(fileReader);
			
			String line = bufferReader.readLine();
			while (line != null){
				newArgs.append(line);
				line = bufferReader.readLine();
			}
			return newArgs.toString().split(" ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	/**
	 * @param args : contains the options entered by the user.
	 */
	public static void main(String[] args) {
		try{
			initialiseIndexModelNames();
			initialiseModelHybridizationType();
			initialiseUnchangedOptions();
			initialisePrecedentOptionsList();
			
			boolean isVerboseMode = false;
			
			if (isAnInputFile(args)){
				String fileName = getInputFileName(args);
				args = readInputFile(fileName);
			}
			
			StringBuffer convertedArgs = new StringBuffer();
			
			convertedArgs = convertFileNameOptions(args, convertedArgs);
			convertedArgs = collectIonConcentrations(args, convertedArgs);
			
			for (int i = 0; i < args.length; i++){
				String optionName = args[i].substring(0, 2);
				if (unchangedOptions.contains(optionName)){
						convertedArgs.append(convertOptionSyntax(args[i]));
				}
				else if (optionName.equals("-x")){
					convertedArgs.append(" " + OptionManagement.approximativeMode);
				}
				else if (optionName.equals("-v")){
					isVerboseMode = !isVerboseMode;
				}
				else if (optionName.equals("-O")){
					if (args[i].length() > 2){
						convertedArgs.append(convertOptionSyntax(args[i]));
					}
					else {
						Calendar calendar = Calendar.getInstance(Locale.UK);
						convertedArgs.append(" " + OptionManagement.outPutFile + " melting" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) + ".out");
					}
				}
				else if (precedentOptionsList.contains(optionName) == false){
					throw new OptionSyntaxError("\n MELTING doesn't know the option " + optionName);
				}
			}
			
			if (isVerboseMode){
				convertedArgs.append(" " + OptionManagement.verboseMode);
			}
			convertedArgs.deleteCharAt(0);
			
			Main.main(convertedArgs.toString().split(" "));
		}catch(Exception e){
			OptionManagement.logError(e.getMessage());
		}
		
	}
}
