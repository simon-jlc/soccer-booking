package com.soccerbooking

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

        def soccerService = new UrbanSoccerProvider(options: cliArgs)
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
        def httpCheckQuery = soccerService.createAvailabilityCheckQuery()
        def httpCheckResponse = client.execute(httpCheckQuery)
        soccerService.checkAvailability(httpCheckResponse)
        client.close()
    }

    def parse(def args) {
        CliBuilder cli = new CliBuilder(usage: 'soccer booking helper')
        cli.with {
            h longOpt: 'help', 'Show usage information'
            u longOpt: 'user', args: 1, 'Platform username'
            p longOpt: 'password', args: 1, 'Platform user\'s password'
            d longOpt: 'duration', args: 1, 'Values are [ 60, 90, 120 ]'
            c longOpt: 'center', args: 1, 'Values are [ PUTEAUX, ASNIERES, LADEFENSE ]'
            at longOpt: 'atDate', args: 1, 'Expected format date and time like < dd/MM/yyyy HH:mm>'
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
}
