package solutions;

import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebPage { // implements Comparable<WebPage>
	public int id;
	public String url;
	public Boolean crawled = false;
	public int numInboundLinks = 0;
	
	private String title = "";
	private String description = "";
	private String text = "";
	
	public WebPage(int id, String url, Document jsoupDoc) {
		this.id = id;
		this.url = url;	
		++this.numInboundLinks;
		if (jsoupDoc != null) {
			this.parseHTMLData(jsoupDoc);
		}
	}
	
	public WebPage(JSONObject obj) {
		Long id = (Long)obj.get("id");
		this.id = id.intValue();
		this.url = (String)obj.get("url");
		this.crawled = (boolean)obj.get("crawled");
		this.title = (String)obj.get("title");
		this.description = (String)obj.get("description");
		this.text = (String)obj.get("text");
//		Long pageRank = (Long)obj.get("page_rank");
//		this.numInboundLinks = pageRank.intValue();
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
		return "[" + this.numInboundLinks + "] " + this.url;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject page = new JSONObject();
		page.put("id", this.id);
		page.put("url", this.url);
//		page.put("page_rank", this.numInboundLinks);
		page.put("crawled", this.crawled);
		page.put("title", this.title);
		page.put("description", this.description);
		page.put("text", this.text);
		return page;
	}
	
//	public int compareTo(WebPage webpage){  
//		if (this.numInboundLinks == webpage.numInboundLinks)  
//			return 0;  
//		else if(this.numInboundLinks > webpage.numInboundLinks)  
//			return -1;  
//		else  
//			return 1;  
//	}  

}
