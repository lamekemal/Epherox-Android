package bj.archeos.epherox;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomDrawerFragment extends BottomSheetDialogFragment {


    public BottomDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_bottom_drawer, container, false);
        NavigationView navigationview = view.findViewById(R.id.navigationdraw);
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.nav_wh:
                        Toast.makeText(getActivity(),"1",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_clt:
                        Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.navigation_home:
                        Intent iCitation = new Intent(getContext(), MainActivity.class);
                        iCitation.putExtra("fragmentRequest",1);
                        startActivity(iCitation);
                        break;
                    case R.id.nav_hm:
                        Intent iMois = new Intent(getContext(), MainActivity.class);
                        iMois.putExtra("fragmentRequest",2);
                        startActivity(iMois);
                        break;
                    case R.id.nav_game:
                        Toast.makeText(getActivity(),"2",Toast.LENGTH_LONG).show();
                        break;
                    //case R.id.nav_set:
                    //    Toast.makeText(getActivity(),"Paramètre",Toast.LENGTH_LONG).show();
                    //    break;
                }
                return false;
            }
        });
        return view;
    }

}
