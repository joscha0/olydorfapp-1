/**
 * Copyright (C) OlyNet e.V. 2015 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.utils;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Utility class
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class UtilsMiscellaneous
{
    /**
     * Returns the size in pixels of an attribute dimension
     *
     * @param context the context to get the resource from
     * @param attr is the attribute dimension we want to know the size from
     *
     * @return the size in pixels of an attribute dimension
     */
    public static int getThemeAttributeDimensionSize(Context context, int attr)
    {
        TypedArray a = null;
        try
        {
            a = context.getTheme().obtainStyledAttributes(new int[] { attr });
            return a.getDimensionPixelSize(0, 0);
        }
        finally
        {
            if(a != null)
            {
                a.recycle();
            }
        }
    }

}
