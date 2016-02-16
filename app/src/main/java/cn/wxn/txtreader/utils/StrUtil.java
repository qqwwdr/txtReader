package cn.wxn.txtreader.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2014/8/29
 *
 * @NewY. W
 */
public class StrUtil {

	// 邮箱的匹配
	private final static Pattern          emailer       = Pattern
			                                                      .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	// 2种日期的匹配
	private final static SimpleDateFormat dateFormater  = new SimpleDateFormat(
			                                                                          "yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat(
			                                                                          "yyyy-MM-dd");
	private static final Object              TAG           = "StrUtil";

	/**
	 * 将日期格式的字符串转化成Date对象
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 判断字符串是否是null, 或者是"", 或者是包含空字符的字符串
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input) || "null".equalsIgnoreCase(input)) {
			return true;
		}
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否是匹配 email格式
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0) {
			return false;
		}
		return emailer.matcher(email).matches();
	}

	/**
	 * 校验一个字符串是否匹配 手机号码的格式
	 */
	public static boolean checkMobile(String mobile) {
		String  regex = "^1(3[0-9]|5[0-9]|8[0-9])\\d{8}$";
		Pattern p     = Pattern.compile(regex);
		Matcher m     = p.matcher(mobile);
		return m.find();
	}

	// public static boolean checkPwd(String password){
	// String regex = "^$"
	// }

	/**
	 * 将一个手机号的中间4位隐藏
	 *
	 * @param phoneNum
	 * @return 如果不是手机号, 直接返回原有的字符串
	 */
	public static String hiddenMobile(String phoneNum) {
		if (TextUtils.isEmpty(phoneNum)) {
			return "";
		}
		if (isMobileNumber(phoneNum)) {
			char[] chars = phoneNum.toCharArray();
			for (int i = 3; i <= 6; i++) {
				chars[i] = '*';
			}
			return new String(chars);
		}
		return phoneNum;
	}


	public static String hiddenStr(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}

		char[] chars = str.toCharArray();

