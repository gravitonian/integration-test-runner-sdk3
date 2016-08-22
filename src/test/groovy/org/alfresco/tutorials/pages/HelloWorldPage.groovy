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
package org.alfresco.tutorials.pages

import geb.Page

/**
 * Alfresco Share Sample Hello World Page Object
 *
 * @author marting.bergljung@alfresco.com
 */
class HelloWorldPage extends Page {
    // The relative URL of the page;
    // used by the to() method to determine which URL to send the HTTP request to.
    static url = "page/hdp/ws/simple-page"

    // A closure that indicates whether the current page is this one or not -
    // called by the at() method; it should return a boolean, but you can also include assertions.
    static at = { title == "Alfresco » This is a sample HELLO page" }

    // A description of the page content, allowing for easy access to the parts declared here.
    // If it is not working for some content items then have a look at the Geb reports directory,
    // which contains the HTML that Geb is seeing... (i.e.
    // integration-test-runner/target/geb-reports/org/alfresco/tutorials/testSpecs/HelloWorldPageTestSpec)
    // A description of the page content, allowing for easy access to the parts declared here.
    static content = {
        /*
           <h1 class="alfresco-header-Title" id="HEADER_TITLE" widgetid="HEADER_TITLE">
                <span class="alfresco-header-Title__text has-max-width" data-dojo-attach-point="textNode"
                    data-dojo-attach-event="ondijitclick:onClick" title="null" style="max-width: 500px;">
                    This is a sample HELLO page
                </span>
            </h1>

             Wait for the header to appear...
             */
        headerTitle(wait: true) { $("h1#HEADER_TITLE span").text() }


        /*
            <div class="alfresco-html-Image alfresco-logo-Logo alfresco-core-PublishOrLinkMixin alfresco-html-Image--block"
                id="DEMO_SIMPLE_LOGO" widgetid="DEMO_SIMPLE_LOGO">
                <img class="alfresco-html-Image__img alfresco-logo-only" data-dojo-attach-point="imageNode"
                src="/share/res/js/aikau/1.0.82/alfresco/css/images/blank.gif" style="" alt="Logo image"></div>
         */
        simpleLogoDivExists { $("div#DEMO_SIMPLE_LOGO") != null }

        /*
            <div class="my-template-widget" id="DEMO_SIMPLE_MSG" widgetid="DEMO_SIMPLE_MSG">Hello from i18n</div>
         */
        simpleMessageText { $("div#DEMO_SIMPLE_MSG").text() }
    }
}
