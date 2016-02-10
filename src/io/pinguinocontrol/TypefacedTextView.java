package io.pinguinocontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypefacedTextView extends TextView
{
 public TypefacedTextView(Context context, AttributeSet attrs)
 {
  super(context, attrs);
 
  // Typeface.createFromAsset doesn't work in the layout editor. Skipping ...
  if (isInEditMode())
   {
    return;
   }
 
   TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
   int fontName = styledAttrs.getInt(R.styleable.TypefacedTextView_typeface, 0);
   styledAttrs.recycle();
 
  if (fontName == 0 || fontName == 1)
   {
    Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
    setTypeface(typeface);
   }
  }
 }