package com.hnhy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

/**
 * Author:guc
 * Time:2018/5/20
 * Description:
 */
public abstract class GroupAdapter<Parent, Child> extends BaseExpandableListAdapter {

    protected List<Parent> mGroups;
    protected List<List<Child>> mChilds;
    protected Context context;
    private LayoutInflater inflater;
    private int mParentLayoutId;
    private int mChildLayoutId;

    public GroupAdapter(Context context, List<Parent> groups, List<List<Child>> childs, int parentLayoutId, int childLayoutId) {
        super();
        this.mGroups = groups;
        this.mChilds = childs;
        this.context = context;
        this.mParentLayoutId = parentLayoutId;
        this.mChildLayoutId = childLayoutId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChilds.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return mChilds.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ParentViewHolder holder = ParentViewHolder.get(context, convertView, null, mParentLayoutId, groupPosition);
//        TextView groupNameTextView = (TextView) convertView
//                .findViewById(R.id.tv_group);
//        ImageView ivSelector = (ImageView) convertView
//                .findViewById(R.id.iv_selector);
//        groupNameTextView.setText(getGroup(groupPosition).toString());
//        ivSelector.setImageResource(R.drawable.icon_close);
//
//        // 更换展开分组图片
//        if (!isExpanded) {
//            ivSelector.setImageResource(R.drawable.icon_open);
//        }
        bindParentData(holder, (Parent) getGroup(groupPosition), groupPosition, isExpanded);
        return holder.getConvertView();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
//        convertView = inflater.inflate(mChildLayoutId, null);
        ChildViewHolder holder = ChildViewHolder.get(context, convertView, null, mChildLayoutId, childPosition);
//        TextView nickTextView = (TextView) convertView
//                .findViewById(R.id.tv_child);
//
//        nickTextView.setText(getChild(groupPosition, childPosition).toString());

        bindChildData(holder, (Child) getChild(groupPosition, childPosition), groupPosition, childPosition, isLastChild);
        return holder.getConvertView();
    }

    // 子选项是否可以选择
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public abstract void bindParentData(ParentViewHolder holder, Parent parent, int groupPosition, boolean isExpanded);

    public abstract void bindChildData(ChildViewHolder holder, Child child, int groupPosition, int childPosition,
                                       boolean isLastChild);

}
