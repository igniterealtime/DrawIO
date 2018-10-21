/*
 * Copyright (C) 2018 Ignite Realtime. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ifsoft.draw.openfire;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.http.HttpBindManager;
import org.jivesoftware.openfire.XMPPServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jivesoftware.util.*;

import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.webapp.WebAppContext;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;


public class PluginImpl implements Plugin, PropertyEventListener
{
    private static final Logger Log = LoggerFactory.getLogger(PluginImpl.class);
    private static final String ourHostname = XMPPServer.getInstance().getServerInfo().getHostname();

    private PluginImpl plugin;
    private WebAppContext context6;

    public void destroyPlugin()
    {
        Log.info("Destroy Draw Webservice");

        HttpBindManager.getInstance().removeJettyHandler(context6);
        PropertyEventDispatcher.removeListener(this);
    }

    public void initializePlugin(final PluginManager manager, final File pluginDirectory)
    {
        plugin = this;

        boolean drawEnabled = JiveGlobals.getBooleanProperty("draw.enabled", false);

        if (drawEnabled)
        {
            PropertyEventDispatcher.addListener(this);

            try {
                Log.info("Initialize Draw WebService ");

                context6 = new WebAppContext(null, pluginDirectory.getPath() + "/classes/draw", "/drawio");
                //context6.setClassLoader(this.getClass().getClassLoader());

                final List<ContainerInitializer> initializers6 = new ArrayList<>();
                initializers6.add(new ContainerInitializer(new JettyJasperInitializer(), null));
                context6.setAttribute("org.eclipse.jetty.containerInitializers", initializers6);
                context6.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
                //context6.setWelcomeFiles(new String[]{"index.jsp"});
                HttpBindManager.getInstance().addJettyHandler(context6);

            } catch (Exception e) {
                Log.error("draw disabled", e);
            }

        } else {
            Log.info("draw disabled");
        }
    }

    private String getIpAddress()
    {
        String ourIpAddress = "127.0.0.1";

        try {
            ourIpAddress = InetAddress.getByName(ourHostname).getHostAddress();
        } catch (Exception e) {

        }

        return ourIpAddress;
    }

    //-------------------------------------------------------
    //
    //  PropertyEventListener
    //
    //-------------------------------------------------------


    public void propertySet(String property, Map params)
    {

    }

    public void propertyDeleted(String property, Map<String, Object> params)
    {

    }

    public void xmlPropertySet(String property, Map<String, Object> params) {

    }

    public void xmlPropertyDeleted(String property, Map<String, Object> params) {

    }

}

