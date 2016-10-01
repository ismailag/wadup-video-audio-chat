package com.example.ismail.wadup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class Connection_Mon implements ValueEventListener{
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Firebase pres=new Firebase("https://wadup-a1d4b.firebaseio.com/pres/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        boolean connected = dataSnapshot.getValue(Boolean.class);
        if (connected)
        {

            System.out.println("connected");
            pres.setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            pres.onDisconnect().removeValue();


        }
        else
        {
            System.out.println("not connected");
        }


    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
