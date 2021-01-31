package com.example.kakacommunitymanager.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.kakacommunitymanager.R;
import com.example.kakacommunitymanager.constant.StringUtil;
import com.google.android.material.textfield.TextInputEditText;

public class SearchActivity extends AppCompatActivity {

    private ImageView back;

    private TextInputEditText searchText;

    private ImageView search;

    private String keyWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.search_back);
        searchText = (TextInputEditText) findViewById(R.id.search_text);
        search = (ImageView) findViewById(R.id.search_search);
    }

    @Override
    protected void onResume() {
        super.onResume();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyWord = searchText.getText().toString();
                if (!StringUtil.isBlank(keyWord)) {
                    Intent intent = new Intent(SearchActivity.this, ShowSearchActivity.class);
                    intent.putExtra("keyword", keyWord);
                    startActivity(intent);
                }
            }
        });
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    keyWord = searchText.getText().toString();
                    if (!StringUtil.isBlank(keyWord)) {
                        Intent intent = new Intent(SearchActivity.this, ShowSearchActivity.class);
                        intent.putExtra("keyword", keyWord);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
