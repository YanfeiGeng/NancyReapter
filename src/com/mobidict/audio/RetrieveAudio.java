package com.mobidict.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.mobidict.util.MBConstants;

public class RetrieveAudio {
	
	private List<String> words = null;
	
	public RetrieveAudio(){
		super();
		try {
			this.loadAllWords();
		} catch (Throwable e) {
			e.printStackTrace(); //FIXME
		}
	}
	
	private void loadAllWords() throws Throwable{
		BufferedReader bfReader = null;
		FileReader reader = null;
		try{
			reader = new FileReader(new File(MBConstants.AUDIO_FOLDER + MBConstants.DEFINED_WORDS_FILE_NAME));
			bfReader = new BufferedReader(reader);
			words = new ArrayList<String>();
			String oneWord = bfReader.readLine();
			while(null != oneWord){
				words.add(oneWord);
				oneWord = bfReader.readLine();
			}
		} finally {
			if(null != reader){
				reader.close();
			}
			if(null != bfReader){
				bfReader.close();
			}
		}
	}
	
	public void retrieveAudios() throws Throwable{
		for(String word : words){
			System.out.println("Retrieve word: " + word);
			this.retrieveAudioByWord(word);
		}
	}

	public void retrieveAudioByWord(String word) throws Throwable{
		HttpClient httpclient = new DefaultHttpClient();
		String RETRIEVE_URL = new StringBuffer(MBConstants.HTTP_REQUEST_TARGET_URL).
								append(word).
								append(MBConstants.AUDIO_SUFFIX).toString();
		System.out.println("\t" + RETRIEVE_URL);
		HttpGet httpget = new HttpGet(RETRIEVE_URL);
		HttpHost proxy = new HttpHost(MBConstants.HTTP_PROXY_SERVER, MBConstants.HTTP_PROXY_PORT);
		httpget.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		HttpResponse response = httpclient.execute(httpget);
		StatusLine status = response.getStatusLine();
		System.out.println(status.getStatusCode());
		System.out.println(status);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			byte[] content = EntityUtils.toByteArray(entity);
			this.wirteAudioToDisk(content, word);
		}
	}
	
	public boolean wirteAudioToDisk(byte[] content, String word) throws Throwable{
		FileOutputStream fileOut = null;
		try{
			fileOut = new FileOutputStream(MBConstants.AUDIO_FOLDER + word + MBConstants.AUDIO_SUFFIX);
			fileOut.write(content);
		} finally {
			if(null != fileOut){
				fileOut.close();
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args){
		try {
			new RetrieveAudio().retrieveAudios();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
