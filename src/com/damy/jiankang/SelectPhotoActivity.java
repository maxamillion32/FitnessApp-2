package com.damy.jiankang;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;


public class SelectPhotoActivity extends BaseActivity
{
	LinearLayout back_layout = null;
	int nScrWidth = 0, nScrHeight = 0;
	Button btnTakePhoto = null, btnSelImage = null, btnCancel = null;

	static Uri fileUri = null;

	public static int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
	public static int SELECT_IMAGE_ACTIVITY_REQ = 1;

	public static String szRetCode = "RET";
	public static String szRetPath = "PATH";
	public static String szRetUri = "URI";

	public static int nRetSuccess = 1;
	public static int nRetCancelled = 0;
	public static int nRetFail = -1;

	private String photo_path = "";
	private Uri photo_uri = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.photoselector);
		
		initActivity(R.id.rl_photoselect);

		initControls();
		initHandlers();
	}

	@Override
	public void finish()
	{
		super.finish();    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void initControls()
	{
		btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
		btnSelImage = (Button)findViewById(R.id.btnSelImage);
		btnCancel = (Button)findViewById(R.id.btnCancel);
	}

	public void initHandlers()
	{	
		btnTakePhoto.setOnClickListener(onClickTakePhoto);
		btnSelImage.setOnClickListener(onClickSelImage);
		btnCancel.setOnClickListener(onClickCancel);
	}

	@Override
	protected void onResume()
	{
		super.onResume();    //To change body of overridden methods use File | Settings | File Templates.

		if (photo_path.equals("") && photo_uri == null)
			return;

		Intent retIntent = new Intent();
		retIntent.putExtra(szRetCode, nRetSuccess);

		if (!photo_path.equals("") && photo_path != null)
			retIntent.putExtra(szRetPath, photo_path);

		if (photo_uri != null)
			retIntent.putExtra(szRetUri, photo_uri);

		setResult(RESULT_OK, retIntent);

		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ)
		{
			if (resultCode == RESULT_OK)
			{
				Uri photoUri = null;

				if (data == null)
					photoUri = fileUri;
				else
					photoUri = data.getData();

				try
				{
					if (photoUri != null)
					{
						String szPath = photoUri.getPath();
						if (szPath == null || szPath.equals(""))
						{
							Toast.makeText(this, "Error occurred while loading photograph", Toast.LENGTH_SHORT).show();
						}
						else
						{
							photo_path = szPath;
							photo_uri = null;
						}
					}
					else
					{
						photo_path = fileUri.getPath();
						photo_uri = null;
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					Toast.makeText(this, "Exception occurred while taking photograph", Toast.LENGTH_SHORT).show();
				}
			}
		}
		else if (requestCode == SELECT_IMAGE_ACTIVITY_REQ)
		{
			if (resultCode == RESULT_OK)
			{
				Uri selImage = data.getData();
				if (selImage != null)
				{
					photo_path = "";
					photo_uri = selImage;
				}
			}
		}
	}

	public View.OnClickListener onClickTakePhoto = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = getOutputPhotoFile();
			fileUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQ);
		}
	};

	public View.OnClickListener onClickSelImage = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_ACTIVITY_REQ);
		}
	};

	public View.OnClickListener onClickCancel = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			cancelWithData();
		}
	};

	private File getOutputPhotoFile()
	{
		File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
		if (!directory.exists())
		{
			if (!directory.mkdirs())
				return null;
		}

//		String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
		String timeStamp = "userPhoto";
		return new File(directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
	}

	private void cancelWithData()
	{
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		SelectPhotoActivity.this.finish();
	}
}
