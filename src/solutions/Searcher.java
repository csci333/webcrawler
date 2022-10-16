package solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author svanwart
 * This is a very simple search engine that just matches on ANY
 * keyword in the text, and prints the matches in the order of
 * their page rank (descending).
 * 
 * Takes a graph as an input, where each node of the graph has
 * a pageRank and a text field with keywords.
 */
public class Searcher {
	
	private Graph graph;
	
	public Searcher(Graph graph) {
		this.graph = graph;
	}
	
	public List<WebPage> search(String term) {
		List<WebPage> matches = new ArrayList<WebPage>();
		for( WebPage page : this.graph.getCrawledPages()) {
			// case-insensitive search:
			if (page.text.toLowerCase().indexOf(term.toLowerCase()) != -1) {
				matches.add(page);
			}
		}
		// sorts by page rank descending
		matches.sort(null);
		return matches;
	}
	
	public void printMatches(List<WebPage> matches) {
		int num = 1;
		for (WebPage match : matches) {
			System.out.println(num + ". " + " (" + match.pageRank + ") " + match.url);
			System.out.println(match.text.substring(0, Math.min(match.text.length(), 400)) + "...");
			System.out.println("---------------------------");
			++num;
		}
		if (matches.size() == 0) {
			System.out.println("no matches found");
		}
	}
	
	public static void main(String[] args) {
		while(true) {
			Scanner scanner = new Scanner(System.in);  // Create a Scanner object
			System.out.println("Enter a search term (or Q to quit): ");
			String term = scanner.nextLine();  // Read user input
			if (term.toUpperCase().trim().equals("Q")) {
				scanner.close();
				System.out.println("Quitting program...");
				break;
			}
			
			Graph graph = new Graph();
			Searcher searcher = new Searcher(graph);
			List<WebPage> matches = searcher.search(term);
			searcher.printMatches(matches);
		}
	}
}
