package com.ids.vcoulter.minesweeper

import com.google.inject.Inject
import com.ids.ratpack.common.context.IdsContext
import com.ids.ratpack.common.handler.IdsHandler
import com.ids.ratpack.common.logging.IdsLogManager
import groovy.sql.Sql

/**
 * Created by vcoulter in Oct 2015.
 * Example input: { "name": "John" } and then John is recorded as having a draw.
 */
class DrawHandler extends IdsHandler{
    @Inject Sql sql

    private static final log = IdsLogManager.getLogger(this);

    @Override
    void handle(IdsContext context) {

        // First get the name.
        /* Pass "Map" to parseJson because we are getting a key-value pair.
         * If there's a parsing failure, client will get a 400. */
        context.parseJson(Map).then {
            String playerName;
            playerName = Common.getNameFromMap(it)
            // If playerName was "", then getNameFromMap would throw an error.
            if (Common.getVerboseLogging()) log.debug(it.toString())

            // Add a draw for this player.
            def playerRow = Common.getPlayerRecord(sql, playerName)
            def queryResult

            if (!playerRow) {
                // We don't have a record for this player.
                queryResult = Common.addNewPlayer(sql, playerName, 0, 0, 0, 1)
                // TODO - queryResult should be true or 1. What if it's not?

            } else {  // if this is an existing player

                def newDrawCount = playerRow.get("draws") + 1
                String updateQuery = """\
                        UPDATE PlayerScore
                        SET draws = $newDrawCount
                        WHERE name = '$playerName';
                        """.stripIndent()
                queryResult = Common.executeInsertOrUpdate(sql, updateQuery)
                // TODO - queryResult should be true or 1. What if it's not?
            }
            // TODO - figure out what signal to send the client that we're done
            context.render(playerName + " updated with 1 more draw.")
        } // end parseJson().then

    }
}