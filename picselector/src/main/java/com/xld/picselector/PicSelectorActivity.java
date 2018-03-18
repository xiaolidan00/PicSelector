package com.xld.picselector;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicSelectorActivity extends AppCompatActivity {
    private RecyclerView rvPic;
    public List<Map<String, Object>> imgList;//存储显示的图片信息
    public static List<Map<String, Object>> imgSelectList;//存储选择的图片信息
    private List<Map<String, String>> pathList;//存储文件夹信息
    private Spinner spFolder;//文件夹Spinner
    private RecyclerView.Adapter<PicViewHolder> adapter;
    private int MAX_NUM = 9;//选择图片数
    private int SPAN_COUNT = 4;//GridLayout 列数
    private  int SELECT_OK = 0x1001;//resultCode
    private final static String SELECT_IMAGES = "select_images";
    private final static String ALL_IMAGES = Environment.getExternalStorageDirectory().getAbsolutePath();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picselector);
        //设置需选择的图片数
        MAX_NUM = getIntent().getIntExtra("selectPicNum", 9);
        SELECT_OK=getIntent().getIntExtra("selectOk", 0x1001);
        initView();
    }

    private void initView() {

        //初始化变量
        imgList = new ArrayList<>();
        imgSelectList = new ArrayList<>();
        pathList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name",  "所有图片");
        map.put("path", ALL_IMAGES);
        pathList.add(map);

        rvPic = findViewById(R.id.rv_picselector);
        GridLayoutManager manager = new GridLayoutManager(this, SPAN_COUNT);
        rvPic.setLayoutManager(manager);
       searchImage();
        if (imgList.size() > 0) {
            adapter = new RecyclerView.Adapter<PicViewHolder>() {
                @Override
                public PicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvitem_picselector, parent, false);
                    PicViewHolder holder = new PicViewHolder(view);
                    return holder;
                }

                @Override
                public void onBindViewHolder(final PicViewHolder holder, final int position) {
                        final String path = String.valueOf(imgList.get(position).get("path"));
                        Picasso.with(PicSelectorActivity.this)
                                .load("file://" + path)
                                .placeholder(R.drawable.ic_picselector_image_default)
                                .into(holder.imgPic);
                        holder.cbPic.setChecked(false);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!holder.cbPic.isChecked()) {
                                    if (imgSelectList.size() <= 8 && imgSelectList.size() >= 0) {
                                        holder.cbPic.setChecked(true);
                                        Map<String, Object> info = imgList.get(position);
                                        imgSelectList.add(info);
                                    }
                                } else {
                                    if (imgSelectList.size() <= 9 && imgSelectList.size() >= 1) {
                                        holder.cbPic.setChecked(false);
                                        Map<String, Object> info = imgList.get(position);
                                        imgSelectList.remove(info);
                                    }
                                }
                                setTitle(imgSelectList.size() + "/" + MAX_NUM);
                            }
                        });
                }

                @Override
                public int getItemCount() {
                    return imgList.size();
                }
            };
            rvPic.setAdapter(adapter);
            //文件夹spinner
            spFolder = findViewById(R.id.sp_picselector_folder);
            SimpleAdapter folderAdapter = new SimpleAdapter(this, pathList, R.layout.spitem_picselector_folder, new String[]{"name"}, new int[]{R.id.tv_picselector_folder_spitem});
            spFolder.setMinimumWidth(WindowManager.LayoutParams.MATCH_PARENT);
            spFolder.setAdapter(folderAdapter);
            spFolder.setSelection(0);
            spFolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    searchImage(pathList.get(position).get("path"));

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void searchImage(){
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        imgList.clear();
        imgSelectList.clear();
        setTitle("0/" + MAX_NUM);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片地址
                //图片所在文件夹
                File parent = new File(new File(filePath).getParent());
                Map<String, String> info = new HashMap<>();
                info.put("name", parent.getName());
                info.put("path", parent.getAbsolutePath());
                if (!pathList.contains(info)) {
                    pathList.add(info);
                }

                    Map<String, Object> picInfo = new HashMap<>();
                    picInfo.put("parent",parent.getAbsolutePath());
                    picInfo.put("path", filePath);
                    imgList.add(picInfo);

            }//while (cursor.moveToNext())
        }// if (cursor != null)
        cursor.close();
    }
    private void searchImage(String path) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        imgList.clear();
        imgSelectList.clear();
        setTitle("0/" + MAX_NUM);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片地址
                //图片所在文件夹
                File parent = new File(new File(filePath).getParent());
                //添加图片信息
                if(parent.getAbsolutePath().contains(path)){
                    Map<String, Object> picInfo = new HashMap<>();
                    picInfo.put("parent",parent.getAbsolutePath());
                    picInfo.put("path", filePath);
                    imgList.add(picInfo);
                }
            }//while (cursor.moveToNext())
        }// if (cursor != null)
        cursor.close();

    }
    //预览图片
    public void picShow(View view){
        if(imgSelectList.size()>0) {
            Intent intent = new Intent(this, PicSelectorShowActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this,"请选择预览图片",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picselector, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_picselector_ok://确认选择
                Intent data = new Intent();
                String[] selectImages = new String[imgSelectList.size()];
                for (int i = 0; i < imgSelectList.size(); i++) {
                    selectImages[i] = String.valueOf(imgSelectList.get(i).get("path"));
                }
                data.putExtra(SELECT_IMAGES, selectImages);
                setResult(SELECT_OK, data);
                this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    //RecyclerView.ViewHolder
    static class PicViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPic;
        CheckBox cbPic;

        public PicViewHolder(View itemView) {
            super(itemView);
            imgPic = itemView.findViewById(R.id.img_picselector_rvitem);
            cbPic = itemView.findViewById(R.id.cb_picselector_rvitem);
        }
    }
}
