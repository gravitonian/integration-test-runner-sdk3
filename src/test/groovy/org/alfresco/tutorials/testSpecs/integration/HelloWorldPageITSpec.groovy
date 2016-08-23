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

import geb.spock.GebReportingSpec
import org.alfresco.tutorials.pages.AdminHomePage
import org.alfresco.tutorials.pages.LoginPage
import org.alfresco.tutorials.pages.HelloWorldPage
import spock.lang.Stepwise

/**
 * Test Hello World Sample Aikau Page
 * <p/>
 * We extend our test specification from GebReportingSpec. You can also extend from GebSpec but GebReportingSpec will
 * automatically create a screenshot if your test fails, and scrape HTML, which is more convenient.
 * <p/>
 * Please note that you need Firefox to be installed if you want to run the tests.
 */
// Assume a workflow.
// Make sure we run each test method in the order they are written in the class and re-use browser context for each test.
// If one method fails the test will stop.
@Stepwise
class HelloWorldPageITSpec extends GebReportingSpec {
    def "Test valid login"() {
        given: "I navigate to the login page"
        def currentPage = to LoginPage

        when: "I enter a valid Administrator username and password and click login"
        currentPage.login("admin", "admin")

        then: "I'm redirected to the Admin User Dashboard page"
        at AdminHomePage
    }

    def "Test navigating to the Hello World Sample Page"() {
        given: "I'm at the Admin User Dashboard Page"
        def currentPage = at AdminHomePage

        when: "And I navigate to the Hello World Page"
        currentPage = to HelloWorldPage

        then: "Then the page has Hello title and Hello sample widget"
        currentPage.headerTitle == "This is a sample HELLO page"
        currentPage.simpleLogoDivExists
        currentPage.simpleMessageText == "Hello from i18n"
    }
}

