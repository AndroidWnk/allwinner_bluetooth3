package com.etrans.bluetooth.bean;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Phonebook {


    public String name;
    public String num;
    private String sortLetters;  //显示数据拼音的首字母


    public Phonebook() {
    }

    public Phonebook(String name, String num, Bitmap headBitmap) {
        this.name = name;
        this.num = num;
        this.headBitmap = headBitmap;
    }

    /**
     * 头像
     */
    private Bitmap headBitmap = null;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Bitmap getHeadBitmap() {
        return headBitmap;
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }




    public static List<Phonebook> mocks(Context c) {
        List<Phonebook> contacts = new ArrayList<>();
        contacts.add(fromRes("阿妹","13655433245",null));
        contacts.add(fromRes("阿郎","13898748372",null));
        contacts.add(fromRes("李德华","13598748394",null));
        contacts.add(fromRes("陈丽丽","13898748372",null));
        contacts.add(fromRes("龚琳娜","13560228695",null));
        contacts.add(fromRes("宋冬野","13598748394",null));
        contacts.add(fromRes("品冠","13655433245",null));
        contacts.add(fromRes("BOBO","13655433245",null));
        contacts.add(fromRes("Jobs","13898748372",null));
        contacts.add(fromRes("动力火车","13560228695",null));
        contacts.add(fromRes("伍佰","13655433245",null));
        contacts.add(fromRes("~夏先生","13598748394",null));
        contacts.add(fromRes("苏醒","13655433245",null));
        contacts.add(fromRes("阮今天","13598748394",null));
        contacts.add(fromRes("曾一鸣","13560228695",null));

        return contacts;
    }

//    public static List<Phonebook> mocks(Context c) {
//        List<Phonebook> contacts = new ArrayList<>();
//
//        contacts.add(fromRes("王五","13560228695",null);
//        contacts.add(fromRes("王五","13560228695",null);
//
//
//    }


    private static Phonebook fromRes(String img,String fn, Bitmap bitmap) {
        return new Phonebook( img,fn,bitmap);
    }

}
