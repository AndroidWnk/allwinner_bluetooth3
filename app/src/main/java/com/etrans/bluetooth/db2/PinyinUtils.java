package com.etrans.bluetooth.db2;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.UnsupportedEncodingException;


public class PinyinUtils {

    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600};
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z'};

    /**
     * 根据传入的字符串(包含汉字),得到拼音
     *
     * @param str 字符串
     * @return
     */
    public static String getPinyin(String str) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        str = str.trim();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // 如果是空格, 跳过
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (!String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                Log.d("PinYin", "getPinyin: c = " + c);
                sb.append(c);
                continue;
            }

            if (c >= -127 && c < 128) {
                // 肯定不是汉字
                sb.append(c);
            } else {
                String s = "";
                try {
                    // 通过char得到拼音集合. 单 -> dan, shan
                    String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinYin == null) {
                        sb.append(c);
                    } else {
                        s = pinYin[0];
                        sb.append(s);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    sb.append(s);
                }
            }
        }

        return sb.toString().toUpperCase();
    }

    /**
     * 将联系人的姓名拼音全部转化为数字
     *
     * @param search 要查询联系人的姓名拼音
     * @param status 是否转化成缩写
     * @return
     */
    public static String getNum(String search, boolean status) {
        String str = "";
        for (int i = 0; i < search.length(); i++) {
            String c = search.charAt(i) + "";
            if (c.equals("1")) {
                str = str + "1";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("A") || c.equals("B") || c.equals("C") || c.equals("2")
                    || c.equals("a") || c.equals("b") || c.equals("c")) {
                str = str + "2";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("D") || c.equals("E") || c.equals("F") || c.equals("3")
                    || c.equals("d") || c.equals("e") || c.equals("f")) {
                str = str + "3";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("G") || c.equals("H") || c.equals("I") || c.equals("4")
                    || c.equals("g") || c.equals("h") || c.equals("i")) {
                str = str + "4";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("J") || c.equals("K") || c.equals("L") || c.equals("5")
                    || c.equals("j") || c.equals("k") || c.equals("l")) {
                str = str + "5";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("M") || c.equals("N") || c.equals("O") || c.equals("6")
                    || c.equals("m") || c.equals("n") || c.equals("o")) {
                str = str + "6";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("P") || c.equals("Q") || c.equals("R") || c.equals("S") || c.equals("7")
                    || c.equals("p") || c.equals("q") || c.equals("r") || c.equals("s")) {
                str = str + "7";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("T") || c.equals("U") || c.equals("V") || c.equals("8")
                    || c.equals("t") || c.equals("u") || c.equals("v")) {
                str = str + "8";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("W") || c.equals("X") || c.equals("Y") || c.equals("Z") || c.equals("9")
                    || c.equals("w") || c.equals("x") || c.equals("y") || c.equals("z")) {
                str = str + "9";
                if (status) {
                    i = i + 1;
                }
                continue;
            } else if (c.equals("0")) {
                str = str + "0";
                if (status) {
                    i = i + 1;
                }
                continue;
            }
        }
        return str;
    }

    /**
     * 获取汉字的首字母
     *
     * @param characters
     * @return
     */
    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        try {
            for (int i = 0; i < characters.length(); i++) {

                char ch = characters.charAt(i);
                if ((ch >> 7) == 0) {
                    // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
                } else {
                    char spell = getFirstLetter(ch);
                    buffer.append(String.valueOf(spell));
                }
            }
            return buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {

        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            return null;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }


}
