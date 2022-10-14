package solutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class Database {
	private Map<String, WebPage> tracker;
	private Map<Integer, WebPage> idLookup;
	public int numUnprocessedPages = 0;
	
	public Database() {
		this.tracker = new HashMap<String, WebPage>();
		this.idLookup = new HashMap<Integer, WebPage>();
		this.load();
	}
	
	public void add(WebPage page) {
		this.tracker.put(page.url, page);
		this.idLookup.put(page.id, page);
		++this.numUnprocessedPages;
	}
	
	public WebPage getNext() {
		// keep looking for a page until you find one that hasn't been crawled:
		// ideally, we'd create a queue, but good enough for now:
		while (true) {
			List<Integer> keys = (List<Integer>)idLookup.keySet();
			int randIdx = (int)(Math.random() * this.tracker.size());
			int id = keys.get(randIdx);
			System.out.println(id);
			WebPage page = this.get(id);
			if (page.crawled == false) {
				--this.numUnprocessedPages;
				return page;
			}
		}
	}
	
	public WebPage get(String url) {
		return this.tracker.get(url);
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
//		List<Map.Entry<String, WebPage>> list = new LinkedList<Map.Entry<String, WebPage>>(this.tracker.entrySet());
//        list.sort(Entry.comparingByValue());
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
	
	@SuppressWarnings("unchecked")
	public void save() {
		JSONArray pages = new JSONArray();
		for (String key: this.tracker.keySet()) {
			pages.add(this.tracker.get(key).toJSON());
    	}
		
        try (FileWriter file = new FileWriter(this.getFilePath())) {
            file.write(pages.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
