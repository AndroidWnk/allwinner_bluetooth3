package com.etrans.bluetooth.View;

import com.etrans.bluetooth.bean.Phonebook;

import java.util.List;

public interface IPhoneView {



    void showQueryList(boolean b);

    void notify(List<Phonebook> mLstContact);

}
