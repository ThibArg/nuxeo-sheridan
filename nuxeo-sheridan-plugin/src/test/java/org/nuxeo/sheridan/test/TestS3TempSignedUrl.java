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
package org.nuxeo.sheridan.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.sheridan.S3TempSignedURLBuilder;

/**
 * Important: To test the feature, we don't want to hard code the AWS keys, since this code could be published on GitHub
 * for example. So, the principles used are the following:
 * <ul>
 * <li>We have a file named aws-test.conf at nuxeo-sheridan-plugin/src/test/resources/</li>
 * <li>The file contains the keys</li>
 * <li>The .gitignore config file ignores this file, so it is not sent on GitHub</li>
 * </ul>
 * Also, of course, make sure the file you test exist in your bucket: Change TEST_FILE_KEY and TEST_FILE_SIZE
 * accordingly
 * 
 * @since 7.10
 */
@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({ "nuxeo-sheridan" })
public class TestS3TempSignedUrl {

    protected static final String TEST_CONF = "aws-test.conf";

    protected static final String TEST_FILE_KEY = "Creative-Brief-Lorem-ipsum.pdf";

    protected static final int TEST_FILE_SIZE = 39119;

    @Before
    public void setup() throws Exception {
        // Get our local aws-test.conf file and load the properties
        File file = FileUtils.getResourceFileFromContext(TEST_CONF);
        FileInputStream fileInput = new FileInputStream(file);
        Properties props = new Properties();
        props.load(fileInput);
        fileInput.close();

        // Check we do have the keys
        String key, secret, bucket;
        key = props.getProperty(S3TempSignedURLBuilder.CONF_KEY_NAME_ACCESS_KEY);
        assertTrue("Missing " + S3TempSignedURLBuilder.CONF_KEY_NAME_ACCESS_KEY, StringUtils.isNotBlank(key));

        secret = props.getProperty(S3TempSignedURLBuilder.CONF_KEY_NAME_SECRET_KEY);
        assertTrue("Missing " + S3TempSignedURLBuilder.CONF_KEY_NAME_SECRET_KEY, StringUtils.isNotBlank(secret));

        bucket = props.getProperty(S3TempSignedURLBuilder.CONF_KEY_NAME_BUCKET);
        assertTrue("Missing " + S3TempSignedURLBuilder.CONF_KEY_NAME_BUCKET, StringUtils.isNotBlank(bucket));

        Properties systemProps = System.getProperties();
        systemProps.setProperty(S3TempSignedURLBuilder.CONF_KEY_NAME_ACCESS_KEY, key);
        systemProps.setProperty(S3TempSignedURLBuilder.CONF_KEY_NAME_SECRET_KEY, secret);
        systemProps.setProperty(S3TempSignedURLBuilder.CONF_KEY_NAME_BUCKET, bucket);

    }

    @Test
    public void testGetTempSignedUrl() throws Exception {

        S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
        String urlStr = builder.build(TEST_FILE_KEY, 0, null, "filename=" + TEST_FILE_KEY);
        assertTrue(StringUtils.isNotBlank(urlStr));
      
        // We must be able to download the file without authentication
        File f = downloadFile(urlStr);
        assertNotNull(f);
        // Delete it now
        String name = f.getName();
        long size = f.length();
        f.delete();

        assertEquals(TEST_FILE_KEY, name);
        assertEquals(TEST_FILE_SIZE, size);

    }

    @Test
    public void testTempSignedUrlShouldFail() throws Exception {

        int duration = 5; // 5 seconds, not 20 minutes or whatever S3TempSignedURLBuilder.DEFAULT_EXPIRE is

        S3TempSignedURLBuilder builder = new S3TempSignedURLBuilder();
        String urlStr = builder.build(TEST_FILE_KEY, duration, null, "filename=" + TEST_FILE_KEY);
        assertTrue(StringUtils.isNotBlank(urlStr));
                
        // Let's wait
        Thread.sleep(duration * 1000);

        // Downloading should fail, so the returned File is null
        File f = downloadFile(urlStr);
        assertNull(f);

    }

    /*
     * The returned file is a temp file. Still, caller should delete it once done dealing with it
     */
    protected File downloadFile(String url) throws IOException {

        File resultFile = null;

        HttpURLConnection http = null;
        int BUFFER_SIZE = 4096;

        URL theURL = new URL(url);

        http = (HttpURLConnection) theURL.openConnection();
        // HTTPUtils.addHeaders(http, headers, headersAsJSON);

        if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = http.getHeaderField("Content-Disposition");

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > -1) {
                    fileName = disposition.substring(index + 9);
                }
            } else {
                // extracts file name from URL
                fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
            }
            if (StringUtils.isEmpty(fileName)) {
                fileName = "DownloadedFile-" + java.util.UUID.randomUUID().toString();
            }

            String tempDir = System.getProperty("java.io.tmpdir");
            resultFile = new File(tempDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(resultFile);
            InputStream inputStream = http.getInputStream();
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        }

        return resultFile;
    }

}
