package com.soccerbooking

import com.soccerbooking.filters.DayOfWeekFilter
import com.soccerbooking.filters.RangeHourFilter
import com.soccerbooking.urban.UrbanSoccerProvider
import groovy.util.logging.Slf4j
import org.apache.http.impl.client.HttpClientBuilder

@Slf4j
class Application {

    static void main(String[] args) {
        log.info("Soccer booking helper is starting..")
        def app = new Application()
        def cliArgs = app.parse(args)
        if (!cliArgs) {
            return
        }

        // optional args
        def filters = app.filters(cliArgs)
        def soccerService = new UrbanSoccerProvider(options: cliArgs, filters: filters)
        def cookies = soccerService.getOrBuildCookieStore()

        // build HTTPClient
        def client = HttpClientBuilder.create()
                .setDefaultCookieSpecRegistry()
                .setDefaultCookieStore(cookies)
                .build()

        // authentication
        def httpAuthQuery = soccerService.createAuthenticationQuery()
        def httpAuthResponse = client.execute(httpAuthQuery)
        soccerService.authenticate(httpAuthResponse)

        // check availability
        def httpCheckQueies = soccerService.createAvailabilityCheckQueries()
        httpCheckQueies.each {
            def httpCheckResponse = client.execute(it)
            soccerService.checkAvailability(httpCheckResponse)
        }

        client.close()
    }

    def parse(def args) {
        CliBuilder cli = new CliBuilder(usage: 'soccer booking helper')
        cli.with {
            h longOpt: 'help', 'Show usage information'
            u longOpt: 'user', required: true, args: 1, 'Platform username'
            p longOpt: 'password', required: true, args: 1, 'Platform user\'s password'
            d longOpt: 'duration', required: true, args: 1, 'Values are [ 60, 90, 120 ]'
            at longOpt: 'atCenter', required: true, args: 1, 'Values are [ PUTEAUX, ASNIERES, LADEFENSE ]'
            on longOpt: 'onDate', required: true, args: 1, 'Expected format date and time like < dd/MM/yyyy HH:mm>'
            fd longOpt: 'filterDayOfWeek', args: 1, 'Keep only suggested alternative on specified days. Values [ MONDAY, TUESDAY.., SUNDAY ]'
            fh longOpt: 'filterRangeHour', args:1, 'Keep only suggested alternative on specified range hours. Expected format < HH:mm-HH:mm >'
            urban longOpt: 'urban', 'Check UrbanSoccer platform'
            five longOpt: 'lefive', 'Check LeFive platform'
        }

        def options = cli.parse(args)
        if (!options) {
            return
        }

        if (options.h) {
            cli.usage()
            return
        }

        return options
    }

    Set<CenterFilter> filters(final OptionAccessor options) {
        def filters = [] as Set
        if (options.'filterDayOfWeek') filters << new DayOfWeekFilter(options)
        if (options.'filterRangeHour') filters << new RangeHourFilter(options)
        filters
    }
}
