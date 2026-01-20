package com.policyboss.policybosspro.view.raiseTicketDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImageContractOptions;
import com.policyboss.policybosspro.BaseActivity;
import com.policyboss.policybosspro.BaseJavaActivity;

import com.policyboss.policybosspro.R;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.oldWayApi.controller.zoho.ZohoController;
import com.policyboss.policybosspro.core.response.APIResponse;
import com.policyboss.policybosspro.core.response.raiseTicket.RaiseTicketWebDocResponse;
import com.policyboss.policybosspro.utility.Utility;
import com.policyboss.policybosspro.utils.Constant;
import com.policyboss.policybosspro.utils.FileUtilNew;

import java.io.File;

import okhttp3.MultipartBody;

public class RaiseTicketDialogActivity extends BaseJavaActivity implements BaseJavaActivity.PopUpListener, IResponseSubcriber {


    WebView webView;
    TextView txtTitle;
    ImageView ivCross;
    String url = "";
    // region Camera Permission
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 1800;
    private int PICK_PDF_REQUEST = 1805;

    String[] perms = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.READ_MEDIA_IMAGES"

    };
    //endregion

    // region Camera Declaration

    File Docfile;
    File file;
    Uri imageUri;
    private Uri cropImageUri;
    //    InputStream inputStream;
//    ExifInterface ei;
//    Bitmap bitmapPhoto = null;
    private String PHOTO_File = "";
    MultipartBody.Part part;
    //HashMap<String, String> body;


    android.app.AlertDialog alertDialog;
    //endregion

    ActivityResultLauncher<String> galleryLauncher;
    ActivityResultLauncher<Uri> cameraLauncher;

    ActivityResultLauncher<String> pdfLauncher; // For PDF
    private ActivityResultLauncher<CropImageContractOptions> cropImageLauncher; // For Cropping

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_ticket_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFinishOnTouchOutside(false);
        if (getIntent().getExtras() != null) {

            url = getIntent().getStringExtra("URL");

        }

        init();


        settingWebview(webView, url);

        // region  Camera and Gallery Launcher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result ->  {

//            Intent intent = new Intent(RaiseTicketDialogActivity.this.getApplicationContext(), UcropperActivity.class);
//
//            intent.putExtra("SendImageData",result.toString());
//
//            startActivityForResult(intent, SELECT_PICTURE);

            if (result != null) {
                // Start built-in crop activity
                startCrop(result);
            }
        });

     

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                startCrop(imageUri); // Proceed to crop
            }
        });
        //endregion
    }
    private void init()
    {
        webView = findViewById(R.id.webView);
        txtTitle = findViewById(R.id.txtTitle);
        ivCross = findViewById(R.id.ivCross);

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                RaiseTicketDialogActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        galleryLauncher.unregister();
        cameraLauncher.unregister();

    }
    private void settingWebview(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setLightTouchEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        String googleDocs = "https://docs.google.com/viewer?url=";
                        webView.loadUrl(googleDocs + url);
                    }
                }*/
                return false;
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);

        webView.addJavascriptInterface(new MyJavaScriptInterface(RaiseTicketDialogActivity.this), "Android");

        Log.d("URL", url);
        if (url.endsWith(".pdf")) {

            webView.loadUrl("https://docs.google.com/viewer?url=" + url);
            //webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
        } else {
            webView.loadUrl(url);
        }
    }


    public class MyJavaScriptInterface {
        Context mContext;
        String data;

        MyJavaScriptInterface(Context ctx) {
            this.mContext = ctx;
        }


        // region Raise Ticket
        @JavascriptInterface
        public void Upload_doc(String randomID) {

            galleryCamPopUp( randomID);
        }

        @JavascriptInterface
        public void Upload_doc_view(String randomID) {


            galleryCamPopUp( randomID);


        }

        // endregion



    }


    // region Camera & Gallery Popup For Raise Ticket

