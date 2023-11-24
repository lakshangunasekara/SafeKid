package com.project.safekid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FamilyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamilyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button goNext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FamilyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FamilyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FamilyFragment newInstance(String param1, String param2) {
        FamilyFragment fragment = new FamilyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




    }

    private ListView listView;
    private FirebaseFirestore db;
    public static final String childIDToPass="id";
    public static final String parentEmailToPass="email";
    Bundle bundle;
    String parentEmail;
    FloatingActionButton refreshFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family, container, false);

        listView = view.findViewById(R.id.list_view);
        db = FirebaseFirestore.getInstance();
        bundle = getArguments();
        parentEmail = bundle.getString("parentEmail");//"acdb@jshyd.cok";

        refreshList();
        refreshFab = view.findViewById(R.id.refreshFab);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList();
            }
        });



        /*parentsRef.get().addOnSuccessListener(querySnapshot -> {


            for (QueryDocumentSnapshot parentDoc : querySnapshot) {

                CollectionReference childrenRef = parentDoc.getReference().collection("children");

                childrenRef.get().addOnSuccessListener(childSnapshot -> {
                    childDocs.addAll(childSnapshot.getDocuments());


                        for (DocumentSnapshot childDoc : childDocs) {
                            childDocIds.add(childDoc.getId());

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, childDocIds);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener((parent, view1, position, id) -> {
                            String childDocId = childDocIds.get(position);

                            Intent intent = new Intent(getActivity(), ChildInfo.class);
                            intent.putExtra(valueToPass, childDocId);
                            startActivity(intent);
                        });
                    }
                }).addOnFailureListener(e -> {
                    Log.d("Subcollection", "Data error");
                });
            }
        }).addOnFailureListener(e -> {
            // Handle errors that occurred while retrieving the "parents" collection
            Log.d("Parentcollection", "Data error");
        });*/

        return view;
    }

    private void refreshList() {
        DocumentReference parentsRef = db.collection("parents").document(parentEmail);
        CollectionReference childCollection = parentsRef.collection("children");
        childCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> childDocIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        childDocIds.add(document.getId());
                    }

                    Toast.makeText(getActivity().getApplicationContext(), "Getting data!", Toast.LENGTH_SHORT).show();

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, childDocIds);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener((parent, view1, position, id) -> {
                        String childDocId = childDocIds.get(position);

                        Intent intent = new Intent(getActivity(), ChildInfo.class);
                        intent.putExtra(childIDToPass, childDocId);
                        intent.putExtra(parentEmailToPass, parentEmail);
                        startActivity(intent);
                    });
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    //@Override
   // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                         Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //View rootview =  inflater.inflate(R.layout.fragment_family, container, false);
        //Button goNext = (Button) rootview.findViewById(R.id.goToLocation);
        //goNext.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
            //    Intent intent = new Intent(getActivity(), MapsActivity.class);
            //    startActivity(intent);
            //}
        //});
       // return rootview;
    //}



}