package es.us.lsi.springer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import es.us.lsi.util.Ficheros;
import es.us.lsi.util.SpringerUtils;

public class SpringerCSV2bibtex {
	
	static final String DEFAULT_INPUT_FILENAME = "file.csv";
	static final String DEFAULT_OUTPUT_FILENAME = "file.bib";
	private static final String MSG_ERR = "use: java -jar SPRINGER_CSV2BIB.jar <file_in.csv> <file_out.bib>";
	
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		 String inputFileName = checkFilenameParam(args[0], DEFAULT_INPUT_FILENAME);
		 String outputFileName = checkFilenameParam(args[1], DEFAULT_OUTPUT_FILENAME);

		// List<String> lines = text.readSmallTextFile(fileName);
		List<String> lines = Ficheros.leeFichero(MSG_ERR, inputFileName);
		List<String> urls = SpringerUtils.extractURLS(lines);
		List<String> bibs = SpringerUtils.readSpringerBibFromURL(urls);
		System.out.println("---------------------------------------");
		System.out.println("Found: " + lines.size() + " citations");
		System.out.println("---------------------------------------");
		System.out.println("Workaround: adding spaces between \"=\"");
		List<String> bibs2 = formatBibs(bibs, 
				             SpringerUtils.getSpringerFormatter());
		System.out.println("---------------------------------------");
		System.out.println("Writing output file..." + bibs2.size() + " citations");
		System.out.println("---------------------------------------");
		Ficheros.escribeFichero(MSG_ERR, bibs2, outputFileName);
		Ficheros.escribeFichero(MSG_ERR, bibs, outputFileName+".origin");
		System.out.println("---------------------------------------");
		System.out.println("DONE!");
	}
	
	private static String checkFilenameParam(String arg, String defaultFilename){
		return arg!= null ? arg: defaultFilename;
	}

	/**
	 * @param bibs Array of string that has a bib representation of a file in each element
	 * @return The same list, but with blank spaces around equals.
	 */
	private static List<String> formatBibs(List<String> bibs, Function<String, String> formatFunction) {
		List<String> bibs2 = bibs.stream()
				.map(formatFunction)
				.collect(Collectors.toList());
	
		return bibs2;

	}

}