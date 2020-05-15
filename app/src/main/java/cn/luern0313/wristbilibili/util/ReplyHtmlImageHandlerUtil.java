package cn.luern0313.wristbilibili.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Html;
import android.util.LruCache;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.luern0313.wristbilibili.R;

/**
 * 被 luern0313 创建于 2020/2/25.
 */
public class ReplyHtmlImageHandlerUtil implements Html.ImageGetter
{
    private Context ctx;
    private TextView container;
    private LruCache<String, BitmapDrawable> lruCache;
    private HashMap<String, Integer> emoteSize;

    public ReplyHtmlImageHandlerUtil(Context ctx, LruCache<String, BitmapDrawable> lruCache, TextView text, HashMap<String, Integer> emoteSize)
    {
        this.ctx = ctx;
        this.container = text;
        this.lruCache = lruCache;
        this.emoteSize = emoteSize;
    }

    @Override
    public Drawable getDrawable(String source)
    {
        if(source == null) return null;

        if(isNumericZidai(source))
        {
            Drawable drawable;
            drawable = ctx.getResources().getDrawable(Integer.parseInt(source));
            drawable.setBounds(0, 0, (int)(DataProcessUtil.sp2px(ctx, 14) * 1.6), DataProcessUtil.sp2px(ctx, 14));
            return drawable;
        }
        else
        {
            if(source.indexOf("//") == 0) source = "http:" + source;
            if(source.endsWith(".webp")) source = source.substring(0, source.lastIndexOf("@"));

            if(lruCache.get(source) != null) return lruCache.get(source);
            else
            {
                final LevelListDrawable drawable = new LevelListDrawable();
                final String finalSource = source;

                drawable.addLevel(0, 0, container.getResources().getDrawable(R.drawable.img_default_article_img));
                drawable.setBounds(0, 0, emoteSize.get(source) * DataProcessUtil.sp2px(ctx, 18), emoteSize.get(source) * DataProcessUtil.sp2px(ctx, 18));

                Glide.with(ctx).asBitmap().load(source).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.img_default_article_img).into(
                        new SimpleTarget<Bitmap>()
                        {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition)
                            {
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                                if(lruCache.get(finalSource) == null) lruCache.put(finalSource, bitmapDrawable);

                                drawable.addLevel(1, 1, bitmapDrawable);
                                drawable.setLevel(1);
                                container.invalidate();
                                container.setText(container.getText());
                            }
                        });
                return drawable;
            }
        }
    }

    private static boolean isNumericZidai(String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }
}