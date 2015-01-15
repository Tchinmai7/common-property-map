package in.co.aarthi.locatormaps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class CameraPhotoCapture extends Activity {

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private long then;
    private int longClickDuration = 1000;
    Uri imageUri                      = null;
    public  static ImageView showImg  = null;
    CameraPhotoCapture CameraActivity = null;
    class Coordinate{
        float x;
        float y;
        public Coordinate(float x, float y){
            this.x = x;
            this.y = y;
        }
    }
    public ArrayList<Coordinate> pointsList;

    public int intrinsicHeight,intrinsicWidth,scaledHeight,scaledWidth,scaledImageOffsetX, scaledImageOffsetY,originalImageOffsetX,originalImageOffsetY;
  public  float heightRatio,widthRatio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_photo_capture);
        CameraActivity = this;

        showImg = (ImageView) findViewById(R.id.showImg);

        final Button photo = (Button) findViewById(R.id.photo);
pointsList=new ArrayList<>(10);
        showImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then = (long) System.currentTimeMillis();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then) > longClickDuration) {
            /* Implement long click behavior here */
                        pointsList.add(new Coordinate(event.getX(), event.getY()));
                        Toast.makeText(CameraPhotoCapture.this,event.getX()+""+event.getY(),Toast.LENGTH_LONG).show();
                        return false;
                    } else {

                        return true;
                    }
                }
                return true;
            }

        });
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*************************** Camera Intent Start ************************/
   // Define the file-name to save photo taken by Camera activity
                String fileName = "papermap"+ SystemClock.elapsedRealtime() +".jpg";

                // Create parameters for Intent with filename

                ContentValues values = new ContentValues();
		   values.put(MediaStore.Images.Media.TITLE, fileName);

                values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");

                // imageUri is the current activity attribute, define and save it for later usage

                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/


                // Standard Intent action that can be sent to have the camera
                // application capture an image and return it.

                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                /*************************** Camera Intent End ************************/
       }

        });
    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if ( resultCode == RESULT_OK) {

                /*********** Load Captured Image And Data Start ****************/

                String imageId = convertImageUriToFile( imageUri,CameraActivity);
        //  Create and excecute AsyncTask to load capture image
                new LoadImagesFromSDCard().execute(""+imageId);

                /*********** Load Captured Image And Data End ****************/
            } else if ( resultCode == RESULT_CANCELED) {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /************ Convert Image Uri path to physical path **************/

    public static String convertImageUriToFile ( Uri imageUri, Activity activity )  {

        Cursor cursor = null;
        int imageID = 0;

        try {

            /*********** Which columns values want to get *******/
            String [] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data
 int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //int orientation_ColumnIndex = cursor.
            //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {
 }
            else
            {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID     = cursor.getInt(columnIndex);

                    thumbID     = cursor.getInt(columnIndexThumb);

                    String Path = cursor.getString(file_ColumnIndex);

                    //String orientation =  cursor.getString(orientation_ColumnIndex);


                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return ""+imageID;
    }


    // Class with extends AsyncTask class

    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(CameraPhotoCapture.this);

        Bitmap mBitmap;

        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/

            // Progress Dialog
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }


        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;


            try {

                /**  Uri.withAppendedPath Method Description
                 * Parameters
                 *    baseUri  Uri to append path segment to
                 *    pathSegment  encoded path segment to append
                 * Returns
                 *    a new Uri based on baseUri with the given segment appended to the path
                 */

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);

                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    newBitmap = Bitmap.createScaledBitmap(bitmap,width-50,height-50,true);

                    bitmap.recycle();

                    if (newBitmap != null) {

                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                // Error fetching image, try to recover

                /********* Cancel execution of this task. **********/
                cancel(true);
            }

            return null;
        }


        protected void onPostExecute(Void unused) {

            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if(mBitmap != null)
            {
                // Seast Image to ImageView

                showImg.setImageBitmap(mBitmap);
                Drawable drawable = showImg.getDrawable();
                Rect imageBounds = drawable.getBounds();

            }

        }

    }

}
