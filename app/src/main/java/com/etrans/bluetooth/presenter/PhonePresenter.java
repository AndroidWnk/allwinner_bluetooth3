package com.etrans.bluetooth.presenter;

import android.content.Context;

import com.etrans.bluetooth.View.IPhoneView;
import com.etrans.bluetooth.bean.Phonebook;
import com.etrans.bluetooth.db.Database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PhonePresenter {

    private IPhoneView iPhoneView;
    private Context mcontext;
    private List<Phonebook> mLstContact;




    public PhonePresenter(IPhoneView view){

        iPhoneView = view;

    }


    public void queryContact(String num){

        if(mLstContact == null){
            mLstContact = new ArrayList<Phonebook>();
        }


        List<Phonebook> phoneName = Database.queryPhoneNames(num);
        mLstContact.clear();
        HashSet<String> set = new HashSet<String>();
        if (phoneName != null) {
            for (int i = 0; i < phoneName.size(); i++) {
                Phonebook bean = phoneName.get(i);
                if (bean == null) {
                    continue;
                }
                String str = bean.getName() + bean.getNum();
                if (set.contains(str))
                    continue;
                set.add(str);
                mLstContact.add(bean);
            }
        }

        if (mLstContact.size() > 0) {
            iPhoneView.notify(mLstContact);
            iPhoneView.showQueryList(true);
        } else {
            iPhoneView.showQueryList(false);
        }

    }






}
