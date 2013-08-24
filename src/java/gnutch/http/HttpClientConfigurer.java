package gnutch.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;


class HttpClientConfigurer implements org.apache.camel.component.http.HttpClientConfigurer{

    private String userAgent;

    public HttpClientConfigurer(String userAgent){
        this.userAgent = userAgent;
    }

    @Override
    public void configureHttpClient(HttpClient client){
        client.getParams().setParameter(HttpClientParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        if(userAgent != null)
            client.getParams().setParameter(HttpClientParams.USER_AGENT, userAgent);
    }
}
