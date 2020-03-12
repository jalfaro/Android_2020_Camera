package com.julioalfaro.camaraejemplo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btnFoto;
    private ImageView foto;
    private boolean isValid;
    private Uri fileUri;
    private String APP_FOLDER = "fotos_prueba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFoto = findViewById(R.id.btnFoto);
        foto = findViewById(R.id.foto);
        btnFoto.setOnClickListener(this);
        isValid = false;

        validatePermission();
    }

    private void validatePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                isValid = true;
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }

        }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                foto.setImageURI(fileUri);
            }
        }
    }

    @Override
    public void onClick(View v) {
        File imageFile = null;
        if (v.getId() == R.id.btnFoto) {
            if (isValid) {
                Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    imageFile = createImageFile();
                } catch (IOException e) {
                    finish();
                }
                fileUri = FileProvider.getUriForFile(this,this.getPackageName() + ".fileprovider", imageFile);
                camara.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(camara, 101);
            } else {
                Toast.makeText(this, "Es necesario validar todos los permisos", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "foto_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + File.separator + APP_FOLDER );
        storageDir.mkdirs();
        File image = File.createTempFile(imageFileName,
                ".jpg",
                storageDir);
        return image;
    }

}
