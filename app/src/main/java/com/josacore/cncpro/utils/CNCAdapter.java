package com.josacore.cncpro.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.josacore.cncpro.MainActivity;
import com.josacore.cncpro.classes.CNC;
import com.josacore.cncpro.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CNCAdapter extends RecyclerView.Adapter<CNCViewHolder> {

    private Context mContext;
    private List<CNC> cncList;
    private String uid;
    private MainActivity mainActivity;
    private Activity mActivity;

    public CNCAdapter(Context mContext, List<CNC> cncList,String uid, MainActivity mainActivity, Activity mActivity) {
        this.mContext = mContext;
        this.cncList = cncList;
        this.uid = uid;
        this.mainActivity = mainActivity;
        this.mActivity = mActivity;
    }

    @Override
    public CNCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_cnc, parent, false);

        return new CNCViewHolder(itemView,mContext);
    }

    @Override
    public void onBindViewHolder(final CNCViewHolder holder, int position) {
        final CNC cnc = cncList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PostDetailActivity
                //Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                //intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                //startActivity(intent);
                //viewCNC(cnc.getId());
                //Log.e("devise ADAPTER","ADptereee");
            }
        });
        String conectado = "";
        if(cnc.isConnected()) conectado = "CONECTADO";
        else conectado = "DESCONECTADO";

        TextView tv_main_type = (TextView) holder.itemView.findViewById(R.id.tv_main_brand);
        tv_main_type.setText(cnc.getBrand());
        TextView tv_main_name = (TextView) holder.itemView.findViewById(R.id.tv_main_name);
        tv_main_name.setText(cnc.getName());
        TextView tv_main_subname = (TextView) holder.itemView.findViewById(R.id.tv_main_subname2);
        tv_main_subname.setText(conectado);
        ImageView iv_main_cnc_connected = (ImageView) holder.itemView.findViewById(R.id.iv_main_connected);
        ImageView iv_main_cnc_created = (ImageView) holder.itemView.findViewById(R.id.iv_main_created);
        ImageView iv_main_cnc_image = (ImageView) holder.itemView.findViewById(R.id.iv_main_image);

        if(cnc.isConnected()) iv_main_cnc_connected.setImageResource(R.drawable.ic_connected);
        else iv_main_cnc_connected.setImageResource(R.drawable.ic_disconnected);
        if(uid.equals(cnc.getUid())){
            iv_main_cnc_created.setVisibility(View.VISIBLE);
            iv_main_cnc_created.setImageResource(R.drawable.correct_green);
        }else{
            iv_main_cnc_created.setVisibility(View.GONE);
        }
        iv_main_cnc_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCNC(cnc.getId());
            }
        });

        try {
            Picasso.with(mContext)
                    .load(cnc.getPhoto())
                    .placeholder(R.mipmap.ic_launcher2_round)
                    .fit()
                    .into(iv_main_cnc_image);
        }catch (Exception e){
            Log.e("ADAPTER",e.getMessage());
        }
        final ImageView holderMenu = (ImageView) holder.itemView.findViewById(R.id.overflow);
        holderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holderMenu);
            }
        });
    }

    private void viewCNC(String cncId) {
        //TODO: add se
        Toast.makeText(mContext, "ver", Toast.LENGTH_SHORT).show();
        if(isValidDestination(R.id.nav_fragment_control)) {
            Bundle bundle = new Bundle();
            bundle.putString("deviceId", cncId);
            Navigation.findNavController(mActivity, R.id.nav_host_fragment).navigate(R.id.nav_fragment_control,bundle);
            mainActivity.setCheckMenuDrawer("control");
            mainActivity.setCNCId(cncId);
        }
        mainActivity.enableFunctionsMenu();
    }
    public boolean isValidDestination(int destiantion){
        return destiantion != Navigation.findNavController(mActivity,R.id.nav_host_fragment).getCurrentDestination().getId();
    }
    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(cncList.size()==0){
                arr=0;
            }else{
                arr= cncList.size();
            }
        }catch (Exception e){}
        return arr;
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    //TODO: menu 3 puntos
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cardview_cnc, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }


    /**
     * Click listener for popup menu items
     */

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Editar", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Eliminar", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

}
