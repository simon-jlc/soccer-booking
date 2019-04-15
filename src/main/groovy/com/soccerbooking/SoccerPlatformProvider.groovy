package com.soccerbooking

import org.apache.http.HttpEntity
import org.apache.http.client.CookieStore
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.message.BasicNameValuePair

trait SoccerPlatformProvider {

    /**
     * Authentication query builder, using CLI credentials.
     * @return
     */
    abstract HttpPost createAuthenticationQuery()

    /**
     * Availability check query builder, according your CLI parameters.
     * @return
     */
    abstract List<HttpPost> createAvailabilityCheckQueries()

    /**
     * Build cookie store. Used to store session authenticated token.
     * @return
     */
    abstract CookieStore getOrBuildCookieStore()

    /**
     * Authenticating response parser.
     * @param httpResponse
     */
    abstract void authenticate(final CloseableHttpResponse httpResponse)

    /**
     * Availability checking response parse.
     * @param httpResponse
     */
    abstract void checkAvailability(final CloseableHttpResponse httpResponse)

    /**
     * MIME Form parameters builder from params's map, and use boundary as param separator.
     * @param params
     * @param boundary
     * @return
     */
    HttpEntity createMimeFormEntity(final Map<String, String> params, final String boundary) {
        def builder = MultipartEntityBuilder.create()
        params.each { k, v -> builder.addTextBody(k, v) }
        return builder.setBoundary(boundary).build()
    }
}
