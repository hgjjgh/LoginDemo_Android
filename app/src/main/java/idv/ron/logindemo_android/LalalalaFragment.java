package idv.ron.logindemo_android;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;



import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

public class LalalalaFragment extends Fragment {
    private Activity activity;
    private EditText edUserName,edPassword,edName;
    private Button btSumit,btTakePicture,btPickPicture;
    private ImageView ivPhoto;
    private Uri contentUri;
    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    private final static String TAG = "TAG_LalalalaFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lalalala, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        edUserName = view.findViewById(R.id.edUserName);
        edPassword = view.findViewById(R.id.edPassword);
        edName = view.findViewById(R.id.edName);
        btSumit = view.findViewById(R.id.btSumit);
        btPickPicture = view.findViewById(R.id.btPickPicture);
        btTakePicture = view.findViewById(R.id.btTakePicture);
        ivPhoto = view.findViewById(R.id.ivPhoto);


        btSumit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edUserName.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String name = edName.getText().toString().trim();
                if (name.length() <= 0 || password.length() <= 0 || userName.length() <= 0) {
                    Toast.makeText(activity, "Input To Something Idiot", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "LoginServlet";
                    User user = new User(0, userName, password, name);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "userInsert");
                    jsonObject.addProperty("user", new Gson().toJson(user));

                    if (image != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                    }

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Toast.makeText(activity, R.string.textInsertFail,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, R.string.textInsertSuccess,Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, R.string.textNoNetwork,Toast.LENGTH_SHORT).show();
                }
                navController.popBackStack();
            }
        });

        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Toast.makeText(activity,"No Camera App",Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btPickPicture = view.findViewById(R.id.btPickPicture);
        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    Uri uri = intent.getData();
                    Bitmap bitmap = null;
                    if (uri != null) {
                        try {
                            bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(uri));
                            ivPhoto.setImageBitmap(bitmap);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            image = out.toByteArray();
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    if (bitmap != null) {
                        ivPhoto.setImageBitmap(bitmap);
                    } else {
                        ivPhoto.setImageResource(R.drawable.no_image);
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(sourceImageUri, "image/*");
        // 設定要截圖
        intent.putExtra("crop", "true");
        // 設定截圖框大小，0代表user任意調整大小
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 0);
        intent.putExtra("outputY", 0);
        // 是否保持原圖比例
        intent.putExtra("scale", true);
        // 設定截圖後圖片位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 設定是否要回傳值
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 開啟截圖activity
            startActivityForResult(intent, REQ_CROP_PICTURE);
        } else {
            Toast.makeText(activity,"NoImageCropAppFound",
                    Toast.LENGTH_SHORT).show();
        }
    }

}



