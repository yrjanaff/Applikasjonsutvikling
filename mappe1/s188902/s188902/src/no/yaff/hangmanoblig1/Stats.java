
package no.yaff.hangmanoblig1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Stats extends Activity{
	
	TextView winTxt, lossTxt, percentTxt;
	SharedPreferences sharedPrefs;
	int loseCnt, winCnt;
	double percent;
	String winString, loseString, winPercent;
	
	protected void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stat);
		winTxt = (TextView)findViewById(R.id.winCntTxt);
    	lossTxt = (TextView)findViewById(R.id.loseCntTxt);
    	percentTxt = (TextView)findViewById(R.id.percentTxt);
    	
    	winString = getString(R.string.win_string);
    	loseString = getString(R.string.lose_string);
    	winPercent = getString(R.string.winPercent_string);
    	
    	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	setStats();
    }
	
	//Henter variabler fra sharedPrefs og setter de i tekstfelt
	private void setStats(){
		winCnt = sharedPrefs.getInt("winCnt", 0);
    	loseCnt = sharedPrefs.getInt("lossCnt", 0);
    	percent = ((double)winCnt/(winCnt + loseCnt))*100.0;
    	
		winTxt.setText(winString + ": " + winCnt);
    	lossTxt.setText(loseString + ": " + loseCnt);
    	percentTxt.setText(winPercent + ": " + (int)percent + "%");
	}
	    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}

	@Override
	 public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_close) 
        {
        	setResult(1);
        	finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
