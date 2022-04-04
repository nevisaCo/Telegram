package com.finalsoft.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.finalsoft.helper.CanvasView;
import com.finalsoft.helper.LabelHelper;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

//import com.flask.colorpicker.ColorPickerView;
//import com.flask.colorpicker.OnColorSelectedListener;
//import com.flask.colorpicker.builder.ColorPickerClickListener;
//import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.HashMap;


class ProfileAvatarEditor extends BaseFragment {
    private Context context;
    private ActionBarLayout actionBarLayout;
    public String path="";
    private int menu_done=1;
    private CanvasView canvasView;
    private int menu_others=2;
    private int menu_setprofile=3;
    private int menu_exit=4;
    private int menu_savetogallery=5;

    public ProfileAvatarEditor(Bundle bundle) {
        super(bundle);
        this.path=bundle.getString("path");
    }
    @Override
    public View createView(final Context context) {
        this.context=context;
        ActionBarMenu actionmenu = this.actionBar.createMenu();
        this.actionBarLayout = new ActionBarLayout(context);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("AvatarProfileMaker", R.string.AvatarProfileMaker));
        this.actionBar.setAllowOverlayTitle(true);

        actionmenu.addItem(menu_done, R.drawable.ic_ab_done);
        ActionBarMenuItem othermenus = actionmenu.addItem(menu_others, R.drawable.arrow_more);
      ///  othermenus.addSubItem(menu_setprofile,LocaleController.getString("SetAsProfile",R.string.SetAsProfile));
        othermenus.addSubItem(menu_savetogallery, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
        othermenus.addSubItem(menu_exit, LocaleController.getString("Close", R.string.Close));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){
            @Override
            public void onItemClick(int id) {
                if(id==-1) {
                    ProfileAvatarMaker.thiscontext.finishFragment();
                    finishFragment();
                }else if(id==menu_done){
                    canvasView.Save(true,ProfileAvatarEditor.this,false);
                    ProfileAvatarMaker.thiscontext.finishFragment();
                    finishFragment();
                }else if(id==menu_setprofile){
                  //  canvasView.setAsProfile(ProfileAvatarEditor.this);
                }else if(id==menu_savetogallery){
                    ProfileAvatarMaker.thiscontext.finishFragment();
                    canvasView.Save(false,ProfileAvatarEditor.this,true);
                    finishFragment();
                }else if(id==menu_exit){
                    ProfileAvatarMaker.thiscontext.finishFragment();
                    finishFragment(false);
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        canvasView=new CanvasView(context,null,this.path);
        frameLayout.addView(canvasView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,300, Gravity.TOP));

        FrameLayout framebuttons=new FrameLayout(context);
        framebuttons.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));

        final EditText txttext=new EditText(context);
        txttext.setSingleLine(false);
        txttext.setLines(2);
        txttext.setText(LocaleController.getString("AppName", R.string.AppName));
        txttext.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        txttext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                canvasView.matn(s.toString());
            }
        });
        framebuttons.addView(txttext, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,70,0,60,0));
        framebuttons.addView(LabelHelper.CreateLabel(context,"ShareTools_Text", R.string.ShareTools_Text), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT));

        Button btnnext=new Button(context);
        btnnext.setTypeface(AndroidUtilities.getTypeface("fonts/IRANSansLight.ttf"));
        btnnext.setText(LocaleController.getString("Next", R.string.Next));
        btnnext.setTextSize(15);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.Save(false,null,false);
                txttext.setText("");
            }
        });
        framebuttons.addView(btnnext, LayoutHelper.createFrame(60, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.LEFT,0,0,60,0));

        SeekBar seekBarfontsize=new SeekBar(context);
        seekBarfontsize.setMax(110);
        seekBarfontsize.setProgress(30);
        seekBarfontsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                canvasView.getvaluefontsize(30+arg1);
            }
        });
        framebuttons.addView(seekBarfontsize, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,0,80,60,0));
        framebuttons.addView(LabelHelper.CreateLabel(context,"FontSize", R.string.FontSize), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,0,70,0,0));

        SeekBar seekBarrotate=new SeekBar(context);
        seekBarrotate.setMax(360);
        seekBarrotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                canvasView.getvaluefontrotate(arg1);
            }
        });
        framebuttons.addView(seekBarrotate, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,0,120,60,0));
        framebuttons.addView(LabelHelper.CreateLabel(context,"AccDescrRotate", R.string.AccDescrRotate), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,0,110,0,0));

        final TextView btnchangecolor=new TextView(context);
        btnchangecolor.setText("");
        btnchangecolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnchangecolor.setBackgroundColor(Color.RED);
                canvasView.getvaluefontcolor(Color.GREEN);

/*                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("رنگ را انتخاب کنید")
                        .initialColor(0xffffffff)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {

                            }
                        })
                        .setNegativeButton("cancel", (dialog, which) -> {
                        })
                        .build();
//                        .show();*/
            }
        });
        btnchangecolor.setBackgroundColor(0xff000000);
        framebuttons.addView(btnchangecolor, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,40, Gravity.TOP| Gravity.RIGHT,60,160,60,0));
        framebuttons.addView(LabelHelper.CreateLabel(context,"SelectColor", R.string.SelectColor), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,0,150,0,0));


        TextView btnchangefont=new TextView(context);
        btnchangefont.setText("انتخاب");
        btnchangefont.setTypeface(AndroidUtilities.getTypeface("fonts/IRANSansLight.ttf"));;
        btnchangefont.setTextSize(17);
        btnchangefont.setTextColor(0xff000000);
        btnchangefont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> listfonts = FontHelper.LoadFonts();
                final String[] items= listfonts.keySet().toArray(new String[listfonts.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("انتخاب قلم");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        String font = listfonts.get(items[item]);
                        canvasView.getvaluefonttypeface(font  + ".ttf");
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });
        btnchangefont.setBackgroundColor(0xffffffff);
        framebuttons.addView(btnchangefont, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,30, Gravity.TOP| Gravity.RIGHT,60,200,60,0));
        framebuttons.addView(LabelHelper.CreateLabel(context,"FontType", R.string.FontType), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP| Gravity.RIGHT,0,190,0,0));


        frameLayout.addView(framebuttons, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP,0,300,0,0));
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

}
