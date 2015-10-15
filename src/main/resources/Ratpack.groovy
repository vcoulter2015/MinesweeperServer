import com.google.inject.Provides
import com.ids.ratpack.common.IdsRatpack
import com.zaxxer.hikari.HikariConfig
import groovy.sql.Sql
import com.ids.vcoulter.minesweeper.*
import ratpack.func.Action
import ratpack.hikari.HikariModule

import javax.sql.DataSource

// Call the builder function.
IdsRatpack.builder {
    bindings {
        module(new SqlModule(), { HikariConfig hc ->
            hc.dataSourceClassName = 'com.mysql.jdbc.jdbc2.optional.MysqlDataSource'
            hc.addDataSourceProperty('user', 'root')
            hc.addDataSourceProperty('databaseName', 'minesweeper')
            hc.addDataSourceProperty('serverName', 'localhost')
        } as Action)

        bind HelloHandler

        bind WinHandler
        bind LossHandler
        bind DrawHandler
        bind GetScoreHandler

    }

    handlers {
        all {
            def hdrs = response.headers
            // Point of the following: Allow access from other ports & servers
            // aka CORS = cross-origin resource sharing.
            hdrs.add("Access-Control-Allow-Origin", "*")
            hdrs.add("Access-Control-Allow-Methods", "POST,PUT,DELETE,GET,OPTIONS")
            hdrs.add("Access-Control-Allow-Headers", request.headers.get("Access-Control-Request-Headers") ?: "");
            next()
        }

        // BTW, binding get & post to the same handler does not work
        //  (if one of the HelloHandler lines is uncommented, it'll work, but
        //  having both uncommented doesn't work.)
        //get 'hello', HelloHandler

        post 'hello', HelloHandler
        post 'win', WinHandler
        post 'loss', LossHandler
        post 'draw', DrawHandler
        post 'getscore', GetScoreHandler
    }
}

class SqlModule extends HikariModule {
    // When someone asks for an Sql, give them one of these.

    @Provides
    Sql getSql(DataSource ds){ new Sql(ds) }
}
