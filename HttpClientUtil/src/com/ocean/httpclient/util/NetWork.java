package com.ocean.httpclient.util;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.CustomMultiPartEntity;
import org.apache.http.entity.mime.CustomMultiPartEntity.ProgressListener;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;


public class NetWork {

	private static final String TAG = "NetWork";
	private int REQUEST_TIMEOUT = 8000;
	private int SO_TIMEOUT = 8000;
	private String charSet = "UTF-8";
	private HttpClient client;
	private HttpPost post;
	private Header[] headers;
	
	public NetWork(String url){
		Log.d(TAG, "NetWork is started.");
		setHttpClient();
		post = new HttpPost(url);
	}
	
	public String getCharSet(){
		return charSet;
	}
	
	public void setCharSet(String charset){
		this.charSet = charset;
	}
	
	public int getRequestTimeOut(){
		return REQUEST_TIMEOUT;
	}
	
	public int getSoTimeOut(){
		return SO_TIMEOUT;
	}
	
	public void setRequestTimeOut(int requestT){
		this.REQUEST_TIMEOUT = requestT;
	}
	
	public void setSoTimeOut(int soT){
		this.SO_TIMEOUT = soT;
	}
	
	private void setHttpClient(){
		client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, charSet);
	}
	
	/**
	 * If you want to add some headers in your http request,you must use this method before 
	 * {@link #doPost(Map, String, File, boolean, ProgressListener)}doPost.
	 * @param headersSet The Map you save your header's params.
	 */
	public void createCommonHeader(Map<String,	String> headersSet) {
		headers = new Header[headersSet.size()];
		Set<Entry<String,String>> entrySet = headersSet.entrySet();
		int i = 0 ;
    	for (Entry<String, String> entry : entrySet) {
    		headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
    		i += 1;
		}
	}
	
	/**
	 * Use this method to upload some string or files.Only do the post method.You can do get by yourself.
	 * @param fileType The file's type such as a file or a picture.
	 * @param file The file you want to upload to the server.
	 * @param params You want to set some params such as method name or json string.
	 * @param listener Upload progress listener.
	 * @param headers You want to set some headers such as username and password.
	 * @return Null if exception happened or string of response.
	 */
	public String doPost(Map<String,String> params,String fileType,File file,boolean hasHeader,ProgressListener listener){
		try {
			CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(listener);
			
	        if(params!=null){
	        	Set<Entry<String,String>> entrySet = params.entrySet();
	        	for (Entry<String, String> entry : entrySet) {
	        		multipartContent.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
	        }
	        
	        if (fileType != null && file != null ) {
	        	// We use FileBody to transfer an file
	        	multipartContent.addPart(fileType, new FileBody(file));
			}
			
			post.setEntity(multipartContent);
			
			if (hasHeader) {
				if (headers != null) {
					post.setHeaders(headers);
				}
			}
			
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			
			Log.v(TAG,  "statusCode : " + statusCode);
			//Using the same charset to read the response.
			String serverResponse = EntityUtils.toString(response.getEntity(),charSet);
			Log.v(TAG,  "serverResponse : " + serverResponse);
			
			if(statusCode!=200){
				Log.v(TAG,  "serverResponse : " + serverResponse);
	        	return null;
			}
			return serverResponse;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
}