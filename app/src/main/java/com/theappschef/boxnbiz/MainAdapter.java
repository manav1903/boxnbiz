package com.theappschef.boxnbiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.LinkedList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private List<User> UserList;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public MainAdapter(Activity context) {
        this.context = context;
        UserList = new LinkedList<>();
    }

    public void setUserList(List<User> UserList) {
        this.UserList = UserList;
    }

    public void updateList(List<User> list) {
        UserList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_list, parent, false);
                viewHolder = new UserViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        User User = UserList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                UserViewHolder UserViewHolder = (UserViewHolder) holder;
                UserViewHolder.UserTitle.setText(User.getTitle());
                Glide.with(context).load(User.getImageUrl()).apply(RequestOptions.centerCropTransform()).into(UserViewHolder.UserImage);
                UserViewHolder.click.setOnClickListener(view -> {
                    showDialog(User);
                });
                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return UserList == null ? 0 : UserList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == UserList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new User());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = UserList.size() - 1;
        User result = getItem(position);

        if (result != null) {
            UserList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(User User) {
        UserList.add(User);
        notifyItemInserted(UserList.size() - 1);
    }

    public void addAll(List<User> moveResults) {
        for (User result : moveResults) {
            add(result);
        }
    }

    public User getItem(int position) {
        return UserList.get(position);
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView UserTitle;
        private ImageView UserImage;
        private ConstraintLayout click;

        public UserViewHolder(View itemView) {
            super(itemView);
            UserTitle = (TextView) itemView.findViewById(R.id.title);
            UserImage = (ImageView) itemView.findViewById(R.id.poster);

            click = itemView.findViewById(R.id.click);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }

    private void showDialog(User user) {

        final android.app.AlertDialog dialog;
        View view = context.getLayoutInflater().inflate(R.layout.dialog_list, null);
        dialog = new android.app.AlertDialog.Builder(context).setView(view).create();
        Window window = dialog.getWindow();

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.show();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv = view.findViewById(R.id.title);
        tv.setText(user.getTitle());
        ImageView tv2 = view.findViewById(R.id.poster);
        Glide.with(context).load(user.getImageUrl()).apply(RequestOptions.centerCropTransform()).into(tv2);
        TextView tv3 = view.findViewById(R.id.desc);
        tv3.setText(user.getDesc());
    }
}