package net.cyclestreets;

import net.cyclestreets.api.Photo;
import net.cyclestreets.view.R;
import net.cyclestreets.util.ImageDownloader;
import net.cyclestreets.util.Share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class DisplayPhotoActivity extends Activity implements View.OnClickListener {
	private Photo photo_;

  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showphoto);
		
	  photo_ = getIntent().getParcelableExtra("photo");

    final TextView text = (TextView)findViewById(R.id.photo_text);
    text.setText(photo_.caption());

    final Button b = (Button)findViewById(R.id.photo_share);
    b.setOnClickListener(this);

    if (photo_.hasVideos())
      setupVideo();
    else
      setupPhoto();
	} // onCreate

  private void setupVideo() {
    final String bestUrl = videoUrl();

    final VideoView vv = (VideoView)findViewById(R.id.video);
    sizeView(vv);

    final MediaController mc = new MediaController(this);
    mc.setAnchorView(vv);
    mc.setMediaPlayer(vv);
    vv.setMediaController(mc);

    final Uri uri = Uri.parse(bestUrl);
    vv.setVideoURI(uri);
    vv.start();
  } // setupVideo

  private String videoUrl() {
    for (String format : new String[]{ "mp4", "mov", "3gp" }) {
      Photo.Video v = photo_.video(format);
      if (v != null)
        return v.url();
    } // for ...
    return null;
  } // videoUrl

  private void setupPhoto() {
    final ImageView iv = (ImageView)findViewById(R.id.photo);
    sizeView(iv);
    iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    iv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner));

    final String thumbnailUrl = photo_.thumbnailUrl();
    ImageDownloader.get(thumbnailUrl, iv);
  } // setupPhoto

  private void sizeView(View v) {
    final WindowManager wm = getWindowManager();
    final int device_height = wm.getDefaultDisplay().getHeight();
    final int device_width = wm.getDefaultDisplay().getWidth();
    final int height = (device_height > device_width)
        ? device_height / 10 * 4
        : device_height / 10 * 8;
    final int width = device_width;
    v.setLayoutParams(new LinearLayout.LayoutParams(width, height));
    v.setVisibility(View.VISIBLE);
  } // sizeVide

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
    if (photo_.hasVideos())
      return false;
		if (event.getAction() == MotionEvent.ACTION_UP)
			finish();
		return false;
	} // onTouchEvent

	@Override
	public void onClick(View v)	{
    final int id = v.getId();
		if(R.id.photo_share == id) {
		  String photoUrl_ = photo_.url();
		  String caption_ = photo_.caption();
		  Share.Url(this, photoUrl_, caption_, "Photo on CycleStreets.net");
		} // if ...
	} // onClick
} // DisplayPhotoActivity
