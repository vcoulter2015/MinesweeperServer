package com.ids.vcoulter.minesweeper

import com.google.inject.Inject
import com.ids.ratpack.common.context.IdsContext
import com.ids.ratpack.common.handler.IdsHandler
import com.ids.ratpack.common.logging.IdsLogManager
import groovy.sql.Sql

/**
 * Created by vcoulter in Oct 2015.
 * Example input: { "name": "John" } and then John is recorded as having a loss.
 */
class LossHandler extends IdsHandler {
    /* Use the Sql getSql defined in Ratpack.groovy. */
    @Inject Sql sql

    private static final log = IdsLogManager.getLogger(this);

    @Override
    void handle(IdsContext context) {

        if (Common.getVerboseLogging()) log.debug("Started LossHandler")
        /* Pass "Map" to parseJson because we are getting a key-value pair.
            If we get no input, or a string that's not JSON, or JSON that's not a key-value pair,
            then that gets a 400 bad request with the failure message "Invalid JSON in request body."
            (TODO - figure out where in the IdsContext.parseJson code it figures out to return a 400.)
            (Throwing an IdsClientFailure returns a 400.)
         */
        context.parseJson(Map).then {
            String playerName;
            if (Common.getVerboseLogging()) log.debug(it.toString())
            playerName = Common.getNameFromMap(it)
            // If playerName was "", then getNameFromMap would throw an error.

            def playerRow = Common.getPlayerRecord(sql, playerName)
            def queryResult

            if (!playerRow) {
                // We don't have a record for this player.
                if (Common.getVerboseLogging()) log.debug("In LossHandler: new player")
                queryResult = Common.addNewPlayer(sql, playerName, 0, 0, 1, 0)
                // TODO - queryResult should be true or 1. What if it's not?
                if (Common.getVerboseLogging()) log.debug("In LossHandler: new player query result: '" + queryResult.toString() + "'")
            } else {
                // playerRow is an object of the class groovy.sql.GroovyRowResult
                //  (I know because I had it print it out in debugging), which implements Map.

                def newLossCount = playerRow.get("losses") + 1
                String updateQuery = """\
                    UPDATE PlayerScore
                    SET losses = $newLossCount
                    WHERE name = '$playerName';
                    """.stripIndent()
                queryResult = Common.executeInsertOrUpdate(sql, updateQuery)
                // TODO - queryResult should be true or 1. What if it's not?
                if (Common.getVerboseLogging()) log.debug("In LossHandler: update player query result: '" + queryResult.toString() + "'")
            }
            // TODO - figure out what signal to send the client that we're done
            context.render(playerName + " updated.")

        }  // end parseJson().then

    }
}