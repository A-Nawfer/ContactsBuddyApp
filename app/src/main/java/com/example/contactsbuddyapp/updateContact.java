package com.example.contactsbuddyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.contactsbuddyapp.helpers.constants;
import com.example.contactsbuddyapp.helpers.dbHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class updateContact extends AppCompatActivity {

    private ImageView btnClose, btnDeleteContact;
    private CircularImageView IV_addPhoto;
    private TextView btnSaveContact;
    private EditText ET_contactName, ET_phoneNumber, ET_email;

    private dbHelper dbHelper;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri contactImageUri;

    private String contactId, contactName, contactNumber, email, contactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

        btnClose = findViewById(R.id.btnClose);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);
        btnSaveContact = findViewById(R.id.btnSaveContact);
        IV_addPhoto = findViewById(R.id.IV_addPhoto);
        ET_contactName = findViewById(R.id.ET_contactName);
        ET_phoneNumber = findViewById(R.id.ET_phoneNumber);
        ET_email = findViewById(R.id.ET_email);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        contactId = getIntent().getStringExtra("ID");

        dbHelper = new dbHelper(this);

        getContactDetails();

        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        IV_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContactImage();
            }
        });

        btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataValidation();
            }
        });
    }

    private void getContactDetails() {
        String selectQuery = "SELECT * FROM " + constants.table +
                " WHERE " + constants.contactId + "=" + contactId;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String contactId = "" + cursor.getInt(cursor.getColumnIndexOrThrow(constants.contactId));
                contactName = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactName));
                contactNumber = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactNumber));
                email = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.email));
                contactImage = "" + cursor.getString(cursor.getColumnIndexOrThrow(constants.contactImage));

                ET_contactName.setText(contactName);
                ET_phoneNumber.setText(contactNumber);
                ET_email.setText(email);

                if (contactImage.equals("") || contactImage.equals("null")) {
                    IV_addPhoto.setImageResource(R.drawable.placeholder);
                } else {
                    IV_addPhoto.setImageURI(Uri.parse(contactImage));
                }

            } while (cursor.moveToNext());
        }
    }

    private void addGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void addCameraImage() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        contactImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contactImageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void addContactImage() {
        //options to display in dialog
        String[] options = {"Take Photo", "Select Photo from Gallery"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle clicks
                        if (i == 0) {
                            //Take Photo clicked
                            if (ContextCompat.checkSelfPermission(updateContact.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(updateContact.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                //if camera permission allowed, can pick image from camera
                                addCameraImage();
                            } else {
                                //if camera permission not allowed, request permission
                                getPermissionCamera();
                            }
                        } else {
                            //Choose Photo clicked
                            if (ContextCompat.checkSelfPermission(updateContact.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                //if storage permission allowed, can pick image from galley
                                addGalleryImage();
                            } else {
                                //if storage permission not allowed, request permission
                                getPermissionStorage();
                            }
                        }
                    }
                }).show();
    }

    private void getPermissionCamera() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void getPermissionStorage() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                addCameraImage();
            } else {
                Toast.makeText(this, "Camera Permission is Required!", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGalleryImage();
            } else {
                Toast.makeText(this, "Storage Permission is Required!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dataValidation() {
        String phoneRegex = "\\d{10}";

        contactName = ET_contactName.getText().toString().trim();
        contactNumber = ET_phoneNumber.getText().toString().trim();
        email = ET_email.getText().toString().trim();

        if (TextUtils.isEmpty(contactName)) {
            Toast.makeText(this, "Contact Name cannot be blank!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(contactNumber)) {
            Toast.makeText(this, "Contact Number cannot be blank!", Toast.LENGTH_LONG).show();
            return;
        } else if (!contactNumber.matches(phoneRegex)) {
            Toast.makeText(this, "Contact Number is formatted incorrectly!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Address cannot be blank!", Toast.LENGTH_LONG).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email Address is formatted incorrectly!", Toast.LENGTH_LONG).show();
            return;
        }

        save();
    }

    private void save() {
        String timestamp = "" + System.currentTimeMillis();

        dbHelper.update(
                "" + contactId,
                "" + contactName,
                "" + contactNumber,
                "" + email,
                "" + contactImageUri,
                "" + timestamp
        );

        Toast.makeText(this, "" + contactName + " has been updated successfully!", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    private void saveImageToDirectory(String srcDir, String desDir) throws IOException {
        File src = new File(srcDir);
        File des = new File(desDir, src.getName());

        FileChannel source = null;
        FileChannel destination = null;

        try {
            if (!des.getParentFile().exists()) {
                des.mkdirs();
            }
            if (!des.exists()) {
                des.createNewFile();
            }

            source = new FileInputStream(src).getChannel();
            destination = new FileOutputStream(des).getChannel();
            destination.transferFrom(source, 0, source.size());

            contactImageUri = Uri.parse(des.getPath());

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

        } finally {
            if (source != null) {
                source.close();

            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(contactImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    contactImageUri = resultUri;
                    IV_addPhoto.setImageURI(resultUri);

                    try {
                        saveImageToDirectory("" + contactImageUri.getPath(), "" + getDir("contacts_images", MODE_PRIVATE));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(updateContact.this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete '" + contactName + "' from ContactsBuddy?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.delete(contactId);
                        Toast.makeText(updateContact.this, "" + contactName + " has been deleted successfully!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss the alert dialog
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}