package solutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class Graph {
	private SortedMap<String, WebPage> urlLookup;
	public SortedMap<Integer, WebPage> idLookup;
	public int numUnprocessedPages = 0;
	
	public Graph() {
		this.urlLookup = new TreeMap<String, WebPage>();
		this.idLookup = new TreeMap<Integer, WebPage>();
		this.load();
	}
	
	public void add(WebPage page) {
		// only add to the graph if the page is new:
		if (this.get(page.url) == null) {
			this.urlLookup.put(page.url, page);
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
		return this.urlLookup.get(url);
	}
	
	public WebPage get(int id) {
		return this.idLookup.get(id);
	}
	
	public WebPage getOrCreate(String url) {
		WebPage page = this.urlLookup.get(url);
		if (page != null) {
			return page;
		} else {
			return new WebPage(this.nextId(), url, null);
		}
	}
	
	public int nextId() {
		return this.urlLookup.size() + 1;
	}
	
	public int size() {
		return this.urlLookup.size();
	}
	
	public void printCrawledPages() {
		// first, sort the pages by their page rank:
		List<WebPage> pageList = new ArrayList<WebPage>();
		for (WebPage page : this.urlLookup.values()) {
			pageList.add(page);
		}
		pageList.sort(null);
		
		// then output them:
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
		// loads the graph from the JSON file (if it exists):
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
	
	public List<WebPage> getCrawledPages() {
		List<WebPage> crawledPages = new ArrayList<WebPage>();
		for (WebPage page : this.idLookup.values()) {
			if (page.crawled) {
				crawledPages.add(page);
			}
		}
		return crawledPages;
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
