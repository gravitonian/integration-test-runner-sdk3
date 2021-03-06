/**
 * Copyright (C) 2015 Alfresco Software Limited.
 *
 * This file is part of the Alfresco SDK Samples project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.tutorials.testSpecs.integration

import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Tests the Hello World Platform Web Scripts
 */
// Assume a workflow.
// Make sure we run each test method in the order they are written in the class and re-use authentication context for each test.
// If one method fails the test will stop.
@Stepwise
class RepoWebScriptITSpec extends Specification {
    def userName = "admin"
    def password = "admin"

    @Shared RESTClient restClient = new RESTClient("http://localhost:8080/alfresco/service/")

    def "Call Hello World Web Script and verify response"() {
        given: "Successful authentication"
        restClient.auth.basic userName, password

        when: "We invoke the Hello World Web Script"
        def response = restClient.get( path: 'sample/helloworld') // Important! No leading slash

        then: "HTTP response status should be 200 and response format should be in HTML"
        response.status == 200
        response.contentType == "text/html"

        and: "Returned HTML page should contain Hello from JS Controller and Java Controller"
        response.data.text().contains("Hello from JS")
        response.data.text().contains("HelloFromJava")
    }
}