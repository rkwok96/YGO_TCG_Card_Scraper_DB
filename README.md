# YGO_TCG_Card_Scraper_DB

### Basics

- Basic written-for-fun scraper for YuGiOh TCG card data, written in Java in Eclipse IDE;
- Scrapes names from the YGO Wikia page and compiles them all in a file (card names.txt) while using the JSoup library;
- Also scrapes card information for each of those cards (name, attribute, effect, atk, def, etc.) and outputs to another file (card info.txt);
- Also parses all of the data retrieved into a file with comma-separated values (card database.txt);

### External Sources
- Uses the Jsoup library for link seeking
- Uses the yugiohprices API for retrieving card data. Shoutout to [yugiohprices.com](http://yugiohprices.com)!

### Issues and Bugs
- Since the scraper relies on two separate services, some data might not correlate between both and is thus ignored (eg. Cyberse White Hat is scraped from wikia but has no such data on yugiohprices API, hence it is ignored).
- Some special characters may cause the program to crash if not running within the Eclipse IDE with UTF-8 encoding for text (eg. Ω).
- The "A" archetype of cards is hardcoded, not automated.

### To-Do
- Automate all cards, including "A" cards.
- Rewrite the Card class as inheritable and divide the cards into monsters, spells, and traps.
- Fix the text encoding issue.

7/8/2018 initial commit
