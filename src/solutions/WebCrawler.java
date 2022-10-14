package solutions;

import java.io.IOException;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private Database database;
    private LinkQueue queue;
    private int numPagesCrawled = 0;
    private int maxPagesToCrawl = 100;

    public WebCrawler() {
    	this.database = new Database();
    	this.queue = new LinkQueue();
    }
    
    public void initialize(String baseURL) {
    	WebPage newPage = new WebPage(this.database.nextId(), baseURL, null);
    	this.database.add(newPage);
    }

    public void start() {
    	System.out.println("Starting! " + this.queue.size());
    	while (this.database.numUnprocessedPages > 0 && this.numPagesCrawled <= this.maxPagesToCrawl) {
    		
    		// Fetch the HTML code
    		WebPage currentPage = this.database.getNext();
    		try {
    			System.out.println("Processing " + currentPage.url + "...");
                
    			// index the current page:
        		Document document = Jsoup.connect(currentPage.url).get();
        		currentPage.parseHTMLData(document);
        		// don't forget to decrement the # of unprocessed links in the DB:
        		--this.database.numUnprocessedPages;
                
                // Queue up the links that haven't been visited.
                for (String link : this.getLinksOnPageUnique(document)) {
                	if (!link.startsWith("http")) {
                		continue;
                	}
                	// create new WebPage object (before it's been processed):
                	WebPage newPage = new WebPage(this.database.nextId(), link, null);
                	
                	// add to the database if the link isn't there already:
                	if (this.database.get((String)newPage.url) != null) {
                		this.database.add(newPage);
                	}
                	
                	// add to the queue if not in there already:
            		this.queue.add(currentPage, newPage);
                }
                this.database.save();
                this.queue.save();
                
                ++this.numPagesCrawled;
            } catch (IOException e) {
                System.err.println("For '" + currentPage.url + "': " + e.getMessage());
            }
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	} 
    }
    
    private HashSet<String> getLinksOnPageUnique(Document document) {
        Elements linksOnPage = document.select("a[href]");
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
    	
        // initialize the crawler:
    	WebCrawler crawler = new WebCrawler();
    	
    	// if there are no links in the queue, initialize:
    	if (crawler.database.size() == 0) {
    		crawler.initialize("https://www.unca.edu/");
    	}
    	
    	// begin crawling:
    	crawler.start();
    	System.out.println("Links in the Queue");
    	crawler.queue.print();
    	System.out.println("Links that have been crawled");
    	crawler.database.print();
    }

}