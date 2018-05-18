package com.etrans.bluetooth.db2;

import android.content.Context;
import android.util.Log;

import com.etrans.bluetooth.bean.Phonebook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluetoothPhoneBookModule {

//    private static final String TAG = BluetoothPhoneModule.class.getSimpleName();
    private static BluetoothPhoneBookModule instance;
    private Context mContext;
//    private List<BluetoothPhoneBookInfo> mPhoneNumList;

    private BluetoothPhoneBookModule() {
        super();
    }

    public static BluetoothPhoneBookModule getInstance() {
        synchronized (BluetoothPhoneBookModule.class) {
            if (instance == null) {
                instance = new BluetoothPhoneBookModule();
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
    }

    /**
     * 插入数据库
     *
     * @param bookInfoList
     */
    //    private void insertPhoneBook(List<BluetoothPhoneBookInfo> bookInfoList) {
    public static void insertPhoneBook(Phonebook phonebook) {
        Map<String, String> params = new HashMap<String, String>();
        String newName = phonebook.name;
//        for (BluetoothPhoneBookInfo bluetoothPhoneBookInfo : bookInfoList) {
//            String name = bluetoothPhoneBookInfo.getContactInfo();
//            if (name.lastIndexOf("/") == name.length() - 2) {
//                if (name.length() > 3) {
//                    //android
//                    newName = name.substring(0, name.length() - 2);
//                } else {
//                    newName = name.split("/")[0];
//                }
//            }else if (name.lastIndexOf("(手机)") == name.length() - 4 || name.lastIndexOf("(住宅)") == name.length() - 4) {
//                if (name.length() > 5) {
//                    newName = name.substring(0, name.length() - 4);
//                } else {
//                    newName = name.split("\\(")[0];
//                }
//            }else {
//                newName = name;
//            }
            String pinyinKey = PinyinUtils.getPinyin(newName);
            String initialKey = PinyinUtils.getSpells(newName);
            String pinyinkeyT9 = PinyinUtils.getNum(pinyinKey, false);
            String initialkeyT9 = PinyinUtils.getNum(initialKey, false);

            params.put("ID", "008");
            params.put("NAME", newName);
            params.put("NUM",phonebook.num);
            //全拼
            params.put("PY_KEY", pinyinKey);
            //拼音首字母
            params.put("INITIAL_KEY", initialKey);
            //拼音首字母_T9
            params.put("INITIAL_KEY_T9", initialkeyT9);
            //全拼T9
            params.put("PY_KEY_T9", pinyinkeyT9);
            //插入到数据库
            //TODO 判断是否存在 如果存在则不插入，如果存在则更新联系人，不存在则插入
//            if (queryContact(bluetoothPhoneBookInfo.getIndex() + "", newName, bluetoothPhoneBookInfo.getContactNumber()) != null) {
//                DataOperation.getInstance().updateContacts(params, bluetoothPhoneBookInfo.getIndex() + "", newName, bluetoothPhoneBookInfo.getContactNumber());
//            } else {
                DataOperation.getInstance().insertContacts(params);
//            }
//            Log.d(TAG, "insertPhoneBook: bluetoothPhoneBookInfo = " + bluetoothPhoneBookInfo.getContactNumber());
//        }


    }
//    private void insertPhoneBook(List<BluetoothPhoneBookInfo> bookInfoList) {
//        Map<String, String> params = new HashMap<String, String>();
//        String newName = null;
//        for (BluetoothPhoneBookInfo bluetoothPhoneBookInfo : bookInfoList) {
//            String name = bluetoothPhoneBookInfo.getContactInfo();
//            if (name.lastIndexOf("/") == name.length() - 2) {
//                if (name.length() > 3) {
//                    //android
//                    newName = name.substring(0, name.length() - 2);
//                } else {
//                    newName = name.split("/")[0];
//                }
//            }else if (name.lastIndexOf("(手机)") == name.length() - 4 || name.lastIndexOf("(住宅)") == name.length() - 4) {
//                if (name.length() > 5) {
//                    newName = name.substring(0, name.length() - 4);
//                } else {
//                    newName = name.split("\\(")[0];
//                }
//            }else {
//                newName = name;
//            }
//            String pinyinKey = PinyinUtils.getPinyin(newName);
//            String initialKey = PinyinUtils.getSpells(newName);
//            String pinyinkeyT9 = PinyinUtils.getNum(pinyinKey, false);
//            String initialkeyT9 = PinyinUtils.getNum(initialKey, false);
//
//            params.put("ID", bluetoothPhoneBookInfo.getIndex() + "");
//            params.put("NAME", newName);
//            params.put("NUM", bluetoothPhoneBookInfo.getContactNumber());
//            //全拼
//            params.put("PY_KEY", pinyinKey);
//            //拼音首字母
//            params.put("INITIAL_KEY", initialKey);
//            //拼音首字母_T9
//            params.put("INITIAL_KEY_T9", initialkeyT9);
//            //全拼T9
//            params.put("PY_KEY_T9", pinyinkeyT9);
//            //插入到数据库
//            //TODO 判断是否存在 如果存在则不插入，如果存在则更新联系人，不存在则插入
//            if (queryContact(bluetoothPhoneBookInfo.getIndex() + "", newName, bluetoothPhoneBookInfo.getContactNumber()) != null) {
//                DataOperation.getInstance().updateContacts(params, bluetoothPhoneBookInfo.getIndex() + "", newName, bluetoothPhoneBookInfo.getContactNumber());
//            } else {
//                DataOperation.getInstance().insertContacts(params);
//            }
//            Log.d(TAG, "insertPhoneBook: bluetoothPhoneBookInfo = " + bluetoothPhoneBookInfo.getContactNumber());
//        }
//
//
//    }


    private ContactBean queryContact(String id, String name, String num) {
        ContactBean contactBean = DataOperation.getInstance().queryByPrimaryKey(id, name, num);
        if (contactBean != null) {
            return contactBean;
        } else {
            return null;
        }
    }


    /**
     * 将汉字转化成拼音 存入到数据库中
     *
     * @param bookInfoList
     */
    public void setSearchKey(Phonebook phonebook) {
        insertPhoneBook(phonebook);
    }
//    public void setSearchKey(List<BluetoothPhoneBookInfo> bookInfoList) {
//        insertPhoneBook(bookInfoList);
//    }

    //对数据库进行操作 增 删 改 查

    /**
     * 根据姓名查询联系人
     *
     * @param contactName
     * @return
     */
    public List<ContactBean> queryContactListByName(String contactName) {
        return DataOperation.getInstance().queryContactByName(contactName);
    }

    /**
     * 根据电话号查询联系人
     *
     * @param contactPhoneNum
     * @return
     */
    public ContactBean queryContact(String contactPhoneNum) {
        List<ContactBean> contactInfoList = DataOperation.getInstance().queryContactByNum(contactPhoneNum);
        if (contactInfoList.size() > 0) {
            return contactInfoList.get(0);
        } else {
            return null;
        }
    }

    public List<ContactBean> queryContactListByNum(String contactPhoneNum) {
        List<ContactBean> contactInfoList = DataOperation.getInstance().queryContactByNum(contactPhoneNum);
        if (contactInfoList.size() > 0) {
            return contactInfoList;
        } else {
            return null;
        }
    }

    /**
     * 根据拼音查询T9
     *
     * @param py
     * @return
     */
    public List<ContactBean> queryContactByPinyinT9(String py) {
        return DataOperation.getInstance().queryByPyT9(py);
    }

    /**
     * 根据全拼查询T9
     *
     * @param initial
     * @return
     */
    public List<ContactBean> queryContactByInitialT9(String initial) {
        return DataOperation.getInstance().queryByInitialT9(initial);
    }

    /**
     * 全拼查询
     *
     * @param num
     * @return
     */
    public List<ContactBean> queryContactByInitial(String num) {
        return DataOperation.getInstance().queryByInitial(num);
    }

    /**
     * 拼音查询
     *
     * @param num
     * @return
     */
    public List<ContactBean> queryContactByPinyin(String num) {
        return DataOperation.getInstance().queryByPy(num);
    }

    /**
     * 查询所有
     */
    public List<ContactBean> queryContactAll() {
        return DataOperation.getInstance().getAllContacts();
    }
}
