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
import com.datastax.driver.core.policies.AddressTranslator;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.*;

/**
 *
 * @author dj@compose.com
 */
class ComposeAddressTranslator implements AddressTranslator {

    public Map<InetSocketAddress, InetSocketAddress> addressMap = new HashMap<>();

    @Override
    public void init(Cluster cluster) {
    }

    public void setMap(String addressMapString) {
        JSONObject jsonmap;

        if (addressMapString.charAt(0) == '[') {
            JSONArray jsonarray = new JSONArray(addressMapString);
            System.out.println(jsonarray.toString());
            Iterator jai = jsonarray.iterator();

            while (jai.hasNext()) {
                JSONObject element = (JSONObject) jai.next();
                Iterator subpart = element.keys();
                String internal = (String) subpart.next();
                String external = element.getString(internal);
                addAddresses(internal, external);
            }
        } else {
            jsonmap = new JSONObject(addressMapString);
            Iterator keys = jsonmap.keys();
            while (keys.hasNext()) {
                String internal = (String) keys.next();
                String external = (String) jsonmap.getString(internal);
                addAddresses(internal, external);
            }
        }
    }

    public void addAddresses(String internal, String external) {
        String[] internalhostport = internal.split(":");
        String[] externalhostport = external.split(":");
        InetSocketAddress internaladdress = new InetSocketAddress(internalhostport[0], Integer.parseInt(internalhostport[1]));
        InetSocketAddress externaladdress = new InetSocketAddress(externalhostport[0], Integer.parseInt(externalhostport[1]));
        addressMap.put(internaladdress, externaladdress);
    }

    public Collection<InetSocketAddress> getContactPoints() {
        return addressMap.values();
    }

    @Override
    public InetSocketAddress translate(final InetSocketAddress inetSocketAddress) {
        return addressMap.getOrDefault(inetSocketAddress, inetSocketAddress);
    }

    @Override
    public void close() {
    }
}
