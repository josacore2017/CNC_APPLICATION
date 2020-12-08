package com.josacore.cncpro.ui.adding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josacore.cncpro.MainActivity;
import com.josacore.cncpro.ProfileEditActivity;
import com.josacore.cncpro.R;
import com.josacore.cncpro.classes.CNC;
import com.josacore.cncpro.classes.Command;
import com.josacore.cncpro.utils.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddingFragment extends Fragment {

    private final static String TAG= "AddingFragment";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private AddingViewModel addingViewModel;

    private ProgressBar pb_file_progress;
    private ProgressBar pb_queued_commands;
    private Button btn_choose_file;
    private FloatingActionButton fab;
    private LinearLayout ll_fragment_adding_run;
    private TextView tv_file_progress;
    private TextView tv_queued_commands;

    private ImageView iv_cardview_cnc_solo_state;
    private ImageView iv_cardview_cnc_solo_photo;
    private TextView tv_cardview_cnc_solo_name;
    private TextView tv_cardview_cnc_solo_brand;
    private ImageView iv_cardview_cnc_solo_list;

    private Intent fileIntent;

    private String action = "stop";
    private String action_btn = "start";
    private String serUsingphoto="";
    private String deviceId="";
    private String lastTime="";

    private CNC cnc;

    private ArrayList<String> listCommads;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        deviceId = getArguments().getString("deviceId");

        addingViewModel =
                ViewModelProviders.of(this).get(AddingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_adding, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        tv_file_progress = root.findViewById(R.id.tv_file_progress);
        tv_queued_commands = root.findViewById(R.id.tv_queued_commands);
        pb_file_progress = root.findViewById(R.id.pb_file_progress);
        pb_queued_commands = root.findViewById(R.id.pb_queued_commands);
        btn_choose_file = root.findViewById(R.id.btn_choose_file);
        ll_fragment_adding_run = root.findViewById(R.id.ll_fragment_adding_run);

        btn_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(action.equals("stop")) {
                    showChooseFileManager();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Se paran las tareas de la CNC, continuar?");
                    builder.setPositiveButton("Si,Seleccionar otro achivo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showChooseFileManager();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        });

        fab = root.findViewById(R.id.fab_run_commads);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(action.equals("stop")) {
                    startRunFile(view);
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Se paran las tareas de la CNC, continuar?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopRunFile(view);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();

                }
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
                lastTime = dataSnapshot.child("lastTime").getValue(String.class);
                long time = dataSnapshot.child("time").getValue(Long.class);
                boolean state = dataSnapshot.child("state").getValue(Boolean.class);
                boolean connected = dataSnapshot.child("connected").getValue(Boolean.class);
                List<String> userAllowed = dataSnapshot.child("userAllowed").getValue(List.class);

                cnc = new CNC(id,uid,name,brand,photo,userUsing,time, lastTime, state,connected,null);

                tv_cardview_cnc_solo_name.setText(name);
                tv_cardview_cnc_solo_brand.setText(brand);

                Date date = new Date();
                long tiempoActual = date.getTime();
                Log.e(TAG,"tiempo: "+(tiempoActual-time));
                if((tiempoActual-time)>60000){
                    if(!serUsingphoto.equals(""))
                    Picasso.with(getActivity())
                            .load(R.drawable.ic_baseline_account_circle_24)
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .transform(new PicassoCircleTransformation())
                            .fit()
                            .into(iv_cardview_cnc_solo_list);
                }else{
                    if(!serUsingphoto.equals(""))
                    Picasso.with(getActivity())
                            .load(serUsingphoto)
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .transform(new PicassoCircleTransformation())
                            .fit()
                            .into(iv_cardview_cnc_solo_list);
                }
                if(!photo.equals(""))
                Picasso.with(getActivity())
                        .load(photo)
                        .placeholder(R.mipmap.ic_launcher2_foreground)
                        .transform(new PicassoCircleTransformation())
                        .fit()
                        .into(iv_cardview_cnc_solo_photo);

                mDatabase.child("profiles").child(userUsing).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        serUsingphoto = dataSnapshot.child("photo").getValue(String.class);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





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



        showRun(false);
        return root;
    }
    public void stopRunFile(View view){
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        fab.setImageResource(R.drawable.ic_action_playback_play);
        action = "stop";
        Snackbar.make(view, "Se paro el recorrido de cnc", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    public void startRunFile(View view){
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.stop)));
        fab.setImageResource(R.drawable.ic_action_playback_stop);

        startRunnungFile();

        action = "play";
        Snackbar.make(view, "Iniciamos el recorrido de cnc", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    public void startRunnungFile(){
        int totalLines = listCommads.size();
        int lineExecuted = 0;

        String group = "";

        int recorrido = 0;
        ArrayList<Integer> listGroup = getListGroup();
        int groupTotalLines = listGroup.size();
        Log.e(TAG,"TOTAL GROUP LINES: "+groupTotalLines);
        int groupLines = 0;
        int i = 0;
        while(i<listCommads.size()) {
            String command=listCommads.get(i);
            String timeOk = updateNewCommand(command);
            boolean resOK = verifyResponseCNC(timeOk);
            if(resOK == true){
                Log.e(TAG,"INTENTO");
                try {
                    Random random = new Random();
                    Thread.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String groupActual="";
                try {
                    groupActual = (command.split("\\."))[0];
                }catch (Exception e){
                    groupActual = "";
                }
                lineExecuted++;
                pb_file_progress.setProgress(lineExecuted*100/totalLines);
                tv_file_progress.setText((lineExecuted*100/totalLines)+" %");


                if(group.equals(groupActual)){
                    groupLines++;
                }else{
                    groupLines = 0;
                    group = groupActual;
                    recorrido++;
                }

                int listGroupNumber = 1;
                try{
                    listGroupNumber = listGroup.get(recorrido);
                }catch (Exception e){
                    listGroupNumber = 1;
                }

                pb_queued_commands.setProgress(groupLines*100/listGroupNumber);
                //Log.e(TAG,"TOTA: "+listGroup.get(recorrido));
                Log.e(TAG,"RECORRIDO: "+recorrido);
                tv_queued_commands.setText(listGroupNumber+" -");
                i++;
            }else{
                Log.e(TAG,"FALLO - INTENTO");
            }
        }

    }

    private boolean verifyResponseCNC(String tiempoSend) {
        boolean res=false;

        if(tiempoSend.equals(lastTime)){
            res = true;
        }
        return res;
    }

    private ArrayList<Integer> getListGroup() {
        ArrayList<Integer> res = new ArrayList<>();
        String group="";
        int contador = 1;
        for (String command: listCommads) {
            String compare="";
            try {
                compare = command.split("\\.")[0];
            }catch (Exception e){}
            if(group.equals(compare)){
                contador++;
            }else{
                res.add(contador);
                contador=1;
                try {
                    group = command.split("\\.")[0];
                }catch (Exception e){
                    group = "";
                }
            }
        }
        return res;
    }

    private String updateNewCommand(String command) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String res = "";
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

        //updateCNCTimeUsed(tiempo);
        res = String.valueOf(tiempo);
        return res;
    }
    public void updateCNCTimeUsed(long tiempo){
        cnc.setTime(tiempo);
        cnc.setUserUsing(mAuth.getUid());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/devices/" + deviceId + "/" , cnc);
        mDatabase.updateChildren(childUpdates);
    }
    public void showChooseFileManager(){
        fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        startActivityForResult(Intent.createChooser(fileIntent, getText(R.string.select_file)), 10);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10){
            Uri uri = data.getData();
            try {
                listCommads = new ArrayList<>();
                InputStream in = getContext().getContentResolver().openInputStream(uri);
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                    listCommads.add(line);
                }
                String content = total.toString();
                btn_choose_file.setBackgroundColor(getResources().getColor(R.color.stop));
                btn_choose_file.setText("archivo: "+uri.getLastPathSegment());
                showRun(true);
            }catch (Exception e) {

            }
            
        }
    }
    public void showRun(boolean enable_run){
        if(enable_run) {
            fab.setVisibility(View.VISIBLE);
            ll_fragment_adding_run.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
            ll_fragment_adding_run.setVisibility(View.GONE);
        }
    }
}