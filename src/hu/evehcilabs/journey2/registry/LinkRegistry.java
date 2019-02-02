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
package hu.evehcilabs.journey2.registry;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import hu.evehcilabs.journey2.connection.Database;
import java.util.ArrayList;

/**
 *
 * @author AdamLaszlo
 */
public class LinkRegistry {

    private final Database database;

    public LinkRegistry(Database database) {
        this.database = database;
    }

    /**
     * Returns an unvisited link
     *
     * @return The link
     */
    public @Nullable
    String getUnvisitedLink() {
        return database.getRandomUnvisitedLink();
    }

    /**
     * Marks a link as visited
     *
     * @param link the link
     */
    public void setLinkVisited(@NotNull String link) {
        database.setLinkVisited(link);
    }

    /**
     * Saves newly added links
     *
     * @param links
     */
    public void saveNewLinks(ArrayList<String> links) {
        database.saveNewLinks(links);
    }

}
