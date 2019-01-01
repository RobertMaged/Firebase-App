package com.robert.android.firebaseapp;

public class StorageImageUpload {

    public String id, name, uploadTime, size, extension, url, thumbUrl;

    //shape of data in RealTime DataBase

    public StorageImageUpload(String id, String name, String uploadTime, String size, String extension, String url, String thumbUrl){
        this.id = id;
        this.name = name;
        this.uploadTime = uploadTime;
        this.size = size;
        this.extension = extension;
        this.url = url;
        this.thumbUrl = thumbUrl;
    }

    public StorageImageUpload(){

    }


    //know file category
    public String getType(){
        String type = "unknown";
        switch ( extension ){
            case "JPG": type = "image"; break;
            case "JPEG": type = "image"; break;
            case "PNG": type = "image"; break;
            case "MP3": type = "audio"; break;
            case "WAV": type = "audio"; break;
            case "AMR": type = "audio"; break;
            case "MP4": type = "video"; break;
            case "ZIP": type = "winrar"; break;
            case "RAR": type = "winrar"; break;
            case "DOC": type = "word"; break;
            case "DOCX": type = "word"; break;
            case "PPT": type = "power point"; break;
            case "PPTX": type = "power point"; break;
            case "MDB": type = "access"; break;
            case "ACCDE": type = "access"; break;
            case "XLS": type = "excel"; break;
            case "XLSX": type = "excel"; break;
            case "PDF": type = "pdf"; break;
            case "APK": type = "apk"; break;
        }
        return type;
    }


    //based on file category push icon for the view to show
    public int typePic(){
        int imageId = R.drawable.unknown_ic;
        switch ( getType() ){
            case "image": imageId = R.drawable.image_ic; break;
            case "audio": imageId = R.drawable.mp3_ic; break;
            case "video": imageId = R.drawable.mp4_ic; break;
            case "winrar": imageId = R.drawable.zip_ic; break;
            case "word": imageId = R.drawable.doc_ic; break;
            case "power point": imageId = R.drawable.ppt_ic; break;
            case "access": imageId = R.drawable.access_ic; break;
            case "excel": imageId = R.drawable.excel_ic; break;
            case "pdf": imageId = R.drawable.pdf_ic; break;
            case "apk": imageId = R.drawable.apk_ic; break;
        }
        return imageId;
    }

}