//    public void galleryCamPopUp(String randomID) {
//
//        PHOTO_File = "";
//        PHOTO_File = "fm_file" + "_" + randomID;
//        Log.i("RAISE_TICKET Uploding", PHOTO_File);
//
//        if (!checkPermission()) {
//
//            if (checkRationalePermission()) {
//                //Show Information about why you need the permission
//                requestPermission();
//
//            } else {
//                //Previously Permission Request was cancelled with 'Dont Ask Again',
//                // Redirect to Settings after showing Information about why you need the permission
//
//                //  permissionAlert(navigationView,"Need Call Permission","This app needs Call permission.");
//                openPopUp(webView, "Need  Permission", "This app needs all permissions.", "GRANT", true);
//
//
//            }
//        } else {
//
//            showCamerGalleryPopUp();
//        }
//    }

    private void startCropImageActivity(Uri imageUri) {
//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMultiTouchEnabled(true)
//                .start(this);
    }

    // region Camera & Gallery Popup For Raise Ticket

    public void galleryCamPopUp(String randomID) {
        PHOTO_File = "";
        PHOTO_File = "fm_file" + "_" + randomID;
        Log.i("RAISE_TICKET Uploding", PHOTO_File);

        // ✅ FIX: REMOVED the "checkPermission" block here.
        // We open the dialog immediately. We only check permission inside the "Camera" button.
        showCamerGalleryPopUp();
    }

    // ✅ FIX: New clean method to check Camera Permission ONLY
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    // ✅ FIX: New clean method to Request Camera Permission ONLY
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                Constant.PERMISSION_CAMERA_STORAGE_CONSTANT
        );
    }

    // region permission
    private boolean checkPermission() {

        int camera = ActivityCompat.checkSelfPermission(getApplicationContext(), perms[0]);

        int WRITE_EXTERNAL = ActivityCompat.checkSelfPermission(getApplicationContext(), perms[1]);
        int READ_EXTERNAL = ActivityCompat.checkSelfPermission(getApplicationContext(), perms[2]);
        int READ_MEDIA_IMAGE = ActivityCompat.checkSelfPermission(getApplicationContext(), perms[3]);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            return camera == PackageManager.PERMISSION_GRANTED

                    && READ_MEDIA_IMAGE == PackageManager.PERMISSION_GRANTED;

        }
        else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return camera == PackageManager.PERMISSION_GRANTED

                    && READ_EXTERNAL == PackageManager.PERMISSION_GRANTED;
        }else{
            return camera == PackageManager.PERMISSION_GRANTED
                    &&  WRITE_EXTERNAL == PackageManager.PERMISSION_GRANTED
                    && READ_EXTERNAL == PackageManager.PERMISSION_GRANTED;

        }
    }

    private boolean checkRationalePermission() {

        boolean camera = ActivityCompat.shouldShowRequestPermissionRationale(RaiseTicketDialogActivity.this, perms[0]);

        boolean write_external = ActivityCompat.shouldShowRequestPermissionRationale(RaiseTicketDialogActivity.this, perms[1]);
        boolean read_external = ActivityCompat.shouldShowRequestPermissionRationale(RaiseTicketDialogActivity.this, perms[2]);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return  camera ||  read_external;
        }else{
            return  camera ||write_external   || read_external;

        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, perms, Constant.PERMISSION_CAMERA_STORAGE_CONSTANT);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case Constant.PERMISSION_CAMERA_STORAGE_CONSTANT:
