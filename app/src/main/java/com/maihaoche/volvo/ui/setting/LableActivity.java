package com.maihaoche.volvo.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.data.OnDataError;
import com.maihaoche.volvo.data.OnDataGet;
import com.maihaoche.volvo.dao.po.LablePO;
import com.maihaoche.volvo.databinding.ActivityLableBinding;
import com.maihaoche.volvo.databinding.ItemLableListBinding;

import java.util.ArrayList;
import java.util.List;


public class LableActivity extends HeaderProviderActivity<ActivityLableBinding> {
    private static final String CHOSEN_WMS_GARAGE_ID = "chosen_wms_garage_id";
    private static final int WRITE_LABLE = 10;
    private LableListAdapter adapter;

    private Long mGarageId;


    public static Intent createIntent(Context context, long chosenWmsGarageId) {
        Intent intent = new Intent(context, LableActivity.class);
        intent.putExtra(CHOSEN_WMS_GARAGE_ID, chosenWmsGarageId);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_lable;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initDate();
        initView();
    }

    private void initView() {
        getContentBinding().list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LableListAdapter(this);
        getContentBinding().list.setAdapter(adapter);
        adapter.setOption(new LableListAdapter.OptionListener() {
            @Override
            public void delete(int position) {

                AppApplication.getDaoApi().deleteLable(adapter.list.get(position).getLableId())
                        .setOnResultGet(aBoolean -> {
                            AlertToast.show("删除标签成功");
                            adapter.delete(position);
                        })
                        .setOnDataError(new OnDataError() {
                            @Override
                            public void onDataError(String emsg) throws Exception {
                                AlertToast.show("删除标签失败:" + emsg);
                            }
                        })
                        .call(LableActivity.this);
            }

            @Override
            public void edit(int position) {
                startActivity(WriteLableActivity.createIntent(LableActivity.this, WriteLableActivity.EXTRA_EDIT, adapter.getList().get(position)));
                //编辑界面删除，列表页更新
                RxBus.getDefault().register(RefreshEvent.class, o -> {
                    initDate();
                });
            }
        });
        getContentBinding().addLable.setOnClickListener(v -> {
            startActivityForResult(WriteLableActivity.createIntent(LableActivity.this, WriteLableActivity.EXTRA_ADD), WRITE_LABLE);
        });
    }

    private void initDate() {
        mGarageId = getIntent().getLongExtra(CHOSEN_WMS_GARAGE_ID, 0);
        AppApplication.getDaoApi().getWmsGarage(mGarageId)
                .setOnResultGet(garagePO -> initHeader(garagePO.getGarageName()))
                .call(this);
        AppApplication.getDaoApi().getLable(mGarageId)
                .setOnResultGet(lablePOs -> adapter.setList(lablePOs))
                .call(this);
    }


    /**
     * 仓库列表的adapter
     */
    private static class LableListAdapter extends RecyclerView.Adapter<LableListAdapter.MyHolder> {

        private LayoutInflater inflater;
        private List<LablePO> list;
        private OptionListener listener;

        public void setList(List<LablePO> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addDate(LablePO lablePO) {
            if (list != null) {
                list.add(lablePO);
            }
            notifyDataSetChanged();
        }

        public void delete(int position) {
            list.remove(position);
            notifyDataSetChanged();
        }

        public List<LablePO> getList() {
            return list;
        }

        public void setOption(OptionListener listener) {
            this.listener = listener;
        }

        public LableListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            list = new ArrayList<>();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new MyHolder(inflater.inflate(R.layout.item_lable_list, parent, false));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.binding.itemLableListName.setText(list.get(position).getLableName());
            holder.binding.delete.setOnClickListener(v -> {
                if (listener != null){
                    listener.delete(position);
                }
            });
            holder.binding.edit.setOnClickListener(v -> {
                if (listener != null){
                    listener.edit(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class MyHolder extends RecyclerView.ViewHolder {
            public ItemLableListBinding binding;

            public MyHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }

        }

        public interface OptionListener {
            void delete(int position);

            void edit(int position);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == WriteLableActivity.RESULT_OK && requestCode == WRITE_LABLE) {
            LablePO lablePO = new LablePO();
            lablePO.setGarageId(mGarageId);
            lablePO.setLableName(data.getStringExtra(WriteLableActivity.EXTRA_LABLE_NAME));
            AppApplication.getDaoApi().saveLable(lablePO)
                    .setOnResultGet(new OnDataGet<Boolean>() {
                        @Override
                        public void onDataGet(Boolean aBoolean) throws Exception {
                            AlertToast.show("添加标签成功");
                            adapter.addDate(lablePO);
                        }
                    })
                    .setOnDataError(new OnDataError() {
                        @Override
                        public void onDataError(String emsg) throws Exception {
                            AlertToast.show("添加标签失败");

                        }
                    })
                    .call(this);
        }
    }
}
