/*
 * The MIT License
 *
 * Copyright 2019 AdamLaszlo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hu.evehcilabs.journey2;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import hu.evehcilabs.journey2.connection.Database;
import hu.evehcilabs.journey2.registry.EmailRegistry;
import hu.evehcilabs.journey2.registry.LinkRegistry;
import hu.evehcilabs.journey2.worker.Crawler;
import java.util.ArrayList;

/**
 * Collects email addresses from the web
 *
 * @author AdamLaszlo
 */
public class Journey implements Crawler.InteractionInterface {

    private EmailRegistry emailRegistry;
    private LinkRegistry linkRegistry;
    private Database database;
    private ArrayList<Crawler> crawlers;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO: Make parameter
        String startLink = "https://www.startlap.hu/";
        // TODO: Make parameter
        int crawlerNum = 16;

        Journey journey = new Journey();
        journey.start(startLink, crawlerNum);
    }

    public Journey() {
        database = new Database();
        database.connect();

        emailRegistry = new EmailRegistry(database);
        linkRegistry = new LinkRegistry(database);
    }

    /**
     * Starts the crawling process
     *
     * @param link The first link
     * @param crawlerNum Number of crawlers working parallel
     */
    private void start(@NotNull String link, int crawlerNum) {
        ArrayList<String> starterLinks = new ArrayList<String>() {
            {
                add(link);
            }
        };
        linkRegistry.saveNewLinks(starterLinks);

        crawlers = new ArrayList<>();
        Crawler crawler;
        for (int i = 1; i <= crawlerNum; i++) {
            crawler = new Crawler(this, i);
            crawler.start();
            crawlers.add(crawler);
        }

    }

    /**
     * Stops the crawling process
     */
    private void stop() {
        // TODO: Implement
    }

    boolean was = false;

    @Override
    public @Nullable
    String getNextLink() {
        return linkRegistry.getUnvisitedLink();
    }

    @Override
    public void saveLinks(String link, ArrayList<String> links) {
        linkRegistry.setLinkVisited(link);
        linkRegistry.saveNewLinks(links);
    }

    @Override
    public void saveEmails(String Link, ArrayList<String> emails) {
        emailRegistry.saveEmails(Link, emails);
    }
}
