package com.example.imagepicking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.imagepicking.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding mBinding;
    EasyImage mEasyImage;
    MediaFile mMediaFile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mEasyImage = new EasyImage.Builder(this)
                .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false)
                                .build();

        mBinding.pickImgFromCamera.setOnClickListener(v->{
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                           if(multiplePermissionsReport.areAllPermissionsGranted()){
                               mEasyImage.openCameraForImage(MainActivity.this);
                           }else{
                               Toast.makeText(MainActivity.this, "This app requires that permission in order to fulfil your request", Toast.LENGTH_SHORT).show();

                           }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.cancelPermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();
        });
        mBinding.pickImgFromGallery.setOnClickListener(v->{
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            if(multiplePermissionsReport.areAllPermissionsGranted()){
                                mEasyImage.openGallery(MainActivity.this);
                            }else{
                                Toast.makeText(MainActivity.this, "This app requires that permission in order to fulfil your request", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.cancelPermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mEasyImage.handleActivityResult(requestCode,resultCode,data,this, new EasyImage.Callbacks() {
            @Override
            public void onMediaFilesPicked(@NonNull MediaFile[] mediaFiles, @NonNull MediaSource mediaSource) {
                mMediaFile = mediaFiles[0];
                renderImageInImageView();
            }

            @Override
            public void onImagePickerError(@NonNull Throwable throwable, @NonNull MediaSource mediaSource) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(@NonNull MediaSource mediaSource) {
                Toast.makeText(MainActivity.this, "Operation cancelled by user", Toast.LENGTH_SHORT).show();
                renderImageInImageView();
            }
        });
    }
    public void renderImageInImageView(){
        if(mMediaFile==null){
            mBinding.imageArea.setImageResource(R.color.black);
        }else{
            Glide.with(this).load(mMediaFile.getFile()).into(mBinding.imageArea);
        }
    }
}