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
 * Bean helper for the Sheridan application
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
     * An empty key just returns an empty URL in this context (no error)
     * 
     * @param inKey
     * @return
     * @throws IOException
     * @since 7.10
     */
    public String getS3TempSignedUrl(String inKey) throws IOException {

        String url = "";

        if (StringUtils.isNotBlank(inKey)) {
            S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
            url = builder.build(inKey, 0, null, null);
        }

        return url;
    }

    /**
     * Return an S3 Temp Signed Url for this key in this bucket, using the key id and secret key set in the configuration.
     * <p>
     * An empty key just returns an empty URL in this context (no error)
     * <p>
     * if the bucket is empty, the code uses the bucket set in the configuraiton, if any.
     * 
     * @param inKey
     * @return
     * @throws IOException
     * @since 7.10
     */
    public String getS3TempSignedUrl(String inBucket, String inKey) throws IOException {

        String url = "";

        if (StringUtils.isNotBlank(inKey)) {
            S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
            url = builder.build(inBucket, inKey, 0, null, null);
        }

        return url;
    }

}
