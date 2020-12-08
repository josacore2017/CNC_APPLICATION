package com.josacore.cncpro.ui.control;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josacore.cncpro.MainActivity;
import com.josacore.cncpro.R;
import com.josacore.cncpro.classes.CNC;
import com.josacore.cncpro.classes.Command;
import com.josacore.cncpro.utils.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlFragment extends Fragment {

    private static String TAG = "ControlFragment";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ControlViewModel controlViewModel;

    private ImageView iv_control_arrow_left_top;
    private ImageView iv_control_arrow_top;
    private ImageView iv_control_arrow_right_top;
    private ImageView iv_control_arrow_left;
    private ImageView iv_control_home;
    private ImageView iv_control_arrow_right;
    private ImageView iv_control_arrow_left_bottom;
    private ImageView iv_control_arrow_bottom;
    private ImageView iv_control_arrow_right_bottom;

    private ImageView iv_cardview_cnc_solo_state;
    private ImageView iv_cardview_cnc_solo_photo;
    private TextView tv_cardview_cnc_solo_name;
    private TextView tv_cardview_cnc_solo_brand;
    private ImageView iv_cardview_cnc_solo_list;

    private CNC cnc;
    private String deviceId="";
    private String userUsingphoto ="";
    private String timeCNC="";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        deviceId = getArguments().getString("deviceId");
        Log.e("X DEVICE CONTROL: ",deviceId);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        controlViewModel =
                ViewModelProviders.of(this).get(ControlViewModel.class);
        View root = inflater.inflate(R.layout.fragment_control, container, false);

        iv_control_arrow_left_top = root.findViewById(R.id.iv_control_arrow_left_top);
        iv_control_arrow_top = root.findViewById(R.id.iv_control_arrow_top);
        iv_control_arrow_right_top = root.findViewById(R.id.iv_control_arrow_right_top);
        iv_control_arrow_left = root.findViewById(R.id.iv_control_arrow_left);
        iv_control_home = root.findViewById(R.id.iv_control_home);
        iv_control_arrow_right = root.findViewById(R.id.iv_control_arrow_right);
        iv_control_arrow_left_bottom = root.findViewById(R.id.iv_control_arrow_left_bottom);
        iv_control_arrow_bottom = root.findViewById(R.id.iv_control_arrow_bottom);
        iv_control_arrow_right_bottom = root.findViewById(R.id.iv_control_arrow_right_bottom);

        iv_control_arrow_left_bottom.setEnabled(false);
        iv_control_arrow_left_top.setEnabled(false);
        iv_control_arrow_right_top.setEnabled(false);
        iv_control_arrow_right_bottom.setEnabled(false);

        iv_control_arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNewCommand("G21G91G1X1F10");
            }
        });
        iv_control_arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNewCommand("G21G91G1X-1F10");
            }
        });
        iv_control_arrow_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNewCommand("G21G91G1Y1F10");
            }
        });
        iv_control_arrow_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNewCommand("G21G91G1Y-1F10");
            }
        });
        iv_control_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //G28 - G91 G28 X0 Y0
                updateNewCommand("G90 G0 X0 Y0 Z0");
            }
        });

        iv_cardview_cnc_solo_state = root.findViewById(R.id.iv_cardview_cnc_solo_state);
        iv_cardview_cnc_solo_photo = root.findViewById(R.id.iv_cardview_cnc_solo_photo);
        tv_cardview_cnc_solo_name = root.findViewById(R.id.tv_cardview_cnc_solo_name);
        tv_cardview_cnc_solo_brand = root.findViewById(R.id.tv_cardview_cnc_solo_brand);
        iv_cardview_cnc_solo_list = root.findViewById(R.id.iv_cardview_cnc_solo_list);

        mDatabase.child("devices").child(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.child("id").getValue(String.class);
                String uid = dataSnapshot.child("uid").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                String brand = dataSnapshot.child("brand").getValue(String.class);
                String photo = dataSnapshot.child("photo").getValue(String.class);
                String userUsing = dataSnapshot.child("userUsing").getValue(String.class);
                String lastTime = dataSnapshot.child("lastTime").getValue(String.class);
                final long time = dataSnapshot.child("time").getValue(Long.class);
                boolean state = dataSnapshot.child("state").getValue(Boolean.class);
                boolean connected = dataSnapshot.child("connected").getValue(Boolean.class);
                List<String> userAllowed = dataSnapshot.child("userAllowed").getValue(List.class);

                mDatabase.child("profiles").child(userUsing).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userUsingphoto = dataSnapshot.child("photo").getValue(String.class);
                        Log.e(TAG,"USER USING PHOTO: "+userUsingphoto);

                        Date date = new Date();
                        long tiempoActual = date.getTime();
                        Log.e(TAG,"tiempo: "+(tiempoActual-time));
                        Log.e(TAG,"VIENDO SI TIENE PHOTO EL USER USING");
                        if((tiempoActual-time)>60000){
                            if(!userUsingphoto.equals(""))
                                Picasso.with(getActivity())
                                        .load(R.drawable.ic_baseline_account_circle_24)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .transform(new PicassoCircleTransformation())
                                        .fit()
                                        .into(iv_cardview_cnc_solo_list);
                            else
                                iv_cardview_cnc_solo_list.setVisibility(View.INVISIBLE);
                        }else{
                            if(!userUsingphoto.equals(""))
                                Picasso.with(getActivity())
                                        .load(userUsingphoto)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .transform(new PicassoCircleTransformation())
                                        .fit()
                                        .into(iv_cardview_cnc_solo_list);
                            else
                                iv_cardview_cnc_solo_list.setVisibility(View.INVISIBLE);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                cnc = new CNC(id,uid,name,brand,photo,userUsing,time, lastTime, state,connected,null);

                tv_cardview_cnc_solo_name.setText(name);
                tv_cardview_cnc_solo_brand.setText(brand);


                if(!photo.equals(""))
                Picasso.with(getActivity())
                        .load(photo)
                        .placeholder(R.mipmap.ic_launcher2_foreground)
                        .transform(new PicassoCircleTransformation())
                        .fit()
                        .into(iv_cardview_cnc_solo_photo);


                if(connected == true) {
                    iv_cardview_cnc_solo_state.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }else{
                    iv_cardview_cnc_solo_state.setBackgroundColor(getResources().getColor(R.color.stop));
                }

                String subtitle = "";
                if(connected) subtitle = "CONECTADO"; else subtitle = "DESCONECTADO";
                ((MainActivity) getActivity()).setTitleSubtitle(cnc.getName(),subtitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ((MainActivity) getActivity()).enableFunctionsMenu();
        return root;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_listing, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_menu_fragment_listing_add) {
            //updateProperty();
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateNewCommand(String command) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        String uid = mAuth.getCurrentUser().getUid();
        Date date = new Date();
        long tiempo = date.getTime();
        Command command1 = new Command(key, uid, tiempo+"|"+command, tiempo);
        Map<String, Object> commandValues = command1.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/commands/1/"+deviceId+"/" , commandValues);
        childUpdates.put("/history/" + deviceId + "/" + key, commandValues);

        mDatabase.updateChildren(childUpdates);


        updateCNCTimeUsed(tiempo);

    }
    public void updateCNCTimeUsed(long tiempo){
        cnc.setTime(tiempo);
        cnc.setUserUsing(mAuth.getUid());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/devices/" + deviceId + "/" , cnc);
        mDatabase.updateChildren(childUpdates);
    }


}