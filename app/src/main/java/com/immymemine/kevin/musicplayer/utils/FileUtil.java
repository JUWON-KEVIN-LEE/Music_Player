package com.immymemine.kevin.musicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.immymemine.kevin.musicplayer.model.MusicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quf93 on 2017-10-15.
 */

public class FileUtil {
    public static final List<MusicItem> ITEMS = new ArrayList<>();

    public static List<MusicItem> readMusicList(Context context) {
        ContentResolver resolver = context.getContentResolver();
        // get uri
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED
        };
        Cursor cursor = resolver.query(uri, proj, null, null, proj[5] + " ASC");

        if(ITEMS != null)
            ITEMS.clear();
        while(cursor.moveToNext()) {
            MusicItem musicItem = new MusicItem(
                    getValue(cursor, proj[0]), getValue(cursor, proj[1]), getValue(cursor, proj[2]),
                    getValue(cursor, proj[3]), Long.parseLong(getValue(cursor, proj[4])), Long.parseLong(getValue(cursor, proj[5]))
            );
            musicItem.setMusicUri();
            musicItem.setAlbumUri();
            ITEMS.add(musicItem);
        }
        cursor.close();
        Log.d("DATA"," ====================== 준비 완료");
        return ITEMS;
    }

    private static String getValue(Cursor cursor, String name) {
        int index = cursor.getColumnIndex(name);
        return cursor.getString(index);
    }
}
