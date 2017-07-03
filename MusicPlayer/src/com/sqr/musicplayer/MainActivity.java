package com.sqr.musicplayer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnCompletionListener {

	ImageButton prevbtn,playbtn,nextbtn;
	SeekBar songProgressBar;
	TextView music_title, current_duration, total_duration;
	MediaPlayer mp;
	Handler mHandler = new Handler();
	int i = 0;
	long totalDuration = 0;
	long currentDuration = 0;
	int current = 0;
	
	ListView musicList;
	
	int index[] = {
			R.raw.shelter,
			R.raw.happy,
			R.raw.hollowhills,
			R.raw.rippedaway
	};
	
	String title[] = {
			"Shelter.mp3",
			"Happy people.mp3",
			"Hollow Hills.mp3",
			"Ripped Away.mp3"
	};
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//setting the layout of the application
        
        musicList= (ListView) findViewById(R.id.musicList);//setting ListView for music list
        
        ArrayAdapter<String> a = new ArrayAdapter<String>(MainActivity.this, 
				android.R.layout.simple_list_item_1, title);//setting layout for items in the ListView
        
        musicList.setAdapter(a);//setting the adapter for the ListView 
        
        //setting the handler for ListView
        musicList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long index) {
				
				int selectedMusic = position;
				playMusic(selectedMusic);
				i=selectedMusic;
			}
			
		});
        
        //setting the ImageButton for all the buttons
        prevbtn = (ImageButton) findViewById(R.id.prevbtn);
        playbtn = (ImageButton) findViewById(R.id.playbtn);
        nextbtn = (ImageButton) findViewById(R.id.nextbtn);
        
        //setting the SeekBar for the music progress bar and the handler for the SeekBar
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songProgressBar.setOnSeekBarChangeListener(new SeekBarHandler());
        
        //setting the TextView for the music details
		music_title = (TextView) findViewById(R.id.music_title);
		current_duration = (TextView) findViewById(R.id.current_duration);
		total_duration = (TextView) findViewById(R.id.total_duration);
		
		//setting the MediaPlayer to play the music
		mp = new MediaPlayer();
		mp = MediaPlayer.create(this, index[i]);
		totalDuration = mp.getDuration();
		mp.setOnCompletionListener(this);
		
		//setting the handler for the buttons
        prevbtn.setOnClickListener(new ButtonHandler());
        playbtn.setOnClickListener(new ButtonHandler());
        nextbtn.setOnClickListener(new ButtonHandler());
        
        //setting the text for music detail (music title, current duration, and total duration)
        music_title.setText(title[i]);
        current_duration.setText(milliSecondsToTimer(currentDuration));
        total_duration.setText(milliSecondsToTimer(totalDuration));
        
    }
    
    //the handler for the buttons
    class ButtonHandler implements OnClickListener{
    	public void onClick(View v) {
    		//if play or pause button is clicked
    		if (v.getId()==R.id.playbtn){
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						playbtn.setImageResource(R.drawable.play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Updating progress bar
						updateProgressBar();
						// Changing button image to pause button
						playbtn.setImageResource(R.drawable.pause);
					}
				}
    		}
    		
    		//if next button is clicked
    		if (v.getId()==R.id.nextbtn){
    			if(i < (index.length - 1)){
    				//play next song
					playMusic(i + 1);
					i = i + 1;
				}else{
					// play first song
					playMusic(0);
					i = 0;
				}
    			
    		}
    		
    		//if previous button is clicked
    		if (v.getId()==R.id.prevbtn){
    			if(i > 0){
    				//play prev song
					playMusic(i - 1);
					i = i - 1;
				}else{
					// play last song
					playMusic(index.length - 1);
					i = index.length - 1;
				}
    			
    		}
			
		}
    }
    
    //handler for the SeekBar
    class SeekBarHandler implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// to stop mHandler from updating progress bar
			mHandler.removeCallbacks(mUpdateTimeTask);
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			mHandler.removeCallbacks(mUpdateTimeTask);
			int totalDuration = mp.getDuration();
			int currentPosition = progressToTimer(songProgressBar.getProgress(), totalDuration);
			
			// setting the music forward or backward to certain seconds
			mp.seekTo(currentPosition);
			
			// update timer progress again
			updateProgressBar();
		}
    	
    }
    
    public void  playMusic(int i){
		// Play song
		mp.reset();
    	mp = MediaPlayer.create(this, index[i]);// changing the music
    	mp.setOnCompletionListener(this);
		mp.start();
		
    	// Changing Button Image to pause image
		playbtn.setImageResource(R.drawable.pause);
		
		//Display the music title
		music_title.setText(title[i]);
		
		//Get and display total music duration
		totalDuration = mp.getDuration();
		total_duration.setText(milliSecondsToTimer(totalDuration));
		
		// Updating progress bar
		updateProgressBar();
	}
    
    //update the timer on SeekBar
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }
    
    //runnable thread for the music progress
    private Runnable mUpdateTimeTask = new Runnable() {
	   public void run() {
		   currentDuration = mp.getCurrentPosition();
		  
		   // Displaying time completed playing
		   current_duration.setText(milliSecondsToTimer(currentDuration));
		   
		   // Updating progress bar
		   int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
		   songProgressBar.setProgress(progress);
		   
		   // Running this thread after 100 milliseconds
	       mHandler.postDelayed(this, 100);
	   }
	};
    
	//method to change milliseconds to string with desired format
    public String milliSecondsToTimer(long milliseconds){
		String TimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   TimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   TimerString = TimerString + minutes + ":" + secondsString;
		
		// return timer string
		return TimerString;
	}
    
    //method to get progress percentage for the SeekBar
    public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}
	
    public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	//execute the code when a music is completed
	public void onCompletion(MediaPlayer mp) {
		if(i < (index.length - 1)){
			//play next song
			playMusic(i + 1);
			i = i + 1;
		}else{
			// play first song
			playMusic(0);
			i = 0;
		}
	}
	
}