package cn.ucai.live.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.GiftStatements;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.net.NetDao;
import cn.ucai.live.data.model.net.OnCompleteListener;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.ResultUtils;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class GiveStatementsFragment extends Fragment {

    private static int TYPE_PULL_DOWN = 1;
    private static int TYPE_PULL_UP = 2;
    @BindView(R.id.tv_refresh)
    TextView tvRefresh;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    Unbinder unbinder;
    MyAdapter adapter;
    List<GiftStatements> dataList;
    LinearLayoutManager layoutManager ; 
    int pageId = 1;
    int pageSize = 10;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_give_statements, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        setPullDownListener();
        setPullUpListener();
    }

    private void setPullUpListener() {
        recycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (newState==RecyclerView.SCROLL_STATE_IDLE&&adapter.isMore()&&lastPosition==adapter.getItemCount()-1){
                    pageId++;
                    downloadListData(TYPE_PULL_UP);
                }
            }
        });
    }

    private void setPullDownListener() {
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                tvRefresh.setVisibility(View.VISIBLE);
                pageId = 1;
                downloadListData(TYPE_PULL_DOWN);
            }
        });
    }

    private void initData() {
        pageId = 1;
        downloadListData(TYPE_PULL_DOWN);
    }

    private void downloadListData(final int type) {
        NetDao.getGivingGiftStatements(getContext(),
                EMClient.getInstance().getCurrentUser(),
                pageId,
                pageSize,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (type==TYPE_PULL_DOWN){
                            srl.setRefreshing(false);
                            tvRefresh.setVisibility(View.GONE);
                        }
                        boolean success = false;
                        if (s!=null){
                            Result result = ResultUtils.getListResultFromJson(s,GiftStatements.class);
                            if (result!=null&&result.isRetMsg()){
                                success = true;
                                GiftStatements[] array= (GiftStatements[]) result.getRetData();
                                List<GiftStatements> list = Arrays.asList(array);
                                if (list.size()==pageSize){
                                    adapter.setMore(true);
                                }else{
                                    adapter.setMore(false);
                                }
                                if (type == TYPE_PULL_DOWN){
                                    adapter.initData(list);
                                }else{
                                    adapter.addData(list);
                                }
                            }
                        }
                        if (!success){
                            CommonUtils.showShortToast("服务器拉取数据失败");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast("服务器拉取数据失败"+error);
                        srl.setRefreshing(false);
                        tvRefresh.setVisibility(View.GONE);
                    }
                });
    }

    private void initView() {
        dataList = new ArrayList<>();
        adapter = new MyAdapter(getContext(),dataList);
        recycleview.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        recycleview.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_CONTENT = 1;
        private static final int TYPE_FOOTER = 2;
        Context context;
        List<GiftStatements> stateList;

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
            footer = isMore?"上拉加载数据":"没有更多数据了";
            notifyDataSetChanged();
        }

        boolean isMore;
        public String getFooter() {
            return footer;
        }

        public void setFooter(String footer) {
            this.footer = footer;
            notifyDataSetChanged();
        }

        public void  initData(List<GiftStatements> list){
                stateList.clear();
                addData(list);
        }
        
        public void addData(List<GiftStatements> list){
            if (list!=null){
                stateList.addAll(list);
                notifyDataSetChanged();
            }
        }
        String footer;

        MyAdapter(Context context,List<GiftStatements> list){
            this.context = context;
            if (list==null){
                stateList = new ArrayList<>();
            }else{
                stateList = new ArrayList<>();
                stateList.addAll(list);
            }
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout ;
            if (viewType==TYPE_FOOTER){
                layout = inflater.inflate(R.layout.item_footer,null);
                return  new FooterHolder(layout);
            }else{
                layout = inflater.inflate(R.layout.item_statements_give,null);
                return new ContentHolder(layout);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position)==TYPE_FOOTER){
                FooterHolder footer = (FooterHolder) holder;
                footer.footer.setText(getFooter());
                return;
            }
            GiftStatements gift = stateList.get(position);
            ContentHolder content = (ContentHolder)holder;
            content.giftSender.setText(gift.getUname());
            content.giftNum.setText(gift.getGiftnum());
            content.giftReceive.setText(gift.getAnchor());
            EaseUserUtils.setAppUserAvatarByPath(context, 
                    LiveHelper.getInstance().getAppGiftList().get(gift.getId()).getGurl(),
                    content.imgGift, I.TYPE_GIFT);
        }

        @Override
        public int getItemCount() {
            return stateList.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position==getItemCount()-1){
                return TYPE_FOOTER;
            }else{
                return TYPE_CONTENT;
            }
        }
        
        class FooterHolder extends RecyclerView.ViewHolder{
            TextView footer;
            FooterHolder(View view){
                super(view);
                footer = (TextView) view.findViewById(R.id.tv_footer);
            }
        }
        class ContentHolder extends RecyclerView.ViewHolder{
             ImageView imgGift;
            TextView giftSender;
            TextView giftReceive;
            TextView giftNum;
            TextView giftPrice;
            public ContentHolder(View itemView) {
                super(itemView);
                imgGift = (ImageView) itemView.findViewById(R.id.img_gift);
                giftNum = (TextView) itemView.findViewById(R.id.tv_gift_num);
                giftSender = (TextView) itemView.findViewById(R.id.tv_uname);
                giftReceive = (TextView) itemView.findViewById(R.id.tv_toname);
                giftPrice = (TextView) itemView.findViewById(R.id.tv_price);
            }
        }
    }
    
}
