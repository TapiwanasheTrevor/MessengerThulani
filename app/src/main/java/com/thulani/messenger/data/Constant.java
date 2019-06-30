package com.thulani.messenger.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import com.thulani.messenger.R;
import com.thulani.messenger.model.Chat;
import com.thulani.messenger.model.ChatsDetails;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.model.Group;
import com.thulani.messenger.model.GroupDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SuppressWarnings("ResourceType")
public class Constant {

    public static Resources getStrRes(Context context) {
        return context.getResources();
    }

    public static String formatTime(long time) {
        // income time
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);

        // current time
        Calendar curDate = Calendar.getInstance();
        curDate.setTimeInMillis(System.currentTimeMillis());

        SimpleDateFormat dateFormat = null;
        if (date.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
            if (date.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR)) {
                dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
            } else {
                dateFormat = new SimpleDateFormat("MMM d", Locale.US);
            }
        } else {
            dateFormat = new SimpleDateFormat("MMM yyyy", Locale.US);
        }
        return dateFormat.format(time);
    }


    public static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "erro ao recuperar a versão da API" + e.getMessage());
        }

        return f.floatValue();
    }


    private static Random rnd = new Random();

    public static boolean getBoolean() {
        return rnd.nextBoolean();
    }

    public static List<Friend> getFriendsData(Context ctx) {
        List<Friend> items = new ArrayList<>();
        String s_arr[] = ctx.getResources().getStringArray(R.array.people_names);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.people_photos);
        for (int i = 0; i < s_arr.length; i++) {
            Friend fr = new Friend(i, s_arr[i], "https://api.adorable.io/avatars/285/abott@adorable.png");
            items.add(fr);
        }
        return items;
    }

    private static ArrayList<Friend> friendSubList(Context ctx, int start, int end) {
        ArrayList<Friend> friends = new ArrayList<>();
        friends.addAll(getFriendsData(ctx));
        ArrayList<Friend> friends_ = new ArrayList<>();
       /* for (int i = start; i <= end; i++) {
            friends_.add(friends.get(i));
        }*/
        return friends_;
    }

}
