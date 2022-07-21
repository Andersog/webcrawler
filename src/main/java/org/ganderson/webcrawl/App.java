package org.ganderson.webcrawl;

import org.ganderson.webcrawl.service.WebCrawler;

import java.net.URL;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        new WebCrawler(new URL("https://monzo.com/")).crawl();
    }
}
