package com.maihaoche.commonbiz.module.ui.recyclerview;

import java.util.ArrayList;
import java.util.List;

/**
 * 提取{@link SimpleAdapter}中与数据访问有关的方法。方便其子类重写
 * 作者：yang
 * 时间：17/6/15
 * 邮箱：yangyang@maihaoche.com
 */
public interface AdapterDataHandler<DATA> {

    void addData(DATA data);

    void clear();

    void addAll(List<DATA> datas);

    ArrayList<DATA> getData();

}
