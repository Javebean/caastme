package com.example.jake.caastme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jake on 2016/12/3.
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     * @param shareEntity
     */
    public void add(ShareEntity shareEntity) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO share_entity VALUES(null, ?, ?, ?)", new Object[]{shareEntity.getFavicon(), shareEntity.getTitle(), shareEntity.getUrl()});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     * @param shareEntity
     */
    public void updateAge(ShareEntity shareEntity) {
        ContentValues cv = new ContentValues();
        cv.put("title", shareEntity.getTitle());
        db.update("share_entity", cv, "title = ?", new String[]{shareEntity.getTitle()});
    }

    /**
     * delete old person
     * @param shareEntity
     */
    public void deleteOldPerson(ShareEntity shareEntity) {
        db.delete("share_entity", "age >= ?", new String[]{String.valueOf(shareEntity.getTitle())});
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<ShareEntity> query() {
        ArrayList<ShareEntity> persons = new ArrayList<ShareEntity>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
           /* Person person = new Person();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.age = c.getInt(c.getColumnIndex("age"));
            person.info = c.getString(c.getColumnIndex("info"));
            persons.add(person);*/
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM share_entity", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}