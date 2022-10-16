package solutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class Graph {
	private SortedMap<String, WebPage> tracker;
	public SortedMap<Integer, WebPage> idLookup;
	public int numUnprocessedPages = 0;
	
	public Graph() {
		this.tracker = new TreeMap<String, WebPage>();
		this.idLookup = new TreeMap<Integer, WebPage>();
		this.load();
	}
	
	public void add(WebPage page) {
		// only add to the graph if the page is new:
		if (this.get(page.url) == null) {
			this.tracker.put(page.url, page);
			this.idLookup.put(page.id, page);
			if (page.crawled == false) {
				++this.numUnprocessedPages;
			}
    	}
	}
	
	private WebPage getNext() {
		// keep looking for a page until you find one 
		// that hasn't been crawled (like a queue):
		Collection<WebPage> pages = idLookup.values();
		while (true) {
			for (WebPage page : pages) {
				if (page.crawled == false) {
					return page;
				}
			}
		}
	}
	
	public WebPage dequeue() {
		// don't forget to decrement the # of unprocessed links in the DB:
		--this.numUnprocessedPages;
		return this.getNext();
	}
	
	public WebPage get(String url) {
		return this.tracker.get(url);
	}
	
	public WebPage get(int id) {
		return this.idLookup.get(id);
	}
	
	public WebPage getOrCreate(String url) {
		WebPage page = this.tracker.get(url);
		if (this.tracker.get(url) != null) {
			return page;
		} else {
			return new WebPage(this.nextId(), url, null);
		}
	}
	
	public int nextId() {
		return this.tracker.size() + 1;
	}
	
	public int size() {
		return this.tracker.size();
	}
	
	public void print() {
		List<WebPage> pageList = new ArrayList<WebPage>();
		for (WebPage page : this.tracker.values()) {
			pageList.add(page);
		}
		pageList.sort(null);
    	for (WebPage page : pageList) {
    		if (page.crawled) {
    			System.out.println(page);
    		}
    	}
    }
	
	private String getFilePath() {
		String dir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        return dir + separator + "src/solutions/index.json";
	}
	
	public void load() {
		String filePath = this.getFilePath();
		File tempFile = new File(filePath);
		if (!tempFile.exists()) {
			return;
		}
		JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(filePath)) {

        	JSONArray pages = (JSONArray)parser.parse(reader);
            for (Object page : pages) {
            	this.add(new WebPage((JSONObject)page));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	
	private List<WebPage> getCrawledPages() {
		List<WebPage> crawledPages = new ArrayList<WebPage>();
		for (WebPage page : this.idLookup.values()) {
			if (page.crawled) {
				crawledPages.add(page);
			}
		}
		return crawledPages;
	}
	
	public void resetPageRanks() {
		Collection<WebPage> pagesOfInterest = this.getCrawledPages();
		for (WebPage voter : pagesOfInterest) {
			voter.pageRankOld = 0;
			voter.pageRank = 1;
		}
		this.save();	
	}
	
	public void processPageRank(int iterations) {
		if (iterations == 0) {
			return;
		}
		/**
		 * 1. Copy current rank to previous rank for all pages.
		 * 3. In this algo, we're only interested in analyzing 
		 * inbound / outbound links that have already been 
		 * crawled (strongly connected components)..
		 */
//		Collection<WebPage> pagesOfInterest = this.idLookup.values();
		Collection<WebPage> pagesOfInterest = this.getCrawledPages();

		// move pageRank to pageRankOld 
		double oldTotal = 0.0;
		for (WebPage voter : pagesOfInterest) {
			voter.pageRankOld = voter.pageRank;
			voter.pageRank = 0;
			oldTotal = oldTotal + voter.pageRankOld;
		}
		
		// assign pageRanks
		System.out.println("Calculating ranks...");
		double newTotal = 0.0;
		for (WebPage voter : pagesOfInterest) {
			List<WebPage> votees = voter.getCrawledOutboundPages(this);
//			System.out.println(votees.toString());
			double points = voter.pageRankOld / votees.size();
			for (WebPage votee : votees) {
				System.out.println(voter.id + " -> " + votee.id + ", points: " + points);
				votee.pageRank += points;
				newTotal += points;
			}
		}
		
		// evaporation:
		System.out.println("Evaporation...");
		double evap = (oldTotal - newTotal) / pagesOfInterest.size();
		for (WebPage voter : pagesOfInterest) {
			voter.pageRank += evap;
		}
		
		/***********************/
		/* get some statistics */
		/***********************/
		
		// calculate new total again:
		System.out.println("Calculate new totals...");
		newTotal = 0.0;
		for (WebPage voter : pagesOfInterest) {
			newTotal += voter.pageRank;
		}
		
		double totalDiff = 0.0;
		for (WebPage voter : pagesOfInterest) {
			totalDiff += Math.abs(voter.pageRankOld - voter.pageRank);
		}
		double avgDiff = totalDiff / pagesOfInterest.size();
		System.out.println("Average diff (" + iterations + "): " + avgDiff);
		
		
		processPageRank(iterations - 1);
		this.save();
	}
	
	
	@SuppressWarnings("unchecked")
	public void save() {
		JSONArray pages = new JSONArray();
		for (int key: this.idLookup.keySet()) {
			pages.add(this.idLookup.get(key).toJSON());
    	}
		
        try (FileWriter file = new FileWriter(this.getFilePath())) {
            file.write(pages.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
