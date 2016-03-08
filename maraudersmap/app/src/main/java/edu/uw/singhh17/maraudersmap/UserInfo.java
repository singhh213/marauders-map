package edu.uw.singhh17.maraudersmap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ImageView profilePic = (ImageView)this.findViewById(R.id.profileImg);
        TextView name = (TextView)this.findViewById(R.id.name);
        final TextView phoneNo = (TextView)this.findViewById(R.id.phoneNo);
        Button sendTxtMsg = (Button)this.findViewById(R.id.sendTxtMsg);
        //final TextView email = (TextView)rootView.findViewById(R.id.email);
        //Button sendEmailMsg = (Button)rootView.findViewById(R.id.sendEmailMsg);

        sendTxtMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeText(phoneNo.getText().toString());
            }
        });
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
        intent.setData(Uri.parse("smsto:" + number));
        startActivity(intent);
        /*Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+number));
        startActivity(intent);*/
    }
}
