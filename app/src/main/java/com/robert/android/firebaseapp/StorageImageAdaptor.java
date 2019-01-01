package com.robert.android.firebaseapp;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

//for the recycler view
public class StorageImageAdaptor extends RecyclerView.Adapter<StorageImageAdaptor.ImageViewHolder> {


    private Context mContext;
    private List<StorageImageUpload> mUpload;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Storage").child(mAuth.getCurrentUser().getUid());
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid());


    public StorageImageAdaptor(Context context, List<StorageImageUpload> upload){
        mContext = context;
        mUpload = upload;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.storage_image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {

        StorageImageUpload currUpload = mUpload.get(position);
        holder.viewName.setText(currUpload.name);
        holder.viewType.setText(currUpload.extension);
        holder.viewSize.setText(Formatter.formatFileSize(mContext, Long.valueOf(currUpload.size)));

        //view the file thumbnail if don't has push icon base on type
        GlideApp.with(mContext).load(currUpload.thumbUrl).placeholder(currUpload.typePic()).fitCenter().centerCrop().into(holder.viewImage);

        //you can download the file in your device
        holder.viewDownload.setOnClickListener(new View.OnClickListener() {
            int poo = holder.getLayoutPosition();   //get position
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager;
                DownloadManager.Request request ;
                        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

                    request = new DownloadManager.Request(Uri.parse(mUpload.get(poo).url));
                    request.setTitle(mUpload.get(poo).name);
                    request.setDescription(null);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mUpload.get(poo).name);
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
            }
        });

        //you can delete the file from Storage dataBase and RealTime database
        holder.viewDelete.setOnClickListener(new View.OnClickListener() {
            int poo = holder.getLayoutPosition();
            @Override
            public void onClick(View v) {

                mStorage.child("thumbnails").child("thumb-" + mUpload.get(poo).name).delete();
                mStorage.child(mUpload.get(poo).name).delete();
                mRef.child(mUpload.get(poo).id).removeValue();

            }
        });

        //you can click on the file to view its image
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int poo = holder.getLayoutPosition();

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

                View viewNote = LayoutInflater.from(mContext).inflate(R.layout.storage_image_viewer, null);
                alertBuilder.setView(viewNote);
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.dismiss();
                alertDialog.show();

                Toast.makeText(mContext, "position= " + String.valueOf(poo), Toast.LENGTH_SHORT).show();

               ImageView imageView = (ImageView) viewNote.findViewById(R.id.viewer_image);

                GlideApp.with(mContext).load(mUpload.get(poo).url).thumbnail(GlideApp.with(mContext).load(mUpload.get(poo).thumbUrl)).placeholder(mUpload.get(poo).typePic()).into(imageView);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mUpload.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView viewName, viewSize, viewType, viewUploadTime;
        public ImageView viewImage, viewDownload, viewDelete;
        public RelativeLayout relativeLayout;

        public ImageViewHolder(View itemView) {
            super(itemView);

            viewImage = (ImageView) itemView.findViewById(R.id.view_image);
            viewName = (TextView) itemView.findViewById(R.id.view_name);
            viewSize = (TextView) itemView.findViewById(R.id.view_size);
            viewType = (TextView) itemView.findViewById(R.id.view_type);
            viewDownload = (ImageView) itemView.findViewById(R.id.view_download);
            viewDelete = (ImageView) itemView.findViewById(R.id.view_delete);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.container);

        }
    }
}
