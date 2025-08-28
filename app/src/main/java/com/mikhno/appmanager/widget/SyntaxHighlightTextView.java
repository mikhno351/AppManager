package com.mikhno.appmanager.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.mikhno.appmanager.data.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlightTextView extends AppCompatTextView {

    int colorTag, colorAttr, colorQuo;

    public SyntaxHighlightTextView(Context context) {
        super(context);
        init();
    }

    public SyntaxHighlightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SyntaxHighlightTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("PrivateResource")
    private void init() {
        if (Data.isDarkThemeEnabled(getContext())) {
            colorTag = getColor(com.google.android.material.R.color.m3_sys_color_dark_primary);
            colorAttr = getColor(com.google.android.material.R.color.m3_sys_color_dark_on_surface);
            colorQuo = getColor(com.google.android.material.R.color.m3_sys_color_dark_error);
        } else {
            colorTag = getColor(com.google.android.material.R.color.m3_sys_color_light_primary);
            colorAttr = getColor(com.google.android.material.R.color.m3_sys_color_light_on_surface);
            colorQuo = getColor(com.google.android.material.R.color.m3_sys_color_light_error);
        }
    }

    private int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(getContext(), colorRes);
    }

    public void setTextHighlightSyntax(CharSequence sequence) {
        String xmlText = sequence.toString();

        SpannableString spannableString = new SpannableString(xmlText);

        Pattern tagPattern = Pattern.compile("<([\\w:-]+)(\\s*[^>]*)?>|</([\\w:-]+)>");
        Matcher tagMatcher = tagPattern.matcher(xmlText);
        while (tagMatcher.find()) {
            int startIndex = tagMatcher.start();
            int endIndex = tagMatcher.end();

            spannableString.setSpan(
                    new ForegroundColorSpan(colorTag),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        Pattern attributePattern = Pattern.compile("\\w+(?::\\w+)?\\s*=\\s*(?:\"[^\"]*\"|'[^']*'|[\\w.:/?&=%]+)");
        Matcher attributeMatcher = attributePattern.matcher(xmlText);
        while (attributeMatcher.find()) {
            int startIndex = attributeMatcher.start();
            int endIndex = attributeMatcher.end();

            spannableString.setSpan(
                    new ForegroundColorSpan(colorAttr),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        Pattern quotedTextPattern = Pattern.compile("\"[^\"]*\"");
        Matcher quotedTextMatcher = quotedTextPattern.matcher(xmlText);
        while (quotedTextMatcher.find()) {
            int startIndex = quotedTextMatcher.start();
            int endIndex = quotedTextMatcher.end();

            spannableString.setSpan(
                    new ForegroundColorSpan(colorQuo),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        Pattern textPattern = Pattern.compile(">([^<]+)<");
        Matcher textMatcher = textPattern.matcher(xmlText);
        while (textMatcher.find()) {
            int startIndex = textMatcher.start(1);
            int endIndex = textMatcher.end(1);

            spannableString.setSpan(
                    new ForegroundColorSpan(Color.GREEN),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        setText(spannableString);
    }
}
