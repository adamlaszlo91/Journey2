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
package hu.evehcilabs.journey2.worker;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.EmailValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author AdamLaszlo
 */
public class Crawler implements Runnable {

    private Thread thread;
    private InteractionInterface interactionInterface;
    private int id;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);
    private static final HashMap<String, String> REPLACE_MAP = new HashMap<String, String>() {
        {
            put("[at]", "@");
            put("[kukac]", "@");
            put("[dot]", ".");
            put("[pont]", ".");
        }
    };

    public Crawler(InteractionInterface interactionInterface, int id) {
        this.interactionInterface = interactionInterface;
        this.id = id;
    }

    /**
     * Method to start the thread
     */
    public void start() {
        this.thread = new Thread(this);
        this.thread.start();

        System.out.println("Crawler started id: " + id);
    }

    /**
     * Method to stop the thread
     */
    public void stop() {
        this.thread = null;

        System.out.println("Crawler stopped id: " + id);
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (this.thread == thisThread) {
            String link;
            if (interactionInterface != null && (link = interactionInterface.getNextLink()) != null) {
                ArrayList<String> links = new ArrayList<>();

                try {
                    // Get document
                    Document document = Jsoup.connect(link).get();
                    URL url = new URL(link);

                    // Search for links
                    Elements documentLinks = document.select("a[href]");
                    for (Element documentLink : documentLinks) {
                        String l = documentLink.attr("href");
                        if (l.contains("javascript") || l.contains("mailto:")
                                || url.getHost().equals("")) {
                            continue;
                        }

                        if (!l.startsWith("http")) {
                            links
                                    .add(url.getProtocol() + "://" + url.getHost() + l);
                        } else {
                            links.add(l);
                        }
                    }

                    // Search for emails
                    ArrayList<String> emails = new ArrayList<>();
                    String page = document.toString();
                    for (String key : REPLACE_MAP.keySet()) {
                        page = page.replace(key, REPLACE_MAP.get(key));
                    }
                    page = page.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    page = URLDecoder.decode(page, "UTF-8");

                    Matcher matcher = EMAIL_PATTERN.matcher(page);
                    while (matcher.find()) {
                        if (EmailValidator.getInstance().isValid(matcher.group())) {
                            emails.add(matcher.group());
                        }
                    }

                    if (interactionInterface != null && !emails.isEmpty()) {
                        interactionInterface.saveEmails(link, emails);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Sending empty list too to make the faulty pages marked as visited
                    if (interactionInterface != null) {
                        interactionInterface.saveLinks(link, links);
                    }
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static interface InteractionInterface {

        /**
         * Returns the next available link to visit
         *
         * @return Link
         */
        public @Nullable
        String getNextLink();
        

        /**
         * Called with the found links on the current page
         *
         * @param link The source of the further links
         * @param links Links found on the source page
         */
        public void saveLinks(String link, @NotNull ArrayList<String> links);

        /**
         * Called with the found emails on the current page
         *
         * @param link The link on which the emails have been found
         * @param emails Emails
         */
        public void saveEmails(@NotNull String link, @NotNull ArrayList<String> emails);
    }

}
