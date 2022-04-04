package com.finalsoft.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.DialogsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CanvasView extends View {

	public int width;
	public int height;
	public Bitmap bitmapOrg ,mBitmap;
	private Canvas canvas  ;
	Context context;
	private float mX = 120, mY = 100 ;
	private static final float TOLERANCE = 5;
	public int textsize = 60;
	public int textrotate = 0 ;
	public int textcolor=-15326932 ;
	public String textasli;
	public Align textalign = Align.CENTER;
	public String textfont ="dastnevis.ttf" ;
	 String[] rank  ;
	  int position  ;
	  
	  public int w1, h1 ;
//public String categorystring="pic";
	public String filepath="";
	public CanvasView(Context c, AttributeSet attrs, String fpath) {
		super(c, attrs);
		context = c;
		this.filepath=fpath;
		textasli= LocaleController.getString("AppName", R.string.AppName);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mBitmap);
		
		w1=w;
		h1=h;
		
	}

	
	
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		bitmapOrg= BitmapFactory.decodeFile(filepath);
		
		
		int centreX , centreY ;
		centreX = 0 ;
		centreY = 0;
	//	textasli=String.valueOf(canvas.getWidth());
		Paint paint1= new Paint();
		paint1.setFilterBitmap(true);
		canvas.drawBitmap(fitImageNoMargin(bitmapOrg,w1,h1),centreX,centreY,paint1);
		
		Paint paint = new Paint();
	    paint.setTextSize(textsize);
	    paint.setColor(textcolor);
	    paint.setTextAlign(textalign);
	    paint.setAntiAlias(true);
	    
	    Typeface chops = Typeface.createFromAsset(context.getAssets(), "fonts/"+textfont);
	    paint.setTypeface(chops);
	   // paint.setShadowLayer(1, 1, -2, Color.rgb(230, 230, 230));  // سایه طوسی
	     
		canvas.save();
		canvas.rotate(textrotate,mX, mY);
	    drawString(canvas,textasli,mX,mY,paint);
	    canvas.restore();
	    
	
	}
	
	protected void drawString(Canvas canvas, String text, float x, float y, Paint paint){
		  String[] lines=text.split("\\r?\\n");
		  Rect rect=new Rect();
		  int yOff=0;
		  for (int i=0; i < lines.length; ++i) {
		    canvas.drawText(lines[i],x,y + yOff,paint);
		    paint.getTextBounds(lines[i],0,lines[i].length(),rect);
		    yOff=yOff + rect.height() + 8;
		  }
		}
//---------------------------------متن-----------------------------	
	public void matn(String a) {
		textasli=a;
		invalidate();
	}
//-------------------------------ارسال پارامترها ----------------------------
	
	public void getrankposition(String[] rank1, int position1 , int category1) {
		rank=rank1;
		position=position1;
		//category=category1;
		invalidate();	
	}
	public void getvaluefontsize(int fontsize) {
		textsize=fontsize;
		invalidate();	
	}
	
	public void getvaluefontrotate(int fontrotate) {
		textrotate=fontrotate;
		invalidate();	
	}
	
	
	public void getvaluefontcolor(int color) {
		textcolor=color;
		invalidate();	
	}
	
	public void getvaluefontchinesh(int chinesh) {
		if(chinesh==0){textalign= Align.RIGHT;};
		if(chinesh==1){textalign= Align.CENTER;};
		if(chinesh==2){textalign= Align.LEFT;};
		invalidate();	
	}
	
	public void getvaluefonttypeface(String font) {
		textfont=font;
		invalidate();	
	}
	
	
	
//----------------------------------------------------------------------------	
	private void startTouch(float x, float y) {
		mX = x;
		mY = y;
	}

	private void moveTouch(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOLERANCE || dy >= TOLERANCE) {
			mX = x;
			mY = y;
		}
	}

	private void upTouch() {}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			moveTouch(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			upTouch();
			invalidate();
			break;
		}
		return true;
	}
	
	public static Bitmap fitImageNoMargin(Bitmap baseImage, int width, int height){
	    Point pt=calculateFitImage(baseImage,width,height,null);
	    Bitmap resizedBitmap = Bitmap.createScaledBitmap(baseImage,pt.x, pt.y, true);
	    return resizedBitmap;
	  }
	  
	public static Point calculateFitImage(Bitmap baseImage, int width, int height, Point receiver){
	    if(baseImage==null){
	      throw new RuntimeException("baseImage is null");
	    }
	    if(receiver==null){
	      receiver=new Point();
	    }
	    int dw=width;
	    int dh=height;
	    
	    receiver.x=dw;
	    receiver.y=dh;
	    return receiver;
	  }
	
	 public Bitmap getBitmap(){
		this.setDrawingCacheEnabled(true);  
	    this.buildDrawingCache();
	    Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
	    this.setDrawingCacheEnabled(false);
	    return bmp;
	    }
	 
	 public void clear(){
	     
	        invalidate();
	        System.gc();
	    }


	@SuppressLint("WrongThread")
	public boolean Save(boolean send, final BaseFragment baseFragment, boolean saveingallery) {
		Bitmap bitman = getBitmap();
			final File file = new File(filepath);
			if(file.exists())file.delete();
			try {
				FileOutputStream out = new FileOutputStream(file);
				bitman.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
				if(send){
					Bundle bundle=new Bundle();
					bundle.putBoolean("onlySelect",true);
					DialogsActivity dialogsActivity=new DialogsActivity(bundle);
					dialogsActivity.setDelegate((fragment, dids, message, param) -> {
						for(int i=0;i<dids.size();i++) {
							SendMessagesHelper.getInstance(UserConfig.selectedAccount).prepareSendingPhoto(
									AccountInstance.getInstance(UserConfig.selectedAccount),
									file.getAbsolutePath(),
									null, dids.get(i), null, null, null,
									null, null, null	, 0,null,true,0);

						}							fragment.finishFragment();
					});
					baseFragment.presentFragment(dialogsActivity);
					return false;
				}
				if(saveingallery){
					AndroidUtilities.addMediaToGallery(file.getAbsolutePath());
					Toast.makeText(context, LocaleController.getString("SavedInGallery", R.string.SavedInGallery), Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
return false;
		}

//
//	public void setAsProfile(final BaseFragment context) {
//			Bitmap bitman = getBitmap();
//			final File file = new File(filepath);
//			if (file.exists()) file.delete();
//			try {
//				FileOutputStream out = new FileOutputStream(file);
//				bitman.compress(Bitmap.CompressFormat.PNG, 100, out);
//				out.flush();
//				out.close();
//				AvatarUpdater avatarUpdater=new AvatarUpdater();
////				avatarUpdater.startCrop(fi);
////				avatarUpdater.clear();
////				avatarUpdater.currentPicturePath=file.getAbsolutePath();
//				avatarUpdater.parentFragment=context;
//				avatarUpdater.processBitmap(bitman);
//				avatarUpdater.delegate=new AvatarUpdater.AvatarUpdaterDelegate() {
//					@Override
//					public void didUploadedPhoto(TLRPC.InputFile file, TLRPC.PhotoSize small, TLRPC.PhotoSize big) {
//						Toast.makeText(context.getParentActivity().getApplicationContext(),"uploaded",Toast.LENGTH_SHORT).show();
//					}
//				};
//				//};
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
}