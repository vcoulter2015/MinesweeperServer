package com.ids.vcoulter.minesweeper

// import com.google.inject.Inject
import com.ids.ratpack.common.context.IdsContext
import com.ids.ratpack.common.handler.IdsHandler
// import groovy.sql.Sql

import javax.sql.DataSource


class HelloHandler extends IdsHandler{
    // @Inject Sql sql
    /* Using @Inject Sql sql means we don't have to say: @Inject DataSource ds then
        sent a sql object to a new Sql with the datasource. See setup in Ratpack.groovy.
     */

    @Override
    void handle(IdsContext context) {

        context.renderJson(['hello':'world']);
    }
}
