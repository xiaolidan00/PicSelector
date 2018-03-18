package com.xld.picselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private final static int SELECT_OK = 0x1001;
    private final static String SELECT_IMAGES = "select_images";
    private String[] selectImages;
    private RecyclerView rvPic;
    private RecyclerView.Adapter<PicSelectorActivity.PicViewHolder> adapter;
    private boolean isCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, PicSelectorActivity.class);
                startActivityForResult(intent, SELECT_OK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_OK && resultCode == SELECT_OK) {
            selectImages = data.getStringArrayExtra(SELECT_IMAGES);
            if (!isCreate)
                showImage();
            else
                adapter.notifyDataSetChanged();
        }
    }

    private void showImage() {
        rvPic = findViewById(R.id.rv_pic_show);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rvPic.setLayoutManager(manager);

        if (selectImages.length > 0) {
            adapter = new RecyclerView.Adapter<PicSelectorActivity.PicViewHolder>() {
                @Override
                public PicSelectorActivity.PicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvitem_picselector, parent, false);
                    PicSelectorActivity.PicViewHolder holder = new PicSelectorActivity.PicViewHolder(view);
                    return holder;
                }

                @Override
                public void onBindViewHolder(final PicSelectorActivity.PicViewHolder holder, final int position) {
                    final String path = selectImages[position];
                    Picasso.with(MainActivity.this)
                            .load("file://" + path)
                            .placeholder(R.drawable.ic_picselector_image_default)
                            .into(holder.imgPic);
                    holder.cbPic.setVisibility(View.GONE);
                }

                @Override
                public int getItemCount() {
                    return selectImages.length;
                }
            };
            rvPic.setAdapter(adapter);
            isCreate = true;
        }

    }

}
