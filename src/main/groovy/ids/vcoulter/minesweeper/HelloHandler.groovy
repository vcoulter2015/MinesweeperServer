package ids.vcoulter.minesweeper

import com.google.inject.Inject
import com.ids.ratpack.common.context.IdsContext
import com.ids.ratpack.common.handler.IdsHandler
import groovy.sql.Sql

import javax.sql.DataSource


class HelloHandler extends IdsHandler{
    @Inject Sql sql

    @Override
    void handle(IdsContext context) {
        def result = sql.firstRow("SELECT 1");
        println result
        context.renderJson(['hello':'world']);
    }
}
