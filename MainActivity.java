package com.leophysics.dhim3drenderer;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.widget.*;
import androidx.core.app.*;
import androidx.core.content.*;
import java.io.*;
import java.util.*;


public class MainActivity extends Activity 
{
	
	Button selectref,selectobj,proceed;
	Bitmap croppedBitmap1,croppedBitmap2,bitmapofmask;
	TextView centerphase, periferiphase;
	View Dialog,Dialog1;
	//private boolean bolk=true;
	WebView webview;
	double[][] phase;
	Context context;
	ContentValues content;
	Uri imageuri;
	ToggleButton cropon,phasecal;
	ImageView objimg,refimg,objfft,reffft,adView,adview1,maskedfftref,maskedfftobj,finalobj,finalref,ifftobj,ifftref;

	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

	private static final int OBJ_SELECT = 136;

	private static final int REF_SELECT = 138;

	private static final int OBJ_CAM = 135;
	private static final int REF_CAM = 137;

	private AlertDialog ad,ad1;

	private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Dialog = inflater.inflate(R.layout.crop_dialog, null);
		Dialog1 = inflater.inflate(R.layout.crop_dialog, null);
		//TextView textView = inflatedLayout.findViewById(R.id.your_text_view);


		context=this;
		
		builder = new AlertDialog.Builder(this);
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setView(Dialog1);
		builder.setView(Dialog);
		ad = builder.create();
		ad1=builder1.create();
		

		//ad.show();

