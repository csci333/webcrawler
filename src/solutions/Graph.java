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
	private SortedMap<Integer, WebPage> idLookup;
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
		// that hasn't been crawled. Ideally, we'd 
		// create a queue, but good enough for now:
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
		WebPage page = this.getNext();
		--this.numUnprocessedPages;
		return page;
	}
	
	public WebPage get(String url) {
		return this.tracker.get(url);
	}
	
	public WebPage getOrCreate(String url) {
		WebPage page = this.tracker.get(url);
		if (this.tracker.get(url) != null) {
			return page;
		} else {
			return new WebPage(this.nextId(), url, null);
		}
	}
	
	public WebPage get(int id) {
		return this.idLookup.get(id);
	}
	
	public int nextId() {
		return this.tracker.size() + 1;
	}
	
	public int size() {
		return this.tracker.size();
	}
	
	public void print() {
    	for (Map.Entry<String, WebPage> entry: this.tracker.entrySet()) {
    	    System.out.println(entry.getValue());
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
		return new ArrayList<WebPage>();
	}
	
	public void processPageRank(int iterations) {
		/**
		 * Pseudocode:
		 * 1. Get all web pages that have already been crawled.
		 * 2. Figure out which of the pages point to them.
		 * 		e.g. pp. 1-5 have been crawled.
		 * 			 pp. 1-5 are voting for other pages.
		 */
//		List<WebPage> crawledPages = this.getCrawledPages();
		for (WebPage voter : tracker.values()) {
			List<Integer> ids = voter.getSortedOutboundPageIds();
			System.out.println(ids.toString());
			System.out.println(voter.id);
			for (int i = 0; i < ids.size(); i++) {
				Integer votee = ids.get(i);
				System.out.println("VOTEE: " + votee);
				// give a vote to each page to which the voter points:
				WebPage page = this.idLookup.get(votee);
				page.pageRank += 1;
			}
		}
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
