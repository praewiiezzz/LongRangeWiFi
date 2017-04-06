package com.example.chuti.longrange;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by chuti on 3/29/2017.
 */
public class Test extends Activity {
    private TextView Textv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Textv = (TextView)findViewById(R.id.textView);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            String j =(String) b.get("key");
            Textv.setText(j);
        }
    }


}
