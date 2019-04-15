package com.soccerbooking.urban

import com.soccerbooking.CenterFilter
import com.soccerbooking.SoccerPlatformProvider
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.http.HttpHeaders
import org.apache.http.auth.AuthenticationException
import org.apache.http.client.CookieStore
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.cookie.BasicClientCookie2

import java.time.ZoneId

@Slf4j
class UrbanSoccerProvider implements SoccerPlatformProvider {

    private static final String BOUNDARY = "---------------------------14757162571074492196800350549"
    private static final String HOST = "my.urbansoccer.fr"
    private static final String API_ENDPOINT = "https://$HOST/api"
    private static final String USER_ENDPOINT = "https://$HOST/user"

    private Set<CenterFilter> filters;
    private OptionAccessor options
    private CookieStore cookieStore

    @Override
    CookieStore getOrBuildCookieStore() {
        if (!cookieStore) {
            def cookies = [
                    "_ga" : "GA1.2.182836894.1555169558",
                    "_gid": "GA1.2.1503524379.1555169558",
            ].collect { k, v ->
                def cookie = new BasicClientCookie2(k, v)
                cookie.setPath("/")
                cookie.setDomain(HOST)
                cookie
            } as Cookie[]

            cookieStore = new BasicCookieStore()
            cookieStore.addCookies(cookies)
        }
        cookieStore
    }

    @Override
    HttpPost createAuthenticationQuery() {
        def username = options.'user' as String
        def password = options.'password' as String

        log.info "Creating authentication query [ user = $username / password = ******** ] to $API_ENDPOINT"

        def query = new HttpPost(API_ENDPOINT)
        query.setHeader("apiresource", "check/profil/auth")
        query.setHeader("apiverb", "GET")
        query.setHeader("auth-login", username)
        query.setHeader("auth-password", password)
        commonHeaders().each { k, v -> query.setHeader(k, v) }
        return query
    }

    @Override
    void authenticate(final CloseableHttpResponse httpResponse) {

        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            def content = new JsonSlurper().parse(httpResponse.getEntity().getContent())
            throw new AuthenticationException("Authentication failed. Message: ${content} (${httpResponse})")
        }

        def store = getOrBuildCookieStore()
        ["auth-token", "auth-userid"].each {
            def header = httpResponse.getHeaders(it)[0]
            def cookie = new BasicClientCookie2(header.getName(), header.getValue())
            cookie.setPath("/")
            cookie.setDomain(HOST)
            store.addCookie(cookie)
        }
        httpResponse.close()
    }

    @Override
    List<HttpPost> createAvailabilityCheckQueries() {

        def duration = options.'duration'
        def dateTime = Date.parse("dd/MM/yyyy HH:mm", options.'onDate')
        def localDateTime = dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        def centers = Centers.parseOptions(options)
        return centers.collect {

            def center = it
            def fieldType = center.fieldTypes()[0]

            log.info "Creating checking availability query [ center = $center ($fieldType) / dateTime = $localDateTime / duration (min.) = $duration ] to $API_ENDPOINT"

            def params = [
                    "centerId"        : "${center.id}",
                    "duration"        : "$duration",
                    "fieldType"       : "${fieldType.id}",
                    "fieldTypeDisplay": "${fieldType.desc}",
                    "hour"            : "${localDateTime.hour}",
                    "minute"          : "${localDateTime.minute}",
                    "day"             : "${localDateTime.dayOfMonth}",
                    "month"           : "${localDateTime.monthValue}",
                    "year"            : "${localDateTime.year}"
            ]

            // query BY MIME content
            def mimeEntity = createMimeFormEntity(params, BOUNDARY)

            // create HTTP POST
            def query = new HttpPost(API_ENDPOINT)
            query.setHeader("apiresource", "read/reservation/availabilities")
            query.setHeader("apiverb", "POST")
            commonHeaders().each { k, v -> query.setHeader(k, v) }
            query.setEntity(mimeEntity)
            query
        }
    }

    @Override
    void checkAvailability(final CloseableHttpResponse httpResponse) {

        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            throw new IllegalStateException("Expected HTTP return code 200. Got ${httpResponse.getStatusLine().getStatusCode()}. Reponse: ${httpResponse}")
        }

        // parse JSON
        def json = new JsonSlurper().parse(httpResponse.getEntity().getContent())
        def data = json.'data' as Collection
        def appStatusCode = data[0]
        def centers = data[1] as Map
        if (appStatusCode != 200) {
            throw new IllegalStateException("Expected API response code 200. Got ${appStatusCode}. ${json}")
        }

        // interpret JSON, with exact match center
        def exactCenter = centers.'exact' as Collection
        if (exactCenter) {
            def centerAttr = exactCenter[0] as Map
            def center = Center.of("exactMatch", centerAttr)
            log.info "Find exact match: $center"
        }

        // interpret JSON, dive into map struct collecting other centers
        def centerss = ["otherCenter", "otherDay", "otherDuration", "otherStart"].collect {
            def label = it
            def otherCenters = centers[it] as Collection
            if (otherCenters) {
                otherCenters.collect { Center.of(label, it) }
            }
        }.flatten().findAll { it != null }

        // finally apply filters
        def filteredCenters = centerss.collect { center ->
            if (filters) {
                // all filters should match, in case of any filter exist
                if (filters.every { it.apply(center) }) return center
            } else {
                return center
            }
        }.findAll { it != null } // make sure null are removed

        filteredCenters.each { log.info("$it") }
    }

    def commonHeaders() {
        [
                (HttpHeaders.ACCEPT)         : "application/json, text/plain, */*",
                (HttpHeaders.ACCEPT_ENCODING): "gzip, deflate, br",
                (HttpHeaders.ACCEPT_LANGUAGE): "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3",
                (HttpHeaders.CONNECTION)     : "keep-alive",
                (HttpHeaders.CONTENT_TYPE)   : "multipart/form-data; boundary=$BOUNDARY",
                (HttpHeaders.HOST)           : HOST,
                (HttpHeaders.REFERER)        : USER_ENDPOINT,
                (HttpHeaders.USER_AGENT)     : "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:66.0) Gecko/20100101 Firefox/66.0"
        ]
    }
}
