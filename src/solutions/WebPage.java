package solutions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;

public class WebPage implements Comparable<WebPage> { 
    private static final DecimalFormat df = new DecimalFormat("0.0000");

	public int id;
	public String url;
	public Boolean crawled = false;
	public double pageRankOld = 0;
	public double pageRank = 1;
	public List<Integer> outboundPageIds = new LinkedList<Integer>();

	public String title = "";
	public String description = "";
	public String text = "";
	
	
	public WebPage(int id, String url, Document jsoupDoc) {
		this.id = id;
		this.url = url;	
		if (jsoupDoc != null) {
			this.parseHTMLData(jsoupDoc);
		}
	}
	

	@SuppressWarnings("unchecked")
	public WebPage(JSONObject obj) {
		Long id = (Long)obj.get("id");
		this.id = id.intValue();
		this.url = (String)obj.get("url");
		this.crawled = (boolean)obj.get("crawled");
		this.title = (String)obj.get("title");
		this.description = (String)obj.get("description");
		this.text = (String)obj.get("text");
		List<Long> links = (List<Long>)obj.get("links");
		List<Integer> formattedLinks = new ArrayList<Integer>();
		for(Long link : links) {
			formattedLinks.add(link.intValue());
		}
		this.outboundPageIds = formattedLinks;
		double pageRank = (double)obj.get("page_rank");
		this.pageRank = pageRank;
		double oldPageRank = (double)obj.get("page_rank_old");
		this.pageRankOld = oldPageRank;
	}
	
	@Override
    public boolean equals(Object page) {
		if (this.url == ((WebPage)page).url) {
			return true;
		}
		return false;
	}
	
	public void addOutboundPage(WebPage page) {
		if (this.outboundPageIds.indexOf(page.id) == -1) {
			this.outboundPageIds.add(page.id);
		}
	}
	
	
	public void parseHTMLData(Document jsoupDoc) {
		if (jsoupDoc == null) {
			return;
		}
		this.title = jsoupDoc.title();
		Elements descElements = jsoupDoc.select("meta[name=description]");
		if (descElements.size() > 0) {
			this.description = descElements.get(0).attr("content");
		}
		Element mainTag = jsoupDoc.selectFirst("main");
		if (mainTag != null) {
			this.text = mainTag.text();
		}
	    this.crawled = true;         
	}
	
	@Override
	public String toString() {
		return "[" + df.format(this.pageRank) + "] id=" + this.id + " " + this.url;
	}
	
	
	public List<Integer> getSortedOutboundPageIds() {
		this.outboundPageIds.sort(null);
		return this.outboundPageIds;
	}
	
	public List<WebPage> getCrawledOutboundPages(Graph graph) {
		List<WebPage> crawled = new ArrayList<WebPage>();
		for(Integer id : this.outboundPageIds) {
			WebPage page = graph.idLookup.get(id);
			if (page.crawled) {
				crawled.add(page);
			}
		}
		return crawled;
		
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject page = new JSONObject();
		page.put("id", this.id);
		page.put("url", this.url);
		page.put("page_rank", this.pageRank);
		page.put("page_rank_old", this.pageRankOld);
		page.put("crawled", this.crawled);
		page.put("title", this.title);
		page.put("description", this.description);
		page.put("text", this.text);
		page.put("links", this.getSortedOutboundPageIds());
		return page;
	}
	
	public int compareTo(WebPage webpage) {  
		// sorts by page rank descending
		if (this.pageRank == webpage.pageRank)  
			return 0;  
		else if(this.pageRank > webpage.pageRank)  
			return -1;  
		else  
			return 1;  
	}  

}
