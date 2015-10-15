package com.ids.vcoulter.minesweeper

import com.google.inject.Inject
import com.ids.ratpack.common.context.IdsContext
import com.ids.ratpack.common.handler.IdsHandler
import com.ids.ratpack.common.logging.IdsLogManager
import groovy.sql.Sql

/**
 * Created by vcoulter in Oct 2015.
 * Example input: { "name": "John", "score": "20" } and then John is recorded as having a win worth 20 points.
 */
class WinHandler extends IdsHandler{
    @Inject Sql sql

    private static final log = IdsLogManager.getLogger(this);

    @Override
    void handle(IdsContext context) {

        if (Common.getVerboseLogging()) log.debug("Started WinHandler")
        /* Pass "Map" to parseJson because we are getting a key-value pair.
         * If there's a parsing failure, client will get a 400. */
        context.parseJson(Map).then {

            // Get the player name. (Client will get a 400 if the player name is "".)
            String playerName = Common.getNameFromMap(it)
            if (Common.getVerboseLogging()) log.debug(it.toString())
            def currentScore = 0;
            // Be loosy-goosy. If we can't find the score in what was passed in,
            // or if it's not an integer, then let the poor dude's score be 0.
            if (it.containsKey("score"))
                if (it.get("score").isInteger())
                    currentScore = it.get("score") as Integer

            if (Common.getVerboseLogging()) log.debug("In WinHandler: $playerName earned score $currentScore")

            def playerRow = Common.getPlayerRecord(sql, playerName)
            def queryResult

            if (!playerRow) {
                // We don't have a record for this player.
                queryResult = Common.addNewPlayer(sql, playerName, 1, currentScore, 0, 0)
                // TODO - queryResult should be true or 1. What if it's not?
            } else {

                def newWinCount = playerRow.get("wins") + 1
                def newTotalScore = playerRow.get("score") + currentScore
                String updateQuery = """\
                        UPDATE PlayerScore
                        SET wins = $newWinCount, score = $newTotalScore
                        WHERE name = '$playerName';
                        """.stripIndent()
                queryResult = Common.executeInsertOrUpdate(sql, updateQuery)
                // TODO - queryResult should be true or 1. What if it's not?
            }  // end else update existing player

            // TODO - figure out what signal to send the client that we're done
            context.render(playerName + " updated with 1 more win of " + currentScore.toString() + " points.")

        }  // end parseJson().then
    }
}
