package cn.wxn.txtreader.adapterOrHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.wxn.txtreader.R;

/**
 * Created by wangxn on 2015/12/24.
 */
class MyViewHolder extends RecyclerView.ViewHolder{
	public TextView  tv_name;
	public TextView  tv_path;
	public TextView  tv_size;
	public ImageView iv;
	public RelativeLayout rl_main;

	public MyViewHolder(View itemView) {
		super(itemView);

		tv_name = (TextView) itemView.findViewById(R.id.tv_name);
		tv_size = (TextView) itemView.findViewById(R.id.tv_size);
		tv_path = (TextView) itemView.findViewById(R.id.tv_path);
//		iv = (ImageView) itemView.findViewById(R.id.iv);
		rl_main = (RelativeLayout) itemView.findViewById(R.id.rl_main);
	}
}
