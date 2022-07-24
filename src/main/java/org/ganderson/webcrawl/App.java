package org.ganderson.webcrawl;

import org.ganderson.webcrawl.service.WebCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Entry point of the app.
 */
public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the full URL that you want to crawl (e.g. https://monzo.com) or q to quit");
        while (true) {
            String value = scanner.nextLine();
            if ("q".equals(value)) {
                return;
            }

            try {
                new URL(value);
                new WebCrawler(new URL(value)).crawl();
            } catch (MalformedURLException ex) {
                System.out.println("The provided URL was not valid, please be sure to provide the full path including the protocol (http://monzo.com instead of monzo.com)");
            }
        }
    }
}
