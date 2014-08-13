package com.zdnst.chameleon.httputil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


public class ConvertUtil {
	public static int pixl2DIP(float pixl, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				pixl, context.getResources().getDisplayMetrics());
	}

	
	public static String convertStreamToString(InputStream is)
			throws IOException {

		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayBuffer bab = new ByteArrayBuffer(32);
		int current = 0;
		while ((current = bis.read()) != -1) {
			bab.append((byte) current);
		}
		String result = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
		return result;

	}

	
	public static String convertStreamToString(InputStream is, String encoding)
			throws IOException {

		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayBuffer bab = new ByteArrayBuffer(32);
		int current = 0;
		while ((current = bis.read()) != -1) {
			bab.append((byte) current);
		}
		String result = EncodingUtils.getString(bab.toByteArray(), encoding);
		return result;

	}

	
	public static String dateTimeToStr(Date date, String dateTimeFormatStr) {
		String rsStr = null;
		if (dateTimeFormatStr != null) {
			SimpleDateFormat df = new SimpleDateFormat(dateTimeFormatStr);
			rsStr = df.format(date);
		} else {
			rsStr = dateTimeToStr(date, "yyyy年MM月");
		}
		return rsStr;
	}

	public static Date dateStrToTime(String dateStr, String dateTimeFormatStr) {
		SimpleDateFormat df = new SimpleDateFormat(dateTimeFormatStr);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ͵��ģ�巽��
	// public static void operate(final View view, final String url,
	// final int resId, final Map<String, String> params,
	// final Executable... executables) {
	// view.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// HttpRequestAsynTask httpRequestAsynTask = new HttpRequestAsynTask(
	// view.getContext()) {
	// @Override
	// protected void doPostExecute(String result) {
	// executables[0].execute(result);
	// }
	// };
	// httpRequestAsynTask.execute(url, TemplateUtil
	// .assembleFromTemplate(view.getContext(), resId, params));
	// }
	// });
	// }

	public static String converTimeFromString(String timeString) {
		String hour = timeString.substring(0, 2);
		String min = timeString.substring(2, 4);
		return hour + ":" + min;
	}

	public static String compputeDate(int beforeDay, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, beforeDay);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(calendar.getTime());
	}

	public static String transDate(String dateStr, String orgformat,
			String targetFormat) throws Exception {
		SimpleDateFormat orgDateFormat = new SimpleDateFormat(orgformat);
		Date date = orgDateFormat.parse(dateStr);
		SimpleDateFormat targetDateFormat = new SimpleDateFormat(targetFormat);
		return targetDateFormat.format(date);
	}

	public static int birthday2age(String dateStr, String format) {
		SimpleDateFormat orgDateFormat = new SimpleDateFormat(format);
		Date orgDate = null;
		try {
			orgDate = orgDateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (new Date().getYear() - orgDate.getYear());

	}

	public static int curDateDiff(String oldDateString, String oldDateFormat,
			int calendarType) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(oldDateFormat);
		try {
			Date oldDate = dateFormat.parse(oldDateString);
			Date curDate = new Date();
			long timeDiff = curDate.getTime() - oldDate.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeDiff);

			if (Calendar.YEAR == calendarType) {
				return calendar.get(Calendar.YEAR);
			} else if (Calendar.MONTH == calendarType) {
				return calendar.get(Calendar.MONTH);
			} else if (Calendar.DAY_OF_YEAR == calendarType) {
				return calendar.get(Calendar.MONTH);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void changeTextColor(TextView... textViews) {
		for (TextView textView : textViews) {
			if (textView.isPressed()) {
				textView.setTextColor(Color.WHITE);
			} else {
				textView.setTextColor(Color.GRAY);
			}
		}
	}

	public static void changeInputType(final Map<String, Integer> inputMap,
			final EditText editText) {
		editText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
					if (entry.getKey().equals(editText.getTag().toString())) {
						editText.setInputType(entry.getValue());
						break;
					}

				}

			}
		});
	}

	
	public static String assembleRawResourceString(Context context,
			int resultXmlId) {
		String result = "";
		try {
			result = ConvertUtil.convertStreamToString(context.getResources()
					.openRawResource(resultXmlId));
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	
//	public static String getTextTrimFromJdomElement(Element element,
//			String elementStr) {
//		if (element != null) {
//			Element childElement = element.getChild(elementStr);
//			return childElement == null ? null : childElement.getTextTrim();
//		} else {
//			return null;
//		}
//
//	}
}
