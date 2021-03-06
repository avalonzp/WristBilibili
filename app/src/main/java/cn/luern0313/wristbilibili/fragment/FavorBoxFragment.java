package cn.luern0313.wristbilibili.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import cn.luern0313.wristbilibili.R;
import cn.luern0313.wristbilibili.adapter.FavorBoxAdapter;
import cn.luern0313.wristbilibili.api.FavorBoxApi;
import cn.luern0313.wristbilibili.models.FavorBoxModel;
import cn.luern0313.wristbilibili.ui.FavorArticleActivity;
import cn.luern0313.wristbilibili.ui.FavorVideoActivity;
import cn.luern0313.wristbilibili.util.ColorUtil;
import cn.luern0313.wristbilibili.util.SharedPreferencesUtil;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * 被 luern0313 创建于 2018/11/16.
 * 收藏的fragment
 * 畜生！你收藏了甚么！
 */

public class FavorBoxFragment extends Fragment
{
    private static final String ARG_FAVOR_BOX_MID = "argFavorBoxMid";
    private static final String ARG_FAVOR_BOX_IS_SHOW_OTHER_BOX = "argIsShowOtherBox";

    private Context ctx;
    private String mid;
    private boolean isShowOtherBox;
    private FavorBoxApi favorBoxApi;
    private ArrayList<FavorBoxModel> favorBoxArrayList;
    private FavorBoxAdapter.FavorBoxAdapterListener favorBoxAdapterListener;

    private View rootLayout;
    private ListView favListView;
    private WaveSwipeRefreshLayout waveSwipeRefreshLayout;

    public static boolean isLogin;

    private Handler handler = new Handler();
    private Runnable runnableUi, runnableNoWeb, runnableNoData;

    public FavorBoxFragment() {}

    public static FavorBoxFragment newInstance(String mid, boolean isShowOtherBox)
    {
        FavorBoxFragment fragment = new FavorBoxFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FAVOR_BOX_MID, mid);
        args.putBoolean(ARG_FAVOR_BOX_IS_SHOW_OTHER_BOX, isShowOtherBox);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            mid = getArguments().getString(ARG_FAVOR_BOX_MID);
            isShowOtherBox = getArguments().getBoolean(ARG_FAVOR_BOX_IS_SHOW_OTHER_BOX);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ctx = getActivity();
        rootLayout = inflater.inflate(R.layout.fragment_favor_box, container, false);

        favListView = rootLayout.findViewById(R.id.fav_listview);
        waveSwipeRefreshLayout = rootLayout.findViewById(R.id.fav_swipe);
        waveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        //noinspection ConstantConditions
        waveSwipeRefreshLayout.setWaveColor(ColorUtil.getColor(R.attr.colorPrimary, getContext()));
        waveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(isLogin)
                        {
                            favListView.setVisibility(View.GONE);
                            getFavorBox();
                        }
                        else waveSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        favorBoxAdapterListener = new FavorBoxAdapter.FavorBoxAdapterListener()
        {
            @Override
            public void onClick(int viewId, int position)
            {
                if(viewId == R.id.favor_lay)
                {
                    FavorBoxModel favorBoxModel = favorBoxArrayList.get(position);
                    if(favorBoxModel.getMode() == 0)
                    {
                        Intent intent = new Intent(ctx, FavorVideoActivity.class);
                        intent.putExtra("fid", favorBoxArrayList.get(position).getFid());
                        intent.putExtra("mid", mid);
                        startActivity(intent);
                    }
                    else if(favorBoxModel.getMode() == 1)
                    {
                        Intent intent = new Intent(ctx, FavorArticleActivity.class);
                        startActivity(intent);
                    }
                    else if(favorBoxModel.getMode() == 2)
                    {

                    }
                }
            }
        };

        runnableUi = new Runnable()
        {
            @Override
            public void run()
            {
                rootLayout.findViewById(R.id.fav_nologin).setVisibility(View.GONE);
                rootLayout.findViewById(R.id.fav_noweb).setVisibility(View.GONE);
                rootLayout.findViewById(R.id.fav_nonthing).setVisibility(View.GONE);
                favListView.setAdapter(new FavorBoxAdapter(inflater, favorBoxArrayList, favListView, favorBoxAdapterListener));
                favListView.setVisibility(View.VISIBLE);
                waveSwipeRefreshLayout.setRefreshing(false);
            }
        };

        runnableNoWeb = new Runnable()
        {
            @Override
            public void run()
            {
                rootLayout.findViewById(R.id.fav_nologin).setVisibility(View.GONE);
                rootLayout.findViewById(R.id.fav_noweb).setVisibility(View.VISIBLE);
                rootLayout.findViewById(R.id.fav_nonthing).setVisibility(View.GONE);
                favListView.setVisibility(View.GONE);
                waveSwipeRefreshLayout.setRefreshing(false);
            }
        };

        runnableNoData = new Runnable()
        {
            @Override
            public void run()
            {
                rootLayout.findViewById(R.id.fav_nologin).setVisibility(View.GONE);
                rootLayout.findViewById(R.id.fav_noweb).setVisibility(View.GONE);
                rootLayout.findViewById(R.id.fav_nonthing).setVisibility(View.VISIBLE);
                favListView.setVisibility(View.GONE);
                waveSwipeRefreshLayout.setRefreshing(false);
            }
        };

        isLogin = SharedPreferencesUtil.contains(SharedPreferencesUtil.cookies);
        if(isLogin)
        {
            waveSwipeRefreshLayout.setRefreshing(true);
            getFavorBox();
        }
        else
        {
            rootLayout.findViewById(R.id.fav_noweb).setVisibility(View.GONE);
            rootLayout.findViewById(R.id.fav_nologin).setVisibility(View.VISIBLE);
        }

        return rootLayout;
    }

    private void getFavorBox()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    favorBoxApi = new FavorBoxApi(mid, isShowOtherBox);
                    favorBoxArrayList = favorBoxApi.getFavorBox();
                    if(favorBoxArrayList != null && favorBoxArrayList.size() != 0)
                        handler.post(runnableUi);
                    else
                        handler.post(runnableNoData);
                }
                catch (NullPointerException e)
                {
                    handler.post(runnableNoData);
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    handler.post(runnableNoWeb);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
