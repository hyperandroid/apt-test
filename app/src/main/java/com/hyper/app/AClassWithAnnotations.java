package com.hyper.app;

import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.hyper.annotation.BindView;

/**
 * Created by ibon on 29/1/17.
 */

public class AClassWithAnnotations extends AppCompatActivity {

    @BindView( id = 192 )
    ListView v;
}
