package com.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.gellary.GridViewAdapter;
import com.custom.gellary.MediaModel;
import com.lemda.videoconvert.R;


@SuppressLint({ "NewApi", "SdCardPath" })
@SuppressWarnings("deprecation")
public class UploadFragment  extends Fragment implements SurfaceHolder.Callback  {

	TextView tvTab1,tvTab2;

	LinearLayout llMainPhoto,llMainGallery;

	Button btnCapture,btnFlip;
	//ImageView ivSelfie;
	//View mImageContainer;


	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;

	PictureCallback rawCallback;
	ShutterCallback shutterCallback;
	PictureCallback jpegCallback;

	Boolean blnCamOpen=false,blnFlip=false;;

	ProgressDialog customProgressDialog;



	//for the image editing
	String TAG = "=UploadFragment.java=",  LOG_TAG = "--aviary-launcher-=UploadFragment.java=";
	private static final int EXTERNAL_STORAGE_UNAVAILABLE = 1;
	private File mGalleryFolder;
	/** Folder name on the sdcard where the images will be saved **/
	private static final String FOLDER_NAME = "aviary";
	String mOutputFilePath;
	// your app public key, in the google play console
	private static final String BILLING_API = "b5eac27003e2dd5a";
	// your aviary secret key
	private static final String API_SECRET = "56c2a47fabcafad7";

	private static final int ACTION_REQUEST_FEATHER = 100;
	Uri mImageUri;

	//int imageWidth, imageHeight;

	/*	Key: b5eac27003e2dd5a
  	Secret: 56c2a47fabcafad7
	 */

	/*public static final String LOG_TAG = "aviary-launcher";*/


	Activity activity;
	
	


	
	

	// for the gallery
	private ArrayList<String> mSelectedItems = new ArrayList<String>();
	private ArrayList<MediaModel> mGalleryModelList;
	private GridView mImageGridView;
	private GridViewAdapter mImageAdapter;
	private Cursor mImageCursor;

	// Container Activity must implement this interface
	public interface OnImageSelectedListener {
		public void onImageSelected(int count);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();

