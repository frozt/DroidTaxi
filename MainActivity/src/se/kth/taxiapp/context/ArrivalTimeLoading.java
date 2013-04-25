package se.kth.taxiapp.context;

import se.kth.taxiapp.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ArrivalTimeLoading extends AsyncTask<Void, Integer, Boolean>{

	
	private ProgressBar loadingBar;
	private Activity myParent;
	private TextView arrivalTimeText;

	private int loadingTime = 0;
	private static final int LOADING_TIME = 5; 
	
	public ArrivalTimeLoading(Activity parent, ProgressBar myBar, TextView arrivalTimeText, int loadingTime){
		this.loadingBar = myBar;
		this.myParent = parent;
		this.arrivalTimeText = arrivalTimeText;
		this.loadingTime = loadingTime;
	}
	
	@Override
	protected void onPreExecute (){
		super.onPreExecute();
		
		if(loadingTime != 0){
			loadingBar.setMax(loadingTime);
		}else{
			loadingBar.setMax(LOADING_TIME);
		}
        loadingBar.setProgress(0);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		for(int i=1; i<=10; i++) {
            sleepTask();
            publishProgress(i*10);
            if(isCancelled())
                break;
        }
        return true;
	}
	
	@Override
    protected void onProgressUpdate(Integer... values) {
        int progreso = values[0].intValue();
        loadingBar.setProgress(progreso);
    }
	
	@Override
    protected void onPostExecute(Boolean result) {
		loadingBar.setVisibility(View.INVISIBLE);
		arrivalTimeText.setText("9 minutes");
		arrivalTimeText.setVisibility(View.VISIBLE);
	}
	
	private void sleepTask(){
	    try {
	        Thread.sleep(1000);
	    } catch(InterruptedException e) {}
	}

	

}
