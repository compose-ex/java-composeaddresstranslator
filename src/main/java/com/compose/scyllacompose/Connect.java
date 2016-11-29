// Copyright (c) 2016 Compose, an IBM company
//
// MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.compose.scyllacompose;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 *
 * @author dj@compose.com
 */
public class Connect {

    public static void main(String args[]) {
        Cluster cluster = null;

        try {
          // This value should be a string based on the JSON address map values
          // in the Compose UI - Below is an example.
            String mapstring = "{\n"
                    + "	\"10.0.24.69:9042\": \"sl-eu-lon-2-portal.3.dblayer.com:15227\",\n"
                    + "	\"10.0.24.71:9042\": \"sl-eu-lon-2-portal.2.dblayer.com:15229\",\n"
                    + "	\"10.0.24.70:9042\": \"sl-eu-lon-2-portal.1.dblayer.com:15228\"\n"
                    + "}";

            ComposeAddressTranslator translator = new ComposeAddressTranslator();
            translator.setMap(mapstring);

            cluster = Cluster.builder()
                    .addContactPointsWithPorts(translator.getContactPoints())
                    .withCredentials("scylla", "PASSWORD")
                    .withAddressTranslator(translator)
                    .build();
            Session session = cluster.connect();

            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            System.out.println(row.getString("release_version"));
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }

    }
}
