package solutions;

import java.io.IOException;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private Database index;
    private LinkQueue queue;
    private int numPagesCrawled = 0;
    private int maxPagesToCrawl = 10;

    public WebCrawler(String baseURL) {
    	this.index = new Database();
    	this.queue = new LinkQueue();
        this.queue.add(baseURL);
    }

    public void start() {
    	System.out.println("Starting!");
    	while (this.queue.size() > 0 && this.numPagesCrawled <= this.maxPagesToCrawl) {
    		
    		// Fetch the HTML code
    		String url = this.queue.remove();
    		try {
    			System.out.println("Processing " + url + "...");
                
        		Document document = Jsoup.connect(url).get();
        		this.index.add(new WebPage(url, document));
                this.index.save();
        		
                
                // Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                // For each extracted URL... go back to Step 4.
                for (String link : this.getLinksOnPageUnique(linksOnPage)) {
                	// add to the queue if not in there already and page hasn't been crawled:
                	if (this.index.get(link) == null && !this.queue.contains(link)) {
                		if (link.startsWith("http")) {
                			this.queue.add(link);
                		}
                	} else if (this.index.get(link) != null) {
                		// if page has already been crawled, increment page rank:
                		this.index.get(link).numInboundLinks += 1;
                	}
                }
                this.queue.save();
                
                ++this.numPagesCrawled;
            } catch (IOException e) {
                System.err.println("For '" + url + "': " + e.getMessage());
            }
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	} 
    }
    
    private HashSet<String> getLinksOnPageUnique(Elements linksOnPage) {
    	HashSet<String> s = new HashSet<String>();
    	for (Element linkTag : linksOnPage) {
        	String link = this.getURL(linkTag);
        	s.add(link);
        }
    	return s;
    }
    
    private String getURL(Element linkTag) {
    	String link = linkTag.attr("abs:href").trim();
    	if (link.indexOf("#") != -1) {
    		link = link.substring(0, link.indexOf("#"));
    	}
    	if (link.charAt(link.length()-1) != '/') {
    		link = link + "/";
    	}
    	return link;
    }
    

    public static void main(String[] args) {
    	
        //1. Pick a URL from the frontier
    	WebCrawler crawler = new WebCrawler("https://www.unca.edu/");
    	crawler.start();
    	System.out.println("Links in the Queue");
    	crawler.queue.print();
    	System.out.println("Links that have been crawled");
    	crawler.index.print();
    }

}