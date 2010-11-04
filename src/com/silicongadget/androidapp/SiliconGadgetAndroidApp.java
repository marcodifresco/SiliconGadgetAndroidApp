//============================================================================
// Name        : SiliconGadgetAndroidApp.java
// Author      : Marco Di Fresco
// Version     : 0.3
// Copyright   : Marco Di Fresco
// Description : Android application for SiliconGadget.com
//============================================================================

package com.silicongadget.androidapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener; 
import android.util.Log;
import android.content.Intent;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.admob.android.ads.*;
import com.silicongadget.androidapp.ShowDescription;

public class SiliconGadgetAndroidApp extends Activity implements OnItemClickListener {
	// Variables
	//public final String RSSFEEDOFCHOICE = "http://feeds.feedburner.com/SiliconGadget";
	public final String RSSFEEDOFCHOICE = "http://www.silicongadget.com/category/guides/feed/";
	public final String tag = "RSSReader";
	private RSSFeed feed = null;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle icicle) {
    	// Put AdMob on testing mode when used on the emulator
    	AdManager.setTestDevices( new String[] {
                AdManager.TEST_EMULATOR,
        } );

        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        // go get our feed!
        feed = getFeed(RSSFEEDOFCHOICE);

        // display UI
        UpdateDisplay();
        
    }

    
    private RSSFeed getFeed(String urlToRssFeed)
    {
    	try
    	{
    		// setup the url
    	   URL url = new URL(urlToRssFeed);

           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           RSSHandler theRssHandler = new RSSHandler();
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           // get our data via the url class
           InputSource is = new InputSource(url.openStream());
           // perform the synchronous parse           
           xmlreader.parse(is);
           // get the results - should be a fully populated RSSFeed instance, or null on error
           return theRssHandler.getFeed();
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    		return null;
    	}
    }
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0,0,0,"Choose RSS Feed");
    	menu.add(0,1,1,"Refresh");
    	Log.i(tag,"onCreateOptionsMenu");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        case 0:
        	
        	Log.i(tag,"Set RSS Feed");
            return true;
        case 1:
        	Log.i(tag,"Refreshing RSS Feed");
            return true;
        }
        return false;
    }
    
    
    private void UpdateDisplay()
    {
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        TextView feedpubdate = (TextView) findViewById(R.id.feedpubdate);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
  
        
        if (feed == null)
        {
        	feedtitle.setText("No RSS Feed Available");
        	return;
        }
        
        feedtitle.setText(feed.getTitle());
        feedpubdate.setText(feed.getPubDate());

        ArrayAdapter<RSSItem> adapter = new ArrayAdapter<RSSItem>(this,android.R.layout.simple_list_item_1,extracted());

        itemlist.setAdapter(adapter);
        
        itemlist.setOnItemClickListener(this);
        
        itemlist.setSelection(0);
        
    }


	private List<RSSItem> extracted() {
		return feed.getAllItems();
	}
    
    
     public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id)
     {
    	 Log.i(tag,"item clicked! [" + feed.getItem(position).getTitle() + "]");

    	 Intent itemintent = new Intent(this,ShowDescription.class);
         
    	 Bundle b = new Bundle();
    	 b.putString("title", feed.getItem(position).getTitle());
    	 b.putString("description", feed.getItem(position).getDescription());
    	 b.putString("link", feed.getItem(position).getLink());
    	 b.putString("pubdate", feed.getItem(position).getPubDate());
    	 
    	 itemintent.putExtra("android.intent.extra.INTENT", b);
    	 
    	 startActivity(itemintent);
     }
}