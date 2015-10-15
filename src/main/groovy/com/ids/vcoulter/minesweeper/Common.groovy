package com.ids.vcoulter.minesweeper

import com.ids.ratpack.common.error.IdsClientFailure
import com.ids.ratpack.common.logging.IdsLogManager
import groovy.sql.Sql

/**
 * Created by vcoulter in Oct 2015.
 * Consists of methods that other classes need to call.
 */
class Common {

    private static boolean verboseLog = true;

    static boolean getVerboseLogging() {
        return verboseLog
    }

    static void setVerboseLogging(boolean verboseLogOn) {
        Common.verboseLog = verboseLogOn
    }
/**
     * Purpose: Given the map, returns a name. Throws IdsClientFailure
     *  if there's no "name" key in the map or if there is one but it's blank.
     * @param map
     * @return - the value associated with the "name" key in the map.
     */
    public static String getNameFromMap(Map map) {

        String nameValue = "";

        if (!map.containsKey("name"))
            throw new IdsClientFailure("No name sent. Field 'name' is required.")
        nameValue = map.get("name")
        if (nameValue.equals(""))
            throw new IdsClientFailure("No name sent. Field 'name' is required.")

        return nameValue;
    }

    // TODO - shall I handle SqlExceptions in all sql calls?
    // Danny said in response: If user actions/data entry can call an error, handle gracefully.
    //  If an end user couldn't have caused this error, then let happen what happens (usually a 500).

    /**
     * Purpose: Given a player name, returns their record. (All of it -- so whoever
     *  the caller is, they have what they need.
     * @param sql - the Sql object with its data source that's in use.
     * @param playerName - name of the player to look up.
     * @return one record with the fields: name, wins, losses, draws, score,
     *  or no record (null) if the playerName isn't in the table. Caller is responsible to check.
     */
    public static Map getPlayerRecord(Sql sql, String playerName) {
        // A note on data safety: the client has a restriction on the kinds of characters
        // that can make up playerName. http://docs.groovy-lang.org/latest/html/api/groovy/sql/Sql.html
        // claims that the "'$playername'" syntax will help guard against SQL inject too.
        String findQuery = "SELECT name, wins, losses, draws, score FROM PlayerScore WHERE name = '$playerName'"
        return sql.firstRow(findQuery);
    }

    /**
     * Purpose: adds a new player record with the specified values.
     * @param sql
     * @param playerName
     * @param wins
     * @param score
     * @param losses
     * @param draws
     * @return - true if the insert affected 1 record & false if it didn't.
     */
    public static boolean addNewPlayer(Sql sql, String playerName,
                                       int wins, int score, int losses, int draws) {

        def insertQuery = """\
            INSERT INTO PlayerScore
                (name, wins, score, losses, draws)
            VALUES ('$playerName', $wins, $score, $losses, $draws);""".stripIndent()

        int insertResult = executeInsertOrUpdate(sql, insertQuery)

        if (1 == insertResult)
            return true;
        else
            return false;
    }

    /**
     * Purpose: encapsulates some of the query logging & error handling,
     *  but the caller is responsible for communicating success/failure to the user.
     * @param sql
     * @param query (possible TODO, have an overloaded method so that query could be either a String or a GString.)
     * @return - # of rows affected. 0 probably means it failed.
     */
    public static int executeInsertOrUpdate(Sql sql, String query) {

        boolean result = sql.execute(query)
        def updateCount = sql.updateCount
        // If verbose logging is on
        if (getVerboseLogging()) {
            IdsLogManager.getLogger(this).debug("""Query '$query'\n had result $result and update count is now $updateCount""")
        }
        return updateCount
    }
}
