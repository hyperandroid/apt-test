package com.hyper.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.hyper.annotation.BindView;
import com.hyper.annotation.Binder;

public class Main2Activity extends AppCompatActivity {

    // public/package access so that binder object can set value directly.
    // otherwise it should set the value reflectively.
    @BindView(id = R.id.text_view)
    TextView text;
    @BindView(id = 101)
    EditText text22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // solve annotations.
        Binder.Bind(this);

        text.setText("APT worked!!");
    }
}
