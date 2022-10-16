package solutions;

import java.io.IOException;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private Graph graph;
    private int numPagesCrawled = 0;
    private int maxPagesToCrawl = 10;

    public WebCrawler() {
    	this.graph = new Graph();
    }
    
    public void initialize(String baseURL) {
    	WebPage newPage = new WebPage(this.graph.nextId(), baseURL, null);
    	this.graph.add(newPage);
    }

    public void traverse(int iterations) {
    	this.maxPagesToCrawl = iterations;
    	System.out.println("Starting the traversal: " + this.graph.numUnprocessedPages);
    	while (this.graph.numUnprocessedPages > 0 && this.numPagesCrawled <= this.maxPagesToCrawl) {
    		
    		// Fetch the HTML code
    		WebPage currentPage = this.graph.dequeue();
    		try {
    			System.out.println("Processing " + currentPage.url + "...");
                
    			// index the current page:
        		Document document = Jsoup.connect(currentPage.url).get();
        		currentPage.parseHTMLData(document);
                
                // Queue up the links that haven't been visited.
                for (String link : this.getLinksOnPageUnique(document)) {
                	if (!link.startsWith("http")) {
                		continue;
                	}
                	// convert the link to a new page (or get a handle 
                	// to the page if it already exists in the graph):
                	WebPage newPage = this.graph.getOrCreate(link);
                	
                	// add page to the graph (if it's not there already):
                	this.graph.add(newPage);
                	currentPage.addOutboundPage(newPage);
                }
                
                // save the DB and the queue after every iteration:
                this.graph.save();
                
                ++this.numPagesCrawled;
                
            } catch (IOException e) {
                System.err.println("For '" + currentPage.url + "': " + e.getMessage());
                currentPage.crawled = true;
                this.graph.save();
            }
        	try {
				Thread.sleep(500);
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
    	if (crawler.graph.size() == 0) {
    		crawler.initialize("https://www.unca.edu/");
    	}
    	
    	// begin crawling:
    	crawler.traverse(200);
    	
//    	// 90 works!
//    	crawler.graph.resetPageRanks();
//    	crawler.graph.print();
    	crawler.graph.processPageRank(3);
    	crawler.graph.print();
    	
    }

}