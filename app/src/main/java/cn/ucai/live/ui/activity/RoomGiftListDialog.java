package cn.ucai.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;

/**
 * Created by wei on 2016/7/25.
 */
public class RoomGiftListDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.rv_gift)
    RecyclerView rvGift;
    @BindView(R.id.tv_my_bill)
    TextView tvMyBill;
    @BindView(R.id.tv_recharge)
    TextView tvRecharge;

    GridLayoutManager gm;
    GiftAdapter mAdpter;
    List<Gift> giftList = new ArrayList<>();
    public static RoomGiftListDialog newInstance() {
        RoomGiftListDialog dialog = new RoomGiftListDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gm = new GridLayoutManager(getContext(), I.GIFT_COLUMN_COUNT);
        rvGift.setLayoutManager(gm);
        mAdpter = new GiftAdapter(getContext(),giftList);
        rvGift.setAdapter(mAdpter);
        initData();
        rvGift.setAdapter(mAdpter);
    }

    private void initData() {
        Map<Integer, Gift> giftMap = LiveHelper.getInstance().getAppGiftList();
        Iterator<Map.Entry<Integer, Gift>> iterator = giftMap.entrySet().iterator();
        while (iterator.hasNext()){
            giftList.add(iterator.next().getValue());
        }
        Collections.sort(giftList, new Comparator<Gift>() {
            @Override
            public int compare(Gift lhs, Gift rhs) {
                return lhs.getId().compareTo(rhs.getId());
            }
        });
        mAdpter.notifyDataSetChanged();
    }


    private View.OnClickListener mClickListener;

    public void setGiftOnClickListener(View.OnClickListener dialogListener) {
        this.mClickListener = dialogListener;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带theme的构造器，获得的dialog边框距离屏幕仍有几毫米的缝隙。
        // Dialog dialog = new Dialog(getActivity());
        Dialog dialog = new Dialog(getActivity(), R.style.room_user_details_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        dialog.setContentView(R.layout.fragment_room_user_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftHolder> {
        private Context mContext;
        List<Gift> mGiftList;

        public GiftAdapter(Context mContext, List<Gift> mGiftList) {
            this.mContext = mContext;
            this.mGiftList = mGiftList;
        }


        @Override
        public GiftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_gift, null);
            return new GiftHolder(view);
        }

        @Override
        public void onBindViewHolder(GiftHolder holder, int position) {
            Gift gift = mGiftList.get(position);
            holder.tvGiftName.setText(gift.getGname());
            holder.tvGiftPrice.setText(String.valueOf(gift.getGprice()));
            EaseUserUtils.setAppUserAvatarByPath(mContext,gift.getGurl(),holder.imgGift,I.TYPE_GIFT);
            holder.giftLayout.setTag(gift.getId());
        }

        @Override
        public int getItemCount() {
            return mGiftList != null ? mGiftList.size() : 0;
        }



         class GiftHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.tv_gift_name)
            TextView tvGiftName;
            @BindView(R.id.img_gift)
            ImageView imgGift;
            @BindView(R.id.tv_gift_price)
            TextView tvGiftPrice;
            @BindView(R.id.layout_gift)
            LinearLayout giftLayout;
            GiftHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                giftLayout.setOnClickListener(mClickListener);
            }
        }
    }
}
