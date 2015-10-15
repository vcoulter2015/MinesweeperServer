package com.ids.vcoulter.minesweeper

import com.google.inject.Inject
import com.ids.ratpack.common.context.IdsContext
import com.ids.ratpack.common.handler.IdsHandler
import groovy.sql.Sql

/**
 * Created by vcoulter in Oct 2015.
 * Example input: { "name": "John" } and then John's record is looked up and returned.
 *  If the given name has no record, JSON with the name & all 0's is returned.
 */
class GetScoreHandler extends IdsHandler{
    @Inject Sql sql

    @Override
    void handle(IdsContext context) {

        // First get the name.
        /* Pass "Map" to parseJson because we are getting a key-value pair.
         * If there's a parsing failure, client will get a 400. */
        context.parseJson(Map).then {

            // Get the player name. (Client will get a 400 if the player name is "".)
            String playerName = Common.getNameFromMap(it)

            def playerRow = Common.getPlayerRecord(sql, playerName)

            if (!playerRow) {
                // Create an empty map of 0 scores.
                def emptyScores = [ wins:0, losses:0, draws:0, score:0 ]
                // add the name
                emptyScores.put("name", playerName)

                context.renderJson(emptyScores)

            } else {  // player by that name has a record
                context.renderJson(playerRow)
            }

        } // end parseJson().then
    }
}