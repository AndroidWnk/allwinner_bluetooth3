package com.etrans.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etrans.bluetooth.View.SideBar;
import com.etrans.bluetooth.adapter.SortAdapter;
import com.etrans.bluetooth.bean.Phonebook;
import com.etrans.bluetooth.db.Database;
import com.etrans.bluetooth.domain.ContactInfos;
import com.etrans.bluetooth.utils.CharacterParser;
import com.etrans.bluetooth.utils.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 单元名称:ContactActivity.java
 * 说明:
 */
public class ContactActivity extends Activity implements View.OnClickListener{
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private EditText et_contact_search;
    private ImageView mIvBack;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private List<ContactInfos> mLstContact;
    private SQLiteDatabase systemDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_listview_sort_main);
        initView();
    }


    public void initView() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
//        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
//                Toast.makeText(getApplication(), ((Phonebook)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
//            }
//        });


        ////
        if (mLstContact == null) {
            mLstContact = new ArrayList<ContactInfos>();
        }

//        mLstContact = (ArrayList<Phonebook>) Phonebook.mocks(this);
//        List<ContactInfos> contactInfos = GocDatabase.getDefault().getAllPhonebooks();
        systemDb = Database.getSystemDb();
        List<ContactInfos> contactInfos = Database.queryAllContacts(systemDb);
        mLstContact = filledData3(contactInfos);

        // 根据a-z进行排序源数据
        Collections.sort(mLstContact, pinyinComparator);
        adapter = new SortAdapter(this, mLstContact);
        sortListView.setAdapter(adapter);


        et_contact_search = (EditText) findViewById(R.id.et_contact_search);

        et_contact_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    filterData(et_contact_search.getText().toString().trim());
                }
                return false;
            }
        });
    }

    /**
     * 为ListView填充数据
     * @return
     */
    private List<ContactInfos> filledData3(List<ContactInfos> Contact){
        List<ContactInfos> mSortList = new ArrayList<ContactInfos>();

        for(int i=0; i<Contact.size(); i++){
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(Contact.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                Contact.get(i).setSortLetters(sortString.toUpperCase());
            }else{
                Contact.get(i).setSortLetters("#");
            }
        }
        return Contact;

    }
    /**
     * 为ListView填充数据
     * @return
     */
    private List<Phonebook> filledData2(List<Phonebook> Contact){
        List<Phonebook> mSortList = new ArrayList<Phonebook>();

        for(int i=0; i<Contact.size(); i++){
            Phonebook sortModel = new Phonebook();
            sortModel.setName(Contact.get(i).getName());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(Contact.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

//    /**
//     * 根据输入框中的值来过滤数据并更新ListView
//     * @param filterStr
//     */
    private void filterData(String filterStr){
        List<ContactInfos> filterDateList = new ArrayList<ContactInfos>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = mLstContact;
        }else{
            filterDateList.clear();
            for(ContactInfos sortModel : mLstContact){
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}