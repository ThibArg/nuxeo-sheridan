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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

/**
 * @since 7.10
 */
public class S3TempSignedURLBuilder {

    public static final String CONF_KEY_NAME_ACCESS_KEY = "sheridan.s3.key";

    public static final String CONF_KEY_NAME_SECRET_KEY = "sheridan.s3.secret";

    public static final String CONF_KEY_NAME_BUCKET = "sheridan.s3.bucket";

    public static final int DEFAULT_EXPIRE = 60 * 20; // 20mn

    private static AWSCredentialsProvider awsCredentialsProvider = null;

    protected AmazonS3 s3;

    protected static String awsAccessKeyId;

    protected static String awsSecretAccessKey;

    protected static String awsBucket;
    
    /*
    // TEMP FOR QUICK TEST ONLY
    public S3TempSignedURLBuilder(String key, String secret, String bucket) {
        
        awsBucket = bucket;
        awsAccessKeyId = key;
        awsSecretAccessKey = secret;
        
        awsCredentialsProvider = new SimpleAWSCredentialProvider(awsAccessKeyId, awsSecretAccessKey);
    }
    */

    public S3TempSignedURLBuilder() {

        awsBucket = Framework.getProperty(CONF_KEY_NAME_BUCKET);
        /* Having no bucket name in the config is ok if a bucket is passed as argument to buld()
        if (StringUtils.isNotBlank(awsAccessKeyId)) {
            throw new NuxeoException("AWS bucket (key " + CONF_KEY_NAME_BUCKET + ") not defined in the configuration");
        }
        */

        buildCredentiaProvider();
        if (awsCredentialsProvider == null) {
            throw new NuxeoException("AWS Access Key ID (" + CONF_KEY_NAME_ACCESS_KEY + ") and/or Secret Access Key ("
                    + CONF_KEY_NAME_SECRET_KEY
                    + ") are missing or invalid. Are they correctly set-up in the configuration?");

        }

        s3 = new AmazonS3Client(awsCredentialsProvider);
    }
    
    public String build(String bucket, String objectKey, int expireInSeconds, String contentType, String contentDisposition) throws IOException {
        
        if(StringUtils.isBlank(bucket)) {
            bucket = awsBucket;
        }
        if(StringUtils.isBlank(bucket)) {
            throw new NuxeoException("No bucket provided, and configuration key " + CONF_KEY_NAME_BUCKET + " is missing.");
        }
        
        Date expiration = new Date();
        if(expireInSeconds < 1) {
            expireInSeconds = DEFAULT_EXPIRE;
        }
        expiration.setTime(expiration.getTime() + (expireInSeconds * 1000));

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectKey, HttpMethod.GET);
        
        // Do we need these?
        if(StringUtils.isNotBlank(contentType)) {
            request.addRequestParameter("response-content-type", contentType);
        }
        if(StringUtils.isNotBlank(contentDisposition)) {
            request.addRequestParameter("response-content-disposition", contentDisposition);
        }
        
        request.setExpiration(expiration);
        URL url = s3.generatePresignedUrl(request);
        
        try {
            URI uri = url.toURI();
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        
    }

    public String build(String objectKey, int expireInSeconds, String contentType, String contentDisposition) throws IOException {
        
        return build(awsBucket, objectKey, expireInSeconds, contentType, contentDisposition);

    }

    protected void buildCredentiaProvider() {

        if (awsCredentialsProvider != null) {
            return;
        }

        awsAccessKeyId = Framework.getProperty(CONF_KEY_NAME_ACCESS_KEY);
        awsSecretAccessKey = Framework.getProperty(CONF_KEY_NAME_SECRET_KEY);

        if (StringUtils.isNotBlank(awsAccessKeyId) && StringUtils.isNotBlank(awsSecretAccessKey)) {
            awsCredentialsProvider = new SimpleAWSCredentialProvider(awsAccessKeyId, awsSecretAccessKey);
        }
    }

}
