package cn.luern0313.wristbilibili.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.models.VideoModel;
import cn.luern0313.wristbilibili.util.DataProcessUtil;
import cn.luern0313.wristbilibili.util.ImageTaskUtil;
import cn.luern0313.wristbilibili.util.LruCacheUtil;

/**
 * 被 luern0313 创建于 2020/2/2.
 */

public class VideoRecommendAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;

    private ListView listView;

    private ArrayList<VideoModel.VideoRecommendModel> recommendList;

    public VideoRecommendAdapter(LayoutInflater inflater, ArrayList<VideoModel.VideoRecommendModel> recommendList, ListView listView)
    {
        mInflater = inflater;
        this.listView = listView;
        this.recommendList = recommendList;
    }

    @Override
    public int getCount()
    {
        return recommendList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        final VideoModel.VideoRecommendModel v = recommendList.get(position);
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_list_video, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.img = convertView.findViewById(R.id.item_list_video_img);
            viewHolder.title = convertView.findViewById(R.id.item_list_video_title);
            viewHolder.up = convertView.findViewById(R.id.item_list_video_up);
            viewHolder.play = convertView.findViewById(R.id.item_list_video_play);
            viewHolder.danmaku = convertView.findViewById(R.id.item_list_video_danmaku);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Drawable upDrawable = convertView.getResources().getDrawable(R.drawable.icon_video_up);
        Drawable playNumDrawable = convertView.getResources().getDrawable(R.drawable.icon_number_play);
        Drawable danmakuNumDrawable = convertView.getResources().getDrawable(R.drawable.icon_number_danmu);
        upDrawable.setBounds(0, 0, DataProcessUtil.dip2px(10), DataProcessUtil.dip2px(10));
        playNumDrawable.setBounds(0, 0, DataProcessUtil.dip2px(10), DataProcessUtil.dip2px(10));
        danmakuNumDrawable.setBounds(0, 0, DataProcessUtil.dip2px(10), DataProcessUtil.dip2px(10));
        viewHolder.up.setCompoundDrawables(upDrawable,null, null,null);
        viewHolder.play.setCompoundDrawables(playNumDrawable,null, null,null);
        viewHolder.danmaku.setCompoundDrawables(danmakuNumDrawable,null, null,null);

        viewHolder.img.setImageResource(R.drawable.img_default_vid);
        viewHolder.title.setText(v.video_recommend_video_title);
        viewHolder.up.setText(v.video_recommend_video_owner_name);
        viewHolder.play.setText(v.video_recommend_video_play);
        viewHolder.danmaku.setText(v.video_recommend_video_danmaku);

        viewHolder.img.setTag(v.video_recommend_video_cover);
        BitmapDrawable h = setImageFormWeb(v.video_recommend_video_cover);
        if(h != null) viewHolder.img.setImageDrawable(h);

        return convertView;
    }

    class ViewHolder
    {
        ImageView img;
        TextView title;
        TextView up;
        TextView play;
        TextView danmaku;
    }

    private BitmapDrawable setImageFormWeb(String url)
    {
        if(LruCacheUtil.getLruCache().get(url) != null)
        {
            return LruCacheUtil.getLruCache().get(url);
        }
        else
        {
            ImageTaskUtil it = new ImageTaskUtil(listView);
            it.execute(url);
            return null;
        }
    }
}