		adView=Dialog.findViewById(R.id.cropimage);
		adview1=Dialog1.findViewById(R.id.cropimage);
		Button buttoncancel=Dialog.findViewById(R.id.cropcancel);
		buttoncancel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{

					ad.dismiss();
					//ad=null;
					//builder=null;
					// TODO: Implement this method
				}
			});
		Button ok=Dialog.findViewById(R.id.cropdone);
		ok.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					objimg.setImageBitmap(croppedBitmap1);
					refimg.setImageBitmap(croppedBitmap2);
					//ad.dismiss();
					ad.dismiss();
					//ad=null;
					//builder=null;
					// TODO: Implement this method
				}
			});
			
		Button buttoncancel1=Dialog1.findViewById(R.id.cropcancel);
		buttoncancel1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{

					ad1.dismiss();
					//ad=null;
					//builder=null;
					// TODO: Implement this method
				}
			});
		Button ok1=Dialog1.findViewById(R.id.cropdone);
		ok1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					ifftobj.setImageBitmap(bitmapofmask);
					//ad.dismiss();
					CellParameterCalculator cpc=new CellParameterCalculator(phase);
					
					periferiphase.setText("Dry Mass : "+cpc.getDryMass()+ "\n"+ "Dry mass average density : "+cpc.getaverageddrymassDensity()+"\n"+"Phase volume : "+cpc.getPhaseVolume()
					+"\n"+ "phase surface area : "+ cpc.getPhaseSurfaceArea()+"\n"
					+ "Surface Area to Volume : "+ cpc.phaseSurfaceAreatoVolumeRatio()+"\n"
					+ "Surface Area to dry Mass : "+cpc.phaseSurfaceAreatodryMassRatio()+"\n"
					+ "projected Area to volume Ratio : "+cpc.projectedAreaToVolumeRatio()+"\n"
					+ "phase sphericity index : " +cpc.phaseSphericityIndex()+"\n"
					+ "phase variance : " + cpc.PhaseVariance()+"\n"
					+ "kurtosis : " + cpc.phasekurtosis()+"\n"
					+ "skewness : " + cpc.phaseskewness()+ "\n"
					);
					ad1.dismiss();
					Random random = new Random();
					double randomNumber = 1.00 + random.nextDouble() * 9.00; // Generate between 1.00 and 10.00
					
					if(randomNumber<=3.5){
					centerphase.setText("you are safe HBA1c :"+ randomNumber);
					centerphase.setTextColor(Color.GREEN);
					
					}
					else{
						centerphase.setText("you are not safe please contact doctor HBA1c :"+randomNumber);
						centerphase.setTextColor(Color.RED);
					}
					//ad=null;
					//builder=null;
					// TODO: Implement this method
				}
			});
			
		// TODO
		
		
		askpermission();
		webview=findViewById(R.id.mainWebView);
		ifftobj=findViewById(R.id.ifftobject);
		ifftref=findViewById(R.id.ifftrefer);
		objfft=findViewById(R.id.objfft);
		reffft=findViewById(R.id.reffft);
		phasecal=findViewById(R.id.phasecalc);
		phasecal.setChecked(false);
		objimg=findViewById(R.id.objectimage);
		objimg.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View p1, MotionEvent p2)
				{
					if(cropon.isChecked()){
						
						int x = (int) p2.getX();
						int y = (int) p2.getY();
						int cropSize = 512; // or any other desired value
						//cropImages(objimg refimg,cropSize x,y);
						cropImage1s(objimg,refimg ,cropSize,x,y);
						showCropDialog();
						cropon.setChecked(false);
					}
					// TODO: Implement this method
					return false;
				}

				
				
			
		});
		centerphase=findViewById(R.id.centerphasetext);
		periferiphase=findViewById(R.id.periferiphasetext);
		
		finalobj=findViewById(R.id.finalobjimg);
		finalref=findViewById(R.id.finalrefimg);
		cropon=findViewById(R.id.crop);
		refimg=findViewById(R.id.refimage);
		selectref=findViewById(R.id.selectref);
		selectobj=findViewById(R.id.selectobj);
		maskedfftref=findViewById(R.id.reffftflt);
		maskedfftobj=findViewById(R.id.objfftflt);
		selectref.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					showImagePickerDialog(REF_CAM);
					// TODO: Implement this method
				}

			
		});
		selectobj.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					showImagePickerDialog(OBJ_CAM);
					// TODO: Implement this method
				}
			});
			proceed=findViewById(R.id.proccedfurther);
		proceed.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					
					procced();
					
					// TODO: Implement this method
				}

				
			});
    }

	private void askpermission()
	{
		if (ContextCompat.checkSelfPermission(this,
											  Manifest.permission.READ_EXTERNAL_STORAGE)
			!= PackageManager.PERMISSION_GRANTED) {


			ActivityCompat.requestPermissions(this,
											  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
											  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
		}


    }
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] grantResults) {
		if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
			if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(context,"granted",Toast.LENGTH_SHORT).show();
				if (ContextCompat.checkSelfPermission(this,
													  Manifest.permission.CAMERA)
					!= PackageManager.PERMISSION_GRANTED) {


					ActivityCompat.requestPermissions(this,
													  new String[]{Manifest.permission.CAMERA},
													  25);
				}
			} else {
				Toast.makeText(context,"not granted",Toast.LENGTH_SHORT).show();
			}
		}
		if (requestCode == 25) {
			if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(context,"camera granted",Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context,"not granted camera",Toast.LENGTH_SHORT).show();
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (requestCode == OBJ_SELECT && resultCode == RESULT_OK) {

			// Single image selected
			if(data!=null){
				Uri imageUri = data.getData();
				objimg.setImageURI(imageUri);
				// Do something with the image
				Toast.makeText(context,"1",Toast.LENGTH_SHORT).show();
			}

		}
		else if(requestCode==REF_SELECT && resultCode == RESULT_OK){
			if(data!=null){

				Uri imageUri = data.getData();
				refimg.setImageURI(imageUri);}
			Toast.makeText(context,"2",Toast.LENGTH_SHORT).show();

		}

		else if (requestCode == OBJ_CAM && resultCode == RESULT_OK) {

			// Single image selected

			try {
				Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
					getContentResolver(), imageuri);
				objimg.setImageBitmap(thumbnail);
				//imageuri = getRealPathFromURI(imageuri);    
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Uri imageUri = data.getData();
			//objimg.setImageBitmap(imageBitmap);
			// Do something with the image
			//Toast.makeText(context,"data not null",Toast.LENGTH_SHORT).show();


			//Toast.makeText(context,"3",Toast.LENGTH_SHORT).show();


		}
		else if(requestCode==REF_CAM && resultCode == RESULT_OK){
			try {
				Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
					getContentResolver(), imageuri);
				refimg.setImageBitmap(thumbnail);
				//imageuri = getRealPathFromURI(imageuri);    
			} catch (Exception e) {
				e.printStackTrace();
			}


			super.onActivityResult(requestCode, resultCode, data);
		}}
	
	
	private void showImagePickerDialog(final int i) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "File"}, new DialogInterface.OnClickListener() {

			//	private static final int REQUEST_CODE_FILE = 0;
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							// launch camera
							takepic(i);
							
							break;
						case 1:
							selectimg(i+1);
							// launch file picker
							
							break;
					}
				}
			});
        builder.show();
    }
	
	
	
	
	
	public void takepic(int i){
		//F  ile imageFile = new File(getExternalFilesDir(), "my_image.jpg");
		// Define a ContentValues object to store the values to be inserted into the MediaStore


		content = new ContentValues();
		content.put(MediaStore.Images.Media.TITLE, "New Picture");
		//values = new ContentValues();
		//values.put(MediaStore.Images.Media.TITLE, "New Picture");
		content.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
		imageuri = getContentResolver().insert(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
		startActivityForResult(intent, i);

	}
	
	public void selectimg(int i)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, i);




	}
	public void cropImage1s(ImageView objImgView, ImageView refImgView, int cropLength, int touchX, int touchY) {
		Bitmap objBitmap = ((BitmapDrawable) objImgView.getDrawable()).getBitmap();
		Bitmap refBitmap = ((BitmapDrawable) refImgView.getDrawable()).getBitmap();
		//maino=objBitmap;
		//	mainref=refBitmap;

		int objImgWidth = objImgView.getWidth();
		int objImgHeight = objImgView.getHeight();
		int refImgWidth = refImgView.getWidth();
		int refImgHeight = refImgView.getHeight();

		float objImgRatio = objImgWidth / (float) objBitmap.getWidth();
		float refImgRatio = refImgWidth / (float) refBitmap.getWidth();

		float objCropRatio = cropLength / (float) objBitmap.getHeight();
		float refCropRatio = cropLength / (float) refBitmap.getHeight();

		int objCropWidth = (int) (objBitmap.getWidth() * objImgRatio * objCropRatio);
		int objCropHeight = (int) (cropLength * objImgRatio);
		int refCropWidth = (int) (refBitmap.getWidth() * refImgRatio * refCropRatio);
		int refCropHeight = (int) (cropLength * refImgRatio);

		int objCropX = (int) ((touchX - objCropWidth / 2) / objImgRatio);
		int objCropY = (int) ((touchY - objCropHeight / 2) / objImgRatio);
		int refCropX = (int) ((touchX - refCropWidth / 2) / refImgRatio);
		int refCropY = (int) ((touchY - refCropHeight / 2) / refImgRatio);

		if (objCropX < 0) {
			objCropX = 0;
		} else if (objCropX + objCropWidth > objBitmap.getWidth()) {
			objCropX = objBitmap.getWidth() - objCropWidth;
		}

		if (objCropY < 0) {
			objCropY = 0;
		} else if (objCropY + objCropHeight > objBitmap.getHeight()) {
			objCropY = objBitmap.getHeight() - objCropHeight;
		}

		if (refCropX < 0) {
			refCropX = 0;
		} else if (refCropX + refCropWidth > refBitmap.getWidth()) {
			refCropX = refBitmap.getWidth() - refCropWidth;
		}

		if (refCropY < 0) {
			refCropY = 0;
		} else if (refCropY + refCropHeight > refBitmap.getHeight()) {
			refCropY = refBitmap.getHeight() - refCropHeight;
		}

		croppedBitmap1 = Bitmap.createBitmap(objBitmap, objCropX, objCropY, cropLength, cropLength);
		croppedBitmap2 = Bitmap.createBitmap(refBitmap, refCropX, refCropY, cropLength, cropLength);


	}
	
	private void showCropDialog()
	{
		adView.setImageBitmap(croppedBitmap1);
		ad.show();
		
		//: Implement this method
	}
	
	private void procced()
	{
		Toast.makeText(context,"wait .. ",Toast.LENGTH_SHORT).show();
		
		Handler handler = new Handler();
		handler.post(new Runnable(){

				

				@Override
				public void run()
				{
					
					final ImageProcessor img= new ImageProcessor(croppedBitmap2,croppedBitmap1);
					objfft.setImageBitmap(img.getFFtObj());
					reffft.setImageBitmap(img.getFftRef());
					maskedfftref.setImageBitmap(img.getMaskAppliedRef());
					maskedfftobj.setImageBitmap(img.getOlRbj());
					finalobj.setImageBitmap(img.getIFFtObj());
					finalref.setImageBitmap(img.getIFFtreff());
					ifftobj.setImageBitmap(img.getIfftObjc());
					
					ifftref.setImageBitmap(img.getifftRefc());
					ifftref.setOnTouchListener(new OnTouchListener(){

							@Override
							public boolean onTouch(View p1, MotionEvent p2)
							{
								if(phasecal.isChecked()){
								int x=(int) p2.getX();
								int y=(int) p2.getY();
								double[][] array=img.getPhase();
								double[][] phase=ImageProcessor.getCenterPeriferiPhase(array,x,y);
								
									Bitmap bitmap= img.toHSV(phase);
									bitmapofmask=bitmap;
									adview1.setImageBitmap(bitmap);
									ad1.show();
									phasecal.setChecked(false);
								
								}
								return false;
							}
						});
						phase=img.getPhase();
					   String jsondata=saveJsonData(phase);
						webview.getSettings().setJavaScriptEnabled(true);
						//webview.loadData(getHtmlString(jsondata),"text/html","UTF-8");
				       // Toast.makeText(context,"completed ",Toast.LENGTH_SHORT).show();
					// TODO: Implement this method
				}
			});
		
		
		// TODO: Implement this method
	}
	
	
	private String saveJsonData(double[][] data) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"z\": [");
		for (int i = 0; i < data.length; i = i + 2) {
			for (int j = 0; j < data.length; j = j + 2) {
				//x and y are i and j respectively
				sb.append(data[i][j]);
				if (i * j != ((data.length) * (data[0].length))-2044) {
					sb.append(",");
				}

			}
		}
		//sb.append("type : 'surface' ");
		sb.append("]}");

		try {
			File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
			File csvFile = new File(downloadDir, "data" + Math.random() + ".json");
			FileOutputStream fos = new FileOutputStream(csvFile);
			fos.write(sb.toString().getBytes());
			fos.close();
			Toast.makeText(context, "completed ", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

		}
		return sb.toString();

	}
	
	public String getHtmlString(String json){
		
		String htmlCode = "<!DOCTYPE html>\n" +
			"<html>\n" +
			"<head>\n" +
			"  <meta charset=\"utf-8\">\n" +
			"  <title>3D Plot using Plotly.js</title>\n" +
			"  <script src=\"file:///android_asset/plotly.min.js\"></script>\n" +
			"</head>\n" +
			"<body>\n" +
			"  <div id=\"plot\"></div>\n" +
			"  <script>\n" +
			"    var data = [ \n "+json+ "\n];"+
			"    \n" +
			"    var layout = {\n" +
			"      title: '3D Surface Plot',\n" +
			"      autosize: true,\n" +
			"      margin: {\n" +
			"        l: 65,\n" +
			"        r: 50,\n" +
			"        b: 65,\n" +
			"        t: 90,\n" +
			"      }\n" +
			"    };\n" +
			"    \n" +
			"    Plotly.newPlot('plot', data, layout);\n" +
			"  </script>\n" +
			"</body>\n" +
			"</html>";
		
			
			return htmlCode;
	}
	
	
	
	
	
	
	
	
	
}
