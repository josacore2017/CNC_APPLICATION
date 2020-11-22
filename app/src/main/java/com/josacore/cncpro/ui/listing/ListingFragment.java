package com.josacore.cncpro.ui.listing;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.josacore.cncpro.MainActivity;
import com.josacore.cncpro.classes.CNC;
import com.josacore.cncpro.R;
import com.josacore.cncpro.ui.control.ControlFragment;
import com.josacore.cncpro.utils.CNCAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListingFragment extends Fragment {

    private String TAG = "ListingFragment";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    private RecyclerView mRecycler;
    private List<CNC> list;

    private ListingViewModel listingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listingViewModel =
                ViewModelProviders.of(this).get(ListingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_listing, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mRecycler = root.findViewById(R.id.recycler_view);

        mDatabase.child("devices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    CNC value = dataSnapshot1.getValue(CNC.class);

                    list.add(value);
                    Log.e(TAG,value.getName());
                }

                Log.e(TAG,"INICIO - "+list.size());
                CNCAdapter recyclerAdapter = new CNCAdapter(getContext(),list,mAuth.getCurrentUser().getUid(), (MainActivity) getActivity(), getActivity());
                RecyclerView.LayoutManager recycler = new GridLayoutManager(getContext(),2);
                mRecycler.setLayoutManager(recycler);
                mRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                mRecycler.setHasFixedSize(true);
                mRecycler.setItemAnimator(new DefaultItemAnimator());
                mRecycler.setAdapter(recyclerAdapter);

                Log.e("oOOOOOOOOOOo","ESTAMOS A "+list.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ((MainActivity) getActivity()).setTitleSubtitle("Lista CNC","");
        ((MainActivity) getActivity()).disableFunctionsMenu();

        return root;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

}