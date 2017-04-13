package com.slobodastudio.smspanic.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import android.content.Context;
import android.widget.Toast;
import android.widget.AutoCompleteTextView.Validator;

import com.slobodastudio.smspanic.R;

public class Utils {

	public static ArrayList<String> getStringsArrayListFromStr(Context context,
			String string) {

		ArrayList<String> numbers = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(string, ",");
		boolean wasErrorNumber = false;
		while (st.hasMoreTokens()) {
			String number = st.nextToken().trim();
			// Замена стандартной функции на кастомную.
			if (isCustomValidPhone(number)) {
				numbers.add(number);
			} else {
				wasErrorNumber = true;
			}
			if (wasErrorNumber) {
				Toast.makeText(context, R.string.not_valid_number,
						Toast.LENGTH_LONG).show();
			}
		}
		return numbers;
	}

	public static String getStringFromArrayList(ArrayList<String> list) {

		StringBuilder sb = new StringBuilder();
		for (String str : list) {
			if (str.trim().length() > 0) {
				sb.append(str);
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static int getKeyByValue(LinkedHashMap<Integer, Integer> map,
			int value) {

		for (Entry<Integer, Integer> entry : map.entrySet()) {
			if (entry.getValue() == value) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public static int checkValuesByRepeating(LinkedHashMap<Integer, Integer> map) {

		int previosValue = -1;
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			if (entry.getValue() == previosValue) {
				return entry.getKey();
			} else {
				previosValue = entry.getValue();
			}
		}
		return -1;
	}

	public final static boolean isValidPhone(CharSequence target) {

		try {
			if (android.util.Patterns.PHONE.matcher(target).matches()
					&& target.length() < 13) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException exception) {
			exception.printStackTrace();
			return false;
		}
	}

	// Проверяем номера кастомной функцией
	public final static boolean isCustomValidPhone(CharSequence target) {
 
		boolean pointer = true;
		int error=0;
		try {
			char[] right_dictionary_for_validation = {'+','1','2','3','4','5','6','7','8','9','0'};
			for (int i = 0; i < target.length(); i++) {
				error = 0;
				for (int j = 0; j < right_dictionary_for_validation.length; j++) {
					if (target.charAt(i) != right_dictionary_for_validation[j]){
						error++;
					}
				}
				if (error==11){
					return false;
				}
			}

		} catch (NullPointerException exception) {
			exception.printStackTrace();
			return false;
		}
		
		if(pointer == true){
			return true;
		}else{
			return false;
		}
	}

}
