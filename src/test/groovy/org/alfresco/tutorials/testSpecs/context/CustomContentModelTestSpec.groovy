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
package org.alfresco.tutorials.testSpecs.context

import org.alfresco.model.ContentModel
import org.alfresco.repo.content.MimetypeMap
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator
import org.alfresco.repo.security.authentication.AuthenticationUtil
import org.alfresco.service.ServiceRegistry
import org.alfresco.service.namespace.NamespaceService
import org.alfresco.service.namespace.QName
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Make sure the Custom Content model is present
 *
 * @author martin.bergljung@alfresco.com
 */
@ContextConfiguration(locations = [ "classpath:alfresco/application-context.xml",
                                    "classpath:alfresco/remote-api-context.xml",
                                    "classpath:alfresco/web-scripts-application-context.xml" ] )
@Stepwise
class CustomContentModelTestSpec extends Specification {
    static final String CM_MODEL_NS = "{http://www.alfresco.org/model/content/1.0}";
    static final String ACME_MODEL_NS = "{http://www.acme.org/model/content/1.0}";
    static final String ADMIN_USER_NAME = "admin";

    @Autowired
    ServiceRegistry serviceRegistry;

    def setup() {
        AuthenticationUtil.setFullyAuthenticatedUser(ADMIN_USER_NAME);
    }

    def "Test custom content model presence"() {
        expect:
        Collection<QName> allContentModels = serviceRegistry.dictionaryService.allModels
        allContentModels.contains(createAcmeQName("contentModel"))
    }

    def "Create an ACME Document"() {
        setup:
        def type = createAcmeQName("document")
        def textContent = "Hello World!";
        def nodeProperties = [
                (createAcmeQName("documentId")):"DOC001",
                (createAcmeQName("securityClassification")):"Company Confidential"
        ]
        def aspect = createCmQName("titled")
        def aspectProperties = [
                (createCmQName("title")):"Sample Title",
                (createCmQName("description")):"Sample Description"
        ]

        when:
        def nodeRef = createNode("aSampleFile.txt", type, nodeProperties)
        addFileContent(nodeRef, textContent)
        serviceRegistry.nodeService.addAspect(nodeRef, aspect, aspectProperties)

        then:
        serviceRegistry.nodeService.getType(nodeRef).equals(type)
        serviceRegistry.nodeService.hasAspect(nodeRef, createAcmeQName("securityClassified"))
        serviceRegistry.nodeService.hasAspect(nodeRef, aspect)
        serviceRegistry.nodeService.getProperty(nodeRef, createCmQName("title")).equals("Sample Title")
        readTextContent(nodeRef).equals(textContent)

        cleanup:
        if (nodeRef != null) serviceRegistry.nodeService.deleteNode(nodeRef)
    }

    /**
     * ==================== Helper Methods ============================================================================
     */

    /**
     * Create a new node, such as a file or a folder, with passed in type and properties
     *
     * @param name the name of the file or folder
     * @param type the content model type
     * @param properties the properties from the content model
     * @return the Node Reference for the newly created node
     */
    def createNode(name, type, properties) {
        def parentFolderNodeRef = getCompanyHomeNodeRef();
        def associationType = ContentModel.ASSOC_CONTAINS
        def associationQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                QName.createValidLocalName(name))
        properties[ContentModel.PROP_NAME] = name
        def parentChildAssocRef = serviceRegistry.nodeService.createNode(
                parentFolderNodeRef, associationType, associationQName, type, properties)

        return parentChildAssocRef.childRef
    }

    /**
     * Add some text content to a file node
     *
     * @param nodeRef the node reference for the file that should have some text content added to it
     * @param fileContent the text content
     */
    def addFileContent(nodeRef, fileContent) {
        def updateContentPropertyAutomatically = true
        def writer = serviceRegistry.contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT,
                updateContentPropertyAutomatically)
        writer.mimetype = MimetypeMap.MIMETYPE_TEXT_PLAIN
        writer.encoding = "UTF-8"
        writer.putContent(fileContent)
    }

    /**
     * Read text content for passed in file Node Reference
     *
     * @param nodeRef the node reference for a file containing text
     * @return the text content
     */
    def readTextContent(nodeRef) {
        def reader = serviceRegistry.contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        if (reader == null) {
            return ""; // Maybe it was a folder after all
        }

        InputStream is = reader.getContentInputStream()
        try {
            return IOUtils.toString(is, "UTF-8")
        } catch (IOException ioe) {
            throw new RuntimeException(ioe)
        } finally {
            if (is != null) {
                try {
                    is.close()
                } catch (Throwable e) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Create a QName for the ACME content model
     *
     * @param localname the local content model name without namespace specified
     * @return the full ACME QName including namespace
     */
    def QName createAcmeQName(String localname) {
        return QName.createQName("${ACME_MODEL_NS}${localname}")
    }

    /**
     * Create a QName for the CM content model (out-of-the-box model)
     *
     * @param localname the local content model name without namespace specified
     * @return the full CM QName including namespace
     */
    def QName createCmQName(String localname) {
        return QName.createQName("${CM_MODEL_NS}${localname}")
    }

    /**
     * Get the node reference for the /Company Home top folder in Alfresco.
     * Use the standard node locator service.
     *
     * @return the node reference for /Company Home
     */
    def getCompanyHomeNodeRef() {
        return serviceRegistry.nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null)
    }
}