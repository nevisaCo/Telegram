package com.finalsoft.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ProfileAvatarMaker extends BaseFragment {
    public static ProfileAvatarMaker thiscontext;
    private ActionBarLayout actionBarLayout;
    private Context context;
    public int category = 0;
    private RecyclerView.Adapter listAdapter;
    public ArrayList<Category> PicCategorys = new ArrayList();
    private RecyclerListView listView;
    private Button btnselectcategory;

    public ProfileAvatarMaker() {
        super();
        thiscontext = this;
    }

    @Override
    public View createView(final Context context) {
        this.context = context;
        this.actionBar.createMenu();
        this.actionBarLayout = new ActionBarLayout(context);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("AvatarProfileMaker", R.string.AvatarProfileMaker));
        this.actionBar.setAllowOverlayTitle(true);

        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);

        btnselectcategory = new Button(context);
        btnselectcategory.setTextSize(15);
        btnselectcategory.setTypeface(AndroidUtilities.getTypeface("fonts/IRANSansLight.ttf"));
        btnselectcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcategorydialog(context);
            }
        });
        frameLayout.addView(btnselectcategory, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.TOP | Gravity.LEFT));

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new GridLayoutManager(context, 2));
        listView.setAdapter(listAdapter = new ListAdapter(context));
        listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, 0, 50, 0, 0));

        LoadPics();
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    private void showcategorydialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("انتخاب دسته بندی");
        ArrayList<CharSequence> itar = new ArrayList<>();
        for (int i = 0; i < this.PicCategorys.size(); i++) {
            itar.add(i, PicCategorys.get(i).title);
        }
        builder.setItems(itar.toArray(new CharSequence[PicCategorys.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectcategory(item);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void endthis() {
        finishFragment();
    }

    private void LoadPics() {
        StringRequest stringRequest = new StringRequest(UrlController.PICURL + "?rnd=" + RandomHelper.getRandomNumber(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ScanJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    private void ScanJson(String response) {
        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                ArrayList<String> thispics = new ArrayList<>();
                thispics.clear();
                if (!jsonarray.getJSONObject(i).isNull("pics")) {
                    for (int j = 0; j < jsonarray.getJSONObject(i).getJSONArray("pics").length(); j++) {
                        Log.e("rr", jsonarray.getJSONObject(i).getJSONArray("pics").getJSONObject(j).toString());
                        thispics.add(jsonarray.getJSONObject(i).getJSONArray("pics").getJSONObject(j).getString("pic"));
                    }
                }
                this.PicCategorys.add(new Category(jsonarray.getJSONObject(i).getInt("id"),
                        jsonarray.getJSONObject(i).getString("category"), thispics));
            }
            selectcategory(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void selectcategory(int i) {

        category = i;
        btnselectcategory.setText(LocaleController.getString("SelectCategory", R.string.SelectCategory) + " (" + PicCategorys.get(i).title + ") ");
        listAdapter.notifyDataSetChanged();
    }


    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;


        public ListAdapter(Context context) {
            mContext = context;
            //this.category=category;
        }

        @Override
        public int getItemCount() {
            if (PicCategorys.size() == 0) return 0;
            return PicCategorys.get(category).pics.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((PicCell) holder.itemView).setPic(PicCategorys.get(category).pics.get(position));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PicCell view = new PicCell(mContext);
            return new RecyclerListView.Holder(view);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }
    }

    private class PicCell extends FrameLayout {
        private String picurl = "";
        private ImageView imgview;

        public void setPic(String picurl) {
            this.picurl = picurl;
            init();
        }

        public PicCell(@NonNull Context context) {
            super(context);
            init();
        }

        public PicCell(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public PicCell(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }


        public void init() {
            imgview = new ImageView(getContext());
            imgview.setScaleType(ImageView.ScaleType.FIT_XY);
            imgview.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_launcher));
            AndroidUtilities.runOnUIThread(() -> Picasso.get().load(picurl).into(imgview));
            addView(imgview, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 200, Gravity.TOP | Gravity.LEFT, 2, 2, 2, 2));
            setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 200));
            setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putString("path", getImageStrpath());
                //Toast.makeText(getContext(),bundle.getString("path"), Toast.LENGTH_SHORT).show();
                presentFragment(new ProfileAvatarEditor(bundle));


            });
        }

        private String getImageStrpath() {
            File picfile = AndroidUtilities.generatePicturePath();
            imgview.buildDrawingCache();
            Bitmap bm = imgview.getDrawingCache();
            try {
                FileOutputStream fOut = new FileOutputStream(picfile);
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return picfile.getAbsolutePath();
        }
    }

    class Category {
        public int id = 0;
        public String title = "";
        public ArrayList<String> pics = new ArrayList();

        public Category(int id, String title, ArrayList<String> pics) {
            this.id = id;
            this.title = title;
            this.pics = pics;
        }
    }
}
