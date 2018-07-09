package ygo_card_scraper_db;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import java.util.concurrent.TimeUnit;

//TODO: Rewrite the Card class and make it inheritable, then divide into Monster, Spell, and Trap.
//TODO: Make the "A" archetype cards automatically scraped instead of manually appending.
//TODO: Damage Vaccine Omega MAX is one hell of a drug.

public class main {
	static Writer outputNames;
	static Writer output2;
	static Writer csv;
	static Name_Scraper scraper;
	static ArrayList<String> cardNames = new ArrayList<String>();
	static ArrayList<Card> listOfCards = new ArrayList<Card>();

	public static void main(String[] args) throws IOException, InterruptedException {

		System.out.println("Loading card from wiki and parsing data. Please be patient (this will take a while)!");

		// Create a file with a card name on each line. Each name is scraped from the
		// YGO wikia, with the "A" archetype appended manually.
		outputNames = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\card names.txt"));
		scraper = new Name_Scraper(outputNames);
		scraper.scrapeRecursive("http://yugioh.wikia.com/wiki/Category:TCG_cards?from=0");
		appendAArchetype(outputNames);

		// Create a file with raw card information for each card scraped from the wiki,
		// one on each line
		output2 = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\card info.txt"));
		scanAPI();
		output2.close();

		// create a Card item for each card and add to arraylist of cards
		try (BufferedReader br = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "\\effects2.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (parseCardInfo(line) != null)
					// parse the raw card data into an organized fashion
					listOfCards.add(parseCardInfo(line));
			}
		}

		// create a file with all the parsed card data. each line contains one card (bar
		// the first line), and the properties are
		// organized as 'name,text,card_type,type,family,atk,def,level', using a comma
		// as the delimiter.
		outputResults();
	}
	
	//add the "A" archetype to the file (to be automated later)
	private static void appendAArchetype(Writer w) {	
		try {
			w.append("\"A\" Cell Scatter Burst");
			w.append("\"A\" Cell Breeding Device\n");
			w.append("\"A\" Cell Incubator\n");
			w.append("\"A\" Cell Recombination Device\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// parse the raw card data in the format of what was scraped from the
	// yugiohprices API
	public static Card parseCardInfo(String cardData) throws MalformedURLException, IOException {
		Card thisCard = new Card();
		boolean ST = false;

		// System.out.println(cardData);
		thisCard.setRawInfo(cardData);

		String status = cardData.substring(cardData.indexOf("\"status\"") + 9, cardData.indexOf("," + ""));
		if (status.equals("\"fail\""))
			return null; // if card data wasn't found on the prices API, return a null card
		else {

			String name = cardData.substring(cardData.indexOf("\"name\"") + 7, cardData.indexOf(",\"text\""));
			thisCard.setName(name.replace("\"", ""));

			String text = cardData.substring(cardData.indexOf("\"text\"") + 7, cardData.indexOf(",\"card_type\""));
			thisCard.setText(text.replace("\"", ""));

			String card_type = cardData.substring(cardData.indexOf("\"card_type\"") + 12,
					cardData.indexOf(",\"type\""));
			thisCard.setCard_type(card_type.replace("\"", ""));
			if (thisCard.getCard_type().equals("trap") || thisCard.getCard_type().equals("spell"))
				ST = true;

			String type = cardData.substring(cardData.indexOf("\"type\"") + 7, cardData.indexOf(",\"family\""));
			if (ST)
				thisCard.setType(null);
			else
				thisCard.setType(type.replace("\"", ""));

			String family = cardData.substring(cardData.indexOf("\"family\"") + 9, cardData.indexOf(",\"atk\""));
			if (ST)
				thisCard.setFamily(null);
			else
				thisCard.setFamily(family.replace("\"", ""));

			if (ST)
				thisCard.setAtk(0);
			else {
				int atk = Integer
						.parseInt(cardData.substring(cardData.indexOf("\"atk\"") + 6, cardData.indexOf(",\"def\"")));
				thisCard.setAtk(atk);
			}

			if (ST)
				thisCard.setAtk(0);
			else {
				int def = Integer
						.parseInt(cardData.substring(cardData.indexOf("\"def\"") + 6, cardData.indexOf(",\"level\"")));
				thisCard.setDef(def);
			}

			if (ST)
				thisCard.setLevel(0);
			else {
				int level = Integer.parseInt(
						cardData.substring(cardData.indexOf("\"level\"") + 8, cardData.indexOf(",\"property\"")));
				thisCard.setLevel(level);
			}
		}
		return thisCard;
	}

	// for each of the monsters in the file, retrieve raw card data from the
	// yugiohprices API and append to a separate output file
	public static void scanAPI() throws InterruptedException, IOException {
		String baseURL = "http://yugiohprices.com/api/card_data/";

		scanFileToList(System.getProperty("user.dir") + "\\card names.txt");

		for (String s : cardNames) {
			String temp = s;
			if (temp.contains("Success Probability"))
				temp = temp.replace("%", "%25"); // special character case (%)
			TimeUnit.SECONDS.sleep((long) 0.5);

			@SuppressWarnings("resource")
			String cardData = new Scanner(new URL(baseURL + temp.replace(" ", "%20")).openStream(), "UTF-8")
					.useDelimiter("\\A").next();
			output2.append(cardData + "\n");
		}

	}

	// read each line of the file and add each one to an ArrayList of card names
	private static void scanFileToList(String file) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				cardNames.add(line);
			}
		}
	}

	// comma delimiter, start on second line
	private static void outputResults() throws IOException {
		csv = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\card database.txt"));
		csv.append("name,text,card_type,type,family,atk,def,level");
		for (Card c : listOfCards) {
			csv.append(c.getName() + "," + c.getText() + "," + c.getCard_type() + "," + c.getType() + ","
					+ c.getFamily() + "," + c.getAtk() + "," + c.getDef() + "," + c.getLevel() + "\n");
		}
		csv.close();
	}

}