//                if (grantResults.length > 0) {
//
//                    //boolean writeExternal = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//
//                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writeExternal = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    boolean readExternal = grantResults[2] == PackageManager.PERMISSION_GRANTED;
//                    boolean minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
//
//                    if (camera && (writeExternal || minSdk29) && readExternal) {
//
//                        showCamerGalleryPopUp();
//
//                    }
//                }
//                break;
//
//
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // ✅ FIX: Simplified logic. Only check if Camera is granted.
        if (requestCode == Constant.PERMISSION_CAMERA_STORAGE_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
                if(alertDialog != null && alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //endregion

    // region Camera Dialog
//    private void showCamerGalleryPopUp() {
//
//        if (alertDialog != null && alertDialog.isShowing()) {
//
//            return;
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
//
//        LinearLayout lyCamera, lyGallery, lyPdf;
//        LayoutInflater inflater = this.getLayoutInflater();
//
//        final View dialogView = inflater.inflate(R.layout.layout_cam_gallery_pdf, null);
//
//        builder.setView(dialogView);
//        alertDialog = builder.create();
//        // set the custom dialog components - text, image and button
//        lyCamera = (LinearLayout) dialogView.findViewById(R.id.lyCamera);
//        lyGallery = (LinearLayout) dialogView.findViewById(R.id.lyGallery);
//        lyPdf = (LinearLayout) dialogView.findViewById(R.id.lyPdf);
//
//        lyCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchCamera();
//                alertDialog.dismiss();
//
//            }
//        });
//
//        lyGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//                alertDialog.dismiss();
//
//            }
//        });
//        lyPdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFileChooser();
//                alertDialog.dismiss();
//
//            }
//        });
//        alertDialog.setCancelable(true);
//        alertDialog.show();
//        //  alertDialog.getWindow().setLayout(900, 600);
//
//        // for user define height and width..
//    }


    // region Camera Dialog
    private void showCamerGalleryPopUp() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);

        LinearLayout lyCamera, lyGallery, lyPdf;
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_cam_gallery_pdf, null);

        builder.setView(dialogView);
        alertDialog = builder.create();

        lyCamera = (LinearLayout) dialogView.findViewById(R.id.lyCamera);
        lyGallery = (LinearLayout) dialogView.findViewById(R.id.lyGallery);
        lyPdf = (LinearLayout) dialogView.findViewById(R.id.lyPdf);

        // 1. CAMERA - Check Permission HERE
        lyCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasCameraPermission()){
                    launchCamera();
                    alertDialog.dismiss();
                } else {
                    requestCameraPermission();
                }
            }
        });

        // 2. GALLERY - No Permission Needed (System Picker)
        lyGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                alertDialog.dismiss();
            }
        });

        // 3. PDF - No Permission Needed (System Picker)
        lyPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();
    }
    private void showFileChooser() {
        // ✅ FIX: Use the modern launcher for PDF
        try {
            pdfLauncher.launch("application/pdf");
        } catch (Exception e){
            Toast.makeText(this, "Unable to open file picker", Toast.LENGTH_SHORT).show();
        }
    }


    private void launchCamera() {
        String FileName = PHOTO_File;
        Docfile = createImageFile(FileName);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            imageUri = Uri.fromFile(Docfile);
        } else {
            imageUri = FileProvider.getUriForFile(RaiseTicketDialogActivity.this,
                    getString(R.string.file_provider_authority), Docfile);
        }
        cameraLauncher.launch(imageUri);
    }

    private void openGallery() {
        // ✅ FIX: Use the modern launcher for Gallery
        galleryLauncher.launch("image/*");
    }

    //endregion


    //region Base PopUp EventFor Handling the Default Setting


    @Override
    public void onPositiveButtonClick(Dialog dialog, View view) {

        dialog.cancel();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, Constant.REQUEST_PERMISSION_SETTING);
    }

    @Override
    public void onCancelButtonClick(Dialog dialog, View view) {
        dialog.cancel();
    }

    //endregion


    public String getFileFromDownload(Context context, Uri uri) {
        final String id = DocumentsContract.getDocumentId(uri);

        if (id != null && id.startsWith("raw:")) {
            return id.substring(4);
        }

        String[] contentUriPrefixesToTry = new String[]{
                "content://downloads/public_downloads",
                "content://downloads/my_downloads",
                "content://downloads/all_downloads"
        };

        for (String contentUriPrefix : contentUriPrefixesToTry) {
            Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
            try {
                String path = FileUtilNew.getDataColumn(context, contentUri, null, null);
                if (path != null) {
                    return path;
                }
            } catch (Exception e) {
            }
        }

        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
        String fileName = FileUtilNew.getFileName(context, uri);
        File cacheDir = FileUtilNew.getDocumentCacheDir(context);
        File file = FileUtilNew.generateFileName(fileName, cacheDir);
        String destinationPath = null;
        if (file != null) {
            destinationPath = file.getAbsolutePath();
            FileUtilNew.saveFileFromUri(context, uri, destinationPath);
        }

        return destinationPath;
    }

    private void startCrop(Uri imageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("outputX", 1080);  // Set the desired width
            cropIntent.putExtra("outputY", 1080);  // Set the desired height
            cropIntent.putExtra("aspectX", 1);     // Set aspect ratio
            cropIntent.putExtra("aspectY", 1);     // Set aspect ratio
            cropIntent.putExtra("scale", true);    // Allow scaling
            cropIntent.putExtra("return-data", false);
            // Provide output URI where the cropped image will be saved
            Uri outputUri = Uri.fromFile(new File(getExternalCacheDir(), "cropped_image.jpg"));
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            // Start the crop activity
            startActivityForResult(cropIntent, Constant.CROP_IMAGE_REQUEST_CODE);

        } catch (Exception e) {
            // Handle error if the crop intent is not supported
            showAlert("Your device does not support cropping");
        }
    }
    private void handleCropImage( Uri crop_uri){

        try {

            Bitmap mphoto = null;
            try {
                //  mphoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), cropImageUri);
                mphoto = getBitmapFromContentResolver(crop_uri);


            } catch (Exception e) {
                e.printStackTrace();
            }
            showDialog();


            file = saveImageToStorage(mphoto, PHOTO_File);
            // setProfilePhoto(mphoto);

            part = Utility.getMultipartImage(file, "doc_type");

            new ZohoController(this).uploadRaiseTicketDocWeb(part, this);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    // region Event
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Only For Pdf
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            final Uri uri = data.getData();
            // Get the File path from the Uri
            try {


                String path = FileUtilNew.getPath(this, uri);

                if (!path.contains(".")) {

                    showAlert("Only file type allowed to be uploaded are\n(Image,Docs,PDF,Excel & Text.)");
                    return;
                }
                String extension = path.substring(path.lastIndexOf(".")).toLowerCase();
                if (extension.contains("pdf") || extension.contains("xls") || extension.contains("xlsx") || extension.contains("txt") || extension.contains("doc")
                        || extension.contains("docs") || extension.contains("jpeg") || extension.contains("jpg") || extension.contains("png")) {


                    File pdfFile = new File(path);
                    if (pdfFile.exists()) {
                        showDialog();


                        part = Utility.getMultipartPdf(pdfFile, PHOTO_File, "doc_type");
                        new ZohoController(this).uploadRaiseTicketDocWeb(part, this);

                    } else {
                        showAlert("Select file from File Manager.");
                    }
                } else {

                    showAlert("Only file type allowed to be uploaded are\n(Image,Docs,PDF,Excel & Text.)");
                }


            } catch (Exception ex) {

                showAlert("Select file from File Manager.");
            }
        }

        else {

            // Below For Cropping The Camera Image

//            if(requestCode == CAMERA_REQUEST && resultCode ==101 ){
//
//                String result = data.getStringExtra("CROP");
//                Uri crop_uri = data.getData();
//
//                if(result!= null){
//                    crop_uri = Uri.parse(result);
//
//                }
//
//
//                handleCropImage(crop_uri);
//
//
//
//            }
//            else if(requestCode== SELECT_PICTURE && resultCode ==101 ){
//
//                String result = data.getStringExtra("CROP");
//                Uri crop_uri = data.getData();
//
//                if(result!= null){
//                    crop_uri = Uri.parse(result);
//                }
//
//                handleCropImage(crop_uri);
//
//
//            }

            if (requestCode == Constant.CROP_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
                // Handle cropped image
                Uri croppedImageUri = Uri.fromFile(new File(getExternalCacheDir(), "cropped_image.jpg"));
                handleCropImage(croppedImageUri);  // Handle the cropped image as needed
            }

        }



    }


    @Override
    public void OnSuccess(APIResponse response, String message) {


        cancelDialog();
        //RaiseTicketWebDocResponse
        if (response instanceof RaiseTicketWebDocResponse) {
            if (response.getStatusNo() == 0) {


                Toast.makeText(RaiseTicketDialogActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                String jsonResponse = ((RaiseTicketWebDocResponse) response).getMasterData().getFile_name() + "|" +
                        ((RaiseTicketWebDocResponse) response).getMasterData().getFile_path();
                Log.i("RAISE_TICKET RESPONSE", jsonResponse);


                ////////////
                webView.evaluateJavascript("javascript: " +
                        "uploadImagePath(\"" + jsonResponse + "\")", null);


            } else {
                Toast.makeText(RaiseTicketDialogActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }



    @Override
    public void OnFailure(Throwable t) {

        cancelDialog();
        Toast.makeText(RaiseTicketDialogActivity.this, "Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    //endregion

    // endregion
}