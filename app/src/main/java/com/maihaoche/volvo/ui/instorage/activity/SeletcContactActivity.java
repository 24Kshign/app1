package com.maihaoche.volvo.ui.instorage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.maihaoche.commonbiz.module.ui.AlertToast;
import com.maihaoche.commonbiz.module.ui.BaseActivity;
import com.maihaoche.commonbiz.module.ui.HeaderProviderActivity;
import com.maihaoche.commonbiz.service.utils.RxBus;
import com.maihaoche.volvo.AppApplication;
import com.maihaoche.volvo.R;
import com.maihaoche.volvo.databinding.ActivitySeletcContactBinding;
import com.maihaoche.volvo.ui.instorage.adapter.ContactListAdapter;
import com.maihaoche.volvo.ui.instorage.event.BrandEvent;
import com.maihaoche.volvo.ui.instorage.event.ContactEvent;

import java.util.ArrayList;


public class SeletcContactActivity extends HeaderProviderActivity<ActivitySeletcContactBinding> {

    private static final String COMPANY_ID = "company_id";

    private ActivitySeletcContactBinding binding;
    private ContactListAdapter adapter;


    public static Intent create(BaseActivity activity,Long company){
        Intent intent = new Intent(activity,SeletcContactActivity.class);
        intent.putExtra(COMPANY_ID,company);
        return intent;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_seletc_contact;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initHeader("选择联系人");
        binding = getContentBinding();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.list.setLayoutManager(manager);
        adapter = new ContactListAdapter(this,new ArrayList<>());
        adapter.setListener(contact -> {
            BrandEvent event = new BrandEvent(contact.name,contact.id,BrandEvent.TYPE_MAN);
            event.setPhone(contact.phoneName);
            RxBus.getDefault().post(event);
            finish();
        });
        binding.list.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        AppApplication.getServerAPI().getContact(getIntent().getLongExtra(COMPANY_ID,0))
                .setOnDataError(emsg -> AlertToast.show(emsg))
                .setOnDataGet(response->{
                    if(response.result == null || response.result.size() == 0){
                        binding.empty.getRoot().setVisibility(View.VISIBLE);
                    }else{
                        adapter.setContacts(response.result);
                    }

                })
                .call(this);
    }
}
