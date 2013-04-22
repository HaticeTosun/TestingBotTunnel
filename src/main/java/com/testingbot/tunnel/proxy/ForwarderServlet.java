package com.testingbot.tunnel.proxy;


import com.testingbot.tunnel.App;
import org.eclipse.jetty.servlets.ProxyServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpURI;

public class ForwarderServlet extends ProxyServlet {
    private App app;
    
    public ForwarderServlet(App app) {
        this.app = app;
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    
    @Override
    protected HttpURI proxyHttpURI(String scheme, String serverName, int serverPort, String uri) throws MalformedURLException {
        if (!validateDestination(serverName,uri))
            return null;

        return new HttpURI(scheme + "://127.0.0.1:4446" + uri);
    }
    
    @Override
    protected void customizeExchange(HttpExchange exchange, HttpServletRequest request) {
        exchange.addRequestHeader("TB-Tunnel", this.app.getServerIP());
        exchange.addRequestHeader("TB-Credentials", this.app.getClientKey() + "_" + this.app.getClientSecret());
        
        for (String key : app.getCustomHeaders().keySet()) {
            exchange.addRequestHeader(key, app.getCustomHeaders().get(key));
        }
        
        Logger.getLogger(ForwarderServlet.class.getName()).log(Level.INFO, " >> [{0}] {1}", new Object[]{request.getMethod(), request.getRequestURL()});
    }
}
