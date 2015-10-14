import com.google.inject.Provides
import com.ids.ratpack.common.IdsRatpack
import com.zaxxer.hikari.HikariConfig
import groovy.sql.Sql
import ids.vcoulter.minesweeper.HelloHandler
import ratpack.func.Action
import ratpack.hikari.HikariModule

import javax.sql.DataSource

IdsRatpack.builder {
    bindings {
        module(new SqlModule(), { HikariConfig hc ->
            hc.dataSourceClassName = 'com.mysql.jdbc.jdbc2.optional.MysqlDataSource'
            hc.addDataSourceProperty('user', 'root')
            hc.addDataSourceProperty('databaseName', 'minesweeper')
            hc.addDataSourceProperty('serverName', 'localhost')
        } as Action)

        bind(HelloHandler)

    }

    handlers {
        all {
            def hdrs = response.headers
            hdrs.add("Access-Control-Allow-Origin", "*")
            hdrs.add("Access-Control-Allow-Methods", "POST,PUT,DELETE,GET,OPTIONS")
            hdrs.add("Access-Control-Allow-Headers", request.headers.get("Access-Control-Request-Headers") ?: "");
            next()
        }
        get 'hello', HelloHandler
    }
}

class SqlModule extends HikariModule {
    @Provides
    Sql getSql(DataSource ds){ new Sql(ds) }
}
