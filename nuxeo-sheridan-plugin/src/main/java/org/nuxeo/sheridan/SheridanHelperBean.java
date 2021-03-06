/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Thibaud Arguillere
 */
package org.nuxeo.sheridan;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Bean helper for the Sheridan application, containing misc. functions to be called from a .xml file, typically a
 * widget template.
 * <p>
 * Here is an example, where the objectKey is stored in a document, in the "myschema:S3key" field:
 * <p>
 * 
 * <pre>
 * {@code
 * <div xmlns:c="http://java.sun.com/jstl/core" xmlns:nxl="http://nuxeo.org/nxforms/layout" xmlns:h="http://java.sun.com/jsf/html" xmlns:nxh="http://nuxeo.org/nxweb/html">
 *     <c:if test="#{widget.mode != 'edit'}">
 *         <h:outputLink value="#{sheridanHelper.getS3TempSignedUrl(field)}" target="_blank">#{widget.label}</h:outputLink>
 *     </c:if>  
 *     <c:if test="#{widget.mode == 'edit'}">
 *         <h:inputText value="#{field}" />
 *     </c:if> 
 * </div>
 * }
 * </pre>
 * 
 * @since 7.10
 */
@Name("sheridanHelper")
@Scope(ScopeType.EVENT)
public class SheridanHelperBean implements Serializable {

    private static final long serialVersionUID = -8550491926836699499L;

    /**
     * Return an S3 Temp Signed Url for the key, using the key id, secret key and bucket set in the configuration.
     * <p>
     * An empty key just returns an empty URL, not an error, so the bean can be used with values of fields of a document
     * when the field has not yet been set.
     * 
     * @param inKey
     * @return
     * @throws IOException
     * @since 7.10
     */
    public String getS3TempSignedUrl(String objectKey) throws IOException {

        String url = "";

        if (StringUtils.isNotBlank(objectKey)) {
            S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
            url = builder.build(objectKey, 0, null, null);
        }

        return url;
    }

    /**
     * Return an S3 Temp Signed Url for this key in this bucket, using the key id and secret key set in the
     * configuration.
     * <p>
     * An empty key just returns an empty URL, not an error, so the bean can be used with values of fields of a document
     * when the field has not yet been set.
     * <p>
     * if the bucket is empty, the code uses the bucket set in the configuration.
     * 
     * @param inKey
     * @return
     * @throws IOException
     * @since 7.10
     */
    public String getS3TempSignedUrl(String bucket, String objectKey) throws IOException {

        String url = "";

        if (StringUtils.isNotBlank(objectKey)) {
            S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
            url = builder.build(bucket, objectKey, 0, null, null);
        }

        return url;
    }

    /**
     * The full call. Return an S3 Temp Signed Url for this key, in this bucket, expiring in he given time, and setting
     * content-type and content-disposition (using the key id, secret key and bucket set in the configuration.)
     * <p>
     * Default values can apply, see {@link S3TempSignedURLBuilder} (default expiration, default bucket, ...)
     * <p>
     * An empty key just returns an empty URL, not an error, so the bean can be used with values of fields of a document
     * when the field has not yet been set.
     * <p>
     * if the bucket is empty, the code uses the bucket set in the configuration.
     * 
     * @param inKey
     * @return
     * @throws IOException
     * @since 7.10
     */
    public String getS3TempSignedUrl(String bucket, String objectKey, int expireInSeconds, String contentType,
            String contentDisposition) throws IOException {

        String url = "";

        if (StringUtils.isNotBlank(objectKey)) {
            S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
            url = builder.build(objectKey, objectKey, expireInSeconds, contentDisposition, null);
        }

        return url;
    }

}
