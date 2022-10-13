package solutions;

import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebPage implements Comparable<WebPage> {
	public String url;
	private Document jsoupDoc;
	public Boolean crawled = false;
	public int numInboundLinks = 0;
	private String words = "";
	private String title = "";
	private String description = "";
	private String text = "";
	List<WebPage> outboundLinks = new LinkedList<WebPage>();
	
	public WebPage(String url, Document document) {
		this.url = url;	
		this.jsoupDoc = document;
		++this.numInboundLinks;
		this.parseHTMLData();
	}
	
	private void parseHTMLData() {
		this.title = this.jsoupDoc.title();
		Elements descElements = this.jsoupDoc.select("meta[name=description]");
		if (descElements.size() > 0) {
			this.description = descElements.get(0).attr("content");
		}
		this.text = this.jsoupDoc.selectFirst("main").text();
	              
	}
	
	public WebPage(JSONObject obj) {
		this.url = (String)obj.get("url");
		this.words = (String)obj.get("words");
		this.crawled = (boolean)obj.get("crawled");
		this.title = (String)obj.get("title");
		this.description = (String)obj.get("description");
		this.text = (String)obj.get("text");
		Long pageRank = (Long)obj.get("page_rank");
		this.numInboundLinks = pageRank.intValue();
	}
	
	@Override
	public String toString() {
		return "[" + this.numInboundLinks + "] " + this.url;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject page = new JSONObject();
		page.put("url", this.url);
		page.put("page_rank", this.numInboundLinks);
		page.put("words", this.words);
		page.put("crawled", this.crawled);
		page.put("title", this.title);
		page.put("description", this.description);
		page.put("text", this.text);
		return page;
	}
	
	public int compareTo(WebPage webpage){  
		if (this.numInboundLinks == webpage.numInboundLinks)  
			return 0;  
		else if(this.numInboundLinks > webpage.numInboundLinks)  
			return -1;  
		else  
			return 1;  
	}  

}
