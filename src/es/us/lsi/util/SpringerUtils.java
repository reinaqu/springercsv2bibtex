package es.us.lsi.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringerUtils {
	private static final Function<String, String> FUN_ADD_SPACES_EQUALS = bib -> bib.replaceAll("(=)", " = ");
	private static final Function<String, String> FUN_ADD_CRLF_AFTER_CURLY_BRACKET = bib -> bib.replaceAll("\\},",
			"\\},\r\n");
	private static final Function<String, String> FUN_ADD_CRLF_BEFORE_YEAR = bib -> bib.replaceAll("\\{year",
			"\\{\r\nyear");
	private static final Function<String, String> FUN_ADD_CRLF_AFTER_FIRSTCOMMA = bib -> addCRLFAfterFirstComma(bib);

	private static final Function<String, String> FUN_REPLACE_QUOTES_FOR_CURLY_BRACKETS = bib -> changeQuotesForCurlyBrackets(
			bib);
	private static final Function<String, String> FUN_ADD_CRLF_AFTER_LAST_CLOSED_CURLY_BRACKET = bib -> bib
			.replaceAll("\\}\\}", "\\},\r\n\\}");
	private static final Function<String, String> FUN_FORMAT_SPRINGER = FUN_REPLACE_QUOTES_FOR_CURLY_BRACKETS
			.andThen(FUN_ADD_SPACES_EQUALS).andThen(FUN_ADD_CRLF_AFTER_CURLY_BRACKET).andThen(FUN_ADD_CRLF_BEFORE_YEAR)
			.andThen(FUN_ADD_CRLF_AFTER_FIRSTCOMMA).andThen(FUN_ADD_CRLF_AFTER_LAST_CLOSED_CURLY_BRACKET);

	public static Function<String, String> getSpringerFormatter() {
		return FUN_FORMAT_SPRINGER;
	}

	private static String addCRLFAfterFirstComma(String bib) {
		// TODO Auto-generated method stub
		// bib.replaceAll("author","\r\nauthor"
		int indx = bib.indexOf(",");

		return bib.substring(0, indx) + ",\r\n" + bib.substring(indx + 1);
	}

	/*
	 * @param bib Bibliographic reference in bibtex format. For example,
	 * 
	 * @InProceedings{10.1007/978-3-319-67816-0_22,
	 * author="Augot, Danieland Chabanne, Herv{\'e}and Chenevier, Thomasand George, Williamand Lambert, Laurent"
	 * ,editor="Garcia-Alfaro, Joaquinand Navarro-Arribas, Guillermoand Hartenstein, Hannesand Herrera-Joancomart{\'i}, Jordi"
	 * ,title="A User-Centric System for Verified Identities on the Bitcoin Blockchain"
	 * ,booktitle="Data Privacy Management, Cryptocurrencies and Blockchain Technology"
	 * ,year="2017",publisher="Springer International Publishing",address="Cham"
	 * ,pages="390--407",
	 * abstract="We present an identity management scheme built into the Bitcoin blockchain, allowing for identities that are as indelible as the blockchain itself. Moreover, we take advantage of Bitcoin's decentralized nature to facilitate a shared control between users and identity providers, allowing users to directly manage their own identities, fluidly coordinating identities from different providers, even as identity providers can revoke identities and impose controls."
	 * ,isbn="978-3-319-67816-0"}
	 * 
	 * Note that the values are enclosed in quotes and that Start do not get on well
	 * with quotes and requires the values of the bibtex attributes to be enclosed
	 * in curly brackets.
	 * 
	 * @return This method is in charge of replacing the quotes for
	 * curly brackets. Thus, for the previous example the result of the method
	 * must be
	 * 
	 * @InProceedings{10.1007/978-3-319-67816-0_22,
	 * author={Augot, Danieland Chabanne, Herv{\'e}and Chenevier, Thomasand George, Williamand Lambert, Laurent}
	 * ,editor={Garcia-Alfaro, Joaquinand Navarro-Arribas, Guillermoand Hartenstein, Hannesand Herrera-Joancomart{\'i}, Jordi}
	 * ,title={A User-Centric System for Verified Identities on the Bitcoin Blockchain}
	 * ,booktitle={Data Privacy Management, Cryptocurrencies and Blockchain Technology}
	 * ,year={2017},publisher={Springer International Publishing},address={Cham}
	 * ,pages={390--407},
	 * abstract={We present an identity management scheme built into the Bitcoin blockchain, allowing for identities that are as indelible as the blockchain itself. Moreover, we take advantage of Bitcoin's decentralized nature to facilitate a shared control between users and identity providers, allowing users to directly manage their own identities, fluidly coordinating identities from different providers, even as identity providers can revoke identities and impose controls.}
	 * ,isbn={978-3-319-67816-0}}
	 * 
	 * 
	 */
	private static String changeQuotesForCurlyBrackets(String bib) {
		// System.out.println("Changing...<" + bib.replaceAll("\"", "\\\"") +
		// ">");
		String res = bib;

		//The pattern search for an = followed by a quote followed by any character that is not followed by a quote followed by a comma or a closed curly bracket. 
		String stringPattern = "=\".+?\"[,}]";
		// Pattern pat = Pattern.compile("author=\".+?\"[,}]");
		Pattern pat = Pattern.compile(stringPattern);
		Matcher mat = pat.matcher(bib);
		// System.out.println(mat.toMatchResult());
		while (mat.find()) {
			//mat should have something similar to ="....", or ="...."}
			res = replaceQuotes(res, mat.start(), mat.end());
			
			// System.out.println(res);
		}

		return res;
	}

	/**
	 * @param cad String with a bibtex format.
	 * @param start Index of the starting piece of string
	 * @param end Index of the ending piece of string
	 * @return Replace the first quote found in the substring [start,end) for an open curly bracket and the last quote for a closed curly bracket.
	 */
	private static String replaceQuotes(String cad, int start, int end) {
		String res = "";
		int indxFirst = cad.indexOf("\"", start);
		int indxLast = cad.lastIndexOf("\"", end);
		res = cad.substring(0, indxFirst) + "{" + cad.substring(indxFirst + 1, indxLast) + "}"
				+ cad.substring(indxLast + 1);
		return res;
	}

	/**
	 * @param lines List with the lines obtained by reading the csv file downloaded from Springer.
	 * @return A list with the urls needed to download the Springer bibtex for each entry.
	 */
	public static List<String> extractURLS(List<String> lines) {
		List<String> urls = new ArrayList<String>();
		if (lines.size() == 0)
			System.exit(-1);
		lines.remove(0);// Remove the first line which include headers
		for (String line : lines) {
			try {
				int indexUrlBegin = line.indexOf("http://link.springer.com/");
				int indexUrlEnd = line.indexOf("\"", indexUrlBegin);
				System.out.println("line...<" + line + ">");
				String part = line.substring(indexUrlBegin + 25, indexUrlEnd);
				// System.out.println("urlBegin"+
				// indexUrlBegin+"urlEnd"+indexUrlEnd+"...<"+part+">");
				// This is an old version of the link that do not extract
				// abstracts
				// String url = "https://link.springer.com/export-citation/" +
				// part + ".bib";
				int indexUrl2begin = part.indexOf("/");
				String part2 = part.substring(indexUrl2begin + 1);
				// System.out.println("urlBegin"+
				// indexUrl2begin+"...<"+part2+">");
				String url2 = "https://citation-needed.springer.com/v2/references/" + part2
						+ "?format=bibtex&flavour=citation";
				// https://citation-needed.springer.com/v2/references/10.1007/978-3-319-94478-4_6?format=bibtex&flavour=citation
				// System.out.println("URL2-->"+ url2);
				urls.add(url2);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return urls;
	}

	/**
	 * @param urls List with the urls of all the Springer bibliographic entries to be included in the bib file.
	 * @return A list with the bibtex obtained by getting the resource from Springer by means of the URL.
	 * That is, an http get is sent for every url and the result is a bibtex for the bibliographic entry that represents the url.
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 **/
	public static List<String> readSpringerBibFromURL(List<String> urls)
			throws InterruptedException, URISyntaxException {
		List<String> bibs = new ArrayList<String>();
		if (urls.size() == 0)
			System.exit(-1);
		System.out.println("---------------------------------------");
		System.out.print("Starting...");
		Thread.sleep(3000L);
		System.out.println("---------------------------------------");
		int count = 1;
		for (String strUrl : urls) {
			try {
				System.out.println("Processing: " + count++);
				System.out.println(strUrl);
				String receivedBib = HTTPUtils.httpGet(strUrl);
				System.out.println(receivedBib.toString());
				bibs.add(receivedBib.toString());
			} catch (IOException e) {
				System.err.println("[ERROR] processing url " + strUrl);
				System.err.println("[ERROR] Entry ignored");
			}
		}
		return bibs;
	}
}