		for (int i = 2; i <= str.length() - 3; i++) {
			chars[i] = '*';
		}
		return new String(chars);

	}

	/**
	 * 130、131、132、133、134、135、136、137、138、139、
	 * 145、147、
	 * 150、151、152、153、155、156、157、158、159、
	 * 170,176、177、178、
	 * 180、181、182、183、184、185、186、187、188、189 、
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNumber(String mobiles) {
		if (StrUtil.isEmpty(mobiles)) {
			return false;
		}
		Pattern p = Pattern.compile("^1[34578]\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 如果content的字符长度超过length, 就保留length长度的内容,后面的内容删掉,用 "..."替代
	 *
	 * @param content
	 * @param length
	 * @return
	 */
	public static String endStringWithEllip(String content, int length) {
		if (content.length() > length) {
			StringBuilder sb = new StringBuilder(content.substring(0, length));
			sb.append("...");
			return sb.toString();
		}
		return content;
	}

	/**
	 * @param value 一个可以按照split分割的字符串
	 * @param split
	 * @return List<String> 分割之后的字符串集合
	 */
	public static List<String> split2List(String value, String split) {
		if (value != null && value.length() > 0) {
			List<String> retVal = new ArrayList<String>();
			String[] strArr = value.split(split);
			if (strArr.length > 1) {
				for (String str : strArr) {
					retVal.add(str);
				}
			} else {
				retVal.add(value);
			}
			return retVal;
		}
		return null;
	}

	/**
	 * @param strs  需要被整合成一个字符串的字符串集合
	 * @param split 各个字符串中间用split作为分隔符
	 * @return 得到一个按照spit间隔的字符串
	 */
	public static String integrate2Str(List<String> strs, String split) {
		if (strs != null && strs.size() > 0) {
			StringBuilder retVal = new StringBuilder();
			for (String str : strs) {
				retVal.append(split + str);
			}
			return retVal.substring(1).toString();
		}
		return null;
	}

	/**
	 * 全角转半角
	 *
	 * @param input
	 * @return
	 */
	// 半角空格32,全角空格12288
	// 其他字符半角33~126,其他字符全角65281~65374,相差65248
	public static String SBCToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 半角转换为全角
	 *
	 * @param input
	 * @return
	 */
	// 半角空格32,全角空格12288
	// 其他字符半角33~126,其他字符全角65281~65374,相差65248
	public static String DBCToSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] > 33 && c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * 除去空格，忽略全角半角符号,忽略大小写比较
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isEqual(String a, String b) {
		String na = SBCToDBC(a).replaceAll(" ", "");
		String nb = SBCToDBC(b).replaceAll(" ", "");
		return (na == nb || na.equalsIgnoreCase(nb));
	}

	/**
	 * 校验身份证
	 *
	 * @param str
	 * @return
	 */
	public static boolean identity(String str) {
		if (StrUtil.isEmpty(str)) {
			return false;
		}
		String  regx    = "([0-9]{17}([0-9]|X))|([0-9]{15})";
		Pattern pattern = Pattern.compile(regx);
		return pattern.matcher(str).matches();
	}

	/**
	 * 校验密码, 长度大于八位,小于16位, 数字 和字母的组合
	 *
	 * @param str
	 * @return
	 */
	public static boolean isPwd(String str) {
		if (StrUtil.isEmpty(str)) {
			return false;
		}

		String regx = "^[0-9a-zA-Z@~:\\-\\*^%&',;=?$\\.\\x22]{8,16}$";

		Pattern pattern = Pattern.compile(regx);
		return pattern.matcher(str).matches();
	}

	/**
	 * 校验密码, 数字 和字母的组合
	 *
	 * @param str
	 * @return
	 */
	public static boolean isLetters(String str) {

		String regx = "^[0-9a-zA-Z]+$";

		Pattern pattern = Pattern.compile(regx);
		return pattern.matcher(str).matches();
	}

	/**
	 * 校验密码, 长度大于6
	 *
	 * @param str
	 * @return 大于等于6位数, 则返回true, 否则false
	 */
	public static boolean mixLength(String str) {
		if (str.length() < 6) {
			return false;
		}
		return true;
	}

	/**
	 * 校验密码, 长度小于20
	 *
	 * @param str
	 * @return 大于16位则返回false, 否则返回true
	 */
	public static boolean maxLength(String str) {
		if (str.length() > 16) {
			return false;
		}
		return true;
	}

	/**
	 * 验证验证输入汉字
	 *
	 * @param str 待验证的字符串
	 * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsChinese(String str) {
		if (StrUtil.isEmpty(str)) {
			return false;
		}
		String  regex   = "^[\u4e00-\u9fa5],{0,}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(str).matches();
	}

	public static void clearStringBuilder(StringBuilder sb) {
		if (sb != null) {
			sb.delete(0, sb.length());
		}
	}

	/**
	 * 验证输入是否是特殊字符
	 *
	 * @param str 待验证的字符串
	 * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean isSpecialCharacter(String str) {
		if (isEmpty(str)) {
			return false;
		}
		String  regex   = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(str).matches();
	}

//	public static boolean isUrl(String input) {
//		if (isEmpty(input)) {
//			return false;
//		}
//		// string pattern = @"http://([\w-]+\.)+[\w-]+(/[\w- ./?%&=]*)?";
//		String regex = "^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
//		Pattern pattern = Pattern.compile(regex);
//		return pattern.matcher(input).matches();
//	}

	/**
	 * 提取 一个字符串 中的 数字,
	 *
	 * @param input
	 * @return
	 */
	public static Double getNumber(String input) {
		if (StrUtil.isEmpty(input)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if ((c >= '0' && c <= '9') || c == '.' || c == '-') {
				sb.append(c);
			}
		}

		String numberStr = sb.toString();

		if (StrUtil.isEmpty(numberStr)) {
			return null;
		}

		try {
			return Double.valueOf(numberStr);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 提取 一个字符串 中的 数字,只提取整数
	 *
	 * @param input
	 * @return
	 */
	public static Integer getInteger(String input) {
		Double tmp = getNumber(input);
		if (tmp != null) {
			return tmp.intValue();
		}
		return null;
	}

	public static Long getLong(String input) {
		Double tmp = getNumber(input);
		if (tmp != null) {
			return tmp.longValue();
		}
		return null;
	}


	public static boolean isValidCode(String str) {
		if (StrUtil.isEmpty(str)) {
			return false;
		}

		String regx = "^[0-9]{6}$";

		Pattern pattern = Pattern.compile(regx);
		return pattern.matcher(str).matches();
	}

	/**
	 * 获得2位小数的字符串
	 *
	 * @param txt
	 * @return
	 */
	public static String getTwodecimal(String txt) {
		Double number = getNumber(txt);
		if (number == null) {
			return null;
		}
		return getTwodecimal(number);
	}

	/**
	 * 获得2位小数的字符串
	 *
	 * @param number
	 * @return
	 */
	public static String getTwodecimal(Double number) {

		return String.format("%.2f", number);
	}

	/**
	 * 判断是否为数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 *
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 判断给定的字符串是否是表示16进制的字符串
	 *
	 * @param content
	 * @return
	 */
	public static boolean isHexNumber(String content) {
		if (StrUtil.isEmpty(content)) {
			return false;
		}
		int length = content.length();
		if (length > 50) {
			content = content.substring(0, 30);
		}
		length = content.length();
		String  regex   = "^[0-9a-fA-F]{" + length + "}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(content).matches();
	}


	/**
	 * 获取字符串的长度，如果有中文，则每个中文字符计为2位
	 *
	 * @param value 指定的字符串
	 * @return 字符串的长度
	 */
	public static int length(String value) {
		int    valueLength = 0;
		String chinese     = "[\u0391-\uFFE5]";
	    /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
			String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
				valueLength += 2;
			} else {
                /* 其他字符长度为1 */
				valueLength += 1;
			}
		}
		return valueLength;
	}



}
