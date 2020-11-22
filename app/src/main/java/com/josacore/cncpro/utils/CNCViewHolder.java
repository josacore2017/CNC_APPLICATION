package com.josacore.cncpro.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.josacore.cncpro.classes.CNC;
import com.josacore.cncpro.R;
import com.squareup.picasso.Picasso;

public class CNCViewHolder extends RecyclerView.ViewHolder{

    private ImageView iv_main_cnc_image;
    private TextView tv_main_cnc_name;
    private TextView tv_main_cnc_subname;

    private Context context;

    public CNCViewHolder(View itemView, Context context) {
        super(itemView);
        iv_main_cnc_image =(ImageView) itemView.findViewById(R.id.iv_main_image);
        tv_main_cnc_name = (TextView) itemView.findViewById(R.id.tv_main_name);
        tv_main_cnc_subname = (TextView) itemView.findViewById(R.id.tv_main_subname2);
    }

    public void bindToPost(CNC cnc, View.OnClickListener starClickListener) {

        tv_main_cnc_name.setText(cnc.getName());
        if(cnc.isConnected())
            tv_main_cnc_subname.setText("Conectado");
        else
            tv_main_cnc_subname.setText("Desconectado");
        try {
            Picasso.with(context)
                    .load(cnc.getPhoto())
                    .placeholder(R.mipmap.ic_launcher2_foreground)
                    .fit()
                    .into(iv_main_cnc_image);
        }catch (Exception e){
            e.printStackTrace();
        }
        //starView.setOnClickListener(starClickListener);
    }

}
