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
package hu.evehcilabs.journey2.connection;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author AdamLaszlo
 */
public class Database {

    private Connection connection;
    /**
     * Database version, shall be used on updates
     */
    private final int version = 1;

    /**
     * Connects to the database
     */
    public synchronized void connect() {
        try {
            String url = "jdbc:sqlite:database/database.db";
            // Create a connection to the database
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established");
            initTables();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the necessary tables
     */
    private void initTables() throws SQLException {
        if (version >= 1) {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS email (title TEXT PRIMARY KEY, link TEXT, timestamp INTEGER)");
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS link (title TEXT PRIMARY KEY, timestamp INTEGER)");
        }
    }

    // ----------------------------------------------------------
    /**
     * Returns the number of saved emails in the database
     *
     * @return Number of saved emails
     */
    public synchronized int getEmailCount() {
        String sql = "SELECT COUNT(*) FROM email";

        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves email adresses to the database
     *
     * @param link The link on which the emails have been found
     * @param emails Emails
     * @return Number of inserted
     */
    public synchronized int saveEmails(@NotNull String link, @NotNull ArrayList<String> emails) {
        if (emails.isEmpty()) {
            return 0;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT OR IGNORE INTO email VALUES");
        for (int i = 0; i < emails.size(); i++) {
            builder.append("(?,?,?)");
            if (i != emails.size() - 1) {
                builder.append(",");
            }
        }

        try {
            PreparedStatement statement = connection.prepareStatement(builder.toString());
            for (int i = 0; i < emails.size(); i++) {
                statement.setString(i * 3 + 1, emails.get(i));
                statement.setString(i * 3 + 2, link);
                statement.setLong(i * 3 + 3, System.currentTimeMillis());
            }
            return statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Saves newly found links to the database
     *
     * @param links links
     */
    public synchronized void saveNewLinks(@NotNull ArrayList<String> links) {
        if (links.isEmpty()) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT OR IGNORE INTO link VALUES");
        for (int i = 0; i < links.size(); i++) {
            builder.append("(?,?)");
            if (i != links.size() - 1) {
                builder.append(",");
            }
        }

        try {
            PreparedStatement statement = connection.prepareStatement(builder.toString());
            for (int i = 0; i < links.size(); i++) {
                statement.setString(i * 2 + 1, links.get(i));
                statement.setLong(i * 2 + 2, 0);
            }
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gives a random non-visited ink
     *
     * @return An unvisited link or null, if not found such
     */
    public synchronized @Nullable
    String getRandomUnvisitedLink() {
        String sql = "SELECT title FROM link WHERE timestamp = 0 ORDER BY random() LIMIT 1";

        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Sets the timestamp for the given link, hence marking it as visited
     *
     * @param link The link
     */
    public synchronized void setLinkVisited(@NotNull String link) {

        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE link SET timestamp = ? WHERE title = ?");

        try {
            PreparedStatement statement = connection.prepareStatement(builder.toString());
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, link);

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
