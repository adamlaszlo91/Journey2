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
import hu.evehcilabs.journey2.connection.Database;
import java.util.ArrayList;

/**
 * Acts as a virtual store for the collected email addresses
 *
 * @author AdamLaszlo
 */
public class EmailRegistry {

    /**
     * The total number of saved numbers
     */
    private int savedEmailCounter = 0;
    private final Database database;

    public EmailRegistry(@NotNull Database database) {
        this.database = database;
        savedEmailCounter = database.getEmailCount();
    }

    /**
     * Saves email addresses to the database
     *
     * @param link The link on which the emails have been found
     * @param emails The email addresses
     */
    public void saveEmails(@NotNull String link, @NotNull ArrayList<String> emails) {
        savedEmailCounter += database.saveEmails(link, emails);

        System.out.println("Email count: " + String.valueOf(savedEmailCounter));
        System.out.println("Recently found: \n\t" + String.join("\n\t", emails));
    }
}
