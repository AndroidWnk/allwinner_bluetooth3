package com.etrans.bluetooth.db2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class DataOperation {

    private static DataOperation instance;

    private SQLiteDatabase db;
    private Context mContext;


    private DataOperation() {

    }

    public static DataOperation getInstance() {
        synchronized (DataOperation.class) {
            if (instance == null) {
                instance = new DataOperation();
            }
            return instance;
        }
    }

    public void init(Context context) {
        mContext = context;
        db = new DBOpenHelper(mContext).getWritableDatabase();
    }

    //增,删,改,查

    /**
     * 清除所有数据
     */
    public void clean() {
        String sql = "SELECT count(" + "ID" + ") FROM " + DBOpenHelper.getPhoneBookName();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor == null)
            return;
        int cnt = cursor.getCount();
        cursor.close();
        if (cnt <= 0)
            return;

        db.execSQL("DELETE FROM " + DBOpenHelper.getPhoneBookName() + ";");
//        db.execSQL("update sqlite_sequence set seq=0 where name='" + DBOpenHelper.getPhoneBookName() + "';");
    }

    /**
     * 重置联系人数据库
     *
     * @param params
     */
    public synchronized void reset(Map<String, String> params) {
        clean();

        if (params == null)
            return;
        if (params.size() == 0)
            return;
        insertContacts(params);
    }


    /**
     * 插入联系人
     *
     * @param params
     * @return
     */
    public long insertContacts(Map<String, String> params) {
        long flag;
        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            values.put(entry.getKey(), entry.getValue());
        }
        flag = db.insert(DBOpenHelper.getPhoneBookName(), null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return flag;
    }

    /**
     * 更新联系人
     *
     * @param params
     * @param name
     * @param num
     * @return
     */
    public long updateContacts(Map<String, String> params, String id, String name, String num) {
        long flag;
        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            values.put(entry.getKey(), entry.getValue());
        }
        String[] args = {id, name, num};
        flag = db.update(DBOpenHelper.getPhoneBookName(), values, "ID=? AND NAME=? AND NUM=?", args);
        db.setTransactionSuccessful();
        db.endTransaction();
        return flag;
    }

    /**
     * 查询联系人
     *
     * @param contactName
     * @param contactPhoneNum
     * @return
     */
    public ContactBean queryContact(String contactName, String contactPhoneNum) {
        String sql = "SELECT * FROM " + DBOpenHelper.getPhoneBookName() + " WHERE NAME='" + contactName
                + "' AND NUM='" + contactPhoneNum + "'";
        Cursor cursor = db.rawQuery(sql, null);
        ContactBean ContactBean = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean = new ContactBean();
                ContactBean.setName(cursor.getString(1));
                ContactBean.setMobilePhone(cursor.getString(2));
            }
            cursor.close();
        }
        return ContactBean;
    }

    /**
     * 通过姓名查询联系人
     *
     * @param contactName
     * @return
     */
    public List<ContactBean> queryContactByName(String contactName) {
        List<ContactBean> ContactBeanList = new ArrayList<ContactBean>();
        ContactBean ContactBean = null;
//        String sql = "SELECT * FROM " + DBOpenHelper.getPhoneBookName() + " WHERE NAME ='" + contactName + "'";
//        Cursor cursor = db.rawQuery(sql, null);
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(), new String[]{"ID,NAME,NUM,INITIAL_KEY,PY_KEY"}, "NAME like ?", new String[]{"%" + contactName + "%"}, null, null, "PY_KEY asc");
        if (cursor == null)
            return null;

        ContactBeanList = getData(cursor);
        cursor.close();

        return ContactBeanList;
    }

    /**
     * 通过手机号查询联系人
     *
     * @param contactPhoneNum
     * @return
     */
    public List<ContactBean> queryContactByNum(String contactPhoneNum) {
        List<ContactBean> ContactBeanList = new ArrayList<ContactBean>();
        ContactBean ContactBean = null;
//        String sql = "SELECT * FROM " + DBOpenHelper.getPhoneBookName() + " WHERE NUM ='" + contactPhoneNum + "'";
//        Cursor cursor = db.rawQuery(sql, null);
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(), null, "NUM like ?", new String[]{"%" + contactPhoneNum + "%"}, null, null, "PY_KEY asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean = new ContactBean();
                ContactBean.setName(cursor.getString(1));
                ContactBean.setMobilePhone(cursor.getString(2));
                ContactBeanList.add(ContactBean);
            }
            cursor.close();
        }
        return ContactBeanList;
    }

    /**
     * 根据首字母快速查询一个联系人
     *
     * @param initial
     * @return
     */
    public ContactBean queryOneByInitial(String initial) {
        ContactBean data = null;
        String queryKey = initial.toUpperCase();
        StringBuffer str = new StringBuffer();
        str.append("INITIAL_KEY");
        str.append(" like '");
        str.append(queryKey);
        str.append("%' or ");
        str.append("INITIAL_KEY");
        str.append(" like '% ");
        str.append(queryKey);
        str.append("%'");

        String selection = str.toString();
        Cursor cursor = db.rawQuery(selection, null);
        if (cursor == null) {
            return null;
        } else {
            data = getDataOnlyOne(cursor);
        }
        cursor.close();

        return data;
    }

    /**
     * 根据首字母查询联系人
     *
     * @param initial
     * @return
     */
    public List<ContactBean> queryByInitialT9(String initial) {
        String queryKey = initial;
        List<ContactBean> array = null;
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(), new String[]{"ID,NAME,NUM,INITIAL_KEY,PY_KEY"},
                "INITIAL_KEY_T9 like ?", new String[]{"%" + queryKey + "%"}, null, null, "PY_KEY asc");
        if (cursor == null)
            return null;

        array = getData(cursor);
        cursor.close();

        return array;
    }

    /**
     * 根据拼音快速查询一个联系人
     *
     * @param
     * @return
     */
    public ContactBean queryOneByPY(String py) {
        String queryKey = py.toUpperCase();

        StringBuffer str = new StringBuffer();
        str.append("PY_KEY");
        str.append(" like '");
        str.append(queryKey);
        str.append("%' or ");
        str.append("PY_KEY");
        str.append(" like '% ");
        str.append(queryKey);
        str.append("%'");

        String selection = str.toString();
        Cursor cursor = db.rawQuery(selection, null);

        if (cursor == null)
            return null;

        ContactBean data = getDataOnlyOne(cursor);

        return data;
    }

    public List<ContactBean> queryByPyT9(String py) {
        String queryKey = py;
        List<ContactBean> array = null;
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(), null, "PY_KEY_T9 like ?", new String[]{"%" + queryKey + "%"}, null, null, null);
        if (cursor == null)
            return null;

        array = getData(cursor);

        return array;
    }

    public ContactBean queryOneByPyT9(String str) {
        String key = new String(str);

        //String selection = "PY_KEY"_T9 + " like '%" + key + "%'";
        StringBuffer selection = new StringBuffer();
        selection.append("PY_KEY");
        selection.append(" like '");
        selection.append(key);
        selection.append("%' or ");
        selection.append("PY_KEY");
        selection.append(" like '% ");
        selection.append(key);
        selection.append("%'");
        Cursor cursor = db.rawQuery(selection.toString(), null);

        if (cursor == null)
            return null;
        ContactBean data = getDataOnlyOne(cursor);

        return data;
    }

    public List<ContactBean> queryByPy(String queryKey) {
        List<ContactBean> array = null;
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(), null, "PY_KEY like ?", new String[]{"%" + queryKey + "%"}, null, null, null);

        if (cursor == null)
            return null;
        array = getData(cursor);

        return array;
    }


    public ContactBean queryOneByInitialT9(String str) {
        String key = new String(str);

        //String selection = "INITIAL_KEY"_T9 + " like '%" + key + "%'";
        StringBuffer selection = new StringBuffer();
        selection.append("INITIAL_KEY");
        selection.append(" like '");
        selection.append(key);
        selection.append("%' or ");
        selection.append("INITIAL_KEY");
        selection.append(" like '% ");
        selection.append(key);
        selection.append("%'");
        Cursor cursor = db.rawQuery(selection.toString(), null);

        if (cursor == null)
            return null;
        ContactBean array = getDataOnlyOne(cursor);
        cursor.close();
        return array;
    }


    public List<ContactBean> queryByInitial(String str) {
        String key = new String(str);
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(), null, "INITIAL_KEY like ?"
                , new String[]{"%" + key + "%"}, null, null, null);

        if (cursor == null)
            return null;
        List<ContactBean> array = getData(cursor);
        cursor.close();
        return array;
    }

    public List<ContactBean> getAllContacts() {
        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(),
                new String[]{"ID,NAME,NUM,INITIAL_KEY,PY_KEY"}, null, null, null, null, "PY_KEY asc");
        if (cursor == null)
            return null;
        List<ContactBean> array = getData(cursor);

        cursor.close();
        return array;

    }


    private List<ContactBean> getData(Cursor cursor) {
        List<ContactBean> array = new ArrayList<ContactBean>();
        while (cursor.moveToNext()) {
            ContactBean data = new ContactBean();
            data.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            data.setName(cursor.getString(cursor.getColumnIndex("NAME")));
            data.setMobilePhone(cursor.getString(cursor.getColumnIndex("NUM")));
            data.setSortKey(getSortKey(cursor.getString(cursor.getColumnIndex("INITIAL_KEY"))));
            array.add(data);
        }
        return array;
    }


    private ContactBean getDataOnlyOne(Cursor cursor) {

        if (cursor.moveToNext()) {
            ContactBean data = new ContactBean();
            data.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            data.setName(cursor.getString(cursor.getColumnIndex("NAME")));
            data.setMobilePhone(cursor.getString(cursor.getColumnIndex("NUM")));
            data.setSortKey(getSortKey(cursor.getString(cursor.getColumnIndex("INITIAL_KEY"))));
            return data;

        }
        return null;
    }


    public ContactBean qQueryByPhoneNum(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        phoneNumber.replaceAll(" ", "");

        StringBuilder str = new StringBuilder();

        str.append("NUM");
        str.append(" like '%");
        str.append(phoneNumber);
        str.append("%'");
        Cursor cursor = db.rawQuery(str.toString(), null);
//        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(),
//                new String[]{"ID", "NAME", "NUM", PHOTO_ID, ID, "INITIAL_KEY"}, str.toString(), null, null, null, null);

        if (cursor == null)
            return null;
        ContactBean array = getDataOnlyOne(cursor);

        cursor.close();
        return array;
    }


    public ContactBean queryCallInfoByPhoneNum(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        phoneNumber.replaceAll(" ", "");

        StringBuilder str = new StringBuilder();

        str.append("NUM");
        str.append(" ='");
        str.append(phoneNumber);
        str.append("'");
        Cursor cursor = db.rawQuery(str.toString(), null);
//        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(),
//                new String[]{"ID", "NAME", "NUM", PHOTO_ID, ID, "INITIAL_KEY"}, str.toString(), null, null, null, null);

        if (cursor == null)
            return null;
        ContactBean array = getDataOnlyOne(cursor);

        cursor.close();
        return array;
    }


    public List<ContactBean> queryByPhoneNum(String phoneNumber, boolean hasSpace) {
        StringBuilder str = new StringBuilder();

        str.append("NUM");
        str.append(" like '%");
        str.append(phoneNumber);
        str.append("%'");
        Cursor cursor = db.rawQuery(str.toString(), null);
//        Cursor cursor = db.query(DBOpenHelper.getPhoneBookName(),
//                new String[]{"ID", "NAME", "NUM", PHOTO_ID, ID, "INITIAL_KEY"}, str.toString(), null, null, null, null);

        if (cursor == null)
            return null;
        List<ContactBean> array = getData(cursor);

        cursor.close();
        return array;
    }

    public void deleteById(int id) {
        StringBuffer str = new StringBuffer();
        str.append("ID");
        str.append(" = ");
        str.append(id);
        db.delete(DBOpenHelper.getPhoneBookName(), str.toString(), null);
    }


    public void release() {
        if (db == null)
            db.close();
    }

    static String getSortKey(String str) {
        if (str == null || str.length() == 0)
            return "#";
        return str.substring(0, 1);
    }


    public ContactBean queryByPrimaryKey(String id, String name, String num) {
        ContactBean ContactData = null;
        String sql = "SELECT * FROM " + DBOpenHelper.getPhoneBookName() + " WHERE ID ='" + id +
                "' AND NAME='" + sqliteEscape(name) + "' AND NUM='" + num + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactData = new ContactBean();
                ContactData.setId(cursor.getInt(0));
                ContactData.setName(cursor.getString(1));
                ContactData.setMobilePhone(cursor.getString(2));
                ContactData.setInitial(cursor.getString(3));
                ContactData.setPinyin(cursor.getString(4));
                cursor.close();
            }
        }
        return ContactData;
    }

    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }
}
