package edu.uw.singhh17.maraudersmap;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailFragment extends Fragment {


    public UserDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);

        ImageView profilePic = (ImageView)rootView.findViewById(R.id.profileImg);
        TextView name = (TextView)rootView.findViewById(R.id.name);
        //final TextView email = (TextView)rootView.findViewById(R.id.email);
        final TextView phoneNo = (TextView)rootView.findViewById(R.id.phoneNo);

        //Button sendEmailMsg = (Button)rootView.findViewById(R.id.sendEmailMsg);
        Button sendTxtMsg = (Button)rootView.findViewById(R.id.sendTxtMsg);

        /*sendEmailMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        sendTxtMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeText(phoneNo.getText().toString());
            }
        });

        return rootView;
    }

    /*public void composeMail(String address) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
// The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }*/

    public void composeText(String number) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        intent.setData(Uri.parse("smsto:"+number));
        startActivity(intent);
        /*Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+number));
        startActivity(intent);*/
    }

}