		return inflater.inflate(R.layout.fragment_upload,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{

			setUpPager(view);

	}
	
	private void create43RatioSurface() {
	   
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    int height = 0;
	    int width = 0;

	    if(metrics.widthPixels < metrics.heightPixels){
	        width = metrics.widthPixels;
	        height= (metrics.widthPixels/4) * 3 ;
	    } else {
	        height= metrics.heightPixels;
	        width= (metrics.heightPixels/4) * 3 ;
	    }

	    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
	    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
	    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

	    surfaceView.setLayoutParams(layoutParams);        
	}

	void setUpPager(View view) 
	{ 
		
		tvTab1 = (TextView) view.findViewById(R.id.tvTab1);			
		tvTab2 = (TextView) view.findViewById(R.id.tvTab2);

		llMainPhoto= (LinearLayout)view.findViewById(R.id.llMainPhoto);
		llMainGallery= (LinearLayout)view.findViewById(R.id.llMainGallery);
		
		btnCapture = (Button)view.findViewById(R.id.btnCapture);
		btnFlip  = (Button)view.findViewById(R.id.btnFlip);
		surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);	
		surfaceHolder = surfaceView.getHolder();
		btnCapture.setVisibility(View.VISIBLE);
		btnCapture.setBackgroundResource(R.drawable.capture);
		surfaceView.setVisibility(View.VISIBLE);


		/*DisplayMetrics metrics = getResources().getDisplayMetrics();
		imageWidth = (int) ( (float) metrics.widthPixels / 1.5 );
		imageHeight = (int) ( (float) metrics.heightPixels / 1.5 );
*/
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		
		

		mImageGridView = (GridView) view.findViewById(R.id.gridViewFromMediaChooser);
		try 
		{

			// set default tab 2

			tvTab1.setSelected(false);
			tvTab2.setSelected(true);

			tvTab1.setTextColor(activity.getResources().getColor(R.color.clrUnselectedTab));
			tvTab2.setTextColor(activity.getResources().getColor(R.color.clrselectedTab));

			llMainPhoto.setVisibility(View.VISIBLE);
			llMainGallery.setVisibility(View.GONE);

			setPhoto();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}



		 
		tvTab1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvTab1.setSelected(true);
				tvTab2.setSelected(false);

				tvTab1.setTextColor(activity.getResources().getColor(R.color.clrselectedTab));
				tvTab2.setTextColor(activity.getResources().getColor(R.color.clrUnselectedTab));

				//setUpSearchList();
				llMainGallery.setVisibility(View.VISIBLE);
				llMainPhoto.setVisibility(View.GONE);

				setGallery();

			}
		});



		tvTab2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvTab1.setSelected(false);
				tvTab2.setSelected(true);

				tvTab1.setTextColor(activity.getResources().getColor(R.color.clrUnselectedTab));
				tvTab2.setTextColor(activity.getResources().getColor(R.color.clrselectedTab));

				llMainPhoto.setVisibility(View.VISIBLE);
				llMainGallery.setVisibility(View.GONE);
				setPhoto();
			}
		});

		/*btnFlip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(blnFlip ==false)
				{
					blnFlip = true;
				}
				else
				{
					blnFlip = false;
				}
				try {
					// open the camera
					camera = openFrontFacingCameraGingerbread();// Camera.open();
				} catch (RuntimeException e) {
					// check for exceptions
					System.err.println(e);

				}
				setPhoto();
			}
		});*/

		Log.d(TAG, "onCreated");
	}

	public void setGallery()
	{
		if (getArguments() != null)
		{
			initPhoneImages(getArguments().getString("name"));
			System.out.println("==If Part==");
		}
		else
		{
			System.out.println("=Else Part=");
			initPhoneImages();
		}
		try {
			System.out.println("=----setGallery()--+stop camera+=");
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		e.printStackTrace();
		}
	}


	public void setPhoto()
	{
		refreshCamera();
		surfaceHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		jpegCallback = new PictureCallback() 
		{
			public void onPictureTaken(byte[] data, Camera camera) {
				FileOutputStream outStream = null;
				try {
					System.out.println("=Save image=");
					try 
					{						
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						outStream = new FileOutputStream("/sdcard/Pictures/Selfie_Img.png");
						Matrix matrix = new Matrix();
						if(blnCamOpen == false)
						{
							System.out.println("=======Front camera rotate image  y270   ====");
							//matrix.postRotate(270);
							matrix.postRotate(90);
							Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,height, matrix, false);
							resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
							
							Bitmap dstBmp;
							if (resizedBitmap.getWidth() >= resizedBitmap.getHeight())
							{								
								System.out.println("==IF==resizedBitmap.getWidth() >= resizedBitmap.getHeight()");
								System.out.println("=resizedBitmap.getWidth()="+resizedBitmap.getWidth() );								
								 if(resizedBitmap.getWidth() > 5370)
								 {
									  dstBmp = Bitmap.createBitmap(resizedBitmap,resizedBitmap.getWidth()/2 - resizedBitmap.getHeight()/2,0,resizedBitmap.getHeight(),resizedBitmap.getHeight());
								 }
								 else
								 {
									 System.out.println("==ELSE-2==resizedBitmap.getWidth() > 170");										
									 dstBmp = resizedBitmap;
								 }								
							}
							else
							{
								System.out.println("==ELSE==resizedBitmap.getWidth() >= resizedBitmap.getHeight()");								
								System.out.println("=resizedBitmap.getHeight()="+resizedBitmap.getHeight() );
								 if(resizedBitmap.getHeight() > 5370)
								 {
									 dstBmp = Bitmap.createBitmap(
												resizedBitmap,
											     0, 
											     resizedBitmap.getHeight()/2 - resizedBitmap.getWidth()/2,
											     resizedBitmap.getWidth(),
											     resizedBitmap.getWidth() 
											     );
								 }
								 else
								 {
									 System.out.println("==ELSE-2==resizedBitmap.getHeight() > 170");										
									 dstBmp = resizedBitmap;
								 }
							}
							outStream = new FileOutputStream("/sdcard/Pictures/Selfie_Img.png");
							dstBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
						
						}
						else
						{
							
							System.out.println("=======Back camera no rotate image 000  x270   ====");
							outStream.write(data);
						}						
						outStream.close();
						
					} catch (FileNotFoundException e) 
					{
						e.printStackTrace();
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 					
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				finally {
				}

System.out.println("==================Save path================");

System.out.println("=Pictures Saved to \n ###=="+android.os.Environment.getExternalStorageDirectory()+"Pictures/Selfie_Img.png ==###");
			//	Toast.makeText(getActivity(), "Pictures Saved to \n "+android.os.Environment.getExternalStorageDirectory()+"Pictures/Selfie_Img.png", 100).show();
			
			//--	refreshCamera();
				
				/*Intent intActSelfiPost = new Intent(getActivity(),ActSelfiePost.class);
				intActSelfiPost.putExtra("from", "UploadFragmentPhoto");
				intActSelfiPost.putExtra("path",android.os.Environment.getExternalStorageDirectory()+"Pictures/Selfie_Img.png");
				startActivity(intActSelfiPost);*/
			
				customProgressDialog.dismiss();
				
				try {
					if (surfaceHolder.getSurface() == null) {
						// preview surface does not exist
						System.out.println("=surfaceHolder  null=");
						return;
					}

					// stop preview before making changes
					try {
						System.out.println("=+stop camera+=");
						camera.stopPreview();
					} catch (Exception e) {
						// ignore: tried to stop a non-existent preview
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
	}
		};
		
		try 
		{
			customProgressDialog = new ProgressDialog(getActivity());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		btnCapture.setOnClickListener(new  OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try 
				{
					if(camera != null)
					{
						btnCapture.setBackgroundResource(R.drawable.capture_active);
						customProgressDialog.show();
						captureImage2();
						
					}
					else
					{
						btnCapture.setBackgroundResource(R.drawable.capture);
						Toast.makeText(getActivity(), "Sorry, something went wrong...! \n Please try again.", Toast.LENGTH_SHORT).show();
					}
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		llMainPhoto.setOnTouchListener(new OnSwipeTouchListener(getActivity()) 
		{
			/*public void onSwipeTop() 
		            {
		                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
		            }*/
			public void onSwipeRight()
			{
				//Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();


				// TODO Auto-generated method stub
				tvTab1.setSelected(true);
				tvTab2.setSelected(false);

				tvTab1.setTextColor(activity.getResources().getColor(R.color.clrselectedTab));
				tvTab2.setTextColor(activity.getResources().getColor(R.color.clrUnselectedTab));

				//setUpSearchList();
				llMainGallery.setVisibility(View.VISIBLE);
				llMainPhoto.setVisibility(View.GONE);

				setGallery();

			}
			/* public void onSwipeLeft()
		            {
		                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
		            }*/
			/*  public void onSwipeBottom() {
		                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
		            }*/
		});


	}



	public void captureImage2() throws IOException {
		//take the picture
		camera.takePicture(null, null, jpegCallback);
	}


/*
	public void captureImage(View v) throws IOException {
		//take the picture
		camera.takePicture(null, null, jpegCallback);
	}*/

	public void refreshCamera() {
		if (surfaceHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			System.out.println("=+stop camera+=");
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
			e.printStackTrace();
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
		// start preview with new settings
		try {
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (Exception e) {
e.printStackTrace();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		refreshCamera();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// open the camera
			camera = openFrontFacingCameraGingerbread();// Camera.open();
		} catch (RuntimeException e) {
			// check for exceptions
			System.err.println(e);
			return;
		}
		if(camera != null)
		{		
			System.out.println("====================Setting new params===========================");
			try
			{
				Camera.Parameters params = camera.getParameters();

				List<Camera.Size> sizeList = params.getSupportedPictureSizes();
				int chosenSize = getPictureSizeIndexForHeight(sizeList, 800);
				//				params.setPictureSize(sizeList.get(sizeList.size()-1).width, sizeList.get(sizeList.size()-1).height);
				params.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);

				camera.setParameters(params);
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();

			} catch (Exception e) {
				// check for exceptions
				System.err.println(e);
				return;
			}
		}
	}


	public static int getPictureSizeIndexForHeight(List<Camera.Size> sizeList, int height)
	{
		int chosenHeight = -1;
		for(int i=0; i<sizeList.size(); i++) 
		{
			if(sizeList.get(i).height < height) 
			{
				chosenHeight = i-1;
				if(chosenHeight==-1)
					chosenHeight = 0;
				break;
			}
		}
		return chosenHeight;
	}



	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop preview and release camera
		System.out.println("=+stop camera+=");
		camera.stopPreview();
		camera.release();
		camera = null;
	}



	private Camera openFrontFacingCameraGingerbread() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) 
		{

			blnCamOpen = true;
			Camera.getCameraInfo(camIdx, cameraInfo);
			if(blnFlip == true)
			{
			//	cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
				{
					try {
						System.out.println("=open front cam---front camera not open=");
						cam = Camera.open(camIdx);
						blnCamOpen = false;

					} catch (RuntimeException e) {
						blnCamOpen = true;

						Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO: handle exception
						blnCamOpen = true;

						e.printStackTrace();
					}
				}
			}
			else
			{
			//	cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_BACK;
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
				{
					try {
						System.out.println("=open front cam---back camera not open=");
						cam = Camera.open(camIdx);
						blnCamOpen = false;
					} catch (RuntimeException e) {
						blnCamOpen = true;
						Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO: handle exception
						blnCamOpen = true;
						e.printStackTrace();
					}
				}
			}
		}

		/*if(blnCamOpen) //(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)// (CameraInfo.CAMERA_FACING_BACK)
		{
			try {
				System.out.println("=No front cam---back camera open=");
				cam = Camera.open();
				blnCamOpen = true;
			} catch (RuntimeException e) {
				Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
				blnCamOpen = false;
				e.printStackTrace();

			}
			catch (Exception e) {
				// TODO: handle exception
				blnCamOpen = false;
				e.printStackTrace();
			}
		}*/

		return cam;
	}


	


	// for the gallery


	private void initPhoneImages(String bucketName){
		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			String searchParams = null;
			String bucket = bucketName;
			searchParams = "bucket_display_name = \"" + bucket + "\"";

			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
			mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");

			setAdapter(mImageCursor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPhoneImages() {
		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
			mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");

			setAdapter(mImageCursor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void setAdapter(Cursor imagecursor) {

		if(imagecursor.getCount() > 0){

			mGalleryModelList = new ArrayList<MediaModel>();

			for (int i = 0; i < imagecursor.getCount(); i++) {
				imagecursor.moveToPosition(i);
				int dataColumnIndex       = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
				MediaModel galleryModel   = new MediaModel(imagecursor.getString(dataColumnIndex).toString(), false);
				mGalleryModelList.add(galleryModel);
			}


			mImageAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, false);
			mImageGridView.setAdapter(mImageAdapter);
		}else{
			Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
		}

		mImageGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{

				GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);

				File file = new File(galleryModel.url);

				System.out.println("======position=="+position);

				System.out.println("====My New onItemLongClick ======="+mGalleryModelList.get(position).url.toString());
				System.out.println("====My Old onItemLongClick======="+file.getAbsolutePath());

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(intent);

				return true;
			}
		});

		mImageGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {


				GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);

				//	File file = new File(galleryModel.url);

				//	System.out.println("==File name is =="+file.getAbsolutePath());

				System.out.println("======position=="+position);
				System.out.println("=======My file is --====="+galleryModel.url);
				System.out.println("====My New onclick ======="+mGalleryModelList.get(position).url.toString());


				File file = new File(galleryModel.url);
				System.out.println("====My Old onclick======="+file.getAbsolutePath());


				/*
				Intent intActSelfiPost = new Intent(getActivity(),ActSelfiePost.class);
				intActSelfiPost.putExtra("from", "UploadFragmentGallery");
				intActSelfiPost.putExtra("path",galleryModel.url);
				startActivity(intActSelfiPost);*/
			}
		});

		llMainGallery.setOnTouchListener(new OnSwipeTouchListener(getActivity())
		{

			public void onSwipeLeft()
			{

				tvTab1.setSelected(false);
				tvTab2.setSelected(true);

				tvTab1.setTextColor(activity.getResources().getColor(R.color.clrUnselectedTab));
				tvTab2.setTextColor(activity.getResources().getColor(R.color.clrselectedTab));

				llMainPhoto.setVisibility(View.VISIBLE);
				llMainGallery.setVisibility(View.GONE);

				setPhoto();
			}

		});


	}

	public ArrayList<String> getSelectedImageList() {
		return mSelectedItems;
	}

	public void addItem(String item) {
		if(mImageAdapter != null){
			MediaModel model = new MediaModel(item, false);
			mGalleryModelList.add(0, model);
			mImageAdapter.notifyDataSetChanged();
		}else{
			initPhoneImages();
		}
	}





	// for the swipe view

	public class OnSwipeTouchListener implements OnTouchListener {

		private final GestureDetector gestureDetector;

		public OnSwipeTouchListener(Context ctx) {
			gestureDetector = new GestureDetector(ctx, new GestureListener());
		}

		public boolean onTouch(final View view, final MotionEvent motionEvent) {
			return gestureDetector.onTouchEvent(motionEvent);
		}

		private final class GestureListener extends SimpleOnGestureListener {

			private static final int SWIPE_THRESHOLD = 100;
			private static final int SWIPE_VELOCITY_THRESHOLD = 100;

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				boolean result = false;
				try {
					float diffY = e2.getY() - e1.getY();
					float diffX = e2.getX() - e1.getX();
					if (Math.abs(diffX) > Math.abs(diffY)) {
						if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffX > 0) {
								onSwipeRight();
							} else {
								onSwipeLeft();
							}
						}
					} else {
						if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffY > 0) {
								//   onSwipeBottom();
							} else {
								//  onSwipeTop();
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		}

		public void onSwipeRight() {
		}

		public void onSwipeLeft() {
		}

		/* public void onSwipeTop() {
	    }

	    public void onSwipeBottom() {
	    }*/
	}







	@Override
public void onStart() {
	super.onStart();

}

@Override
public void onStop() {
	super.onStop();
}




}
