package ygo_card_scraper_db;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//This is the scraper. It utilizes the Jsoup library to traverse the main wikia page for the TCG cards.

public class Name_Scraper {
	ArrayList<String> cardNames;
	Writer output;

	public Name_Scraper(Writer output) {
		cardNames = new ArrayList<String>();
		this.output = output;
	}

	//This method gets all the names of the cards, then recursively loops through all the "next page" links until it hits the end.
	public void scrapeRecursive(String startingURL) throws IOException {
		if (startingURL.equals(""))
			return;

		Document doc = Jsoup.connect(startingURL).get();
		Elements links = doc.select("a[href]");

		ArrayList<String> scrapedNames = new ArrayList<String>();

		int j = 0;
		// add all card names of links
		for (Element link : links) {
			if (j < 132) {
			} else
			//These are all special cases in which the data from the API and the wikia don't correspond with each other.
			//Cases may include: card name discrepancies, token cards, and 404 errors from the API
			if (link.text().equals("B.E.S. Big Core")) {
				scrapedNames.add("Big Core");
			} else if (link.text().equals("BIG Win!?")) {
				scrapedNames.add("BIG+Win%21%3F");
			} else if (link.text().contains("#"))
				scrapedNames.add(link.text().replace("#", ""));
			else if (link.text().contains("(original)") || link.text().contains("(temp)")
					|| link.text().contains("(Arkana)") || link.text().contains("(Fusion)")
					|| link.text().contains("(UDE promo)")) {
			} else if (link.text().contains("(Blue)") || link.text().contains("(Orange)")
					|| link.text().contains("Pink") || link.text().contains("Yellow")) {
			} else if (link.text().contains("Magical Musketeer Doc") || link.text().contains("Damage Vaccine")) {
			} else
				scrapedNames.add(link.text());
			j++;
		}

		String nextPage = "";
		int index = 0;
		for (Element link : links) {
			if (index == 333) {
				nextPage = "http://yugioh.wikia.com" + link.attr("href");
			}
			index++;
		}

		// print all of scrapedNames
		int indexOfOneAfterLastCardName = 0;
		for (int i = 0; i < scrapedNames.size(); i++) {
			if (scrapedNames.get(i).equals("previous 200"))
				indexOfOneAfterLastCardName = i;
		}

		// remove non-cards
		scrapedNames.subList(indexOfOneAfterLastCardName, scrapedNames.size()).clear();

		for (String s : scrapedNames) {
			output.append(s + "\n");
		}
		scrapeRecursive(nextPage);
	}

	public ArrayList<String> getCardNames() {
		return cardNames;
	}

}
