package cn.luern0313.wristbilibili.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.models.SearchModel;
import cn.luern0313.wristbilibili.util.DataProcessUtil;
import cn.luern0313.wristbilibili.util.ImageTaskUtil;
import cn.luern0313.wristbilibili.util.LruCacheUtil;
import cn.luern0313.wristbilibili.util.SearchHtmlTagHandlerUtil;

/**
 * 被 luern0313 创建于 2020/2/3.
 */

public class SearchAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;

    private ListView listView;
    private SearchAdapterListener searchAdapterListener;
    private SearchHtmlTagHandlerUtil htmlTagHandlerUtil;

    private ArrayList<SearchModel> searchArrayList;

    public SearchAdapter(LayoutInflater inflater, ArrayList<SearchModel> searchArrayList, ListView listView, SearchAdapterListener searchAdapterListener, SearchHtmlTagHandlerUtil htmlTagHandlerUtil)
    {
        mInflater = inflater;
        this.searchArrayList = searchArrayList;
        this.listView = listView;
        this.searchAdapterListener = searchAdapterListener;
        this.htmlTagHandlerUtil = htmlTagHandlerUtil;
    }

    @Override
    public int getCount()
    {
        return searchArrayList.size();
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
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public int getItemViewType(int position)
    {
        return searchArrayList.get(position).search_mode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        int type = getItemViewType(position);
        BangumiViewHolder bangumiViewHolder = null;
        UserViewHolder userViewHolder = null;
        VideoViewHolder videoViewHolder = null;

        if(convertView == null)
        {
            if(type == 0)
            {
                convertView = mInflater.inflate(R.layout.item_search_bangumi, null);
                bangumiViewHolder = new BangumiViewHolder();
                convertView.setTag(bangumiViewHolder);

                bangumiViewHolder.bangumi_lay = convertView.findViewById(R.id.item_search_bangumi_lay);
                bangumiViewHolder.bangumi_img = convertView.findViewById(R.id.item_search_bangumi_img);
                bangumiViewHolder.bangumi_title = convertView.findViewById(R.id.item_search_bangumi_title);
                bangumiViewHolder.bangumi_score = convertView.findViewById(R.id.item_search_bangumi_score);
                bangumiViewHolder.bangumi_play = convertView.findViewById(R.id.item_search_bangumi_play);
                bangumiViewHolder.bangumi_ep = convertView.findViewById(R.id.item_search_bangumi_ep_count);
            }
            else if(type == 1)
            {
                convertView = mInflater.inflate(R.layout.item_search_user, null);
                userViewHolder = new UserViewHolder();
                convertView.setTag(userViewHolder);

                userViewHolder.user_lay = convertView.findViewById(R.id.item_search_user_lay);
                userViewHolder.user_name = convertView.findViewById(R.id.item_search_user_name);
                userViewHolder.user_face = convertView.findViewById(R.id.item_search_user_face);
                userViewHolder.user_off_1 = convertView.findViewById(R.id.item_search_user_ver_1);
                userViewHolder.user_off_2 = convertView.findViewById(R.id.item_search_user_ver_2);
                userViewHolder.user_sign = convertView.findViewById(R.id.item_search_user_sign);
                userViewHolder.user_desc = convertView.findViewById(R.id.item_search_user_desc);
            }
            else
            {
                convertView = mInflater.inflate(R.layout.item_search_video, null);
                videoViewHolder = new VideoViewHolder();
                convertView.setTag(videoViewHolder);

                videoViewHolder.video_lay = convertView.findViewById(R.id.item_search_video_lay);
                videoViewHolder.video_img = convertView.findViewById(R.id.item_search_video_img);
                videoViewHolder.video_time = convertView.findViewById(R.id.item_search_video_time);
                videoViewHolder.video_title = convertView.findViewById(R.id.item_search_video_title);
                videoViewHolder.video_up = convertView.findViewById(R.id.item_search_video_up);
                videoViewHolder.video_play = convertView.findViewById(R.id.item_search_video_play);
                videoViewHolder.video_danmaku = convertView.findViewById(R.id.item_search_video_danmaku);
            }
        }
        else
        {
            if(type == 0) bangumiViewHolder = (BangumiViewHolder) convertView.getTag();
            else if(type == 1) userViewHolder = (UserViewHolder) convertView.getTag();
            else if(type == 2) videoViewHolder = (VideoViewHolder) convertView.getTag();
        }

        if(type == 0)
        {
            SearchModel.SearchBangumiModel searchBangumiModel = (SearchModel.SearchBangumiModel) searchArrayList.get(position);
            bangumiViewHolder.bangumi_title.setText(Html.fromHtml(searchBangumiModel.search_bangumi_title, null, htmlTagHandlerUtil));
            bangumiViewHolder.bangumi_img.setImageResource(R.drawable.img_default_animation);
            bangumiViewHolder.bangumi_score.setText(searchBangumiModel.search_bangumi_score);
            bangumiViewHolder.bangumi_play.setText(searchBangumiModel.search_bangumi_time + " | " + searchBangumiModel.search_bangumi_area);
            bangumiViewHolder.bangumi_ep.setText(searchBangumiModel.search_bangumi_episode_count + "话");

            bangumiViewHolder.bangumi_lay.setOnClickListener(onViewClick(position));

            bangumiViewHolder.bangumi_img.setTag(searchBangumiModel.search_bangumi_cover);
            BitmapDrawable c = setImageFormWeb(searchBangumiModel.search_bangumi_cover);
            if(c != null) bangumiViewHolder.bangumi_img.setImageDrawable(c);
        }
        else if(type == 1)
        {
            SearchModel.SearchUserModel searchUserModel = (SearchModel.SearchUserModel) searchArrayList.get(position);
            userViewHolder.user_name.setText(searchUserModel.search_user_name);
            userViewHolder.user_face.setImageResource(R.drawable.img_default_head);
            String s = searchUserModel.search_user_official_desc.equals("") ? searchUserModel.search_user_sign : searchUserModel.search_user_official_desc;
            if(!s.equals(""))
            {
                userViewHolder.user_sign.setVisibility(View.VISIBLE);
                userViewHolder.user_sign.setText(s);
            }
            else
                userViewHolder.user_sign.setVisibility(View.GONE);
            userViewHolder.user_desc.setText("粉丝：" + searchUserModel.search_user_fans + "  投稿：" + searchUserModel.search_user_videos);
            switch (searchUserModel.search_user_official_type)
            {
                case 0:
                    userViewHolder.user_off_1.setVisibility(View.VISIBLE);
                    userViewHolder.user_off_2.setVisibility(View.GONE);
                    break;
                case 1:
                    userViewHolder.user_off_1.setVisibility(View.GONE);
                    userViewHolder.user_off_2.setVisibility(View.VISIBLE);
                    break;
                default:
                    userViewHolder.user_off_1.setVisibility(View.GONE);
                    userViewHolder.user_off_2.setVisibility(View.GONE);
                    break;
            }

            userViewHolder.user_lay.setOnClickListener(onViewClick(position));

            userViewHolder.user_face.setTag(searchUserModel.search_user_face);
            BitmapDrawable c = setImageFormWeb(searchUserModel.search_user_face);
            if(c != null) userViewHolder.user_face.setImageDrawable(c);
        }
        else if(type == 2)
        {
            SearchModel.SearchVideoModel searchVideoModel = (SearchModel.SearchVideoModel) searchArrayList.get(position);

            Drawable upDrawable = convertView.getResources().getDrawable(R.drawable.icon_video_up);
            Drawable playNumDrawable = convertView.getResources().getDrawable(R.drawable.icon_number_play);
            Drawable danmakuNumDrawable = convertView.getResources().getDrawable(R.drawable.icon_number_danmu);
            upDrawable.setBounds(0, 0, DataProcessUtil.dip2px(10), DataProcessUtil.dip2px(10));
            playNumDrawable.setBounds(0, 0, DataProcessUtil.dip2px(10), DataProcessUtil.dip2px(10));
            danmakuNumDrawable.setBounds(0, 0, DataProcessUtil.dip2px(10), DataProcessUtil.dip2px(10));
            videoViewHolder.video_up.setCompoundDrawables(upDrawable,null, null,null);
            videoViewHolder.video_play.setCompoundDrawables(playNumDrawable,null, null,null);
            videoViewHolder.video_danmaku.setCompoundDrawables(danmakuNumDrawable,null, null,null);

            videoViewHolder.video_title.setText(Html.fromHtml(searchVideoModel.search_video_title, null, htmlTagHandlerUtil));
            videoViewHolder.video_img.setImageResource(R.drawable.img_default_vid);
            videoViewHolder.video_time.setText(searchVideoModel.search_video_duration);
            videoViewHolder.video_up.setText(searchVideoModel.search_video_up_name);
            videoViewHolder.video_play.setText(searchVideoModel.search_video_play);
            videoViewHolder.video_danmaku.setText(searchVideoModel.search_video_danmaku);

            videoViewHolder.video_lay.setOnClickListener(onViewClick(position));

            videoViewHolder.video_img.setTag(searchVideoModel.search_video_cover);
            BitmapDrawable c = setImageFormWeb(searchVideoModel.search_video_cover);
            if(c != null) videoViewHolder.video_img.setImageDrawable(c);
        }
        return convertView;
    }

    class BangumiViewHolder
    {
        RelativeLayout bangumi_lay;
        ImageView bangumi_img;
        TextView bangumi_title;
        TextView bangumi_score;
        TextView bangumi_play;
        TextView bangumi_ep;
    }

    class UserViewHolder
    {
        RelativeLayout user_lay;
        TextView user_name;
        ImageView user_face;
        ImageView user_off_1;
        ImageView user_off_2;
        TextView user_sign;
        TextView user_desc;
    }

    class VideoViewHolder
    {
        RelativeLayout video_lay;
        ImageView video_img;
        TextView video_time;
        TextView video_title;
        TextView video_up;
        TextView video_play;
        TextView video_danmaku;
    }

    private View.OnClickListener onViewClick(final int position)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchAdapterListener.onClick(v.getId(), position);
            }
        };
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

    public interface SearchAdapterListener
    {
        void onClick(int viewId, int position);
    }
}
