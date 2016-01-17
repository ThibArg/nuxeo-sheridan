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
import java.util.HashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

/**
 * 
 * @since 7.10
 */
@Name("sheridanHelper")
@Scope(ScopeType.EVENT)
public class SheridanHelperBean implements Serializable {

    private static final long serialVersionUID = -8550491926836699499L;
    
    public String getS3TempSignedUrl(String inKey) throws IOException {
        
        String url = "";
        
        S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
        url = builder.build(inKey, 0, null, null);
        
        return url;
    }
    
    public String getS3TempSignedUrl(String inBucket, String inKey) throws IOException {
        
        String url = "";
        
        S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
        url = builder.build(inBucket, inKey, 0, null, null);
        
        return url;
    }

}
